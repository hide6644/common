package common.dao.impl;

import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

/**
 * マッピングから参照できるアナライザー等を定義するクラス.
 */
public class CustomLuceneAnalysisConfigurer implements LuceneAnalysisConfigurer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        context.analyzer("japanese").instance(new JapaneseAnalyzer());
    }
}
