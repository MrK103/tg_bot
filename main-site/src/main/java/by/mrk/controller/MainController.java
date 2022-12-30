package by.mrk.controller;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@Controller
public class MainController {

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/login-error")
    public String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        if (Objects.equals(errorMessage, "Bad credentials")) errorMessage = "User not found";
        model.addAttribute("errorMessage", errorMessage);
        return "login";
    }
    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
