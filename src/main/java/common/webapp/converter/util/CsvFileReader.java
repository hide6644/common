package common.webapp.converter.util;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.TypeMismatchException;

/**
 * CSVファイルを解析するクラス.
 *
 * @author hide6644
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
                try {
                    beanWrapper.setPropertyValue(props[i], line[i].trim());
                } catch (TypeMismatchException e) {
                    // 変換できない場合、スキップする
                }
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
