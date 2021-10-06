package com.artineer.spring_lecture_week3.domain;

import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<Article, Long> {
}
//CrudRepository 스프링 JPA에서 제공하는 CRUD를 바로 사용할 수 있도록 해줌.
// <T(리포지토리가 관리하는 도메인,Id(리포지토리가 관리하는 도메인의 id)>