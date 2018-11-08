package common.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * エラー処理クラス.
 */
@Controller
public class ErrorController extends BaseController {

    /**
     * エラー画面初期処理.
     *
     * @param model
     *            {@link ModelMap}
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先
     */
    @RequestMapping(value = "error", method = { RequestMethod.GET, RequestMethod.POST })
    public String renderErrorPage(ModelMap model, HttpServletRequest request) {
        switch ((Integer) request.getAttribute("javax.servlet.error.status_code")) {
        case 401:
        case 403:
            model.addAttribute("errorTitle", "403");
            break;
        case 404:
            model.addAttribute("errorTitle", "404");
            break;
        default:
            model.addAttribute("errorTitle", "error");
        }

        return "error";
    }
}
