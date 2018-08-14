package common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.groups.Default;

import org.hibernate.validator.constraints.Length;

import common.model.LabelValue;
import common.model.Role;
import common.model.User;
import common.validator.constraints.BasicLatin;
import common.validator.constraints.CompareStrings;
import common.validator.constraints.ComparisonMode;
import common.validator.constraints.UniqueKey;
import common.validator.groups.Modify;

/**
 * ユーザ情報を保持するクラス.
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
public class UserDetailsForm implements Serializable {

    /** ID */
    private Long id;

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

    /** 有効 */
    private boolean enabled;

    /** ロックアウト */
    private boolean accountLocked;

    /** 有効期限切れ日時 */
    private LocalDateTime accountExpiredDate;

    /** 要再認証日時 */
    private LocalDateTime credentialsExpiredDate;

    /** 権限 */
    private Set<Role> roles = new HashSet<>();

    /** 更新回数 */
    private Long version;

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
    @NotEmpty(groups = { Default.class, Modify.class })
    @BasicLatin(groups = { Default.class, Modify.class })
    @Length(min = 6, max = 16, groups = { Default.class, Modify.class })
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
    @NotEmpty(groups = { Default.class, Modify.class })
    @Length(max = 64, groups = { Default.class, Modify.class })
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
    @Length(max = 64, groups = { Default.class, Modify.class })
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
    @NotEmpty(groups = { Default.class, Modify.class })
    @Email(groups = { Default.class, Modify.class })
    @Length(max = 64, groups = { Default.class, Modify.class })
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

    /**
     * ロックアウトを取得する.
     *
     * @return ロックアウト
     */
    public boolean isAccountLocked() {
        return accountLocked;
    }

    /**
     * ロックアウトを設定する.
     *
     * @param accountLocked
     *            ロックアウト
     */
    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    /**
     * 有効期限切れ日時を取得する.
     *
     * @return 有効期限切れ日時
     */
    public LocalDateTime getAccountExpiredDate() {
        return accountExpiredDate;
    }

    /**
     * 有効期限切れ日時を設定する.
     *
     * @param accountExpiredDate
     *            有効期限切れ日時
     */
    public void setAccountExpiredDate(LocalDateTime accountExpiredDate) {
        this.accountExpiredDate = accountExpiredDate;
    }

    /**
     * 要再認証日時を取得する.
     *
     * @return 要再認証日時
     */
    public LocalDateTime getCredentialsExpiredDate() {
        return credentialsExpiredDate;
    }

    /**
     * 要再認証日時を設定する.
     *
     * @param credentialsExpiredDate
     *            要再認証日時
     */
    public void setCredentialsExpiredDate(LocalDateTime credentialsExpiredDate) {
        this.credentialsExpiredDate = credentialsExpiredDate;
    }

    /**
     * 権限を取得する.
     *
     * @return 権限
     */
    @Valid
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * 権限を設定する.
     *
     * @param roles
     *            権限
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * 更新回数を取得する.
     *
     * @return 更新回数
     */
    public Long getVersion() {
        return version;
    }

    /**
     * 更新回数を設定する.
     *
     * @param version
     *            更新回数
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * 権限の表示用リストを取得する.
     *
     * @return 権限の表示用リスト
     */
    @Transient
    public List<LabelValue> getRoleList() {
        return Optional.ofNullable(roles).orElseGet(HashSet::new).stream()
                .map(role -> new LabelValue(role.getDescription(), role.getName()))
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
