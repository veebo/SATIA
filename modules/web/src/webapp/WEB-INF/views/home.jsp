<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>User page</title>
    <link rel="stylesheet" href='<c:url value="/resources/css/style.css" />' />
    <link rel="stylesheet" href='<c:url value="/resources/css/home_style.css" />' />
    <script type="text/javascript" src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
    <script type="text/javascript" src='<c:url value="/resources/js/show_hidden.js" />' ></script>
</head>
<body>
    <div class="header">
        SATIA
    </div>

    <div class="container">

    Hello, <strong>${user_name}</strong>
    <a href="<c:url value="/logout" />">Logout</a>
    <br>
    <br>

    <div class="section" >
    	<div class="title center">My tests:</div>

    	<c:forEach var="t" items="${tests_results}">

        <div class="item">
            <div class="remove_test" onclick="window.location.href = '<c:url value="/remove/${t["test"].testId}" />' ">&times;</div>

            <div class="subtitle">${t["test"].title}</div>
            <div class="hint">${t["test"].description}</div>

            <a href='/edit/${t["test"].testId}'>edit</a><br>
            <div class="show_hidden hint click">show results</div><br>

            <div class="hidden">

    	    <div class="table center">
                <div class="hint row">
                    <div class="cell">user</div>
                    <div class="cell">full name</div>
                    <div class="cell">grade</div>
                    <div class="cell">start time</div>
                </div>

    	        <c:forEach var="r" items='${t["results"]}'>

                <div class="row">
                    <div class="cell">${r.user.username}</div>
                    <div class="cell">${r.fullname}</div>
                    <div class="cell">${r.value}</div>
                    <div class="cell">${r.startTime}</div>
                </div>

    	        </c:forEach>

    	    </div>

            </div>
        </div>

        </c:forEach>
        <div class="center">
            <a href="<c:url value='/edit/create' />"><div class="button">create new test</div></a>
        </div>

    </div>

    </div>
</body>
</html>