package common.webapp.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;

import common.dto.LabelValue;
import lombok.extern.log4j.Log4j2;

/**
 * オブジェクト変換のUtilityクラス.
 */
@Log4j2
public final class ConvertUtil {

    /**
     * プライベート・コンストラクタ.
     * Utilityクラスはインスタンス化禁止.
     */
    private ConvertUtil() {
    }

    /**
     * 指定の引数を変換する.
     *
     * @param rb
     *            リソースバンドル
     * @return マップオブジェクト
     */
    public static Map<String, String> convertBundleToMap(ResourceBundle rb) {
        Map<String, String> map = new HashMap<>();

        for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            map.put(key, rb.getString(key));
        }

        return map;
    }

    /**
     * 指定の引数を変換する.
     *
     * @param list
     *            リストオブジェクト
     * @return マップオブジェクト
     */
    public static Map<String, String> convertListToMap(List<LabelValue> list) {
        return list.stream().collect(Collectors.toMap(LabelValue::getLabel, LabelValue::getValue));
    }

    /**
     * 指定の引数を変換する.
     *
     * @param rb
     *            リソースバンドル
     * @return プロパティー
     */
    public static Properties convertBundleToProperties(ResourceBundle rb) {
        Properties props = new Properties();

        for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            props.put(key, rb.getString(key));
        }

        return props;
    }

    /**
     * 指定の引数を変換する.
     *
     * @param obj
     *            オブジェクト
     * @param rb
     *            リソースバンドル
     * @return 引数に指定されたオブジェクト
     */
    public static Object populateObject(Object obj, ResourceBundle rb) {
        try {
            BeanUtils.copyProperties(obj, convertBundleToMap(rb));
        } catch (Exception e) {
            log.error("Exception occurred populating object:", e);
        }

        return obj;
    }
}
