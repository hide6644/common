<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty error_messages}">
<c:forEach var="error" items="${error_messages}">
        <div class="alert alert-danger alert-dismissable">
            <a href="#" data-dismiss="alert" class="close">&times;</a>
            <c:out value="${error}" />
        </div>
</c:forEach>
</c:if>
<c:if test="${not empty flash_error_messages}">
<c:forEach var="error" items="${flash_error_messages}">
        <div class="alert alert-danger alert-dismissable">
            <a href="#" data-dismiss="alert" class="close">&times;</a>
            <c:out value="${error}" /><br />
        </div>
</c:forEach>
</c:if>
<c:if test="${not empty info_messages}">
<c:forEach var="info" items="${info_messages}">
        <div class="alert alert-success alert-dismissable">
            <a href="#" data-dismiss="alert" class="close">&times;</a>
            <c:out value="${info}" /><br />
        </div>
</c:forEach>
</c:if>
<c:if test="${not empty flash_info_messages}">
<c:forEach var="info" items="${flash_info_messages}">
        <div class="alert alert-success alert-dismissable">
            <a href="#" data-dismiss="alert" class="close">&times;</a>
            <c:out value="${info}" /><br />
        </div>
</c:forEach>
</c:if>
