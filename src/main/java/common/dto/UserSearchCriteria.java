package common.dto;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import common.validator.constraints.BasicLatin;
import lombok.Getter;
import lombok.Setter;

/**
 * ユーザ検索条件を保持するクラス.
 */
@Getter
@Setter
public class UserSearchCriteria implements Serializable {

    /** ユーザ名のフィールド名 */
    public static final String USERNAME_FIELD = "username";

    /** ｅメールのフィールド名 */
    public static final String EMAIL_FIELD = "email";

    /** ユーザ名 */
    @BasicLatin
    @Length(min = 6, max = 16)
    private String username;

    /** ｅメール */
    @BasicLatin
    @Length(max = 64)
    private String email;
}
