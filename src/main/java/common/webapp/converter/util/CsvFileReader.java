package common.webapp.converter.util;

import org.springframework.beans.BeanWrapperImpl;

/**
 * CSVファイルを解析するクラス.
 */
public class CsvFileReader {

    /** Beanの機能を拡張するクラス */
    BeanWrapperImpl beanWrapper = new BeanWrapperImpl();

    /** 変換対象のプロパティ名 */
    String[] props;

    /**
     * コンストラクタ.
     *
     * @param props
     *            変換対象のプロパティ名
     */
    public CsvFileReader(String... props) {
        this.props = props;
    }

    /**
     * CSVファイルから値を抽出する.
     *
     * @param bean
     *            オブジェクト
     * @param line
     *            変換対象の行
     * @return オブジェクト
     */
    public Object read(Object bean, String[] line) {
        beanWrapper.setWrappedInstance(bean);

        for (int i = 0; i < props.length; i++) {
            if (line[i] != null && line[i].trim().length() > 0) {
                beanWrapper.setPropertyValue(props[i], line[i].trim());
            }
        }

        return bean;
    }

    /**
     * Beanの機能を拡張するクラスを設定する.
     *
     * @return Beanの機能を拡張するクラス
     */
    public BeanWrapperImpl getBeanWrapper() {
        return beanWrapper;
    }
}
