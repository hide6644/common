package common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import common.validator.constraints.BasicLatin;
import common.validator.constraints.CompareStrings;
import common.validator.constraints.ComparisonMode;

@Entity
@Table(name = "app_user")
@Indexed
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@CompareStrings.List({ @CompareStrings(propertyNames = { "confirmPassword", "password" }, comparisonMode = ComparisonMode.EQUAL) })
public class User extends BaseObject implements Serializable, UserDetails {

    private String username;

    private String password;

    private String confirmPassword;

    private String firstName;

    private String lastName;

    private String email;

    private Set<Role> roles = new HashSet<Role>();

    private boolean enabled;

    private boolean accountLocked;

    /** 有効期限切れ日時 */
    private Date accountExpiredDate;

    /** 要再認証日時 */
    private Date credentialsExpiredDate;

    /** 認証失敗回数 */
    private Integer badCredentialsCount;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    @NotEmpty
    @BasicLatin
    @Length(min = 6, max = 16)
    @Column(nullable = false, length = 16, unique = true)
    @Field
    public String getUsername() {
        return username;
    }

    @NotEmpty
    @Length(min = 6, max = 80)
    @Column(nullable = false, length = 80)
    @XmlTransient
    public String getPassword() {
        return password;
    }

    @Length(max = 80, message = "{common.validator.constraints.MaxLength.message}")
    @Transient
    @XmlTransient
    public String getConfirmPassword() {
        return confirmPassword;
    }

    @NotEmpty
    @Length(max = 64, message = "{common.validator.constraints.MaxLength.message}")
    @Column(name = "first_name", nullable = false, length = 64)
    @Field
    public String getFirstName() {
        return firstName;
    }

    @Length(max = 64, message = "{common.validator.constraints.MaxLength.message}")
    @Column(name = "last_name", length = 64)
    @Field
    public String getLastName() {
        return lastName;
    }

    @NotEmpty
    @Email
    @Length(max = 64, message = "{common.validator.constraints.MaxLength.message}")
    @Column(nullable = false, length = 64, unique = true)
    @Field
    public String getEmail() {
        return email;
    }

    @NotEmpty
    @Valid
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "user_role",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    public Set<Role> getRoles() {
        return roles;
    }

    @Transient
    public List<LabelValue> getRoleList() {
        List<LabelValue> userRoles = new ArrayList<LabelValue>();

        if (this.roles != null) {
            for (Role role : roles) {
                userRoles.add(new LabelValue(role.getName(), role.getName()));
            }
        }

        return userRoles;
    }

    public void addRole(Role role) {
        getRoles().add(role);
    }

    @Transient
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        authorities.addAll(roles);
        return authorities;
    }

    @Column(name = "account_enabled")
    public boolean isEnabled() {
        return enabled;
    }

    @Column(name = "account_locked", nullable = false)
    public boolean isAccountLocked() {
        return accountLocked;
    }

    @Transient
    public boolean isAccountNonLocked() {
        return !isAccountLocked();
    }

    @Transient
    public boolean isAccountExpired() {
        return accountExpiredDate != null && accountExpiredDate.getTime() < System.currentTimeMillis();
    }

    @Transient
    public boolean isAccountNonExpired() {
        return isAccountExpired();
    }

    @Transient
    public boolean isCredentialsExpired() {
        return credentialsExpiredDate != null && credentialsExpiredDate.getTime() < System.currentTimeMillis();
    }

    @Transient
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired();
    }

    /**
     * 有効期限切れ日時を取得する.
     *
     * @return 有効期限切れ日時
     */
    @Column(name = "account_expired_date", nullable = false)
    public Date getAccountExpiredDate() {
        return accountExpiredDate == null ? null : (Date) accountExpiredDate.clone();
    }

    /**
     * 要再認証日時を取得する.
     *
     * @return 要再認証日時
     */
    @Column(name = "credentials_expired_date", nullable = false)
    public Date getCredentialsExpiredDate() {
        return credentialsExpiredDate == null ? null : (Date) credentialsExpiredDate.clone();
    }

    /**
     * 認証失敗回数を取得する.
     *
     * @return 認証失敗回数
     */
    @Column(name = "bad_credentials_count")
    public Integer getBadCredentialsCount() {
        return badCredentialsCount;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
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
     * 要再認証日時を設定する.
     *
     * @param credentialsExpiredDate
     *            要再認証日時
     */
    public void setCredentialsExpiredDate(Date credentialsExpiredDate) {
        this.credentialsExpiredDate = credentialsExpiredDate == null ? null : new Date(credentialsExpiredDate.getTime());
    }

    /**
     * 認証失敗回数を設定する.
     *
     * @param badCredentialsCount
     *            認証失敗回数
     */
    public void setBadCredentialsCount(Integer badCredentialsCount) {
        this.badCredentialsCount = badCredentialsCount;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        final User user = (User) o;

        return !(username != null ? !username.equals(user.getUsername()) : user.getUsername() != null);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (username != null ? username.hashCode() : 0);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("username", username)
                .append("enabled", enabled)
                .append("accountExpired", isAccountExpired())
                .append("credentialsExpired", isCredentialsExpired())
                .append("accountLocked", accountLocked);

        if (roles != null) {
            sb.append("Granted Authorities: ");
            boolean flag = false;

            for (Role role : roles) {
                if (flag) {
                    sb.append(", ");
                }

                sb.append(role.toString());
                flag = true;
            }
        } else {
            sb.append("No Granted Authorities");
        }

        return sb.toString();
    }
}
