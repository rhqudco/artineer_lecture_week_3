package com.artineer.spring_lecture_week3.dto;

import com.artineer.spring_lecture_week3.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ArticleDto {
    @Getter
    public static class ReqPost {
        String title;
        String content;
    }

    @Getter
    public static class ReqPut {
        String title;
        String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        private String id;
        private String title;
        private String content;

        public static Res of(Article from) {
            return Res.builder()
                    .id(String.valueOf(from.getId()))
                    .title(from.getTitle())
                    .content(from.getContent())
                    .build();
        } // ArticleController.java에 GET과 PUT에 있던 코드 중복 제거 ( of pattern )
    }
}
