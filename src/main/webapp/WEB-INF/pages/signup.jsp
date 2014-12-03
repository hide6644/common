<%@ include file="/includes/taglibs.jsp"%>
<head>
    <title><fmt:message key="signupForm.title" /></title>
</head>

<div class="col-sm-2">
    <h3><fmt:message key="signupForm.heading" /></h3>
    <p><fmt:message key="signupForm.message" /></p>
</div>
<div class="col-sm-7">
    <fieldset id="legend">
        <legend><fmt:message key="button.add" /></legend>
    </fieldset>
<form:form modelAttribute="user" method="post" cssClass="well">
    <input type="hidden" name="roles" value="registration" />
<spring:bind path="user.username">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
        <label for="username" class="control-label">
            <fmt:message key="user.username" /> <span class="required">*</span>
        </label>
        <form:input path="username" cssClass="form-control" />
        <form:errors path="username" cssClass="help-block" />
    </div>
</spring:bind>
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
    <fieldset class="form-actions">
        <button type="submit" class="btn btn-primary">
            <i class="icon-ok icon-white"></i> <fmt:message key="button.add" />
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
        $(location).attr('href', '<c:url value="/top" />');
    });
});
//-->
</script>
</c:set>
