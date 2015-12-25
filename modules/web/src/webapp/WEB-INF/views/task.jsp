<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="true"%>
<html>
<head>
	<link rel="stylesheet" href="/resources/css/style.css" />
	<script type="text/javascript" src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$('form').submit(function (e) {
				if ($('input[type="radio"]:checked').length <= 0) {
					e.preventDefault();
					alert("Please, choose an answer");
				}
			});
		});
	</script>
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
		        <c:if test="${auth_user != null}">
		        	<a href="<c:url value='/' />">back to main page</a>
		    	</c:if>
		    </c:when>
		    <c:otherwise>
		        <form action="/task?${_csrf.parameterName}=${_csrf.token}" method="POST" class="large">
		        	<div class="hint">Choose the correct translation</div>
		        	<b>${question}</b><br>
		        	<br>
		        	<div id="answers" class="left">
		        		<c:forEach var="a" items="${answers}">
		        	    	<input type="radio" name="answer" value='${a["id"]}' /> ${a["value"]}<br>
		            	</c:forEach>
		        	</div>
		        	<br>
		            <input type="submit" value="next" class="button" />
		        </form>
		    </c:otherwise>
		</c:choose>

		</div>
	</div>
</body>
</html>