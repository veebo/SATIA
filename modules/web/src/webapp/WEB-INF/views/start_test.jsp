<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="true"%>
<html>
<head>
	<link rel="stylesheet" href="/resources/css/style.css" />
</head>
<body>
	<div class="header">
		SATIA
	</div>
	<div class="container">
		<div class="center">
			<div class="title">${test.title}</div>
			<div>${test.description}</div>
			<br>
			<div class = "hint">Enter your name and start passing the test:</div>
			<form action="/task?${_csrf.parameterName}=${_csrf.token}" method="POST">
				<input type="text" name="name" />
				<input type="submit" value="start" />
			</form>
		</div>
	</div>
</body>
</html>