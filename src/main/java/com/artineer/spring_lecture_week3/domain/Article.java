package com.artineer.spring_lecture_week3.domain;

import com.artineer.spring_lecture_week3.dto.ArticleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String content;

    public void update(String title, String content) {
        this.title = title;
        this.content = content; // 도메인에 등록하여 JPA의 관리하에 둔다.
    }

    public static Article of(ArticleDto.ReqPost from) {
        return Article.builder()
                .title(from.getTitle())
                .content(from.getContent())
                .build();
    }

    public static Article of(ArticleDto.ReqPut from, Long id) {
        return Article.builder()
                .id(id)
                .title(from.getTitle())
                .content(from.getContent())
                .build();
    }
}
