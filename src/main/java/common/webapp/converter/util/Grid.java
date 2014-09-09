package common.webapp.converter.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.ConversionService;

/**
 * オブジェクトを配列に変換するクラス.
 */
public class Grid {

    /** Beanの機能を拡張するクラス */
    private BeanWrapperImpl beanWrapper;

    /** 型変換サービス */
    private ConversionService conversionService;

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
        this.beanList = new ArrayList<Object>(beanList);
        this.props = props;
    }

    /**
     * 指定されたプロパティを取得する.
     *
     * @param bean
     *            オブジェクト
     * @param columnName
     *            プロパティ名
     * @return プロパティ
     */
    public Object get(Object bean, String columnName) {
        beanWrapper.setWrappedInstance(bean);
        return beanWrapper.getPropertyValue(columnName);
    }

    /**
     * 指定された位置のプロパティを取得する.
     *
     * @param row
     *            行番号
     * @param column
     *            列番号
     * @return プロパティ
     */
    public Object get(int row, int column) {
        return get(beanList.get(row), props[column]);
    }

    /**
     * 指定された位置のプロパティを指定された型で取得する.
     *
     * @param row
     *            行番号
     * @param column
     *            列番号
     * @param clazz
     *            変換する型
     * @param <T>
     *            変換する型
     * @return プロパティ
     */
    public <T> T get(int row, int column, Class<T> clazz) {
        return conversionService.convert(get(beanList.get(row), props[column]), clazz);
    }

    /**
     * 行数を取得する.
     *
     * @return 行数
     */
    public int rows() {
        return beanList.size();
    }

    /**
     * 列数を取得する.
     *
     * @return 列数
     */
    public int columns() {
        return props.length;
    }

    /**
     * 型変換サービスを設定する.
     *
     * @param conversionService
     *            型変換サービス
     */
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }
}
