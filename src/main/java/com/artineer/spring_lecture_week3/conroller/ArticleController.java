package com.artineer.spring_lecture_week3.conroller;

import com.artineer.spring_lecture_week3.domain.Article;
import com.artineer.spring_lecture_week3.dto.ArticleDto;
import com.artineer.spring_lecture_week3.dto.Response;
import com.artineer.spring_lecture_week3.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
@RestController
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public Response<Long> post(@RequestBody ArticleDto.ReqPost request) {
        return Response.ok(articleService.save(Article.of(request)));
    }

    @GetMapping("/{id}")
    public Response<ArticleDto.Res> get(@PathVariable Long id) {
        return Response.ok(ArticleDto.Res.of(articleService.findById(id)));
    }

    @PutMapping("/{id}")
    public Response<Object> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
        return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Response.ok();
    }
}
// 기존 길고 중복이 많던 코드를 가독성이 좋고 중복을 제거한 코드로 변경.