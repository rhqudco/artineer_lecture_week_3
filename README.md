# artineer_week_lecture_3

# JPA
JPA는 ORM형태의 기술이다. 기존에는 DB에 대한 의존도가 높기 때문에 DB Query에 의한 시스템을 구축하였다
자바는 객체지향 언어이기 때문에 DB와 다른 용도로 사용해야 한다.
그래서 ORM이라는 기술이 발전하게 되었다.
ORM(Objective Relation Mapping) : Java의 객체와 DB의 Relation을 Mapping한다 하여 ORM이다. 벤더에 독립적으로 만들 수 있어 DB 프로그램과 분리되어 의존도가 떨어진다.
JPA implementation : implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
H2 Database : 메모라에 저장하는 DB로 테스트를 하거나 예제를 진행하는 프로젝트에서 간단하게 사용할 수 있다.
H2 Database implementation : implementation 'com.h2database:h2'

@Entity - JPA가 관리하는 Domain이라고 알리기 위한 어노테이션으로 DB에 새로 생긴 객체를 생성이나 제거해준다.
@Id - PK로 사용하기 위한 어노테이션
@GeneratedValue - PK를 어떤 전략으로 사용한다 명시하는 어노테이션
@NoArgsConstructor - 생성자의 파라미터가 아무 것도 없는 기본 생성자를 만드는 어노테이션
@AllArgsConstructor - Builder를 위해 명시한 어노테이션으로 없으면 Builder에 오류가 생긴다.

JPA를 위해 엔티티로 변경
Article.java
~~~java
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
~~~
도메인에 CRUD하기 위해 관리하는 객체를 Repository라고 한다.  Repository는 인터페이스로 생성한다.
ArticleRepository.java
~~~java
public interface ArticleRepository extends CrudRepository<Article, Long> {
}
//CrudRepository 스프링 JPA에서 제공하는 CRUD를 바로 사용할 수 있도록 해줌. 
// <T(리포지토리가 관리하는 도메인, Id(리포지토리가 관리하는 도메인의 id)>
~~~
ArticleService 코드를 ArticleRepository 객체와 연계될 수 있는 코드(JPA)
ArticleService.java
~~~java
@RequiredArgsConstructor
@Service
public class ArticleService {
  private final ArticleRepository articleRepository;

  public Long save(Article request) {
    return articleRepository.save(request).getId();
  }

  public Article findById(Long id) {
    return articleRepository.findById(id).orElse(null);
  }

  @Transactional
  public Article update(Article request) {
    Article article = this.findById(request.getId());
    article.update(request.getTitle(), request.getContent());

    return article;
  }

  public void delete(Long id) {
    Article article = this.findById(id);
    articleRepository.delete(article);
  }
}
~~~
JPA는 하나의 Transaction이 끝날 때 마다 갱신하게 된다.
@Transactional - 하나의 Transaction을 알리는 어노테이션

CRUD API

ArticleController.java
~~~java
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
@RestController
public class ArticleController {
    private final ArticleService articleService;

// …
@PutMapping("/{id}")
  public Response<ArticleDto.Res> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
    Article article = Article.builder()
            .id(id)
            .title(request.getTitle())
            .content(request.getContent())
            .build();

    Article articleResponse = articleService.update(article);

    ArticleDto.Res response = ArticleDto.Res.builder()
            .id(String.valueOf(articleResponse.getId()))
            .title(articleResponse.getTitle())
            .content(articleResponse.getContent())
            .build();

    return Response.<ArticleDto.Res>builder().code(ApiCode.SUCCESS).data(response).build();
  }

  @DeleteMapping("/{id}")
  public Response<Void> delete(@PathVariable Long id) {
    articleService.delete(id);
    return Response.<Void>builder().code(ApiCode.SUCCESS).build();
  }
}
~~~

# H2 Database
메모라에 저장하는 DB로 테스트를 하거나 예제를 진행하는 프로젝트에서 간단하게 사용할 수 있다.

application.properties
~~~
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:testdb
~~~

# Of pattern

정적 팩토리메소드 패턴이라고도 한다.
Controller에서 했던 일을 Article에서 하게 해준다. - Article 객체 생성 코드가 Controller에 있기 때문에 개선이 필요.
비즈니스 로직에 대하여 응집도를 높여준다.
가독성을 높여줄 수 있고 객체지향스러운 코드 작성 가능.

Article.java
~~~java
class Article {
    // ...
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
~~~
ArticleDto.java
~~~java
//…
public static Res of(Article from) {
            return Res.builder()
                    .id(String.valueOf(from.getId()))
                    .title(from.getTitle())
                    .content(from.getContent())
                    .build();
        }
    }
} // ArticleController.java에 GET과 PUT에 있던 코드 중복 제거 ( of pattern )
~~~

Response.java
~~~java
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
} // ArticleController.java에 GET과 POST에 있던 코드 중복 제거 ( of pattern )
~~~

ArticleController.java
~~~java
class ArticleController {
    //...
  @PostMapping
  public Response<Long> post(@RequestBody ArticleDto.ReqPost request) {
    return Response.ok(articleService.save(Article.of(request)));
  }

  @GetMapping("/{id}")
  public Response<ArticleDto.Res> get(@PathVariable Long id) {
    return Response.ok(ArticleDto.Res.of(articleService.findById(id)));
  }

