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
import lombok.Getter;
import lombok.Setter;

/**
 * 新規登録ユーザ情報を保持するクラス.
 */
@Getter
@Setter
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
    @NotEmpty
    @BasicLatin
    @Length(min = 6, max = 16)
    private String username;

    /** パスワード */
    @NotEmpty
    @Length(min = 6, max = 80)
    private String password;

    /** パスワード(確認用) */
    @Length(max = 80)
    private String confirmPassword;

    /** 名前 */
    @NotEmpty
    @Length(max = 64)
    private String firstName;

    /** 名字 */
    @Length(max = 64)
    private String lastName;

    /** ｅメール */
    @NotEmpty
    @Email
    @Length(max = 64)
    private String email;
}
