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
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
        ModelAndView errorPage = new ModelAndView("error");
        int errorCode = (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");

        switch (errorCode) {
        case 400:
            errorPage.addObject("errorTitle", "error");
            errorPage.addObject("errorMsg", "Http Error Code: 400. Bad Request");
            break;
        case 401:
            errorPage.addObject("errorTitle", "403");
            errorPage.addObject("errorMsg", "Http Error Code: 401. Unauthorized");
            break;
        case 403:
            errorPage.addObject("errorTitle", "403");
            errorPage.addObject("errorMsg", "Http Error Code: 403. Forbidden");
            break;
        case 404:
            errorPage.addObject("errorTitle", "404");
            errorPage.addObject("errorMsg", "Http Error Code: 404. Resource not found");
            break;
        case 500:
            errorPage.addObject("errorTitle", "error");
            errorPage.addObject("errorMsg", "Http Error Code: 500. Internal Server Error");
            break;
        }

        return errorPage;
    }
}
