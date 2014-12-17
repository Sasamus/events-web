package ae.eventswebassignment4.servlets;

import java.io.IOException;
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
