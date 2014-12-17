<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="Style.css">
<title>Events Overview</title>
</head>
<body>

	<!-- Create a div -->
	<div id="wrap">

		<form action="ControllerServlet" method="post">
			<input type="text" name="filter"> <input type="hidden"
				name="GoToUser" value="false"> <input type="hidden"
				name="NewEvent" value="false"> <input type="hidden"
				name="AddEvent" value="false">
			<button type="submit">Apply Filter</button>
		</form>

		<!-- Create an unordered list -->
		<ul class="menu">

			<!-- Create a variable called i -->
			<c:set var="i" scope="page" value="${0}" />

			<!-- Loop through allEventsList -->
			<c:forEach items="${eventsList}" var="event">

				<!-- Increment i -->
				<c:set var="i" scope="page" value="${i + 1}" />

				<!-- Print the Event title -->
				<li class="title"><a id="${i}" href="#${i}"><span>${event.getEventTitle()}</span></a>

					<!-- Create and unordered list -->
					<ul>

						<!-- Add items to the list -->
						<li><a href="#">${event.getEventDescription()}</a></li>
						<li><a href="#">City: ${event.getEventCity()}</a></li>

						<!-- Loop though the User's Organizing event and print them -->
						<c:forEach items="${organizerUserMap.get(event)}" var="user">
							<li>

								<form id="organizerform" action="ControllerServlet"
									method="post">
									<input type="hidden" name="UserId" value="${user.getId()}">
									<input type="hidden" name="GoToUser" value="true"> <input
										type="hidden" name="NewEvent" value="false"> <input
										type="hidden" name="AddEvent" value="false"> <a
										href="#" onclick="parentNode.submit();"> Organizer:
										${user.getFirstName()} ${user.getLastName()} </a>
								</form>
							</li>
						</c:forEach>

						<li><a href="#">Begins: ${event.getEventStart()}</a></li>
						<li><a href="#">Ends: ${event.getEventEnd()}</a></li>
						<li><a href="#">Last Updated: ${event.getLastUpdate()}</a></li>
					</ul></li>
			</c:forEach>
		</ul>

		<form action="ControllerServlet" method="post">
			<input type="hidden" name="AddEvent" value="true"> <input
				type="hidden" name="GoToUser" value="false"> <input
				type="hidden" name="NewEvent" value="false">
			<button type="submit">Add Event</button>
		</form>
		<p>&nbsp;</p>
		<p>Page have been visited ${eventsOverviewCallCounter} times</p>

	</div>

</body>
</html>