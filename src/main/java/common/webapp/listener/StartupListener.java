package common.webapp.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import common.Constants;
import common.model.LabelValue;
import common.service.RoleManager;
import common.webapp.converter.CsvFileConverter;
import common.webapp.converter.XlsFileConverter;
import common.webapp.converter.XmlFileConverter;
import net.sf.ehcache.CacheManager;

/**
 * アプリケーション初期化処理を実行するクラス.
 */
@Component
public class StartupListener implements ServletContextListener {

    /** ログ出力クラス */
    private static final Logger log = LogManager.getLogger(StartupListener.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (HashMap<String, Object>) context.getAttribute(Constants.CONFIG);

        if (config == null) {
            config = new HashMap<String, Object>();
        }

        context.setAttribute(Constants.CONFIG, config);

        setupContext(context);

        // バージョン番号を確認する
        String appVersion = null;
        try {
            InputStream is = context.getResourceAsStream("/META-INF/MANIFEST.MF");
            if (is == null) {
                log.warn("META-INF/MANIFEST.MF not found.");
            } else {
                Manifest mf = new Manifest();
                mf.read(is);
                Attributes atts = mf.getMainAttributes();
                appVersion = atts.getValue("Implementation-Version");
            }
        } catch (IOException e) {
            log.error("I/O Exception reading manifest: " + e.getMessage());
        }

        // ブラウザのキャッシュを破棄させるために、バージョン番号をページに埋め込む
        // WARにバージョン番号が指定されていれば、それを使用する
        // そうでなければ、開発バージョンであると仮定し、ランダムな番号を生成する
        if (appVersion == null || appVersion.contains("SNAPSHOT")) {
            appVersion = "" + new Random().nextInt(100000);
        }

        log.info("Application version set to: " + appVersion);
        context.setAttribute(Constants.ASSETS_VERSION, appVersion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        CacheManager.getInstance().shutdown();
    }

    /**
     * アプリケーション変数を初期化する.
     *
     * @param context
     *            {@link ServletContext}
     */
    public static void setupContext(ServletContext context) {
        List<LabelValue> fileTypeList = new ArrayList<LabelValue>();
        fileTypeList.add(new LabelValue(ResourceBundle.getBundle(Constants.BUNDLE_KEY).getString("fileType.xml"), XmlFileConverter.FILE_TYPE));
        fileTypeList.add(new LabelValue(ResourceBundle.getBundle(Constants.BUNDLE_KEY).getString("fileType.xls"), XlsFileConverter.FILE_TYPE));
        fileTypeList.add(new LabelValue(ResourceBundle.getBundle(Constants.BUNDLE_KEY).getString("fileType.csv"), CsvFileConverter.FILE_TYPE));
        context.setAttribute("fileTypeList", fileTypeList);

        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);

        RoleManager mgr = (RoleManager) ctx.getBean("roleManager");
        context.setAttribute(Constants.AVAILABLE_ROLES, mgr.getLabelValues());

        mgr.reindexAll(false);
    }
}
