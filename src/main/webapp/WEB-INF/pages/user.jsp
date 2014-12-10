<%@ include file="/includes/taglibs.jsp"%>
<head>
    <title><fmt:message key="userSaveForm.title" /></title>
<c:choose>
<c:when test="${param.from eq 'list'}">
    <meta name="menu" content="admin.topForm" />
</c:when>
<c:otherwise>
    <meta name="menu" content="userSaveForm" />
</c:otherwise>
</c:choose>
</head>
<c:set var="dateTimeFormat" scope="request"><fmt:message key="date.time.format" /></c:set>

<c:choose>
<c:when test="${param.method eq 'Add'}">
    <c:set var="method" value="post" />
</c:when>
<c:otherwise>
    <c:set var="method" value="put" />
</c:otherwise>
</c:choose>
<c:choose>
<c:when test="${param.from eq 'list'}">
<div class="col-sm-2"></div>
</c:when>
<c:otherwise>
<div class="col-sm-2">
    <h3><fmt:message key="userSaveForm.heading" /></h3>
    <p><fmt:message key="userSaveForm.message" /></p>
</div>
</c:otherwise>
</c:choose>
<div class="col-sm-7">
    <fieldset id="legend">
<c:choose>
<c:when test="${method eq 'post'}">
        <legend><fmt:message key="button.add" /></legend>
        <c:set var="method" value="post" />
</c:when>
<c:otherwise>
        <legend><fmt:message key="button.edit" /></legend>
        <c:set var="method" value="put" />
</c:otherwise>
</c:choose>
    </fieldset>
<form:form modelAttribute="user" method="${method}" autocomplete="off" cssClass="well">
    <form:hidden path="id" />
    <form:hidden path="version" />
<c:choose>
<c:when test="${method eq 'post'}">
<spring:bind path="user.username">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
        <label for="username" class="control-label">
            <fmt:message key="user.username" /> <span class="required">*</span>
        </label>
        <form:input path="username" cssClass="form-control" />
        <form:errors path="username" cssClass="help-block" />
    </div>
</spring:bind>
</c:when>
<c:otherwise>
    <div class="control-group">
        <label for="username" class="control-label">
            <fmt:message key="user.username" />
        </label>
        <form:input path="username" readonly="true" cssClass="form-control" />
    </div>
</c:otherwise>
</c:choose>
<c:choose>
<c:when test="${method eq 'post'}">
    <div class="row">
<spring:bind path="user.password">
        <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
            <label for="password" class="control-label">
                <fmt:message key="user.password" /> <span class="required">*</span>
            </label>
            <form:password path="password" showPassword="true" cssClass="form-control" />
            <form:errors path="password" cssClass="help-block" />
        </div>
</spring:bind>
<spring:bind path="user.confirmPassword">
        <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
            <label for="confirmPassword" class="control-label">
                <fmt:message key="user.confirmPassword" />
            </label>
            <form:password path="confirmPassword" showPassword="true" cssClass="form-control" />
            <form:errors path="confirmPassword" cssClass="help-block" />
        </div>
</spring:bind>
    </div>
</c:when>
<c:otherwise>
<c:if test="${pageContext.request.remoteUser eq user.username}">
    <span class="help-block">
<c:choose>
<c:when test="${param.from eq 'list'}">
        <a href="<c:url value="/updatePassword" />?from=list"><fmt:message key="userSaveForm.password.request" /></a>
</c:when>
<c:otherwise>
        <a href="<c:url value="/updatePassword" />"><fmt:message key="userSaveForm.password.request" /></a>
</c:otherwise>
</c:choose>
    </span>
</c:if>
</c:otherwise>
</c:choose>
    <div class="row">
<spring:bind path="user.firstName">
        <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
            <label for="firstName" class="control-label">
                <fmt:message key="user.firstName" /> <span class="required">*</span>
            </label>
            <form:input path="firstName" cssClass="form-control" />
            <form:errors path="firstName" cssClass="help-block" />
        </div>
