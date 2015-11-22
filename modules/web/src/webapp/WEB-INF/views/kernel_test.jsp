<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
Kernel Test:
EntityManageer = ${em}
    <c:forEach var="test" items="${tests}">
    ${test.title}<br>
    ${test.description}<br>
    ${test.created_when}<br>
    ${test.generator.impl}<br>

        <c:forEach var="t" items="${test.tasks}">
        ${t.translation.phrase1.value}<br>
        ${t.translation.phrase2.value}<br>
        ${t.source_num}<br>
        ${t.generator.impl}<br>
        </c:forEach>

    </c:forEach>
</body>
</html>
