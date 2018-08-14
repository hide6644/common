package common.dto;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import common.validator.constraints.BasicLatin;
import common.validator.constraints.CompareStrings;
import common.validator.constraints.ComparisonMode;

/**
 * パスワード情報を保持するクラス.
 */
@CompareStrings.List({
        @CompareStrings(
                propertyNames = { "confirmPassword", "newPassword" },
                comparisonMode = ComparisonMode.EQUAL,
                message = "{common.validator.constraints.confirmPassword.message}"
        )
    })
public class PasswordForm {

    /** ユーザ名 */
    private String username;

    /** トークン */
    private String token;

    /** 現在のパスワード */
    private String currentPassword;

    /** 新しいパスワード */
    private String newPassword;

    /** パスワード(確認用) */
    private String confirmPassword;

    /**
     * ユーザ名を取得する.
     *
     * @return ユーザ名
     */
    @NotEmpty
    @BasicLatin
    @Length(min = 6, max = 16)
    public String getUsername() {
        return username;
    }

    /**
     * ユーザ名を設定する.
     *
     * @param username
     *            ユーザ名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * トークンを取得する.
     *
     * @return トークン
     */
    public String getToken() {
        return token;
    }

    /**
     * トークンを設定する.
     *
     * @param token
     *            トークン
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 現在のパスワードを取得する.
     *
     * @return 現在のパスワード
     */
    @NotEmpty
    @Length(min = 6, max = 80)
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * 現在のパスワードを設定する.
     *
     * @param currentPassword
     *            現在のパスワード
     */
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    /**
     * 新しいパスワードを取得する.
     *
     * @return 新しいパスワード
     */
    @NotEmpty
    @Length(min = 6, max = 80)
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * 新しいパスワードを設定する.
     *
     * @param newPassword
     *            新しいパスワード
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * パスワード(確認用)を取得する.
     *
     * @return パスワード(確認用)
     */
    @Length(max = 80)
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * パスワード(確認用)を設定する.
     *
     * @param confirmPassword
     *            パスワード(確認用)
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