  @PutMapping("/{id}")
  public Response<ArticleDto.Res> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
    return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));
  }

  @DeleteMapping("/{id}")
  public Response<Void> delete(@PathVariable Long id) {
    articleService.delete(id);
    return Response.ok();
  }
}
// 기존 길고 중복이 많던 코드를 가독성이 좋고 중복을 제거한 코드로 변경
~~~
# 예외처리
실무에서는 오류가 발생하면 오류를 나타내면 안된다. 
개발자는 정상 케이스와 비정상 케이스 모두 잡아내어야 한다.
NullPointerException이 발생하는 것은 너무 위험하기 때문에 적절한 예외처리가 필요하다.

ArticleService.java
~~~java
class ArticleService {
    // ...
  @Transactional
  public Article update(Article request) {
    Article article = this.findById(request.getId());

    if(Objects.isNull(article)) {
      throw new RuntimeException("article value is not existed.");
    }

    article.update(request.getTitle(), request.getContent());

    return article;
  }
} // 실제 API에 대한 내용이 담기는 예외처리가 아님.
실제 API에 대한 내용이 담기는 예외처리가 아님.
~~~

ApiCode.java
~~~java
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApiCode {
    /* COMMON */
    SUCCESS("CM0000", "정상입니다"),
    DATA_IS_NOT_FOUND("CM0001", "데이터가 존재하지 않습니다")
    ;
//…
}
~~~
ArticleController.java
~~~java
class ArticleController {
  //...
  @PutMapping("/{id}")
  public Response<Object> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
    try {
      return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));
    } catch (RuntimeException e) {
      return Response.builder().code(ApiCode.DATA_IS_NOT_FOUND).data(e.getMessage()).build();
    }
  }
} // DATA_IS_NOT_FOUND라는 오류만 출력함.
~~~
DATA_IS_NOT_FOUND라는 오류만 출력함. 
유연성이 부족하다.

ApiException.java
~~~java
@Getter
public class ApiException extends RuntimeException {
    private final ApiCode code;

    public ApiException(ApiCode code) {
        this.code = code;
    }

    public ApiException(ApiCode code, String msg) {
        super(msg);
        this.code = code;
    }
}
~~~
ArticleService.java
~~~java
class ArticleService {
    //...
  @Transactional
  public Article update(Article request) {
    Article article = this.findById(request.getId());

    if (Objects.isNull(article)) {
      throw new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article value is not existed.");
    }

    article.update(request.getTitle(), request.getContent());

    return article;
  }
  //...
}
~~~

ArticleController.java
~~~java
class ArticleController {
  @PutMapping("/{id}")
  public Response<Object> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
    try {
      return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));
    } catch (ApiException e) {
      return Response.builder().code(e.getCode()).data(e.getMessage()).build();
    }
  }
}
~~~
API코드에 따라 예외처리를 받을 수 있지만, try-catch문의 중복코드가 발생하여 가독성이 떨어진다.

여러가지 문제들 때문에 Spring에서는 ControllerAdvice를 제공한다. 
ControllerAdvice는 클라이언트의 요청에 따라 컨트롤러가 처리하고 응답이 내려가게 될 때 컨트롤러 다음 단계에서 예외처리를 묶어서 같이 해줄 수 있는 기능을 제공한다.

ControllerExceptionHandler.java
~~~java
@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public Response<String> apiException(ApiException e) {
        return Response.<String>builder().code(e.getCode()).data(e.getMessage()).build();
    }
}
~~~
@ExceptionHandler - 예외처리에 대한 핸들링을 해달라는 어노테이션.

ArticleController.java
~~~java
class ArticleController {
    //...
  @GetMapping("/{id}")
  public Response<ArticleDto.Res> get(@PathVariable Long id) {
    return Response.ok(ArticleDto.Res.of(articleService.findById(id)));
  }

  @PutMapping("/{id}")
  public Response<ArticleDto.Res> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
    return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));
  }
}
~~~
Assert 객체를 통해 예외처리를 하면 if문을 줄여 가독성을 높일 수 있다.

Assert.java
~~~java
public class Asserts {
    public static void isNull(@Nullable Object obj, ApiCode code, String msg) {
        if(Objects.isNull(obj)) {
            throw new ApiException(code, msg);
        }
    }
}
~~~

ArticleService.java
~~~java
public class ArticleService {
    //...
  @Transactional
  public Article update(Article request) {
    Article article = this.findById(request.getId());
    Asserts.isNull(article, ApiCode.DATA_IS_NOT_FOUND, "article value is not existed.");

    article.update(request.getTitle(), request.getContent());

    return article;
  }
}
~~~
자바 자체에서 제공하는 Optional객체는 정해준 경우에 따라 무조건적인 처리를 진행한다.
하지만 비용이 많이 발생하므로 중요한 비즈니스 로직에 사용을 권장한다.
조금 더 단순한 형태에서는 Assert사용.

~~~java
class ArticleService {
    // ...
  public Article findById(Long id) {
    return articleRepository.findById(id)
            .orElseThrow(() -> new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article value is not existed."));
  }

  @Transactional
  public Article update(Article request) {
    Article article = this.findById(request.getId());
    article.update(request.getTitle(), request.getContent());

    return article;
  }
}
~~~
