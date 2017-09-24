package common.webapp.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * FlashMap機能の実装クラス.
 */
public final class FlashMap {

    /** FlashMapを設定するキー文字列 */
    static final String FLASH_MAP_ATTRIBUTE = FlashMap.class.getName();

    /**
     * プライベート・コンストラクタ.
     * インスタンス化禁止.
     */
    private FlashMap() {
    }

    /**
     * HttpRequestを取得する.
     *
     * @param requestAttributes
     *            {@link RequestAttributes}
     * @return {@link HttpServletRequest}
     */
    private static HttpServletRequest getRequest(RequestAttributes requestAttributes) {
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * FlashMapを取得する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return FlashMap
     */
    public static Map<String, Object> getCurrent(HttpServletRequest request) {
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        Map<String, Object> flash = (Map<String, Object>) session.getAttribute(FLASH_MAP_ATTRIBUTE);

        if (flash == null) {
            flash = new HashMap<>();
            session.setAttribute(FLASH_MAP_ATTRIBUTE, flash);
        }

        return flash;
    }

    /**
     * FlashMapから指定されたキーの要素を取得する.
     *
     * @param key
     *            キー
     * @return 指定されたキーの要素
     */
    public static Object get(String key) {
        return getCurrent(getRequest(RequestContextHolder.currentRequestAttributes())).get(key);
    }

    /**
     * FlashMapの指定されたキーに要素を設定する.
     *
     * @param key
     *            キー
     * @param value
     *            要素
     */
    public static void put(String key, Object value) {
        getCurrent(getRequest(RequestContextHolder.currentRequestAttributes())).put(key, value);
    }

    /**
     * FlashMapの指定されたキーに要素を追加する.
     *
     * @param key
     *            キー
     * @param value
     *            要素
     */
    public static void add(String key, Object value) {
        @SuppressWarnings("unchecked")
        List<Object> messages = (List<Object>) get(key);

        if (messages == null) {
            messages = new ArrayList<>();
        }

        messages.add(value);
        put(key, messages);
    }
}
