<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
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

		<c:choose>
		    <c:when test="${end}">
		        Thank you for passing the test. Your grade is ${result.value} of 100<br>
		        <br>
		        <sec:authorize access="hasRole('ROLE_USER')">
		        	<a href="<c:url value='/' />">Back to main page</a>
		    	</sec:authorize>
		    </c:when>
		    <c:otherwise>
		        <form action="/task?${_csrf.parameterName}=${_csrf.token}" method="POST">
		        	<div class="hint">Choose the correct translation</div>
		        	<b>${question}</b><br>
		        	<c:forEach var="a" items="${answers}">
		        	    <input type="radio" name="answer" value='${a["id"]}' /> ${a["value"]}<br>
		            </c:forEach>
		            <input type="submit" value="next" class="button" />
		        </form>
		    </c:otherwise>
		</c:choose>

		</div>
	</div>
</body>
</html>