<%--
  Created by IntelliJ IDEA.
  User: laudukang
  Date: 2016/6/5
  Time: 23:40
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<c:if test="${not empty data}">
    <h3>${data.title}</h3>
    <p>${data.htmlContent}</p>
</c:if>
<c:if test="${empty data}">
    该用户很懒，404都没写
</c:if>
</body>
</html>
