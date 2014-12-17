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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
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
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param city
	 *            the city to filter
	 * @throws ServletException
	 * @throws IOException
	 */
	private void createEventsOverview(HttpServletRequest request,
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
		//TODO: Uncomment
		//incrementEventsOverviewCallCounter(request);
		
		//Get a RequestDispatcher for EventsOverview
		RequestDispatcher requestdispatcher = request.getRequestDispatcher("/EventsOverview.jsp");
		
		//Forward request
        requestdispatcher.forward(request, response);

	}
}
