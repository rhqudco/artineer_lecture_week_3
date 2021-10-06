package com.artineer.spring_lecture_week3.dto;

import com.artineer.spring_lecture_week3.vo.ApiCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Response<T> {
    private ApiCode code;
    private T data;

    public static Response<Void> ok() {
        return Response.<Void>builder()
                .code(ApiCode.SUCCESS)
                .build();
    }
    public static <T> Response<T> ok(T data) {
        return Response.<T>builder()
                .code(ApiCode.SUCCESS)
                .data(data)
                .build();
    }
}
// ArticleController.java에 GET과 POST에 있던 코드 중복 제거 ( of pattern )