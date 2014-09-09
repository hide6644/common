package common.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * エンティティの基底クラス.
 */
@MappedSuperclass
public abstract class BaseObject {

    /** 更新回数 */
    private Integer version;

    /** 登録ユーザ */
    private String createUser;

    /** 登録日時 */
    private Date createDate;

    /** 更新ユーザ */
    private String updateUser;

    /** 更新日時 */
    private Date updateDate;

    /**
     * 更新回数を取得する.
     *
     * @return 更新回数
     */
    @Version
    @XmlTransient
    public Integer getVersion() {
        return version;
    }

    /**
     * 更新回数を設定する.
     *
     * @param version
     *            更新回数
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * 登録ユーザを取得する.
     *
     * @return 登録ユーザ
     */
    @Column(name = "create_user")
    @XmlTransient
    public String getCreateUser() {
        return createUser;
    }

    /**
     * 登録ユーザを設定する.
     *
     * @param createUser
     *            登録ユーザ
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * 登録日時を取得する.
     *
     * @return 更新日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date", updatable = false)
    @XmlTransient
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 登録日時を設定する.
     *
     * @param createDate
     *            登録日時
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 更新ユーザを取得する.
     *
     * @return 更新ユーザ
     */
    @Column(name = "update_user")
    @XmlTransient
    public String getUpdateUser() {
        return updateUser;
    }

    /**
     * 更新ユーザを設定する.
     *
     * @param updateUser
     *            更新ユーザ
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    /**
     * 更新日時を取得する.
     *
     * @return 更新日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date")
    @XmlTransient
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * 更新日時を設定する.
     *
     * @param updateDate
     *            更新日時
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 指定されたオブジェクトのHash Codeを生成する.
     *
     * @param obj
     *            Hash Codeを生成するオブジェクト
     * @return Hash Code
     */
    protected int hashCode(Object obj) {
        return obj == null ? 0 : obj.hashCode();
    }

    /**
     * 指定されたオブジェクトを比較する.
     *
     * @param lhs
     *            比較するオブジェクト
     * @param rhs
     *            比較するオブジェクト
     * @return true 一致
     */
    protected boolean equals(Object lhs, Object rhs) {
        if (lhs == rhs) {
            return true;
        } else if (lhs != null) {
            return lhs.equals(rhs);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int hashCode();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
