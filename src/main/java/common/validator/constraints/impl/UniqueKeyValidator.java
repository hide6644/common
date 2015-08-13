package common.validator.constraints.impl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;

import javax.annotation.Resource;
import javax.persistence.Id;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

import common.validator.constraints.UniqueKey;

/**
 * ユニークか確認する実装クラス.
 */
@Component
public class UniqueKeyValidator implements ConstraintValidator<UniqueKey, Serializable> {

    /** 確認対象の列名 */
    private String[] columnNames;

    /** DBセッション生成クラス */
    @Resource
    private SessionFactory sessionFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(UniqueKey constraintAnnotation) {
        this.columnNames = constraintAnnotation.columnNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Serializable target, ConstraintValidatorContext context) {
        boolean isValid = isValidCriteria(target);

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).addPropertyNode(columnNames[0]).addConstraintViolation().disableDefaultConstraintViolation();
        }

        return isValid;
    }

    /**
     * 検索クエリを作成する.
     *
     * @param target
     *            検索対象
     * @return true:検索結果無し、false:検索結果有り
     */
    private boolean isValidCriteria(Serializable target) {
        if (sessionFactory == null) {
            return true;
        }

        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(target.getClass());
        boolean nullFlag = true;

        for (int i = 0; i < columnNames.length; i++) {
            Object propertyValue = beanWrapper.getPropertyValue(columnNames[i]);

            if (propertyValue != null) {
                criteria.add(Restrictions.eq(columnNames[i], propertyValue));
                nullFlag = false;
            }
        }
        // 検索対象のカラムが全てnullであった場合
        if (nullFlag) {
            session.close();
            return true;
        }

        for (PropertyDescriptor p : beanWrapper.getPropertyDescriptors()) {
            if (beanWrapper.getPropertyTypeDescriptor(p.getName()).getAnnotation(Id.class) != null) {
                Object propertyValue = beanWrapper.getPropertyValue(p.getName());

                if (propertyValue != null) {
                    criteria.add(Restrictions.ne(p.getName(), propertyValue));
                }
            }
        }

        boolean isValid = criteria.list().size() == 0;

        session.close();
        return isValid;
    }
}
