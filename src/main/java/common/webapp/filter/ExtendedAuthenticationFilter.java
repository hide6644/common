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

        String username = getUsername(request);
        String password = getPassword(request);
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
            loginFailure(username, request);

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
            if (countFailedLoginAttempts(request, username) < Constants.LOGIN_FAILURE_UPPER_LIMIT) {
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
     * リクエストからユーザ名を取得する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return ユーザ名
     */
    private String getUsername(HttpServletRequest request) {
        String username = obtainUsername(request);

        if (username == null) {
            username = "";
        } else {
            username = username.trim();
        }

        return username;
    }


    /**
     * リクエストからパスワードを取得する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return パスワード
     */
    private String getPassword(HttpServletRequest request) {
        String password = obtainPassword(request);

        if (password == null) {
            password = "";
        }

        return password;
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
        Integer count = getBadCredentialsMap(request).get(username);

        if (count == null) {
            return 0;
        } else {
            return count;
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
