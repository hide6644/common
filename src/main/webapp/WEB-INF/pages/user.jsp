<%@ include file="/includes/taglibs.jsp"%>
<custom:constants var="DATE_TIME_FORMAT" />
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
<div class="span3"></div>
</c:when>
<c:otherwise>
<div class="span3">
    <h3><fmt:message key="userSaveForm.heading" /></h3>
    <p><fmt:message key="userSaveForm.message" /></p>
</div>
</c:otherwise>
</c:choose>
<div class="span6">
<fieldset>
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
<form:form modelAttribute="user" method="${method}" cssClass="form-horizontal">
    <form:hidden path="id" />
    <form:hidden path="version" />
<c:choose>
<c:when test="${method eq 'post'}">
<spring:bind path="user.username">
    <fieldset class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <label for="username" class="control-label">
            <fmt:message key="user.username" /> <span class="required">*</span>
        </label>
        <div class="controls">
            <form:input path="username" cssClass="input-medium" />
            <form:errors path="username" cssClass="help-inline" />
        </div>
    </fieldset>
</spring:bind>
</c:when>
<c:otherwise>
    <fieldset class="control-group">
        <label for="username" class="control-label">
            <fmt:message key="user.username" />
        </label>
        <div class="controls">
            <form:input path="username" readonly="true" cssClass="input-medium" />
        </div>
    </fieldset>
</c:otherwise>
</c:choose>
<c:choose>
<c:when test="${method eq 'post'}">
<spring:bind path="user.password">
    <fieldset class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <label for="password" class="control-label">
            <fmt:message key="user.password" /> <span class="required">*</span>
        </label>
        <div class="controls">
            <form:password path="password" showPassword="true" cssClass="input-medium" />
            <form:errors path="password" cssClass="help-inline" />
        </div>
    </fieldset>
</spring:bind>
<spring:bind path="user.confirmPassword">
    <fieldset class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <label for="confirmPassword" class="control-label">
            <fmt:message key="user.confirmPassword" />
        </label>
        <div class="controls">
            <form:password path="confirmPassword" showPassword="true" cssClass="input-medium" />
            <form:errors path="confirmPassword" cssClass="help-inline" />
        </div>
    </fieldset>
</spring:bind>
</c:when>
<c:otherwise>
<c:if test="${pageContext.request.remoteUser eq user.username}">
    <fieldset class="control-group">
        <div class="controls">
<c:choose>
<c:when test="${param.from eq 'list'}">
            <a href="<c:url value="/updatePassword" />?from=list"><fmt:message key="userSaveForm.password.request" /></a>
</c:when>
<c:otherwise>
            <a href="<c:url value="/updatePassword" />"><fmt:message key="userSaveForm.password.request" /></a>
</c:otherwise>
</c:choose>
        </div>
    </fieldset>
</c:if>
</c:otherwise>
</c:choose>
<spring:bind path="user.firstName">
    <fieldset class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <label for="firstName" class="control-label">
            <fmt:message key="user.firstName" /> <span class="required">*</span>
        </label>
        <div class="controls">
            <form:input path="firstName" cssClass="input-large" />
            <form:errors path="firstName" cssClass="help-inline" />
        </div>
    </fieldset>
</spring:bind>
<spring:bind path="user.lastName">
    <fieldset class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <label for="lastName" class="control-label">
            <fmt:message key="user.lastName" />
        </label>
        <div class="controls">
            <form:input path="lastName" cssClass="input-large" />
            <form:errors path="lastName" cssClass="help-inline" />
        </div>
    </fieldset>
</spring:bind>
<spring:bind path="user.email">
    <fieldset class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <label for="email" class="control-label">
            <fmt:message key="user.email" /> <span class="required">*</span>
        </label>
        <div class="controls">
            <form:input path="email" cssClass="input-xlarge" />
            <form:errors path="email" cssClass="help-inline" />
        </div>
    </fieldset>
</spring:bind>
<c:choose>
<c:when test="${param.from eq 'list' or method eq 'post'}">
    <fieldset class="control-group">
        <label class="control-label"><fmt:message key="userForm.accountSettings"/></label>
        <div class="controls">
            <label class="checkbox inline">
                <form:checkbox path="enabled" />
                <fmt:message key="user.enabled" />
            </label>
            <label class="checkbox inline">
                <form:checkbox path="accountLocked" />
                <fmt:message key="user.accountLocked" />
            </label>
        </div>
    </fieldset>
<spring:bind path="user.accountExpiredDate">
    <fieldset class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <label for="accountExpiredDate" class="control-label">
            <fmt:message key="user.accountExpiredDate" />
        </label>
        <div class="controls">
            <form:input path="accountExpiredDate" cssClass="text" cssErrorClass="text error" placeholder="${DATE_TIME_FORMAT}" />
            <form:errors path="accountExpiredDate" cssClass="help-inline" />
        </div>
    </fieldset>
</spring:bind>
<spring:bind path="user.credentialsExpiredDate">
    <fieldset class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <label for="credentialsExpiredDate" class="control-label">
            <fmt:message key="user.credentialsExpiredDate" />
        </label>
        <div class="controls">
            <form:input path="credentialsExpiredDate" cssClass="text" cssErrorClass="text error" placeholder="${DATE_TIME_FORMAT}" />
            <form:errors path="credentialsExpiredDate" cssClass="help-inline" />
        </div>
    </fieldset>
</spring:bind>
<spring:bind path="user.roles">
    <fieldset class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <label for="roles" class="control-label">
            <fmt:message key="userForm.assignRoles" />
        </label>
        <div class="controls">
            <select name="roles" multiple="multiple" class="input-xlarge">
<c:forEach items="${availableRoles}" var="role">
                <option value="${role.value}" ${fn:contains(user.roles, role.value) ? 'selected' : ''}>${role.label}</option>
</c:forEach>
            </select>
            <form:errors path="roles" cssClass="help-inline" />
        </div>
    </fieldset>
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
        <button type="reset" class="btn">
            <i class="icon-refresh"></i> <fmt:message key="button.reset" />
        </button>
        <button type="button" class="btn" id="button_cancel">
            <i class="icon-remove"></i> <fmt:message key="button.cancel" />
        </button>
    </fieldset>
</form:form>
</fieldset>
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
