package common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * システム全体の定数クラス.
 */
public class Constants {

    /**
     * プライベート・コンストラクタ.
     * このクラスはインスタンス化禁止.
     */
    private Constants() {
    }

    public static final String ASSETS_VERSION = "assetsVersion";

    public static final String CONFIG = "appConfig";

    public static final String PREFERRED_LOCALE_KEY = "common.LOCALE";

    /** ResourceBundleの名前 */
    public static final String BUNDLE_KEY = "ApplicationResources";

    /** アプリケーション名 */
    public static final String APP_BASE = ResourceBundle.getBundle(BUNDLE_KEY).getString("app.base");

    /** ファイル区切り文字 */
    public static final String FILE_SEP = System.getProperty("file.separator");

    /** デフォルト一時フォルダのパス */
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir") + FILE_SEP;

    /** デフォルトログフォルダのパス */
    public static final String LOG_DIR = System.getProperty("catalina.base") + FILE_SEP + "logs" + FILE_SEP;

    /** デフォルトアップロードフォルダのパス */
    public static final String UL_DIR = LOG_DIR + APP_BASE + FILE_SEP;

    /** デフォルトエンコード */
    public static final Charset ENCODING = StandardCharsets.UTF_8;

    /** Entity Managerクラス名 */
    public static final String PERSISTENCE_UNIT_NAME = "ApplicationEntityManager";

    /** 管理者権限を表す文字列 */
    public static final String ADMIN_ROLE = "ROLE_ADMIN";

    /** 一般利用者権限を表す文字列 */
    public static final String USER_ROLE = "ROLE_USER";

    /** ログインユーザの保持する権限を設定するキー文字列 */
    public static final String USER_ROLES = "userRoles";

    /** ログインユーザの使用可能な権限を設定するキー文字列 */
    public static final String AVAILABLE_ROLES = "availableRoles";

    /** 認証失敗制限数 */
    public static final int LOGIN_FAILURE_UPPER_LIMIT = Integer.parseInt(ResourceBundle.getBundle(BUNDLE_KEY).getString("login.failure.upper.limit"));

    /** 有効期限切れ期間 */
    public static final int ACCOUNT_EXPIRED_TERM = Integer.parseInt(ResourceBundle.getBundle(BUNDLE_KEY).getString("account.expired.term"));

    /** 要再認証期間 */
    public static final int CREDENTIALS_EXPIRED_TERM = Integer.parseInt(ResourceBundle.getBundle(BUNDLE_KEY).getString("credentials.expired.term"));
}
