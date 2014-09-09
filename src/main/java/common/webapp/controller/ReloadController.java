package common.webapp.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import common.webapp.listener.StartupListener;

/**
 * 再読込処理クラス.
 */
@Controller
public class ReloadController extends BaseController {

    @Autowired
    private StartupListener startupListener;

    /**
     * マスタを再読み込し、プルダウンリストを生成する.
     * リファー情報を取得し、遷移元の画面へリダイレクトする.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @param response
     *            {@link HttpServletResponse}
     * @throws IOException
     *             {@link IOException}
     */
    @RequestMapping(value = "admin/reload", method = RequestMethod.GET)
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StartupListener.setupContext(request.getSession().getServletContext());

        String referer = request.getHeader("Referer");

        if (referer != null) {
            saveFlashMessage(getText("master.updated"));
            response.sendRedirect(response.encodeRedirectURL(referer));
        }
    }
}
