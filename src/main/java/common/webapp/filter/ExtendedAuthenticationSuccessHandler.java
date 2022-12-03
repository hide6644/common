package common.webapp.filter;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * 認証成功を処理するクラス.
 */
public class ExtendedAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * デフォルト・コンストラクタ.
     */
    public ExtendedAuthenticationSuccessHandler() {
    }

    /**
     * コンストラクタ.
     * 
     * @param defaultTargetUrl
     *            認証が成功した場合にユーザーがリダイレクトされる URL
     */
    public ExtendedAuthenticationSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);

        ExtendedAuthenticationUtil.getBadCredentialsMap(request).remove(Optional.ofNullable(authentication.getName()).map(username -> username.trim()).orElse(""));
    }
}
