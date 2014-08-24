package common.webapp.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 *
 * @author hide6644
 */
public class ExtendedAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /** ログ処理クラス */
    private final transient Log log = LogFactory.getLog(getClass());

    /** User系処理クラス */
    @Autowired
    private UserManager userManager;

    /*
     * (非 Javadoc)
     *
     * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
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
            recordLoginAttempts(username, true);

            return newAuth;
        } catch (BadCredentialsException e) {
            // ログイン失敗
            try {
                if (countFailedLoginAttempts(username) < Constants.LOGIN_FAILURE_UPPER_LIMIT) {
                    recordLoginAttempts(username, true);
                } else {
                    lockoutUser(username);
                }
            } catch (UsernameNotFoundException unfe) {
                if (log.isDebugEnabled()) {
                    log.debug("Account not found: username=" + username);
                }
            }

            throw e;
        }
    }

    // 何回連続でログインに失敗したかの情報を返す
    private int countFailedLoginAttempts(String username) throws UsernameNotFoundException {
        return userManager.getUserByUsername(username).getBadCredentialsCount();
    }

    // ログイン失敗の情報を DB に記録...
    private void recordLoginAttempts(String username, boolean result) throws UsernameNotFoundException {
        User user = userManager.getUserByUsername(username);

        if (result) {
            user.setBadCredentialsCount(0);
        } else {
            Integer badCredentialsCount = user.getBadCredentialsCount();

            if (badCredentialsCount == null) {
                badCredentialsCount = 1;
            } else {
                badCredentialsCount++;
            }

            user.setBadCredentialsCount(badCredentialsCount);
        }

        userManager.saveUser(user);
    }

    // アカウントをロックアウトする
    private void lockoutUser(String username) throws UsernameNotFoundException {
        User user = userManager.getUserByUsername(username);
        user.setAccountLocked(true);
        userManager.saveUser(user);
    }
}
