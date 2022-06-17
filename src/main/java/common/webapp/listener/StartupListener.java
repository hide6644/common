package common.webapp.listener;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import common.Constants;
import common.dto.LabelValue;
import common.service.RoleManager;
import common.service.UserManager;
import common.webapp.converter.FileType;

/**
 * アプリケーション初期化処理を実行するクラス.
 */
public class StartupListener implements ServletContextListener {

    /** ログ出力クラス */
    private final Logger log = LogManager.getLogger(this);

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        var context = event.getServletContext();

        @SuppressWarnings("unchecked")
        Map<String, Object> config = Optional.ofNullable((Map<String, Object>) context.getAttribute(Constants.CONFIG)).orElseGet(HashMap::new);
        context.setAttribute(Constants.CONFIG, config);
        setAppContext(context);
        setAppVersion(context);
    }

    /**
     * アプリケーション変数を初期化する.
     *
     * @param context
     *            {@link ServletContext}
     */
    public void setAppContext(ServletContext context) {
        List<LabelValue> fileTypeList = new ArrayList<>();
        fileTypeList.add(LabelValue.of(ResourceBundle.getBundle(Constants.BUNDLE_KEY).getString("fileType.xml"), String.valueOf(FileType.XML.getValue())));
        fileTypeList.add(LabelValue.of(ResourceBundle.getBundle(Constants.BUNDLE_KEY).getString("fileType.xls"), String.valueOf(FileType.EXCEL.getValue())));
        fileTypeList.add(LabelValue.of(ResourceBundle.getBundle(Constants.BUNDLE_KEY).getString("fileType.csv"), String.valueOf(FileType.CSV.getValue())));
        context.setAttribute("fileTypeList", fileTypeList);

        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);

        context.setAttribute(Constants.AVAILABLE_ROLES, ((RoleManager) ctx.getBean("roleManager")).getLabelValues());

        ((UserManager) ctx.getBean("userManager")).reindex();
    }

    /**
     * アプリケーションバージョンを設定する.
     *
     * @param context
     *            {@link ServletContext}
     */
    private void setAppVersion(ServletContext context) {
        String appVersion = null;

        // バージョン番号を確認する
        try (InputStream is = context.getResourceAsStream("/META-INF/MANIFEST.MF")) {
            if (is == null) {
                log.warn("META-INF/MANIFEST.MF not found.");
            } else {
                var mf = new Manifest();
                mf.read(is);
                var atts = mf.getMainAttributes();
                appVersion = atts.getValue("Implementation-Version");
            }
        } catch (IOException e) {
            log.error("I/O Exception reading manifest:", e);
        }

        // ブラウザのキャッシュを破棄させるために、バージョン番号をページに埋め込む
        // WARにバージョン番号が指定されていれば、それを使用する
        // そうでなければ、開発バージョンであると仮定し、ランダムな番号を生成する
        if (appVersion == null || appVersion.contains("SNAPSHOT")) {
            var sr = new SecureRandom();
            appVersion = String.valueOf(sr.nextInt(100000));
        }

        log.info("Application version set to:{}", appVersion);
        context.setAttribute(Constants.ASSETS_VERSION, appVersion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // 何もしない
    }
}
