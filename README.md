# KiHiBooks

출판사의 콘텐츠 관리와 독자의 웹소설 구매·열람을 연결하는 B2B2C 플랫폼입니다. 여러 출판사가 입점하고, 출판사 대표와 에디터가 작품을 관리하며, 일반 사용자는 작품을 탐색하고 회차를 구매할 수 있습니다.

- **개발 형태:** 3인 팀 프로젝트
- **주요 담당 영역:** 역할 기반 접근 제어, 출판사별 데이터 관리, 에디터 업무 흐름
- **주요 구성:** 독자 서비스, 출판사 관리 페이지, 서비스 관리자 페이지

## 주요 화면

독자의 작품 탐색부터 출판사의 실적 확인, 담당자 배정, 에디터의 콘텐츠 관리까지 이어지는 화면입니다. 아래 화면의 작품·계정·실적은 샘플 데이터 기준입니다.

### 독자 서비스

장르별 작품 탐색, 프로모션 배너, 실시간 랭킹을 통해 읽을 작품을 찾습니다.

<img width="957" height="945" alt="스크린샷 2026-08-11 173140" src="https://github.com/user-attachments/assets/509b9c25-a4b1-4c3e-b659-57fa75378656" />



### 출판사 운영 대시보드

출판사 전체의 판매 실적과 이전 기간 대비 변화를 확인합니다. 일별 판매 추이와 장기 미연재 작품 등 운영 현황을 함께 살펴볼 수 있습니다.

<img width="1140" height="939" alt="스크린샷 2026-09-04 141756" src="https://github.com/user-attachments/assets/0c0ca11f-f3db-4695-a4b2-71f2d1380f2e" />

### 담당 작품 관리

출판사 대표가 작품별 담당 에디터를 지정하거나 변경합니다. 작품 목록에서 현재 담당자를 확인하고 업무를 배분합니다.

<img width="1356" height="822" alt="스크린샷 2026-09-04 145115" src="https://github.com/user-attachments/assets/aad6c8ec-c627-4cd8-aaef-75c1c456f7d0" />

### 에디터 콘텐츠 관리

에디터는 자신에게 배정된 작품을 연재 상태별로 확인하고, 각 작품의 회차·공지·기본 정보를 관리합니다.

<img width="1143" height="832" alt="스크린샷 2026-09-04 154701" src="https://github.com/user-attachments/assets/0de5f235-f305-4ad4-86a9-6e79d89a3ff9" />

## 역할별 주요 기능

| 구분 | 기능 |
| --- | --- |
| 독자 | 회원가입·로그인, 카카오 로그인, 장르별 탐색·검색, 작품 상세·회차 열람, 장바구니·구매 내역, 포인트, 리뷰·댓글, 출석 체크 |
| 출판사 대표 | 에디터 등록·해제, 작품별 담당 에디터 지정, 소속 출판사 콘텐츠 관리, 출판사 전체 KPI 조회 |
| 출판사 에디터 | 담당 작품 조회, 도서 정보·회차·가격·작품 공지 관리, 담당 작품 KPI 조회 |
| 서비스 관리자 | 출판사 등록 및 출판사 계정 관리 |

## 기술 스택

| 영역 | 기술 |
| --- | --- |
| 언어·서버 | Java 17, Spring Boot 3.4.5, Spring MVC |
| 인증·권한 | Spring Security, BCrypt |
| 데이터 접근 | MyBatis 3.0.3, Spring JDBC |
| 데이터베이스 | MySQL |
| 화면 | Thymeleaf, Thymeleaf Layout Dialect, HTML, CSS, JavaScript, Swiper |
| 외부 연동 | 카카오 로그인, SMTP 메일 |
| 빌드·테스트 | Maven Wrapper, Spring Boot Test, Spring Security Test |

Thymeleaf로 HTML을 렌더링하는 서버 중심 구조입니다. 루트의 `package.json`은 Swiper 의존성을 관리하며, 별도 프런트엔드 실행 스크립트는 없습니다.

## 핵심 설계 및 구현

### 역할과 콘텐츠 소유권 분리

사용자 계정의 `ADMIN`, `USER`, `PUBLISHER` 권한과 출판사 내부의 `SUPER`, `EDITOR` 권한을 구분합니다.

- `SecurityConfig`: 관리자·출판사·에디터 URL의 역할별 접근을 제어합니다.
- `CustomUser`: 인증 정보에 출판사 코드(`pu_code`)와 출판사 계정 식별자(`pi_num`)를 보관합니다.
- `PublisherInterceptor`: 출판사·에디터 경로에서 요청한 도서·회차·공지의 소속 출판사와 담당 에디터를 확인합니다.
- KPI 조회: 대표는 소속 출판사 전체, 에디터는 담당 작품으로 조회 범위를 제한합니다.

