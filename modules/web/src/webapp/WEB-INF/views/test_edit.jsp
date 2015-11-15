<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<html>
<head>
	<link rel="stylesheet" href="/resources/css/style.css" />
	<link rel="stylesheet" href="/resources/css/test_edit_style.css" />
</head>
<body>
	<div class="header">
        SATIA
    </div>
	<div class="container">

	<form action="<c:url value='/edit/${test.id}' />" method='POST'>
	    <div id="tasks">
	    	<div class="table">

	    	<c:foreach var="task" items="${test.tasks}">

	            <div class="row">
	            	<div class="cell">
	        	        <textarea name="task_{task.id}_phrase_1">${task.translation.phrase1.value}</textarea>
	        	    </div>
	        	    <div class="cell">
	                    <textarea name="task_{task.id}_phrase_2">${task.translation.phrase2.value}</textarea>
	                </div>
	                <div class="cell">
	                    <select name="task_{task.id}_generator">
	                    	<option value="null">Default</option>

	                    	<c:foreach var="g" items="${generators}">

                            <option value="${g.genId}">${g.impl}</option>

	                    	</c:foreach>

	                    </select>
	                </div>
	                <div class="cell">
	                    <div class="remove">&times;</div>
	                </div>
	            </div>

	        </c:foreach>

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