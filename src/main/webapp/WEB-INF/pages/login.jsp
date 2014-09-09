<%@ include file="/includes/taglibs.jsp"%>
<head>
    <title><fmt:message key="loginForm.title" /></title>
</head>

<body id="login">
<form action="<c:url value='/login' />" method="post" class="form-signin">
    <h2 class="form-signin-heading">
        <fmt:message key="loginForm.heading"/>
    </h2>
<spring:bind path="username">
    <fieldset class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <div class="controls">
            <input type="text" name="username" id="username" class="input-block-level" placeholder="<fmt:message key='loginForm.username' />"  tabindex="1" />
            <form:errors path="username" cssClass="help-inline" />
        </div>
    </fieldset>
</spring:bind>
<spring:bind path="password">
    <fieldset class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <div class="controls">
            <input type="password" name="password" id="password" class="input-block-level" placeholder="<fmt:message key='loginForm.password' />"  tabindex="2" />
            <form:errors path="password" cssClass="help-inline" />
        </div>
    </fieldset>
</spring:bind>
    <label class="checkbox">
        <input type="checkbox" name="_spring_security_remember_me" id="rememberMe" tabindex="3" />
        <fmt:message key="loginForm.rememberMe" />
    </label>
    <p>
        <button type="submit" class="btn btn-large btn-primary" tabindex="4">
            <fmt:message key="button.login" />
        </button>
        <button type="reset" class="btn btn-large" tabindex="5">
            <fmt:message key="button.reset" />
        </button>
    </p>
    <p>
        <fmt:message key="loginForm.password.request" />
        <br />
        <fmt:message key="loginForm.signup">
            <fmt:param><c:url value="/signup" /></fmt:param>
        </fmt:message>
    </p>
</form>
</body>

<c:set var="scripts" scope="request">
<script type="text/javascript">
<!--
$(function() {
    if ($.cookie("login_username") != null) {
        $('#username').val($.cookie('login_username'));
        $('#password').focus();
    } else {
        $('#username').focus();
    }

    $('#login').submit(function () {
        $.cookie('login_username', $('#username').val(), { expires: 30, path: '<c:url value="/" />' });
    });

    $('#send').click(function() {
        if ($('#username').val().length == 0) {
            alert('<fmt:message key="errors.required"><fmt:param><fmt:message key="loginForm.username" /></fmt:param></fmt:message>');
            $('#username').focus();
        } else {
            $(location).attr('href', '<c:url value="/requestRecoveryToken" />?username=' + $('#username').val());
        }
        return false;
    });
});
//-->
</script>
</c:set>
