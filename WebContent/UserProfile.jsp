<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="Style.css">
<title>User Profile</title>
</head>
<body>
	<img src="ImageServlet?userId=${user.getId()}">
	<p>Name: ${user.getFirstName()} ${user.getLastName()}</p>
	<p>Organizing future events: ${nrFutureEvents}</p>
	<p>Organized past events: ${nrPastEvents}</p>
	<p>Comments: ${nrComments}</p>
	<p>&nbsp;</p>
	<p>Page have been visited ${userProfileCallCounter} times</p>
</body>
</html>