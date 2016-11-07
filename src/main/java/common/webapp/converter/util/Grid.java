package common.webapp.converter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.format.Formatter;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

/**
 * オブジェクトを配列に変換するクラス.
 */
public class Grid {

    /** Beanの機能を拡張するクラス */
    private BeanWrapperImpl beanWrapper;

    /** オブジェクトリスト */
    private List<?> beanList;

    /** 変換対象のプロパティ名 */
    private String[] props;

    /**
     * コンストラクタ.
     *
     * @param beanList
     *            オブジェクトリスト
     * @param props
     *            変換対象のプロパティ名
     */
    public Grid(List<?> beanList, String... props) {
        beanWrapper = new BeanWrapperImpl();
        this.beanList = new ArrayList<>(beanList);
        this.props = props;
    }

    /**
     * 指定された名称のプロパティを取得する.
     *
     * @param bean
     *            オブジェクト
     * @param columnName
     *            プロパティ名
     * @return プロパティ
     */
    public Object getPropertyValue(Object bean, String columnName) {
        beanWrapper.setWrappedInstance(bean);
        return beanWrapper.getPropertyValue(columnName);
    }

    /**
     * オブジェクトをString配列に変換する.
     *
     * @param grid
     *            オブジェクトと配列を相互変換するクラス
     * @param formatters
     *            変換ルール
     * @return 配列に変換されたオブジェクト
     */
    public List<String[]> toStringArray(Set<Formatter<?>> formatters) {
        FormattingConversionServiceFactoryBean factoryBean = new FormattingConversionServiceFactoryBean();
        factoryBean.setFormatters(formatters);
        factoryBean.afterPropertiesSet();

        List<String[]> result = new ArrayList<>();

        for (int row = 0; row < beanList.size(); row++) {
            String[] line = new String[props.length];

            for (int col = 0; col < props.length; col++) {
                line[col] = factoryBean.getObject().convert(getPropertyValue(beanList.get(row), props[col]), String.class);
            }

            result.add(line);
        }

        return result;
    }
}
