<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="Style.css">
<title>Add Event</title>
</head>
<body>
	<!-- A form for creation of a new Event -->
	<form action="ControllerServlet" method="post">
		Title:<input type="text" name="title" required>
		<p></p>
		City:<input type="text" name="city" required>
		<p></p>
		Description:
		<textarea name="description" required></textarea>
		<p></p>
		Start Time:<input type="text" placeholder="YYYY-MM-DD hh:mm:ss"
			name="starttime" required>
		<p></p>
		End Time:<input type="text" placeholder="YYYY-MM-DD hh:mm:ss"
			name="endtime" required>
		<p></p>
		<!-- Loop though User's and add an option for each of them in a select menu-->
		<select name="organizerId">
			<c:forEach items="${allUsers}" var="user">
				<option value="${user.getId()}">${user.getFirstName()}
					${user.getLastName()}</option>
			</c:forEach>
		</select>
		<p>&nbsp;</p>
		<input type="hidden" name="GoToUser" value="false"> <input
			type="hidden" name="NewEvent" value="true"> <input
			type="hidden" name="AddEvent" value="false">
		<button type="submit">Submit</button>
	</form>
	<p>&nbsp;</p>
	<p>Page have been visited ${callCounter} times</p>
</body>
</html>