package common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.groups.Default;

import org.hibernate.validator.constraints.Length;

import common.entity.Role;
import common.entity.User;
import common.validator.constraints.BasicLatin;
import common.validator.constraints.CompareStrings;
import common.validator.constraints.ComparisonMode;
import common.validator.constraints.UniqueKey;
import common.validator.groups.Modify;
import lombok.Getter;
import lombok.Setter;

/**
 * ユーザ情報を保持するクラス.
 */
@Getter
@Setter
@CompareStrings.List({
    @CompareStrings(
            propertyNames = { "confirmPassword", "password" },
            comparisonMode = ComparisonMode.EQUAL,
            message = "{common.validator.constraints.ConfirmPassword.message}"
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
public class UserDetailsForm implements Serializable {

    /** ID */
    private Long id;

    /** ユーザ名 */
    @NotEmpty(groups = { Default.class, Modify.class })
    @BasicLatin(groups = { Default.class, Modify.class })
    @Length(min = 6, max = 16, groups = { Default.class, Modify.class })
    private String username;

    /** パスワード */
    @NotEmpty
    @Length(min = 6, max = 80)
    private String password;

    /** パスワード(確認用) */
    @Length(max = 80)
    private String confirmPassword;

    /** 名前 */
    @NotEmpty(groups = { Default.class, Modify.class })
    @Length(max = 64, groups = { Default.class, Modify.class })
    private String firstName;

    /** 名字 */
    @Length(max = 64, groups = { Default.class, Modify.class })
    private String lastName;

    /** ｅメール */
    @NotEmpty(groups = { Default.class, Modify.class })
    @Email(groups = { Default.class, Modify.class })
    @Length(max = 64, groups = { Default.class, Modify.class })
    private String email;

    /** 有効 */
    private boolean enabled;

    /** ロックアウト */
    private boolean accountLocked;

    /** 有効期限切れ日時 */
    private LocalDateTime accountExpiredDate;

    /** 要再認証日時 */
    private LocalDateTime credentialsExpiredDate;

    /** 権限 */
    @Valid
    private Set<Role> roles = new HashSet<>();

    /** 更新回数 */
    private Long version;

    /**
     * 権限の表示用リストを取得する.
     *
     * @return 権限の表示用リスト
     */
    @Transient
    public List<LabelValue> getRoleList() {
        return Optional.ofNullable(roles).orElseGet(HashSet::new).stream()
                .map(role -> LabelValue.of(role.getDescription(), role.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 権限を追加する.
     *
     * @param role
     *            権限
     */
    public void addRole(Role role) {
        getRoles().add(role);
    }

    /**
     * 権限を削除する.
     *
     * @param role
     *            権限
     */
    public void removeRole(Role role) {
        getRoles().remove(role);
    }
}
