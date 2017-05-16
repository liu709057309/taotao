<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<table border="2">
		<c:forEach var="a" begin="1" end="3">
		<c:out value="${a}"/>
		<tr>
			<th>姓名</th>
		</tr>
		<tr>
			<td>
			1
			</td>
			<td>
			2
			</td>
		</tr>
		<tr>
			
		</tr>
		</c:forEach>
	</table>
</body>
</html>