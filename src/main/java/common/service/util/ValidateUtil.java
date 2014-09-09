package common.service.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * エラーメッセージのUtilityクラス.
 */
public class ValidateUtil {

    /** エラーを表示するフィールド名を設定するキー文字列 */
    public static final String FILED_NAME = "filedName";

    /** エラーメッセージを設定するキー文字列 */
    public static final String MESSAGE = "message";

    /** メッセージに埋め込む文字配列を設定するキー文字列 */
    public static final String ARGS = "args";

    /**
     * プライベート・コンストラクタ.
     * Utilityクラスはインスタンス化禁止.
     */
    private ValidateUtil() {
    }

    /**
     * エラーメッセージを設定する.
     *
     * @param errors
     *            エラー一覧
     * @param filedName
     *            エラーを表示するフィールド名
     * @param message
     *            エラーメッセージ
     * @param args
     *            メッセージに埋め込む文字配列
     * @return エラー一覧
     */
    public static List<Map<String, Serializable>> validateMassage(List<Map<String, Serializable>> errors, String filedName, String message, Serializable... args) {
        if (errors == null) {
            errors = new ArrayList<Map<String, Serializable>>();
        }

        Map<String, Serializable> value = new HashMap<String, Serializable>();
        value.put(FILED_NAME, filedName);
        value.put(MESSAGE, message);
        value.put(ARGS, args);
        errors.add(value);

        return errors;
    }

    /**
     * エラーメッセージを設定する.
     *
     * @param filedName
     *            エラーを表示するフィールド名
     * @param message
     *            エラーメッセージ
     * @param args
     *            メッセージに埋め込む文字配列
     * @return エラー一覧
     */
    public static List<Map<String, Serializable>> validateMassage(String filedName, String message, Serializable... args) {
        return validateMassage(null, filedName, message, args);
    }
}
