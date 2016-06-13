package common.webapp.filter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import common.Constants;
import common.model.User;
import common.service.UserManager;

/**
 * 認証を処理するクラス.
 */
public class ExtendedAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /** ログ出力クラス */
    private final transient Logger log = LogManager.getLogger(getClass());

    /** ユーザ処理クラス */
    @Autowired
    private UserManager userManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        // サブクラスに詳細情報を設定する
        setDetails(request, authRequest);

        try {
            Authentication newAuth = getAuthenticationManager().authenticate(authRequest);
            // ログイン成功
            recordLoginAttempts(request, username, true);

            return newAuth;
        } catch (BadCredentialsException e) {
            // ログイン失敗
            try {
                if (countFailedLoginAttempts(request, username) < Constants.LOGIN_FAILURE_UPPER_LIMIT) {
                    recordLoginAttempts(request, username, false);
                } else {
                    // ユーザをロックする
                    User user = userManager.getUserByUsername(username);
                    user.setConfirmPassword(user.getPassword());
                    user.setAccountLocked(true);
                    userManager.saveUser(user);
                }
            } catch (UsernameNotFoundException unfe) {
                if (log.isDebugEnabled()) {
                    log.debug("Account not found: username=" + username);
                }
            }

            throw e;
        }
    }

    /**
     * ログイン失敗回数を取得する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @param username
     *            ユーザ名
     * @return ログイン失敗回数
     */
    private int countFailedLoginAttempts(HttpServletRequest request, String username) {
        @SuppressWarnings("unchecked")
        Map<String, Integer> badCredentialsMap = (Map<String, Integer>) request.getSession().getAttribute("badCredentialsCount");

        if (badCredentialsMap != null) {
            return badCredentialsMap.get(username);
        }

        return 0;
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
        @SuppressWarnings("unchecked")
        Map<String, Integer> badCredentialsMap = (Map<String, Integer>) request.getSession().getAttribute("badCredentialsCount");

        if (badCredentialsMap == null) {
            badCredentialsMap = new HashMap<String, Integer>();
            request.getSession().setAttribute("badCredentialsCount", badCredentialsMap);
        }

        if (result) {
            badCredentialsMap.remove(username);
        } else {
            Integer badCredentialsCount = 1;

            if (badCredentialsMap.get(username) != null) {
                badCredentialsCount = badCredentialsMap.get(username) + 1;
            }

            badCredentialsMap.put(username, badCredentialsCount);
        }
    }
}
