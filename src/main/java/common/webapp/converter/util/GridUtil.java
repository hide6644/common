package common.webapp.converter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.format.Formatter;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

/**
 * オブジェクトと配列を相互変換するUtilityクラス.
 */
public class GridUtil {

    /**
     * プライベート・コンストラクタ.
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

        List<String[]> result = new ArrayList<>();

        for (int row = 0; row < grid.rows(); row++) {
            String[] line = new String[grid.columns()];

            for (int col = 0; col < grid.columns(); col++) {
                line[col] = grid.get(row, col, String.class);
            }

            result.add(line);
        }

        return result;
    }
}
