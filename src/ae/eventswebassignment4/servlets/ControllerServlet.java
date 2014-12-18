package ae.eventswebassignment4.servlets;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ae.eventsbusinessassignment4.databasemanaging.DatabaseManagerBean;
import ae.eventsbusinessassignment4.entities.Comment;
import ae.eventsbusinessassignment4.entities.Event;
import ae.eventsbusinessassignment4.entities.User;

/**
 * Servlet implementation class ControllerServlet
 * 
 * @author Albin Engström
 */
@Singleton
@WebServlet("/ControllerServlet")
public class ControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// TODO: Page counters shared?
	// TODO: Synchronization
	// TODO: Entities bean name
	// TODO: Images
	// TODO: Only read data on deploy, possible?

	/**
	 * A DatabaseManagerBean object
	 */
	@EJB(beanName = "DatabaseManagerBean")
	private DatabaseManagerBean databaseManagerBean;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ControllerServlet() {
		super();
	}
	
	 /**
     * Increments eventOverviewCallCounter
     */
    private synchronized void incrementEventsOverviewCallCounter(HttpServletRequest request){
    	incrementCallCounter(request, "eventsOverviewCallCounter");
    }
    
    /**
     * Increments addEventsCallCounter
     */
    private synchronized void incrementAddEventCallCounter(HttpServletRequest request){
    	incrementCallCounter(request, "addEventCallCounter");
    }
    
    /**
     * Increments userProfileCallCounter
     */
    private synchronized void incrementUserProfileCallCounter(HttpServletRequest request){
    	incrementCallCounter(request, "userProfileCallCounter");
    }
    
    /**
     * Increments the call counter corresponding to key in session
     * 
     * @param request the request
     * @param key the key to the counter in session
     */
    private synchronized void incrementCallCounter(HttpServletRequest request, String key){
    	
		HttpSession session = request.getSession(true);

	    Integer visitCount = (Integer) session.getAttribute(key);
	    
	    if(visitCount == null) {
	    	visitCount = 0;
	    }
	    
	    visitCount = new Integer(visitCount.intValue() + 1);
	    
	    session.setAttribute(key, visitCount);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected synchronized void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Call readData()
		databaseManagerBean.readData();

		// Create page
		createEventsOverview(request, response, "all");
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		//Check what page to create
		if(request.getParameter("AddEvent").equals("true")){
			
			//A List of User's
			List<User> users = databaseManagerBean.getAllUsers();
			
			//Add users to request
			request.setAttribute("allUsers", users);
						
			//Create AddVent
			createAddEvent(request, response);
		}
		else if(request.getParameter("NewEvent").equals("true")){
			
			//An Event object
			Event event = new Event();
			
			//Set it's values
			event.setEventTitle(request.getParameter("title"));
			event.setEventCity(request.getParameter("city"));
			event.setEventDescription(request.getParameter("description"));
			
			//Get times
			String startTime = request.getParameter("starttime");
			String endTime = request.getParameter("endtime");
			
			//Create Timestamps with the time Strings
			Timestamp timestampStart = Timestamp.valueOf(startTime);
			Timestamp timestampEnd = Timestamp.valueOf(endTime);
			
			//Create a Timestamp with the current time
			Timestamp currentTimestamp = new Timestamp(new Date().getTime());
			
			//Set event's time related fields
			event.setEventStart(timestampStart);
			event.setEventEnd(timestampEnd);
			event.setLastUpdate(currentTimestamp);
			
			//Get Organizer
			String userId = request.getParameter("organizerId");
			
			//The User
			User user = databaseManagerBean.getUser(Integer.parseInt(userId));
			
			//Add Event
			databaseManagerBean.addEvent(event, user);
			
			//Create EventsOverview
			createEventsOverview(request, response, "all");
			
			
		}
		else if(request.getParameter("GoToUser").equals("true")){
			
			//Get the User Id
			String id = request.getParameter("UserId");
			
			//Get the User with id
			User user = databaseManagerBean.getUser(Integer.parseInt(id));
			
			//Get the Comments user have made
			List<Comment> comments = databaseManagerBean.getUserComments(user);
			
			//Add size of comments to request
			request.setAttribute("nrComments", comments.size());
			
			//Create UserProfile
			createUserProfile(request, response, user);
		}
		else{
			//Get filter city
			String city = request.getParameter("filter");
			
			city = new String(city.getBytes("8859_1"),"UTF8");
			
			//Create EventsOverview
			createEventsOverview(request, response, city);
		}
		
		
	}
	
	/**
	 * @param request the request
	 * @param response the response
	 * @param user the user to show
	 * @throws ServletException
	 * @throws IOException
	 */
	private synchronized void createUserProfile(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException{
		
		//Get a RequestDispatcher for UserProfile
		RequestDispatcher requestdispatcher = request.getRequestDispatcher("/UserProfile.jsp");
		
		//Set attribute user
		request.setAttribute("user", user);
		
		//Get Lists of past and future Events user organizes
		List<Event> pastEvents = databaseManagerBean.getEventsUserOrganizes(true, false, user);
		List<Event> futureEvents = databaseManagerBean.getEventsUserOrganizes(false, true, user);
		
		//Set attributes with the number of the events
		request.setAttribute("nrPastEvents", pastEvents.size());
		request.setAttribute("nrFutureEvents", futureEvents.size());
		
		//Increment userProfileCallCounter
		incrementUserProfileCallCounter(request);
		
		//Forward request
        requestdispatcher.forward(request, response);
		
	}
	
	/**
	 * @param request the request
	 * @param response the response
	 * @throws ServletException
	 * @throws IOException
	 */
	private synchronized void createAddEvent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		
		//Increment addEventCallCounter
		incrementAddEventCallCounter(request);
		
		//Get a RequestDispatcher for AddEvent
		RequestDispatcher requestdispatcher = request.getRequestDispatcher("/AddEvent.jsp");
		
		//Forward request
        requestdispatcher.forward(request, response);
		
	}

	/**
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param city
	 *            the city to filter
	 * @throws ServletException
	 * @throws IOException
	 */
	private synchronized void createEventsOverview(HttpServletRequest request,
			HttpServletResponse response, String city) throws ServletException,
			IOException {

		List<Event> events = null;
		if(city.equals("all") || city.equals("")){
			
			//Get all Events and put them in events
			events = databaseManagerBean.getAllEvents();
		}
		else{
			
			//Get Events and put them in events
			events = databaseManagerBean.getCityEvents(city);
		}
		
		
		//Add events to request
		request.setAttribute("eventsList", events);
		
		//A Map using Event's as keys and stores List's of User's
		Map<Event, List<User>> organizerUserMap = new HashMap<Event, List<User>>();
		
		//Loop through events
		for(Event event : events){
			
			//Get the User's that are Organizer's for event
			List<User> users = databaseManagerBean.getEventOrganizers(event);
			
			//Put the List in organizerUserMap
			organizerUserMap.put(event, users);	
		}
		
		//Add organizerUserMap to request
		request.setAttribute("organizerUserMap", organizerUserMap);
		
		//Increment eventsOverviewCallCounter
		incrementEventsOverviewCallCounter(request);
		
		//Get a RequestDispatcher for EventsOverview
		RequestDispatcher requestdispatcher = request.getRequestDispatcher("/EventsOverview.jsp");
		
		//Forward request
        requestdispatcher.forward(request, response);

	}
}
