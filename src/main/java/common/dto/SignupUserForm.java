package common.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import common.entity.User;
import common.validator.constraints.BasicLatin;
import common.validator.constraints.CompareStrings;
import common.validator.constraints.ComparisonMode;
import common.validator.constraints.UniqueKey;

/**
 * 新規登録ユーザ情報を保持するクラス.
 */
@CompareStrings.List({
        @CompareStrings(
                propertyNames = { "confirmPassword", "password" },
                comparisonMode = ComparisonMode.EQUAL,
                message = "{common.validator.constraints.confirmPassword.message}"
        )
    })
@UniqueKey.List({
        @UniqueKey(
                columnNames = { "username" },
                model = User.class
        ),
        @UniqueKey(
                columnNames = { "email" },
                model = User.class
        )
    })
public class SignupUserForm implements Serializable {

    /** ユーザ名 */
    private String username;

    /** パスワード */
    private String password;

    /** パスワード(確認用) */
    private String confirmPassword;

    /** 名前 */
    private String firstName;

    /** 名字 */
    private String lastName;

    /** ｅメール */
    private String email;

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
     * パスワードを取得する.
     *
     * @return パスワード
     */
    @NotEmpty
    @Length(min = 6, max = 80)
    public String getPassword() {
        return password;
    }

    /**
     * パスワードを設定する.
     *
     * @param password
     *            パスワード
     */
    public void setPassword(String password) {
        this.password = password;
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

    /**
     * 名前を取得する.
     *
     * @return 名前
     */
    @NotEmpty
    @Length(max = 64)
    public String getFirstName() {
        return firstName;
    }

    /**
     * 名前を設定する.
     *
     * @param firstName
     *            名前
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * 名字を取得する.
     *
     * @return 名字
     */
    @Length(max = 64)
    public String getLastName() {
        return lastName;
    }

    /**
     * 名字を設定する.
     *
     * @param lastName
     *            名字
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * ｅメールを取得する.
     *
     * @return ｅメール
     */
    @NotEmpty
    @Email
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
