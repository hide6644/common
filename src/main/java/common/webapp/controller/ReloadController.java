package common.webapp.controller;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import common.webapp.listener.StartupListener;

/**
 * 再読込処理クラス.
 */
@Controller
public class ReloadController extends BaseController {

    /**
     * マスタを再読み込し、プルダウンリストを生成する.
     * リファー情報を取得し、遷移元の画面へリダイレクトする.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @throws IOException
     *             {@link IOException}
     * @return 遷移先
     */
    @RequestMapping(value = "admin/reload", method = RequestMethod.GET)
    public String handleRequest(HttpServletRequest request) throws IOException {
        StartupListener.setupContext(request.getSession().getServletContext());

        String referer = request.getHeader("Referer");

        if (referer != null) {
            saveFlashMessage(getText("master.updated"));
            return "redirect:" + new URL(referer).getFile().substring(request.getContextPath().length());
        }

        return "redirect:/top";
    }
}
