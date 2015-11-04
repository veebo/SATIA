<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<html>
<head>
	<link rel="stylesheet" href="resources/style.css" />
</head>
<body>
	<h1>Title : ${title}</h1>
	<h1>Message : ${message}</h1>

	<a href="<c:url value="/home" />">home</a>
</body>
</html>