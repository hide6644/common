package common.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import common.validator.constraints.BasicLatin;
import common.validator.constraints.CompareStrings;
import common.validator.constraints.ComparisonMode;
import lombok.Getter;
import lombok.Setter;

/**
 * パスワード情報を保持するクラス.
 */
@Getter
@Setter
@CompareStrings.List({
    @CompareStrings(
            propertyNames = { "confirmPassword", "newPassword" },
            comparisonMode = ComparisonMode.EQUAL,
            message = "{common.validator.constraints.confirmPassword.message}"
        )
})
public class PasswordForm implements Serializable {

    /** ユーザ名 */
    @NotEmpty
    @BasicLatin
    @Length(min = 6, max = 16)
    private String username;

    /** トークン */
    private String token;

    /** 現在のパスワード */
    @NotEmpty
    @Length(min = 6, max = 80)
    private String currentPassword;

    /** 新しいパスワード */
    @NotEmpty
    @Length(min = 6, max = 80)
    private String newPassword;

    /** パスワード(確認用) */
    @Length(max = 80)
    private String confirmPassword;
}
