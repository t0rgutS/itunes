package com.vakhnenko.controller.ui;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request,
                              Model model) {
        Integer errorCode;
        String errorMsg = "";
        String path = "";
        try {
            errorCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
            errorMsg = (String) request.getAttribute("javax.servlet.error.message");
            path = (String) request.getAttribute("javax.servlet.error.request_uri");
        } catch (IllegalArgumentException iae) {
            errorCode = 500;
            errorMsg = "Неопознанная ошибка!";
        }
        if (errorMsg.equals("")) {
            if (errorCode == 404)
                errorMsg = "Страница не найдена!";
            else if (errorCode == 403)
                errorMsg = "Доступ запрещен!";
            else if (errorCode == 401)
                errorMsg = "Вы не авторизированы!";
            else if (errorCode >= 400 && errorCode < 500)
                errorMsg = "Клиенсткая ошибка. Бывает...";
            else if (errorCode >= 500)
                errorMsg = "Ошибка сервера. Тоже иногда бывает...";
        }
        if (path.equals(""))
            path = "А нет пути, не удалось получить!";
        Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (throwable != null)
            System.out.println(throwable.getMessage());
        model.addAttribute("code", errorCode);
        model.addAttribute("message", errorMsg);
        model.addAttribute("path", path);
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
