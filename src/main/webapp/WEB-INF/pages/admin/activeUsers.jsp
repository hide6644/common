<%@ include file="/includes/taglibs.jsp"%>
<head>
    <title><fmt:message key="activeUsersForm.title"/></title>
    <meta name="menu" content="admin.topForm" />
</head>

<div class="span10">
    <h3><fmt:message key="activeUsersForm.heading" /></h3>

<table class="table table-striped table-condensed">
<thead>
    <tr>
        <th><fmt:message key="user.username" /></th>
        <th><fmt:message key="user.email" /></th>
    </tr>
</thead>
<tbody>
<c:forEach var="user" items="${applicationScope.LOGIN_USERS}" varStatus="status">
    <tr class="row${status.index % 2}">
        <td>${user.username}</td>
        <td>
        <c:if test="${not empty user.email}">
            <a href="mailto:<c:out value="${user.email}" />"> <i class="icon-envelope"></i></a>
        </c:if>
        </td>
    </tr>
</c:forEach>
</tbody>
</table>
<fieldset class="form-actions">
    <button type="button" class="btn" id="button_cancel">
        <i class="icon-remove"></i> <fmt:message key="button.cancel" />
    </button>
</fieldset>
</div>

<c:set var="scripts" scope="request">
<script type="text/javascript">
<!--
$(function() {
    $('#button_cancel').click(function() {
        $(location).attr('href', '<c:url value="/admin/top" />');
    });
});
//-->
</script>
</c:set>
