package common.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * エラー処理クラス.
 */
@Controller
public class ErrorController extends BaseController {

    /**
     * エラー画面初期処理.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先画面設定
     */
    @RequestMapping(value = "error", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView renderErrorPage(HttpServletRequest request) {
        ModelAndView errorPage = new ModelAndView("error");
        int errorCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        switch (errorCode) {
        case 401:
        case 403:
            errorPage.addObject("errorTitle", "403");
            break;
        case 404:
            errorPage.addObject("errorTitle", "404");
            break;
        default:
            errorPage.addObject("errorTitle", "error");
        }

        return errorPage;
    }
}
