<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html" charset="utf-16" >
	<link rel="stylesheet" href="/resources/css/style.css" />
	<link rel="stylesheet" href="/resources/css/test_edit_style.css" />
</head>
<body>
	<div class="header">
        SATIA
    </div>
	<div class="container">

	<form action="<c:url value='/edit/${test.testId}' />" method='POST'>
	    <div id="tasks">
	    	<div class="table">

	    	<c:forEach var="task" items="${test.tasks}">

	            <div class="row">

	            <c:choose>
	              <c:when test="${test.sourceLang.lang==task.translation.phrase1.lang.lang}">
	            	<div class="cell">
	        	        <textarea name="task{task.taskId}_phrase1">${task.translation.phrase1.value}</textarea>
	        	    </div>
	        	    <div class="cell">
	                    <textarea name="task{task.taskId}_phrase2">${task.translation.phrase2.value}</textarea>
	                </div>
	              </c:when>
	              <c:otherwise>
	                <div class="cell">
	                	<textarea name="task{task.taskId}_phrase2">${task.translation.phrase2.value}</textarea>
	        	    </div>
	        	    <div class="cell">
	                    <textarea name="task{task.taskId}_phrase1">${task.translation.phrase1.value}</textarea>
	                </div>
	              </c:otherwise>
	            </c:choose>

	                <div class="cell">
	                    <select name="task{task.taskId}_gen">
	                    	<option value="null">Default</option>

	                    	<c:forEach var="g" items="${generators}">

                            <option value="${g.genId}">${g.impl}</option>

	                    	</c:forEach>

	                    </select>
	                </div>
	                <div class="cell">
	                    <div class="remove">&times;</div>
	                </div>
	            </div>

	        </c:forEach>

	        </div>
	    </div>
	    <div id="add_task" class="button">+</div><br>
	    <div class="center">
	    	<input type="submit" value="save changes" class="button"></input>
	    </div>
    </form>

    </div>
</body>
</html>