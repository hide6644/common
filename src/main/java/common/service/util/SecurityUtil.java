package common.service.util;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import common.Constants;
import common.model.User;

/**
 * SecurityAdviceのUtilityクラス.
 *
 * @author hide6644
 */
public final class SecurityUtil {

    /** "Access Denied" エラーメッセージ (not i18n-ized). */
    public static final String ACCESS_DENIED = "Access Denied: Only administrators are allowed to modify other accounts.";

    /**
     * プライベート・コンストラクタ.<br />
     * Utilityクラスはインスタンス化禁止.
     */
    private SecurityUtil() {
    }

    /**
     * ログインユーザ情報を取得する.
     *
     * @param auth
     *            認証情報
     * @return 値保持モデル
     */
    public static User getCurrentAccount(Authentication auth) {
        if (auth.getPrincipal() instanceof UserDetails) {
            return (User) auth.getPrincipal();
        } else if (auth.getDetails() instanceof UserDetails) {
            return (User) auth.getDetails();
        } else {
            return null;
        }
    }

    /**
     * ログインユーザは管理者か.
     *
     * @param roles
     *            ログインユーザの権限一覧
     * @return true 管理者, false 一般ユーザ
     */
    public static boolean isAdministrator(Collection<? extends GrantedAuthority> roles) {
        for (GrantedAuthority role : roles) {
            if (role.getAuthority().equals(Constants.ADMIN_ROLE)) {
                return true;
            }
        }

        return false;
    }
}
