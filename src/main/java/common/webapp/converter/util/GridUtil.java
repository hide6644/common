package common.webapp.converter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.InvalidPropertyException;
import org.springframework.format.Formatter;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

/**
 * オブジェクトと配列を相互変換するUtilityクラス.
 *
 * @author hide6644
 */
public class GridUtil {

    /**
     * プライベート・コンストラクタ.<br />
     * Utilityクラスはインスタンス化禁止.
     */
    private GridUtil() {
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
    public static List<String[]> toStringArray(Grid grid, Set<Formatter<?>> formatters) {
        FormattingConversionServiceFactoryBean factoryBean = new FormattingConversionServiceFactoryBean();
        factoryBean.setFormatters(formatters);
        factoryBean.afterPropertiesSet();
        grid.setConversionService(factoryBean.getObject());

        List<String[]> result = new ArrayList<String[]>();

        for (int row = 0; row < grid.rows(); row++) {
            String[] line = new String[grid.columns()];

            for (int col = 0; col < grid.columns(); col++) {
                try {
                    line[col] = grid.get(row, col, String.class);
                } catch (InvalidPropertyException e) {
                    line[col] = "";
                }
            }

            result.add(line);
        }

        return result;
    }
}
