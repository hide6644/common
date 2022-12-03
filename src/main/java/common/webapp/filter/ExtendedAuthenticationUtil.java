package common.webapp.filter;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 認証情報を取得するUtilityクラス.
 */
public class ExtendedAuthenticationUtil {

    /**
     * プライベート・コンストラクタ.
     * Utilityクラスはインスタンス化禁止.
     */
    private ExtendedAuthenticationUtil() {
    }

    /**
     * ログイン失敗回数一覧を取得する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return ログイン失敗回数一覧
     */
    public static Map<String, Integer> getBadCredentialsMap(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        var badCredentialsMap = (HashMap<String, Integer>) request.getSession().getAttribute("badCredentialsCount");

        if (badCredentialsMap == null) {
            badCredentialsMap = new HashMap<>();
            request.getSession().setAttribute("badCredentialsCount", badCredentialsMap);
        }

        return badCredentialsMap;
    }
}
