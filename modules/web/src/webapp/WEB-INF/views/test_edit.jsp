<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html" charset="utf-16" >
	<link rel="stylesheet" href="/resources/css/style.css" />
	<link rel="stylesheet" href="/resources/css/test_edit_style.css" />
	<script type="text/javascript" src="/resources/js/add_del_task.js"></script>
</head>
<body>
	<style>
	#added_tasks_input, #add_task_row_to_clone {
		display : none;
	}
	</style>
	<div class="header">
        SATIA
    </div>
	<div class="container">

	<form action="<c:url value='/post/${test.testId}?${_csrf.parameterName}=${_csrf.token}' />" method='POST'>
	    <div class="section">
	    	Title: <input type="text" name="text_title" value="${test.title}"/><br>
	    	Title: <textarea name="test_description">${test.description}</textarea><br>
	    	Generator:
	    	<select name="test_generator">
	            <option value="null">Default</option>
	            <c:forEach var="g" items="${generators}">
                <option value="${g.genId}">${g.impl}</option>
	            </c:forEach>
	        </select>
	        <br>
	        Source lang:
	        <select name="test_sourcelang">
	        	<c:forEach var="l" items="${langs}">
	        	<option value="${l.lang}">${l.lang}</option>
	            </c:forEach>
	        </select>
	        <br>
	        Target lang:
	        <select name="test_targetlang">
	        	<c:forEach var="l" items="${langs}">
	        	<option value="${l.lang}">${l.lang}</option>
	            </c:forEach>
	        </select>
	        <br>
	    </div>
	    <div id="tasks" class="section">
	    	<div class="table">

	    	<c:forEach var="task" items="${test.tasks}">

	            <div class="row">

	            <c:choose>
	              <c:when test="${test.sourceLang.lang==task.translation.phrase1.lang.lang}">
	            	<div class="cell phr1">
	        	        <textarea name="task${task.taskId}_phrase1">${task.translation.phrase1.value}</textarea>
	        	    </div>
	        	    <div class="cell phr2">
	                    <textarea name="task${task.taskId}_phrase2">${task.translation.phrase2.value}</textarea>
	                </div>
	              </c:when>
	              <c:otherwise>
	                <div class="cell phr2">
	                	<textarea name="task${task.taskId}_phrase2">${task.translation.phrase2.value}</textarea>
	        	    </div>
	        	    <div class="cell phr1">
	                    <textarea name="task${task.taskId}_phrase1">${task.translation.phrase1.value}</textarea>
	                </div>
	              </c:otherwise>
	            </c:choose>

	                <div class="cell gen">
	                    <select name="task${task.taskId}_gen">
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

	            <div class="row added" id="add_task_row_to_clone">
	            	<div class="cell phr1">
	            		<textarea name="add_task-1_phrase1"></textarea>
	            	</div>
	            	<div class="cell phr2">
	            		<textarea name="add_task-1_phrase2"></textarea>
	            	</div>
	                <div class="cell gen">
	            	    <select name="add_task-1_gen">
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

	        </div>
	    </div>
	    <div id="add_task" class="button">+</div><br>
	    <input id="added_tasks_input" type="text" value="0" name="added_tasks_num" />
	    <div class="center">
	    	<input type="submit" value="save changes" class="button"></input>
	    </div>
    </form>

    </div>
</body>
</html>