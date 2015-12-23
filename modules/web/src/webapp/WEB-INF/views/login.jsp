<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="true"%>
<html>

<head>
<title>Login Page</title>
<link rel="stylesheet" href="/resources/css/style.css" />
<style>
#loginForm {
	display: ${display_login_form};
}
#logoutLink {
	display: ${display_logout_form};
}
</style>
</head>

<body onload='document.loginForm.username.focus();'>

	<div class="header">
		SATIA
	</div>

	<h1 align="center">Welcome to SATIA!</h1>

	<div id="login-box">

		<h2 align="center">Login Form</h2>

		<c:if test="${not empty error}">
			<div class="error">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="msg">${msg}</div>
		</c:if>

		<form name='loginForm' id="loginForm" action="<c:url value='/login' />" method='POST'>

		<table>
			<tr>
				<td>User:</td>
				<td><input type='text' name='username'></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type='password' name='password' /></td>
			</tr>
			<tr>
				<td colspan='2'><input name="submit" type="submit" value="Log in" /></td>
				<td colspan='2'><button type="submit" formaction="/reg">Register</button></td>
			</tr>
		  </table>

		  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

		</form>

		<a id='logoutLink' href="<c:url value="/logout" />">Logout</a>
	</div>

</body>
</html>