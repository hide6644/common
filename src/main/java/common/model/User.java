package common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Facet;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import common.validator.constraints.BasicLatin;
import common.validator.constraints.CompareStrings;
import common.validator.constraints.ComparisonMode;
import common.validator.constraints.UniqueKey;

/**
 * ユーザ.
 */
@Entity
@Table(name = "app_user")
@NamedQueries({
    @NamedQuery(
        name = User.GET_ALL,
        query = "from User u order by upper(username)"
    ),
    @NamedQuery(
        name = User.FIND_BY_USERNAME,
        query = "from User u where username = :username"
    )
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Indexed
@Analyzer(impl = JapaneseAnalyzer.class)
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@CompareStrings.List({
    @CompareStrings(
        propertyNames = { "confirmPassword", "password" },
        comparisonMode = ComparisonMode.EQUAL,
        message = "{common.validator.constraints.confirmPassword.message}"
    )
})
@UniqueKey.List({
    @UniqueKey(
        columnNames = { "username" }
    ),
    @UniqueKey(
        columnNames = { "email" }
    )
})
public class User extends BaseObject implements Serializable, UserDetails {

    /** 全件検索するクエリ */
    public static final String GET_ALL = "User.getAll";

    /** ユーザをユーザ名で検索するクエリ */
    public static final String FIND_BY_USERNAME = "User.findByUsername";

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
    private Date accountExpiredDate;

    /** 要再認証日時 */
    private Date credentialsExpiredDate;

    /** 権限 */
    private Set<Role> roles = new HashSet<Role>();

    /**
     * デフォルト・コンストラクタ
     */
    public User() {
    }

    /**
     * コンストラクタ
     *
     * @param username
     *            ユーザ名
     */
    public User(String username) {
        this.username = username;
    }

    /**
     * {@inheritDoc}
     */
    @NotEmpty
    @BasicLatin
    @Length(min = 6, max = 16)
    @Column(nullable = false, length = 16, unique = true)
    @Field
    @Override
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
     * {@inheritDoc}
     */
    @NotEmpty
    @Length(min = 6, max = 80)
    @Column(nullable = false, length = 80)
    @Override
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
    @Transient
    @XmlTransient
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
    @Column(name = "first_name", nullable = false, length = 64)
    @Fields({
            @Field,
            @Field(name = "firstNameFacet", analyze = Analyze.NO)
    })
    @Facet(forField = "firstNameFacet")
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
    @Column(name = "last_name", length = 64)
    @Fields({
            @Field,
            @Field(name = "lastNameFacet", analyze = Analyze.NO)
    })
    @Facet(forField = "lastNameFacet")
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
    @Column(nullable = false, length = 64, unique = true)
    @Field
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
     * {@inheritDoc}
     */
    @Column(name = "account_enabled")
    @Override
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
    @Column(name = "account_locked", nullable = false)
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
     * {@inheritDoc}
     */
    @Transient
    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked();
    }

    /**
     * 有効期限切れ日時を取得する.
     *
     * @return 有効期限切れ日時
     */
    @Column(name = "account_expired_date")
    public Date getAccountExpiredDate() {
        return accountExpiredDate == null ? null : (Date) accountExpiredDate.clone();
    }

    /**
     * 有効期限切れ日時を設定する.
     *
     * @param accountExpiredDate
     *            有効期限切れ日時
     */
    public void setAccountExpiredDate(Date accountExpiredDate) {
        this.accountExpiredDate = accountExpiredDate == null ? null : new Date(accountExpiredDate.getTime());
    }

    /**
     * {@inheritDoc}
     */
   @Transient
   @Override
    public boolean isAccountNonExpired() {
        return !(accountExpiredDate != null && accountExpiredDate.getTime() < System.currentTimeMillis());
    }

    /**
     * 要再認証日時を取得する.
     *
     * @return 要再認証日時
     */
    @Column(name = "credentials_expired_date")
    public Date getCredentialsExpiredDate() {
        return credentialsExpiredDate == null ? null : (Date) credentialsExpiredDate.clone();
    }

    /**
     * 要再認証日時を設定する.
     *
     * @param credentialsExpiredDate
     *            要再認証日時
     */
    public void setCredentialsExpiredDate(Date credentialsExpiredDate) {
        this.credentialsExpiredDate = credentialsExpiredDate == null ? null : new Date(credentialsExpiredDate.getTime());
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    @Override
    public boolean isCredentialsNonExpired() {
        return !(credentialsExpiredDate != null && credentialsExpiredDate.getTime() < System.currentTimeMillis());
    }

    /**
     * 権限を取得する.
     *
     * @return 権限
     */
    @NotEmpty
    @Valid
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = { @JoinColumn(name = "user_id", nullable = false) },
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false)
    )
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

    /**
     * {@inheritDoc}
     */
    @Transient
    @Override
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        authorities.addAll(roles);
        return authorities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        User user = (User) o;

        return username != null ? username.equals(user.getUsername()) : user.getUsername() == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}
