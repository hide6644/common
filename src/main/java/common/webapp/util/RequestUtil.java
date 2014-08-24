package common.webapp.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * cookie操作を行うUtilityクラス.
 *
 * @author hide6644
 */
public final class RequestUtil {

    /**
     * プライベート・コンストラクタ.<br />
     * Utilityクラスはインスタンス化禁止.
     */
    private RequestUtil() {
    }

    /**
     * cookieを取得する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @param name
     *            cookie名
     *
     * @return cookieに保持されていた値
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        Cookie returnCookie = null;

        if (cookies == null) {
            return returnCookie;
        }

        for (final Cookie thisCookie : cookies) {
            if (thisCookie.getName().equals(name) && !"".equals(thisCookie.getValue())) {
                returnCookie = thisCookie;
                break;
            }
        }

        return returnCookie;
    }

    /**
     * cookieを設定する.
     *
     * @param response
     *            {@link HttpServletResponse}
     * @param name
     *            cookie名
     * @param value
     *            cookieに保持する値
     * @param path
     *            設定するパス
     */
    public static void setCookie(HttpServletResponse response, String name, String value, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(false);
        cookie.setPath(path);
        cookie.setMaxAge(3600 * 24 * 30); // 30日

        response.addCookie(cookie);
    }

    /**
     * cookieを削除する.
     *
     * @param response
     *            {@link HttpServletResponse}
     * @param cookie
     *            cookie名
     * @param path
     *            設定するパス
     */
    public static void deleteCookie(HttpServletResponse response, Cookie cookie, String path) {
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
