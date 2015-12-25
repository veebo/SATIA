<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page session="true"%>
<html>
<head>
	<link rel="stylesheet" href="/resources/css/style.css" />
	<link rel="stylesheet" href="/resources/css/test_edit_style.css" />
	<script type="text/javascript" src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
	<script type="text/javascript" src="/resources/js/add_del_task.js"></script>
	<script type="text/javascript" src="/resources/js/add_del_field_value.js"></script>
	<script type="text/javascript" src="/resources/js/unchanged_inputs.js"></script>
	<script type="text/javascript" src="/resources/js/validate_test.js"></script>
</head>
<body>
	<div class="header">
        SATIA
    </div>
	<div class="container">

	<c:if test="${create}">
	<form action="<c:url value='/edit/create?${_csrf.parameterName}=${_csrf.token}' />" method='POST'>
    </c:if>
    <c:if test="${!create}">
    <form id="test_form" action="<c:url value='/edit/${test.testId}?${_csrf.parameterName}=${_csrf.token}' />" method='POST' accept-charset="UTF-8" >
    </c:if>
    	<a href="/">View all tests</a>
    	<br>
    	<div id="error_message">${error_message}</div>
	    <div class="section">
	    	<c:if test="${!create}">
	    		<a href="<c:url value='/start_test/${test.testId}' />" >Pass test</a><br>
	    	</c:if>
	    	<div class="hint">Title:</div> <input class="${ create ? 'ignore_unchanged' : '' }" type="text" name="test_title" value="${test.title}"/><br><br>
	    	<div class="hint">Description:</div> <textarea class="${ create ? 'ignore_unchanged' : '' }" name="test_description">${test.description}</textarea><br><br>
	    	<div class="hint">Generator:</div>
	    	<select name="test_generator" class="${ create ? 'ignore_unchanged' : '' }">
	            <c:forEach var="g" items="${generators}">
	            	<c:choose>
                		<c:when test="${g.genId == test.generator.genId}">
                			<option value="${g.genId}" selected>${g.impl}</option>
                		</c:when>
                		<c:otherwise>
                			<option value="${g.genId}">${g.impl}</option>
                		</c:otherwise>
                	</c:choose>
	            </c:forEach>
	        </select>
	        <br><br>
	        <div class="hint">Source language:</div>
	        <select name="test_sourcelang" class="${ create ? 'ignore_unchanged' : '' }">
	        	<c:forEach var="l" items="${langs}">
	        	  <c:if test="${test.sourceLang.lang == l.lang}" >
	        	    <option value="${l.lang}" selected >${l.lang}</option>
	        	  </c:if>
	        	  <c:if test="${test.sourceLang.lang != l.lang}" >
	        	    <option value="${l.lang}" >${l.lang}</option>
	        	  </c:if>
	            </c:forEach>
	        </select>
	        <br><br>
	        <div class="hint">Target language:</div>
	        <select name="test_targetlang" class="${ create ? 'ignore_unchanged' : '' }">
	        	<c:forEach var="l" items="${langs}">
	        	  <c:if test="${test.targetLang.lang == l.lang}" >
	        	    <option value="${l.lang}" selected >${l.lang}</option>
	        	  </c:if>
	        	  <c:if test="${test.targetLang.lang != l.lang}" >
	        	    <option value="${l.lang}" >${l.lang}</option>
	        	  </c:if>
	            </c:forEach>
	        </select>
	        <br><br>
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

                            <c:choose>
                              <c:when test="${(task.generator != null) && (task.generator.genId == g.genId)}">
                                <option value="${g.genId}" selected >${g.impl}</option>
                         	  </c:when>
                         	  <c:otherwise>
                         	    <option value="${g.genId}">${g.impl}</option>
                         	  </c:otherwise>
                        	</c:choose>

	                    	</c:forEach>

	                    </select>

	                </div>

	                <c:set var="task_gen_id" value="${test.generator.genId}"></c:set>
	                <c:if test="${task.generator != null}">
	                	<c:set var="task_gen_id" value="${task.generator.genId}"></c:set>
	            	</c:if>
	                <div class="cell gen_fields">
	                	<c:forEach var="f" items="${gen_fields[task_gen_id]}">
	                	  <div class="field section">
	                	  	<div class="field_type">${f.type}</div>
	                	  	<div class="field_id">${f.fieldId}</div>
	                	  	<div class="hint">${f.name}:</div>
	                		<c:set var="fvalues_count" value="${0}" ></c:set>
	                		<c:forEach var="fv" items="${tasks_fields_values[task.taskId][f.fieldId]}">
	                			<div class="field_value">
	                				<c:if test="${(f.multiple)}">
	                					<div class="del_field_value">&times;</div>
	                				</c:if>
	                				<c:choose>
	                					<c:when test="${f.type == 0}">
	                						<textarea name="field_value_${fv.fieldValueId}" class="${f.type} field_value_input">${fv.value}</textarea>
	                					</c:when>
	                					<c:when test="${f.type == 1}">
	                						<input type="text" name="field_value_${fv.fieldValueId}" class="${f.type} field_value_input" value="${fv.value}"/>
	                					</c:when>
	                					<c:when test="${f.type == 2}">
	                						<input type="text" name="field_value_${fv.fieldValueId}" class="${f.type} field_value_input" value="${fv.value}"/>
	                					</c:when>
	                				</c:choose>
	                				<c:set var="fvalues_count" value="${fvalues_count + 1}" ></c:set>
	                			</div>
	                		</c:forEach>
	                		<c:if test="${(!f.multiple) && (fvalues_count <= 0)}">
	                			<div class="field_value">
	                				<c:choose>
	                					<c:when test="${f.type == 0}">
	                						<textarea name="task${task.taskId}_field${f.fieldId}" class="${f.type} field_value_input"></textarea>
	                					</c:when>
	                					<c:when test="${f.type == 1}">
	                						<input type="text" name="task${task.taskId}_field${f.fieldId}" class="${f.type} field_value_input"/>
	                					</c:when>
	                					<c:when test="${f.type == 2}">
	                						<input type="text" name="task${task.taskId}_field${f.fieldId}" class="${f.type} field_value_input"/>
	                					</c:when>
	                				</c:choose>
	                			</div>
	                		</c:if>
	                		<c:if test="${(f.multiple)}">
	                			<div class="button add_field_value">+</div>
	                		</c:if>
	                	  </div>
	                	</c:forEach>
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
	            		    <option value="null" selected >Default</option>

	                    	<c:forEach var="g" items="${generators}">

                            <option value="${g.genId}">${g.impl}</option>

	                    	</c:forEach>
	            	    </select>
	                </div>
	                <div class="cell gen_fields"></div>
	                <div class="cell">
	                    <div class="remove">&times;</div>
	                </div>
	            </div>

	            <div class="row" id="gen_fields_to_clone">
	            	<c:forEach var="g" items="${generators}" >
	            		<div class="cell gen_fields ${g.genId}">
	            			<c:forEach var="f" items="${gen_fields[g.genId]}">
	            			  <div class="field">
	            			  	<div class="field_type">${f.type}</div>
	                	  		<div class="field_id">${f.fieldId}</div>
	            				<div class="field_value">
	            				<c:choose>
	                				<c:when test="${f.type == 0}">
	                					<textarea name="${f.fieldId}" class="${f.type} field_value_input"></textarea>
	                				</c:when>
	                				<c:when test="${f.type == 1}">
	                					<input type="text" name="${f.fieldId}" class="${f.type} field_value_input" />
	                				</c:when>
	                				<c:when test="${f.type == 2}">
	                					<input type="text" name="${f.fieldId}" class="${f.type} field_value_input" />
	                				</c:when>
	                			</c:choose>
	                			</div>
	                			<c:if test="${f.multiple}">
	                				<div class="button add_field_value">+</div>
	                			</c:if>
	                		  </div>
	            			</c:forEach>
	            		</div>
	            	</c:forEach>
	            </div>

	            <div class="row" id="field_value_inputs_to_clone">
	            	<div class="field_value">
	            		<div class="del_field_value">&times;</div>
	            		<textarea name="" class="0 field_value_input"></textarea>
	            	</div>
	            	<div class="field_value">
	            		<div class="del_field_value">&times;</div>
	            		<input type="text" name="" class="1 field_value_input" />
	            	</div>
	            	<div class="field_value">
	            		<div class="del_field_value">&times;</div>
	            		<input type="text" name="" class="2 field_value_input" />
	            	</div>
	            </div>

	        </div>
	    </div>
	    <div id="add_task" class="button">+ Add task</div><br>
	    <input id="added_tasks_input" class="ignore_unchanged" type="text" value="0" name="added_tasks_num" />
	    <div class="center">
	    	<input type="button" value="save changes" class="button" id="submit_test_form"></input>
	    </div>
    </form>

    </div>
</body>
</html>