### 역할별 KPI 조회 범위

`/publisher/dashboard`에서 최근 7일·30일·90일 실적과 직전 동일 기간을 비교합니다. 인증된 사용자의 출판사 코드와 담당자 식별자를 기준으로 조회 범위를 정합니다.

| 구분 | 조회 범위 및 지표 |
| --- | --- |
| 공통 | 콘텐츠 판매액, 유료 구매 회차 수, 구매 독자 수, 리뷰 수, 평균 별점, 일별 판매 추이 |
| 대표 | 출판사 전체 실적, 에디터별 현황, 상위 작품 매출 비중, 구매 독자당 판매액 |
| 에디터 | 담당 작품 실적, 최근 회차 등록이 없거나 14일 이상 지난 작품 |

### 여러 테이블 변경의 트랜잭션 처리

에디터 등록·해제 시 사용자 권한과 출판사 계정 정보를 함께 변경하고, 도서 정보 수정 시 도서와 키워드 연결을 함께 갱신합니다. 해당 서비스 메서드에 `@Transactional`을 적용하여 처리 중 예외가 발생하면 DB 변경을 롤백합니다.

DB 트랜잭션은 업로드한 파일의 저장·삭제까지 롤백하지 않습니다.

### 현재 구현의 한계

`SecurityService.canAccessBook()`과 `canAccessEpisode()`는 현재 항상 `true`를 반환합니다. 따라서 이 메서드를 호출하는 `@PreAuthorize`와 EPUB 파일 접근 검사는 별도의 소유권·구매 검증을 수행하지 않습니다. 출판사 관리 경로에는 인터셉터 검증이 적용되어 있으며, 위 메서드의 검증 구현은 남은 과제입니다.

## 프로젝트 구조

```text
KiHiBooks/
├── DB/                         # 스키마 및 샘플 데이터 SQL
├── docs/images/                # README 화면 이미지
├── kihibooks/
│   ├── pom.xml                 # Spring Boot 의존성 및 빌드 설정
│   ├── mvnw / mvnw.cmd          # Maven Wrapper
│   └── src/
│       ├── main/
│       │   ├── java/kr/kh/kihibooks/
│       │   │   ├── config/     # Security, MyBatis, MVC 설정
│       │   │   ├── controller/ # 요청 처리
│       │   │   ├── service/    # 업무 로직
│       │   │   ├── dao/        # MyBatis Mapper 인터페이스
│       │   │   ├── model/      # 데이터 모델
│       │   │   ├── interceptor/ # 출판사 접근 검증
│       │   │   └── utils/      # 인증 및 공통 유틸리티
│       │   └── resources/
│       │       ├── mappers/   # SQL 매핑
│       │       ├── templates/ # Thymeleaf 화면
│       │       └── static/    # CSS, JavaScript, 이미지
│       └── test/               # 대시보드 및 접근 제어 테스트
├── package.json
└── README.md
```

## 로컬 실행

### 1. 준비 사항

- JDK 17 및 `JAVA_HOME` 설정
- 실행 중인 MySQL과 DB 생성 권한이 있는 계정
- 의존성 다운로드를 위한 네트워크 연결
- 메일·카카오 로그인 사용 시 해당 서비스의 연동 설정

Maven은 저장소의 Wrapper로 실행할 수 있습니다.

### 2. 데이터베이스 구성

MySQL Workbench에서 아래 SQL을 순서대로 실행합니다. 데이터가 들어갈 스키마는 `KIHIBOOKS`를 선택합니다.

**주의:** `DB/키히북스 DDL.sql`은 기존 `KIHIBOOKS` 데이터베이스를 삭제하고 다시 생성합니다. 새 로컬 개발 DB를 준비할 때만 실행하세요.

| 순서 | 파일 | 내용 |
| --- | --- | --- |
| 1 | `DB/키히북스 DDL.sql` | 데이터베이스·테이블 생성 |
| 2 | `DB/step1_2_3_user_author_cate.sql` | 사용자·출판사·작가·분류 등 기본 데이터 |
| 3 | `DB/step4_books_insert.sql` | 도서 |
| 4 | `DB/step5_insert_reviews.sql` | 리뷰 |
| 5 | `DB/step6_insert_book_keywords.sql` | 도서별 키워드 |
| 6 | `DB/step7_episode_dummy.sql` | 회차 |
| 선택 | `DB/kpi_dashboard_demo.sql` | 실행일 기준 KPI 데모 데이터 |

