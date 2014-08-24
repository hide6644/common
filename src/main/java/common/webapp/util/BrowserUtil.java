package common.webapp.util;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * ブラウザの判定を行うUtilityクラス.
 *
 * @author hide6644
 */
public class BrowserUtil {

    /**
     * プライベート・コンストラクタ.<br />
     * Utilityクラスはインスタンス化禁止.
     */
    private BrowserUtil() {
    }

    /** ブラウザ不明 */
    public static final int BROWSER_UNKNOWN = 0;

    /** ブラウザIE */
    public static final int BROWSER_IE = 1;

    /** ブラウザFirefox */
    public static final int BROWSER_FIREFOX = 2;

    /** ブラウザChrome */
    public static final int BROWSER_CHROME = 3;

    /** ブラウザOpera */
    public static final int BROWSER_OPERA = 4;

    /** ブラウザSafari */
    public static final int BROWSER_SAFARI = 5;

    /** ブラウザNetscape */
    public static final int BROWSER_NETSCAPE = 6;

    /** ブラウザIE判別正規表現パターン */
    public static final Pattern BROWSER_IE_PATTERN = Pattern.compile(".*((MSIE)+ [0-9]\\.[0-9]).*");

    /** ブラウザFirefox判別正規表現パターン */
    public static final Pattern BROWSER_FIREFOX_PATTERN = Pattern.compile(".*((Firefox/)+[0-9]+\\.[0-9]\\.?[0-9]?).*");

    /** ブラウザChrome判別正規表現パターン */
    public static final Pattern BROWSER_CHROME_PATTERN = Pattern.compile(".*((Chrome)+/?[0-9]\\.?[0-9]?).*");

    /** ブラウザOpera判別正規表現パターン */
    public static final Pattern BROWSER_OPERA_PATTERN = Pattern.compile(".*((Opera)+/? ?[0-9]\\.[0-9][0-9]?).*");

    /** ブラウザSafari判別正規表現パターン */
    public static final Pattern BROWSER_SAFARI_PATTERN = Pattern.compile(".*((Version/)+[0-9]\\.?[0-9]?\\.?[0-9]? Safari).*");

    /** ブラウザNetscape判別正規表現パターン */
    public static final Pattern BROWSER_NETSCAPE_PATTERN = Pattern.compile(".*((Netscape/)+[0-9]\\.[0-9][0-9]?).*");

    /**
     * ブラウザの種類を取得する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return ブラウザを表す文字列
     */
    public static int getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null) {
            return BROWSER_UNKNOWN;
        }
        if (BROWSER_IE_PATTERN.matcher(userAgent).matches()) {
            return BROWSER_IE;
        }
        if (BROWSER_FIREFOX_PATTERN.matcher(userAgent).matches()) {
            return BROWSER_FIREFOX;
        }
        if (BROWSER_CHROME_PATTERN.matcher(userAgent).matches()) {
            return BROWSER_CHROME;
        }
        if (BROWSER_OPERA_PATTERN.matcher(userAgent).matches()) {
            return BROWSER_OPERA;
        }
        if (BROWSER_SAFARI_PATTERN.matcher(userAgent).matches()) {
            return BROWSER_SAFARI;
        }
        if (BROWSER_NETSCAPE_PATTERN.matcher(userAgent).matches()) {
            return BROWSER_NETSCAPE;
        }

        return BROWSER_UNKNOWN;
    }
}
