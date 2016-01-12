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
		<div class="title center">Registration</div>
		<form action="/reg?${_csrf.parameterName}=${_csrf.token}" method="POST">
			<div id="error_message" class="center">${error_message}</div>
			<div class="table center section">
				<div class="row"><br></div>
				<div class="row">
					<div class="cell">
						<div class="hint">Login: </div>
					</div>
					<div class="cell">
						<input type="text" name="username" />
					</div>
				</div>
				<div class="row"><br></div>
				<div class="row">
					<div class="cell">
						<div class="hint">First name: </div>
					</div>
					<div class="cell">
						<input type="text" name="first_name" />
					</div>
				</div>
				<div class="row"><br></div>
				<div class="row">
					<div class="cell">
						<div class="hint">Last name: </div>
					</div>
					<div class="cell">
						<input type="text" name="last_name" />
					</div>
				</div>
				<div class="row"><br></div>
				<div class="row">
					<div class="cell">
						<div class="hint">Email: </div>
					</div>
					<div class="cell">
						<input type="text" name="email" />
					</div>
				</div>
				<div class="row"><br></div>
				<div class="row">
					<div class="cell">
						<div class="hint">Password: </div>
					</div>
					<div class="cell">
						<input type="password" name="password" />
					</div>
				</div>
				<div class="row"><br></div>
				<div class="row">
					<div class="cell">
						<div class="hint">Confirm password: </div>
					</div>
					<div class="cell">
						<input type="password" name="confirm_password" />
					</div>
				</div>
				<div class="row"><br></div>
			</div>
			<div class="center">
				<input type="submit" value="sign up" class="button" />
			</div>
		</form>
	</div>
</body>
</html>