기본 샘플 SQL의 비밀번호는 평문이므로 BCrypt를 사용하는 현재 로그인에 그대로 사용할 수 없습니다. 로그인 테스트에는 회원가입으로 만든 계정을 사용하거나 샘플 계정의 `UR_PW`를 BCrypt 해시로 교체해야 합니다. 출판사 화면을 이용하려면 사용자 권한과 `PUBLISHER_ID` 소속·역할 정보도 필요합니다.

### 3. 로컬 설정

`kihibooks/src/main/resources/application.properties`를 생성합니다. 이 파일은 Git에서 제외됩니다. 아래 예시의 경로와 연동 값을 로컬 환경에 맞게 변경하세요.

```properties
spring.application.name=kihibooks
server.port=8080

kihibooks.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
kihibooks.datasource.jdbc-url=jdbc:mysql://localhost:3306/KIHIBOOKS?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul
kihibooks.datasource.username=YOUR_DB_USERNAME
kihibooks.datasource.password=YOUR_DB_PASSWORD

spring.remember.me.key=REPLACE_WITH_A_RANDOM_LOCAL_SECRET
spring.path.upload=C:/kihibooks/uploads/

spring.mail.host=YOUR_SMTP_HOST
spring.mail.port=587
spring.mail.username=YOUR_SMTP_USERNAME
spring.mail.password=YOUR_SMTP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

kakao.client.id=YOUR_KAKAO_REST_API_KEY
kakao.redirect.uri=http://localhost:8080/signup/kakao
```

DB 설정은 `spring.datasource.*` 대신 **`kihibooks.datasource.*`**를 사용합니다. `MyBatisConfig`가 이 접두사로 데이터 소스를 생성합니다.

업로드 디렉터리를 미리 만들고 쓰기 권한을 부여하세요. SQL의 이미지·EPUB 경로에 해당하는 실제 파일은 별도로 준비해야 하며, 데이터만 넣으면 표지나 본문이 표시되지 않을 수 있습니다. 메일 인증과 카카오 로그인은 유효한 연동 값을 설정해야 동작합니다.

### 4. 서버 실행

저장소 루트에서 실행합니다.

**Windows PowerShell**

```powershell
cd kihibooks
.\mvnw.cmd spring-boot:run
```

**macOS / Linux**

```bash
cd kihibooks
sh mvnw spring-boot:run
```

실행 후 [로컬 서비스](http://localhost:8080)에 접속합니다.

| 경로 | 화면 |
| --- | --- |
| `/login` | 로그인 |
| `/genre/romance` | 로맨스 장르 |
| `/publisher/dashboard` | 출판사·에디터 KPI 대시보드 |
| `/publisher/editors` | 대표의 에디터 관리 |
| `/editor/myContent` | 콘텐츠 목록 |
| `/account/mykihi` | 사용자 마이페이지 |

### 5. 테스트 및 빌드

`kihibooks` 디렉터리에서 실행합니다.

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
java -jar target/kihibooks-0.0.1-SNAPSHOT.jar
```

macOS / Linux에서는 `.\mvnw.cmd` 대신 `sh mvnw`를 사용합니다. `package`에도 테스트 실행이 포함됩니다.

## KPI 데모 데이터

`DB/kpi_dashboard_demo.sql`은 은하출판사(`P001`)의 기존 도서·회차를 사용해 실행일 기준 데이터를 생성합니다.

| 기간 | 콘텐츠 판매액 | 유료 구매 회차 | 구매 독자 | 리뷰 |
| --- | ---: | ---: | ---: | ---: |
| 최근 30일 | 1,700원 | 17건 | 4명 | 6개 |
| 이전 30일 | 600원 | 6건 | 3명 | 3개 |

위 수치는 데모로 추가하는 데이터 기준이며, 기존 데이터가 있으면 대시보드 합계는 달라질 수 있습니다.

- 주문 ID는 `KPIDEMO`, 리뷰 내용은 `[KPI_DEMO]`로 구분합니다.
- 재실행하면 해당 데모 주문·리뷰를 교체하여 중복 생성을 방지합니다.
- 필요한 기본 사용자 또는 `P001` 회차가 없으면 오류를 발생시키고 트랜잭션을 롤백합니다.
- 스키마를 준비한 뒤 최소한 `step1_2_3_user_author_cate.sql`, `step4_books_insert.sql`, `step7_episode_dummy.sql`이 적용되어 있어야 합니다.

Workbench에서 파일 전체를 실행하거나, MySQL CLI에서 다음과 같이 실행합니다. 경로는 저장소 위치에 맞게 변경하세요.

```text
mysql -u YOUR_DB_USERNAME -p --default-character-set=utf8mb4 KIHIBOOKS
mysql> source C:/path/to/KiHiBooks/DB/kpi_dashboard_demo.sql;
```
