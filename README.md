업로드해주신 프로젝트의 구조와 설정 파일(Spring Security, JWT, Swagger 등)을 분석하여, 누구나 프로젝트를 쉽게 이해하고 실행할 수 있도록 표준적인 `README.md` 템플릿을 작성해 드립니다.

이 내용을 복사해서 프로젝트 최상위 경로에 `README.md` 파일로 저장하시면 됩니다.

---

```markdown
# Easeek Backend API (Alba Platform)

Easeek 서비스의 백엔드 API 서버입니다. RESTful API 기반으로 작성되었으며, Spring Security와 JWT를 활용한 인증/인가 및 소셜 로그인 기능을 제공합니다.

## 🛠 Tech Stack

**Core**
- Java 17
- Spring Boot 3.2.1
- Spring Data JPA
- Spring Security

**Database & Cache**
- MySQL 8.0
- Redis (Docker 환경)

**API Docs**
- Springdoc OpenAPI (Swagger UI)

**Infrastructure (GCP)**
- Cloud Run (Serverless Computing)
- Cloud Build (CI/CD Pipeline)
- Cloud SQL (MySQL)

---

## 📁 Project Structure

```text
src/main/java/com/alba/platform/
├── config/        # Security, Swagger 등 공통 설정
├── controller/    # REST API 엔드포인트 (AuthController 등)
├── dto/           # Request/Response 데이터 객체
├── entity/        # JPA 엔티티 모델 (User, Term, SocialAccount 등)
├── repository/    # 데이터베이스 접근 계층
├── security/      # JWT 필터 및 인증 처리 (JwtTokenProvider 등)
└── service/       # 비즈니스 로직 (AuthService 등)

```

---

## 🚀 Getting Started (Local Development)

### 1. Prerequisites

* [Java 17](https://adoptium.net/) 설치
* [Docker & Docker Compose](https://www.docker.com/) 설치

### 2. Database Setup (Docker)

로컬 개발 환경에서는 Docker Compose를 사용하여 MySQL과 Redis를 실행합니다.

```bash
# 프로젝트 최상위 경로에서 실행
docker-compose up -d

```

* **MySQL**: `localhost:3307` (계정/비밀번호는 `docker-compose.yml` 참조)
* **Redis**: `localhost:6379`

### 3. Application Run

```bash
# Maven Wrapper를 이용한 실행
./mvnw spring-boot:run

```

---

## 📚 API Documentation (Swagger)

서버가 실행된 후, 아래 주소로 접속하여 API 명세서를 확인하고 직접 테스트해 볼 수 있습니다. (프론트엔드 연동 시 참고)

* **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
* **API Docs (JSON)**: `http://localhost:8080/v3/api-docs`

---

## ☁️ Deployment (Google Cloud Platform)

이 프로젝트는 GitHub `main` 브랜치에 코드가 푸시되면 GCP Cloud Build를 통해 자동으로 Cloud Run에 배포되도록 구성되어 있습니다.

### 환경 변수 (Environment Variables)

프로덕션 배포 시 다음 환경 변수 설정이 필요합니다 (`application-prod.yml` 참조).

* `DB_HOST`: Cloud SQL 공개 IP
* `DB_NAME`: 데이터베이스 이름 (예: `alba_platform`)
* `DB_USER`: 데이터베이스 사용자 계정
* `DB_PASSWORD`: 데이터베이스 비밀번호
* `JWT_SECRET`: JWT 토큰 암호화를 위한 시크릿 키

### 수동 배포 (GCP CLI)

로컬에서 직접 배포가 필요한 경우 아래 명령어를 사용합니다.

```bash
gcloud run deploy alba-platform-api \
  --source . \
  --region asia-northeast3 \
  --allow-unauthenticated \
  # ... 환경변수 설정

```

---

## 🔐 Authentication (JWT & Social Login)

* `Authorization: Bearer <Access_Token>` 헤더를 통해 인증이 필요한 API에 접근할 수 있습니다.
* Refresh Token을 통해 Access Token 만료 시 재발급을 수행합니다.

```

---

**💡 추가 팁:**
* 위 내용을 복사해서 프로젝트 최상위 디렉토리에 `README.md`라는 이름으로 저장하신 후, 띄어쓰기 한 칸이라도 수정해서 `git add .`, `git commit -m "Add README.md"`, `git push`를 진행해 보세요.
* 방금 설정하신 **GCP Cloud Build 트리거가 이 `push`를 감지하여 방화벽 설정이 완료된 새로운 버전의 서버를 자동으로 배포(재배포)**하게 됩니다!

```
