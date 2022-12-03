package common.webapp.util;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * cookie操作するUtilityクラス.
 */
public final class RequestUtil {

    /**
     * プライベート・コンストラクタ.
     * Utilityクラスはインスタンス化禁止.
     */
    private RequestUtil() {
    }

    /**
     * cookieを取得する.
     *
     * @param name
     *            cookie名
     * @param request
     *            {@link HttpServletRequest}
     *
     * @return cookieに保持されていた値
     */
    public static Cookie getCookie(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(name) && !"".equals(cookie.getValue())).findFirst().get();
    }

    /**
     * cookieを設定する.
     *
     * @param name
     *            cookie名
     * @param value
     *            cookieに保持する値
     * @param path
     *            設定するパス
     * @param response
     *            {@link HttpServletResponse}
     */
    public static void setCookie(String name, String value, String path, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(false);
        cookie.setPath(path);
        cookie.setMaxAge(3600 * 24 * 30); // 30日

        response.addCookie(cookie);
    }

    /**
     * cookieを削除する.
     *
     * @param cookie
     *            {@link Cookie}
     * @param path
     *            設定するパス
     * @param response
     *            {@link HttpServletResponse}
     */
    public static void deleteCookie(Cookie cookie, String path, HttpServletResponse response) {
        if (cookie != null) {
            cookie.setMaxAge(0);
            cookie.setPath(path);
            response.addCookie(cookie);
        }
    }

    /**
     * リクエストからアプリケーションのURLを取得する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return アプリケーションのURL
     */
    public static String getAppURL(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        int port = request.getServerPort();

        if (port < 0) {
            port = 80;
        }

        String scheme = request.getScheme();
        StringBuilder url = new StringBuilder();
        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());

        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            url.append(':');
            url.append(port);
        }

        url.append(request.getContextPath());
        return url.toString();
    }
}
