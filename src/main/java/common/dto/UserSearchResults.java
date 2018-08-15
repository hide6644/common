package common.dto;

import java.io.Serializable;

import common.entity.User;

/**
 * ユーザ検索結果を保持するクラス.
 */
public final class UserSearchResults implements Serializable {

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
}
