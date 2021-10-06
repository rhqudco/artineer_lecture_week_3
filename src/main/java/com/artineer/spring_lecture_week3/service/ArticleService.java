package com.artineer.spring_lecture_week3.service;

import com.artineer.spring_lecture_week3.domain.Article;
import com.artineer.spring_lecture_week3.domain.ArticleRepository;
import com.artineer.spring_lecture_week3.exception.ApiException;
import com.artineer.spring_lecture_week3.vo.ApiCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    public Long save(Article request) { //C
        return articleRepository.save(request).getId();
    }

    public Article findById(Long id) { //R
        return articleRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article is not found"));
    }

    @Transactional
    public Article update(Article request) { //U
        Article article = this.findById(request.getId());
        article.update(request.getTitle(), request.getContent());

        return article; //도메인에 있는 함수를 호출만 하더라도 내용이 바뀌게 된다.
    }

    public void delete(Long id) { //D
        articleRepository.deleteById(id);
    }
}
