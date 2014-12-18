package ae.eventswebassignment4.servlets;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ae.eventsbusinessassignment4.databasemanaging.DatabaseManagerBean;
import ae.eventsbusinessassignment4.entities.User;

/**
 * Servlet implementation class ImageServlet
 * 
 * Adds the picture field of the User with the id sent with the request to
 * response's OutputStream
 * 
 * @author Albin Engström
 */
@Singleton
@WebServlet("/ImageServlet")
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * A DatabaseManagerBean object
	 */
	@EJB(beanName = "DatabaseManagerBean")
	private DatabaseManagerBean databaseManagerBean;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected synchronized void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Gets the User's id
		int id = Integer.parseInt(request.getParameter("userId"));

		// Get User with id
		User user = databaseManagerBean.getUser(id);

		// Get the picture field
		byte[] imageBytes = user.getPicture();

		// Sets ContentType of response
		response.setContentType("image/jpeg");

		// Set ContentLength of response
		response.setContentLength(imageBytes.length);

		// Write imageBytes to response's OutputStream
		response.getOutputStream().write(imageBytes);
	}
}
