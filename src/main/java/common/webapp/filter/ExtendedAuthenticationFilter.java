package common.webapp.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import common.Constants;
import common.service.UserManager;

/**
 * 認証を処理するクラス.
 */
public class ExtendedAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /** ユーザ処理クラス */
    @Autowired
    private UserManager userManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String trimUsername = Optional.ofNullable(obtainUsername(request)).map(username -> username.trim()).orElse("");
        String password = Optional.ofNullable(obtainPassword(request)).orElse("");
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(trimUsername, password);

        // サブクラスに詳細情報を設定する
        setDetails(request, authRequest);

        try {
            Authentication newAuth = getAuthenticationManager().authenticate(authRequest);
            // ログイン成功
            recordLoginAttempts(request, trimUsername, true);
            return newAuth;
        } catch (BadCredentialsException e) {
            // ログイン失敗
            loginFailure(trimUsername, request);

            throw e;
        }
    }

    /**
     * ログイン失敗時の処理を行う.
     *
     * @param request
     *            ユーザ名
     * @param request
     *            {@link HttpServletRequest}
     */
    private void loginFailure(String username, HttpServletRequest request) {
        try {
            if (Optional.ofNullable(getBadCredentialsMap(request).get(username)).orElse(0) < Constants.LOGIN_FAILURE_UPPER_LIMIT) {
                recordLoginAttempts(request, username, false);
            } else {
                // ユーザをロックする
                userManager.lockoutUser(username);
            }
        } catch (UsernameNotFoundException unfe) {
            Logger log = LogManager.getLogger(ExtendedAuthenticationFilter.class);

            if (log.isDebugEnabled()) {
                log.debug("Account not found: username=" + username);
            }
        }
    }

    /**
     * ログイン失敗回数を記録する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @param username
     *            ユーザ名
     * @param result
     *            ログイン成否
     */
    private void recordLoginAttempts(HttpServletRequest request, String username, boolean result) {
        Map<String, Integer> badCredentialsMap = getBadCredentialsMap(request);

        if (result) {
            // ログイン成功の場合
            badCredentialsMap.remove(username);
        } else {
            if (badCredentialsMap.containsKey(username)) {
                badCredentialsMap.put(username, badCredentialsMap.get(username) + 1);
            } else {
                badCredentialsMap.put(username, 1);
            }
        }
    }

    /**
     * ログイン失敗回数一覧を取得する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return ログイン失敗回数一覧
     */
    @SuppressWarnings("unchecked")
    private Map<String, Integer> getBadCredentialsMap(HttpServletRequest request) {
        HashMap<String, Integer> badCredentialsMap = (HashMap<String, Integer>) request.getSession().getAttribute("badCredentialsCount");

        if (badCredentialsMap == null) {
            badCredentialsMap = new HashMap<>();
            request.getSession().setAttribute("badCredentialsCount", badCredentialsMap);
        }

        return badCredentialsMap;
    }
}
