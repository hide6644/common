package common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Facet;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Normalizer;
import org.hibernate.search.annotations.NormalizerDef;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import common.validator.constraints.BasicLatin;
import common.validator.constraints.CompareStrings;
import common.validator.constraints.ComparisonMode;
import common.validator.constraints.UniqueKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * ユーザ.
 */
@RequiredArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "app_user")
@Cache(region = "userCache", usage = CacheConcurrencyStrategy.READ_WRITE)
@Indexed
@Analyzer(impl = JapaneseAnalyzer.class)
@NormalizerDef(name = "userSort", filters = @TokenFilterDef(factory = LowerCaseFilterFactory.class))
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
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
public final class User extends BaseObject implements Serializable, UserDetails {

    /** ユーザ名 */
    @NonNull
    @NotEmpty
    @BasicLatin
    @Length(min = 6, max = 16)
    @Column(nullable = false, length = 16, unique = true)
    @Field
    @Field(name = "usernameSort", normalizer = @Normalizer(definition = "userSort"))
    @SortableField(forField = "usernameSort")
    private String username;

    /** パスワード */
    @NotEmpty
    @Length(min = 6, max = 80)
    @Column(nullable = false, length = 80)
    private String password;

    /** パスワード(確認用) */
    @Length(max = 80)
    @Transient
    @XmlTransient
    private String confirmPassword;

    /** 名前 */
    @NotEmpty
    @Length(max = 64)
    @Column(name = "first_name", nullable = false, length = 64)
    @Field
    @Field(name = "firstNameFacet", analyze = Analyze.NO)
    @Field(name = "firstNameSort", normalizer = @Normalizer(definition = "userSort"))
    @Facet(forField = "firstNameFacet")
    @SortableField(forField = "firstNameSort")
    private String firstName;

    /** 名字 */
    @Length(max = 64)
    @Column(name = "last_name", length = 64)
    @Field
    @Field(name = "lastNameFacet", analyze = Analyze.NO)
    @Field(name = "lastNameSort", normalizer = @Normalizer(definition = "userSort"))
    @Facet(forField = "lastNameFacet")
    @SortableField(forField = "lastNameSort")
    private String lastName;

    /** ｅメール */
    @NotEmpty
    @Email
    @Length(max = 64)
    @Column(nullable = false, length = 64, unique = true)
    @Field
    @Field(name = "emailSort", normalizer = @Normalizer(definition = "userSort"))
    @SortableField(forField = "emailSort")
    private String email;

    /** 有効 */
    @Column(name = "account_enabled")
    private boolean enabled;

    /** ロックアウト */
    @Column(name = "account_locked")
    private boolean accountLocked;

    /** 有効期限切れ日時 */
    @Column(name = "account_expired_date")
    private LocalDateTime accountExpiredDate;

    /** 要再認証日時 */
    @Column(name = "credentials_expired_date")
    private LocalDateTime credentialsExpiredDate;

    /** 権限 */
    @NotEmpty
    @Valid
    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "user_role",
            joinColumns = { @JoinColumn(name = "user_id", nullable = false) },
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false)
        )
    private Set<Role> roles = new HashSet<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return !isAccountLocked();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return !(accountExpiredDate != null && accountExpiredDate.isBefore(LocalDateTime.now()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return !(credentialsExpiredDate != null && credentialsExpiredDate.isBefore(LocalDateTime.now()));
    }

    /**
     * 権限を追加する.
     *
     * @param role
     *            権限
     */
    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    /**
     * 権限を削除する.
     *
     * @param role
     *            権限
     */
    public void removeRole(Role role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Set<GrantedAuthority> getAuthorities() {
        return new LinkedHashSet<>(roles);
    }
}
