package common.dto;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import common.validator.constraints.BasicLatin;

/**
 * ユーザ検索条件を保持するクラス.
 */
public class UserSearchCriteria implements Serializable {

    /** ユーザ名のフィールド名 */
    public static final String USERNAME_FIELD = "username";

    /** ｅメールのフィールド名 */
    public static final String EMAIL_FIELD = "email";

    /** ユーザ名 */
    private String username;

    /** ｅメール */
    private String email;

    /**
     * ユーザ名を取得する.
     *
     * @return ユーザ名
     */
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
     * ｅメールを取得する.
     *
     * @return ｅメール
     */
    @BasicLatin
    @Length(max = 64)
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
}
