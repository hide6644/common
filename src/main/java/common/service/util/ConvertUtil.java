package common.service.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.model.LabelValue;

/**
 * オブジェクト変換のUtilityクラス.
 */
public final class ConvertUtil {

    /** ログ出力クラス */
    private static final Log log = LogFactory.getLog(ConvertUtil.class);

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
        Map<String, String> map = new HashMap<String, String>();

        Enumeration<String> keys = rb.getKeys();
        while (keys.hasMoreElements()) {
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
        Map<String, String> map = new LinkedHashMap<String, String>();

        for (LabelValue option : list) {
            map.put(option.getLabel(), option.getValue());
        }

        return map;
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
            e.printStackTrace();
            log.error("Exception occurred populating object: " + e.getMessage());
        }

        return obj;
    }
}
