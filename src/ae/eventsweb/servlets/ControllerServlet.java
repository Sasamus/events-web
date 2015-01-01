package ae.eventsweb.servlets;

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

import ae.eventsbusiness.beans.AddEventBean;
import ae.eventsbusiness.beans.CallCounterBean;
import ae.eventsbusiness.entities.Comment;
import ae.eventsbusiness.entities.Event;
import ae.eventsbusiness.entities.User;

/**
 * Servlet implementation class ControllerServlet
 * 
 * @author Albin Engström
 */
@Singleton
@WebServlet("/ControllerServlet")
public class ControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * A DatabaseManagerBean object
	 */
	@EJB(beanName = "DatabaseManagerBean")
	private ae.eventsbusiness.beans.DatabaseManagerBean databaseManagerBean;

	/**
	 * A CallCounterBean object
	 */
	@EJB(beanName = "CallCounterBean")
	private CallCounterBean callcounterbean;

	/**
	 * An AddEventBean object
	 */
	@EJB(beanName = "AddEventBean")
	private AddEventBean addeventbean;

	/**
	 * Keeps track of if data have been read from file
	 */
	boolean dataRead = false;

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
	protected synchronized void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Read Data
		if (!dataRead) {
			databaseManagerBean.readData();
			dataRead = true;
		}

		// Create page
		createEventsOverview(request, response, "all");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected synchronized void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Check what page to create
		if (request.getParameter("AddEvent").equals("true")) {

			// A List of User's
			List<User> users = databaseManagerBean.getAllUsers();

			// Add users to request
			request.setAttribute("allUsers", users);

			// Create AddVent
			createAddEvent(request, response);

		} else if (request.getParameter("NewEvent").equals("true")) {

			// Add event
			addeventbean.addEvent(request);

			// Create EventsOverview
			createEventsOverview(request, response, "all");

		} else if (request.getParameter("GoToUser").equals("true")) {

			// Get the User Id
			String id = request.getParameter("UserId");

			// Get the User with id
			User user = databaseManagerBean.getUser(Integer.parseInt(id));

			// Get the Comments user have made
			List<Comment> comments = databaseManagerBean.getUserComments(user);

			// Add size of comments to request
			request.setAttribute("nrComments", comments.size());

			// Create UserProfile
			createUserProfile(request, response, user);
		} else {
			// Get filter city
			String city = request.getParameter("filter");

			city = new String(city.getBytes("8859_1"), "UTF8");

			// Create EventsOverview
			createEventsOverview(request, response, city);
		}

	}

	/**
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param user
	 *            the user to show
	 * @throws ServletException
	 * @throws IOException
	 */
	private synchronized void createUserProfile(HttpServletRequest request,
			HttpServletResponse response, User user) throws ServletException,
			IOException {

		// Get a RequestDispatcher for UserProfile
		RequestDispatcher requestdispatcher = request
				.getRequestDispatcher("/UserProfile.jsp");

		// Set attribute user
		request.setAttribute("user", user);

		// Get Lists of past and future Events user organizes
		List<Event> pastEvents = databaseManagerBean.getEventsUserOrganizes(
				true, false, user);
		List<Event> futureEvents = databaseManagerBean.getEventsUserOrganizes(
				false, true, user);

		// Set attributes with the number of the events
		request.setAttribute("nrPastEvents", pastEvents.size());
		request.setAttribute("nrFutureEvents", futureEvents.size());

		// Increment userProfileCallCounter
		callcounterbean.incrementUserProfileCallCounter(user.getId());

		// Add userPofileCallCounter to request
		request.setAttribute("callCounter",
				callcounterbean.getUserProfileCallCounter(user.getId()));

		// Forward request
		requestdispatcher.forward(request, response);

	}

	/**
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 * @throws IOException
	 */
	private synchronized void createAddEvent(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Increment addEventCallCounter
		callcounterbean.incrementAddEventCallCounter();

		// Add addUsereCallCounter to request
		request.setAttribute("callCounter",
				callcounterbean.getAddEventCallCounter());

		// Get a RequestDispatcher for AddEvent
		RequestDispatcher requestdispatcher = request
				.getRequestDispatcher("/AddEvent.jsp");

		// Forward request
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
		if (city.equals("all") || city.equals("")) {

			// Get all Events and put them in events
			events = databaseManagerBean.getAllEvents();
			
			// Set calendarSrc int request to null 
			request.setAttribute("calendarSrc", null);
			
		} else {

			// Get Events and put them in events
			events = databaseManagerBean.getCityEvents(city);
			
			// Create a calendar source
			String calendarSrc = "https://www.google.com/calendar/embed?src=" 
			+ databaseManagerBean.getCalendarId(city) + "&ctz=Europe/Stockholm";
			
			System.out.println("MyDebug:" + calendarSrc);
			
			// Add calendarSrc to request 
			request.setAttribute("calendarSrc", calendarSrc);
		}	

		// Add events to request
		request.setAttribute("eventsList", events);

		// A Map using Event's as keys and stores List's of User's
		Map<Event, List<User>> organizerUserMap = new HashMap<Event, List<User>>();

		// Loop through events
		for (Event event : events) {

			// Get the User's that are Organizer's for event
			List<User> users = databaseManagerBean.getEventOrganizers(event);

			// Put the List in organizerUserMap
			organizerUserMap.put(event, users);
		}

		// Add organizerUserMap to request
		request.setAttribute("organizerUserMap", organizerUserMap);

		// Increment eventsOverviewCallCounter
		callcounterbean.incrementEventsOverviewCallCounter();

		// Add eventsOverviewCallCounter to request
		request.setAttribute("callCounter",
				callcounterbean.getEventsOverviewCallCounter());

		// Get a RequestDispatcher for EventsOverview
		RequestDispatcher requestdispatcher = request
				.getRequestDispatcher("/EventsOverview.jsp");

		// Forward request
		requestdispatcher.forward(request, response);

	}
}
