<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>User page</title>
    <link rel="stylesheet" href="/resources/css/style.css" />
    <link rel="stylesheet" href="/resources/css/home_style.css" />
    <script type="text/javascript" src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
    <script type="text/javascript" src="/resources/js/show_hidden.js"></script>
</head>
<body>
    <div class="header">
        SATIA
    </div>

    <div class="container">

    Hello, <strong>${userName}</strong>
    <a href="<c:url value="/logout" />">Logout</a>
    <br>
    <br>

    <div class="section" >
        <div class="title center">Available tests:</div>
        <div class="table">

    	<c:forEach var="t" items="${all_tests}">

    	<div class="row" >
    	    <div class="cell test_col">
                <div class="subtitle">${t.title}</div>
                <div class="hint">${t.description}</div>
                <div><b>author :</b> ${t.user.username}</div>
            </div>
            <div class="cell btn_col center">
                <div class="button">pass now</div>
            </div>
    	</div>

        </c:forEach>

        </div>
    </div>

    <div class="section" >
    	<div class="title center">My tests:</div>

    	<c:forEach var="t" items="${my_tests}">

        <div class="item">
            <div class="subtitle">${t["test"].title}</div>
            <div class="hint">${t["test"].description}</div>

            <a href='/edit/${t["test"].id}'>edit</a><br>
            <div class="show_hidden hint click">show results</div><br>

            <div class="hidden">

    	    <div class="table center">
                <div class="hint row">
                    <div class="cell">user</div>
                    <div class="cell">grade</div>
                    <div class="cell">start time</div>
                </div>

    	        <c:forEach var="r" items='${t["results"]}'>

                <div class="row">
                    <div class="cell">${r.user.username}</div>
                    <div class="cell">${r.value}</div>
                    <div class="cell">${r.startTime}</div>
                </div>

    	        </c:forEach>

    	    </div>

            </div>
        </div>

        </c:forEach>

    </div>

    </div>
</body>
</html>