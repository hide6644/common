package common.dto;

import common.model.User;

/**
 * ユーザ検索結果を保持するクラス.
 */
public class UserSearchResults {

    /** ID */
    private Long id;

    /** ユーザ名 */
    private String username;

    /** ｅメール */
    private String email;

    /** 有効 */
    private boolean enabled;

    /**
     * コンストラクタ
     *
     * @param user
     *            ユーザ
     */
    public UserSearchResults(User user) {
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        enabled = user.isEnabled();
    }

    /**
     * IDを取得する.
     *
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * IDを設定する.
     *
     * @param id
     *            ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * ユーザ名を取得する.
     *
     * @return ユーザ名
     */
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
     * ｅメールを取得する.
     *
     * @return ｅメール
     */
    public String getEmail() {
        return email;
    }

    /**
     * ｅメールを設定する.
     *
     * @param email
     *            ｅメール
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 有効を取得する.
     *
     * @return 有効
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 有効を設定する.
     *
     * @param enabled
     *            有効
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
