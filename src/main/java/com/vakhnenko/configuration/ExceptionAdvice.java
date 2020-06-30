package com.vakhnenko.configuration;

import com.vakhnenko.dto.ExceptionResponse;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.exception.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;

@Log4j2
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<Object> handle(Throwable exception) {
        Throwable root = ExceptionUtils.getRootCause(exception);
        String errorMessage = "";
        if (root.getMessage() != null) {
            if (root.getMessage().contains("default message ["))
                errorMessage = root.getMessage().substring(root.getMessage().lastIndexOf("[") + 1,
                        root.getMessage().lastIndexOf("]") - 1);
            else
                errorMessage = root.getMessage();
        }
        HttpStatus status;
        if (exception instanceof NotFoundException)
            status = HttpStatus.NOT_FOUND;
        else if (exception instanceof BadRequestException || exception instanceof MethodArgumentNotValidException
                || root instanceof ConstraintViolationException)
            status = HttpStatus.BAD_REQUEST;
        else if (exception instanceof AccessDeniedException || root instanceof AccessDeniedException)
            status = HttpStatus.FORBIDDEN;
        else
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        String path = "";
        String method = "";
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            path = ((ServletRequestAttributes) attributes).getRequest().getRequestURI();
            method = ((ServletRequestAttributes) attributes).getRequest().getMethod();
        }
        if (!path.equals("") && !method.equals(""))
            log.error("Ошибка " + root.getClass() + " по адресу: " + path + " (метод: " + method + "): "
                    + errorMessage);
        else
            log.error("Ошибка: " + root.getClass() + ": " + errorMessage);
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON)
                .body(new ExceptionResponse(status.value(), errorMessage, path, method));
    }
}
