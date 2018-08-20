package common.dto;

import java.io.Serializable;

import common.entity.User;

/**
 * ユーザ検索結果を保持するクラス.
 */
public final class UserSearchResults implements Serializable {

    /** ID */
    private final Long id;

    /** ユーザ名 */
    private final String username;

    /** ｅメール */
    private final String email;

    /** 有効 */
    private final boolean enabled;

    /**
     * コンストラクタ
     *
     * @param id
     *            ID
     * @param username
     *            ユーザ名
     * @param email
     *            ｅメール
     * @param enabled
     *            有効
     */
    private UserSearchResults(Long id, String username, String email, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
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
     * ユーザ名を取得する.
     *
     * @return ユーザ名
     */
    public String getUsername() {
        return username;
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
     * 有効を取得する.
     *
     * @return 有効
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * {@code UserSearchResults}のインスタンスを取得する.
     *
     * @param user
     *            ユーザ
     * @return {@code UserSearchResults}のインスタンス
     */
    public static UserSearchResults of(User user) {
        return new UserSearchResults(user.getId(), user.getUsername(), user.getEmail(), user.isEnabled());
    }
}
