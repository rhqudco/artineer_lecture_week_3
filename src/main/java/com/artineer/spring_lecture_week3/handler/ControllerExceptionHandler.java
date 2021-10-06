package com.artineer.spring_lecture_week3.handler;

import com.artineer.spring_lecture_week3.dto.Response;
import com.artineer.spring_lecture_week3.exception.ApiException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public Response<String> apiException(ApiException e) {
        return Response.<String>builder().code(e.getCode()).data(e.getMessage()).build();
    }
}
