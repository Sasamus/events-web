package ae.eventswebassignment4.servlets;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ae.eventsbusinessassignment4.databasemanaging.DatabaseManager;

/**
 * Servlet implementation class ControllerServlet
 * 
 * @author Albin Engström
 */
@WebServlet("/ControllerServlet")
public class ControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * A DatabaseManagerBean object
	 */
	@EJB(beanInterface = DatabaseManager.class)//(beanName = "DatabaseManagerBean")
	private static DatabaseManager databaseManager;

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

		// Call it readData()
		//databaseManager.readData();

		// Create page
		//createEventsOverview(request, response, "all");
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

		// Get a RequestDispatcher for EventsOverview
		RequestDispatcher requestdispatcher = request
				.getRequestDispatcher("/EventsOverview.jsp");

		// Forward request
		requestdispatcher.forward(request, response);

	}
}