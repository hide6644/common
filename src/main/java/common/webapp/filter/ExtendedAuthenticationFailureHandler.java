package common.webapp.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import common.Constants;
import common.service.UserManager;
import lombok.extern.log4j.Log4j2;

/**
 * 認証失敗を処理するクラス.
 */
@Log4j2
public class ExtendedAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {

    /** ユーザ処理クラス */
    @Autowired
    private UserManager userManager;

    /** 例外クラスの完全修飾名をキーとし、対応する失敗 URL を値として持つマップ */
    private final Map<String, String> failureUrlMap = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        var key = exception.getClass().getName();
        var url = this.failureUrlMap.get(key);

        if (url != null) {
            if (key.equals("org.springframework.security.authentication.BadCredentialsException")) {
                loginFailure(request);
            }

            getRedirectStrategy().sendRedirect(request, response, url);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExceptionMappings(Map<?, ?> failureUrlMap) {
        this.failureUrlMap.clear();

        for (var entry : failureUrlMap.entrySet()) {
            var exception = entry.getKey();
            var url = entry.getValue();
            Assert.isInstanceOf(String.class, exception, "Exception key must be a String (the exception classname).");
            Assert.isInstanceOf(String.class, url, "URL must be a String");
            Assert.isTrue(UrlUtils.isValidRedirectUrl((String) url), () -> "Not a valid redirect URL: " + url);
            this.failureUrlMap.put((String) exception, (String) url);
        }
    }

    /**
     * ログイン失敗時の処理を行う.
     *
     * @param request
     *            {@link HttpServletRequest}
     */
    private void loginFailure(HttpServletRequest request) {
        var trimUsername = Optional.ofNullable(request.getParameter("username")).map(username -> username.trim()).orElse("");

        try {
            if (Optional.ofNullable(ExtendedAuthenticationUtil.getBadCredentialsMap(request).get(trimUsername)).orElse(0) < Constants.LOGIN_FAILURE_UPPER_LIMIT) {
                recordLoginAttempts(trimUsername, request);
            } else {
                // ユーザをロックする
                userManager.lockoutUser(trimUsername);
            }
        } catch (UsernameNotFoundException unfe) {
            log.debug("Account not found: username={}", () -> trimUsername);
        }
    }

    /**
     * ログイン失敗回数を記録する.
     *
     * @param username
     *            ユーザ名
     * @param request
     *            {@link HttpServletRequest}
     */
    private void recordLoginAttempts(String username, HttpServletRequest request) {
        var badCredentialsMap = ExtendedAuthenticationUtil.getBadCredentialsMap(request);

        if (badCredentialsMap.containsKey(username)) {
            badCredentialsMap.put(username, badCredentialsMap.get(username) + 1);
        } else {
            badCredentialsMap.put(username, 1);
        }
    }
}
