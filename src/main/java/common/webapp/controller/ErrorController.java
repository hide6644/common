package common.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * エラー処理クラス.
 */
@Controller
public class ErrorController extends BaseController {

    /**
     * エラー画面初期処理.
     *
     * @param model
     *            {@link Model}
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先
     */
    @GetMapping(value = "error")
    public String renderErrorPage(Model model, HttpServletRequest request) {
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
