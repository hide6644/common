package common.validator.constraints.impl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;

import javax.annotation.Resource;
import javax.persistence.Id;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import common.validator.constraints.UniqueKey;

/**
 * ユニークかチェックする実装クラス.
 */
public class UniqueKeyValidator implements ConstraintValidator<UniqueKey, Serializable> {

    /** ユニークを確認する列名 */
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
        if (sessionFactory == null) {
            return true;
        }

        Criteria criteria = buildCriteria(target);
        boolean isValid = criteria == null ? true : criteria.list().size() == 0;

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
     * @return 検索クエリ
     */
    private Criteria buildCriteria(Serializable target) {
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
        Criteria criteria = sessionFactory.openSession().createCriteria(target.getClass());
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
            return null;
        }

        for (PropertyDescriptor p : beanWrapper.getPropertyDescriptors()) {
            if (beanWrapper.getPropertyTypeDescriptor(p.getName()).getAnnotation(Id.class) != null) {
                Object propertyValue = beanWrapper.getPropertyValue(p.getName());

                if (propertyValue != null) {
                    criteria.add(Restrictions.ne(p.getName(), propertyValue));
                }
            }
        }

        return criteria;
    }
}
