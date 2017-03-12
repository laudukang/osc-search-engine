<%--
  Created by IntelliJ IDEA.
  User: laudukang
  Date: 2016/6/5
  Time: 22:21
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="/search" method="GET">
    <input type="text" name="key" value="${key}"/>
    <input type="submit" value="搜索">
</form>

<%--<c:if test="${total != 0}">--%>
<%--&lt;%&ndash;<fmt:formatNumber var="totalPage" type="number" value="${total/20+0.5}" maxFractionDigits="0"/>&ndash;%&gt;--%>
<%--<p>为你找到${total}条结果,共${totalPage}页</p>--%>
<%--<c:if test="${total != 0}">--%>
<%--<ul>--%>
<%--<c:forEach var="i" begin="${1}" end="${totalPage}">--%>
<%--<li style="display:inline;text-align:center;"><a href="/search?key=${key}&p=${i}">${i}</a></li>--%>
<%--</c:forEach>--%>
<%--</ul>--%>
<%--</c:if>--%>
<%--</c:if>--%>

<c:if test="${not empty total}">
    <ul>
        <p>为你找到${total}条结果,共${totalPage}页${page}</p>
        <c:if test="${totalPage > 0 && page > 1}">
            <li style="display:inline;text-align:center;">
                <a href="/search?key=${key}&pageIndex=${page-1}">上一页</a>
            </li>
        </c:if>

        <c:if test="${totalPage > 0}">
            <c:forEach begin="${page-3 > 0 ? page - 3 : 0}" end="${page}" var="v_page">
                <c:if test="${v_page + 1 > 0}">
                    <li style="display:inline;text-align:center;">
                        <a href="/search?key=${key}&pageIndex=${v_page+1}"
                           <c:if test="${page==v_page+1}">style="color:red;font-weight: bold;"</c:if>
                        >${v_page+1}</a>
                    </li>
                </c:if>
            </c:forEach>
        </c:if>

        <c:if test="${totalPage >= 3}">
            <c:forEach begin="${page+1}" end="${page+3}" var="v_page">
                <c:if test="${v_page < totalPage}">
                    <li style="display:inline;text-align:center;">
                        <a href="/search?key=${key}&pageIndex=${v_page+1}">${v_page+1}</a>
                    </li>
                </c:if>
            </c:forEach>
        </c:if>

        <c:if test="${totalPage > 0 && page != totalPage}">
            <li style="display:inline;text-align:center;">
                <a href="/search?key=${key}&pageIndex=${page+1}">下一页</a>
            </li>
        </c:if>
    </ul>
</c:if>

<c:if test="${not empty data}">
    <table>
        <c:forEach items="${data}" var="blog" varStatus="obj">

            <tr>
                <td><h3><a href="/blog/${blog.id}" target="_blank" data-id="${obj.count}">${blog.title}</a></h3></td>
            </tr>
            <tr>
                <td>
                    <div style="border-style: groove; ">${blog.content}</div>
                </td>
            </tr>

        </c:forEach>
    </table>
</c:if>
</body>
</html>
