package common.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TopController extends BaseController {

    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public String handleRequest() {
    	return "top";
    }
}