</spring:bind>
<spring:bind path="user.lastName">
        <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
            <label for="lastName" class="control-label">
                <fmt:message key="user.lastName" />
            </label>
            <form:input path="lastName" cssClass="form-control" />
            <form:errors path="lastName" cssClass="help-block" />
        </div>
</spring:bind>
    </div>
<spring:bind path="user.email">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
        <label for="email" class="control-label">
            <fmt:message key="user.email" /> <span class="required">*</span>
        </label>
        <form:input path="email" cssClass="form-control" />
        <form:errors path="email" cssClass="help-block" />
    </div>
</spring:bind>
<c:choose>
<c:when test="${param.from eq 'list' or method eq 'post'}">
    <div class="form-group">
        <label class="control-label"><fmt:message key="userForm.accountSettings"/></label>
        <label class="checkbox-inline">
            <form:checkbox path="enabled" />
            <fmt:message key="user.enabled" />
        </label>
        <label class="checkbox-inline">
            <form:checkbox path="accountLocked" />
            <fmt:message key="user.accountLocked" />
        </label>
    </div>
    <div class="row">
<spring:bind path="user.accountExpiredDate">
        <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
            <label for="accountExpiredDate" class="control-label">
                <fmt:message key="user.accountExpiredDate" />
            </label>
            <form:input path="accountExpiredDate" cssClass="form-control" placeholder="${dateTimeFormat}" />
            <form:errors path="accountExpiredDate" cssClass="help-block" />
        </div>
</spring:bind>
<spring:bind path="user.credentialsExpiredDate">
        <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
            <label for="credentialsExpiredDate" class="control-label">
                <fmt:message key="user.credentialsExpiredDate" />
            </label>
            <form:input path="credentialsExpiredDate" cssClass="form-control" placeholder="${dateTimeFormat}" />
            <form:errors path="credentialsExpiredDate" cssClass="help-block" />
        </div>
</spring:bind>
    </div>
<spring:bind path="user.roles">
    <div class="form-group">
        <label for="roles" class="control-label">
            <fmt:message key="userForm.assignRoles" />
        </label>
        <select name="roles" multiple="multiple" class="form-control">
<c:forEach items="${availableRoles}" var="role">
            <option value="${role.value}" ${fn:contains(user.roles, role.value) ? 'selected' : ''}>${role.label}</option>
</c:forEach>
        </select>
        <form:errors path="roles" cssClass="help-block" />
    </div>
</spring:bind>
</c:when>
<c:otherwise>
<c:forEach var="role" items="${user.roles}" varStatus="status">
    <input type="hidden" name="roles" value="<c:out value="${role.name}" />" />
</c:forEach>
    <form:hidden path="enabled" />
    <form:hidden path="accountLocked" />
    <form:hidden path="accountExpiredDate" />
    <form:hidden path="credentialsExpiredDate" />
</c:otherwise>
</c:choose>
    <fieldset class="form-actions">
        <button type="submit" class="btn btn-primary">
            <i class="icon-ok icon-white"></i>
<c:choose>
<c:when test="${method eq 'post'}">
            <fmt:message key="button.add" />
</c:when>
<c:otherwise>
            <fmt:message key="button.edit" />
</c:otherwise>
</c:choose>
        </button>
        <button type="reset" class="btn btn-default">
            <i class="icon-refresh"></i> <fmt:message key="button.reset" />
        </button>
        <button type="button" class="btn btn-default" id="button_cancel">
            <i class="icon-remove"></i> <fmt:message key="button.cancel" />
        </button>
    </fieldset>
</form:form>
</div>

<c:set var="scripts" scope="request">
<script type="text/javascript">
<!--
$(function() {
    $('#button_cancel').click(function() {
        $(location).attr('href', '<c:choose><c:when test="${param.from eq 'list'}"><c:url value="/admin/master/users" /></c:when><c:otherwise><c:url value="/top" /></c:otherwise></c:choose>');
    });
});
//-->
</script>
</c:set>
