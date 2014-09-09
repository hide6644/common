<%@ include file="/includes/taglibs.jsp"%>
<head>
    <title><fmt:message key="signupForm.title" /></title>
</head>

<div class="span3">
    <h3><fmt:message key="signupForm.heading" /></h3>
    <p><fmt:message key="signupForm.message" /></p>
</div>
<div class="span6">
<fieldset>
    <fieldset id="legend">
        <legend><fmt:message key="button.add" /></legend>
    </fieldset>
<form:form modelAttribute="user" method="post" cssClass="form-horizontal">
    <input type="hidden" name="roles" value="registration" />
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
    <fieldset class="form-actions">
        <button type="submit" class="btn btn-primary">
            <i class="icon-ok icon-white"></i> <fmt:message key="button.add" />
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
        $(location).attr('href', '<c:url value="/top" />');
    });
});
//-->
</script>
</c:set>
