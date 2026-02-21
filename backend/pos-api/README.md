## pos-api (Swing POS → Spring Boot 백엔드)

현재 레포의 Swing POS가 MySQL에 직접 붙던 구조를, **Spring Boot API 서버로 분리**하기 위한 백엔드 모듈입니다.

### 요구사항
- Java 17+
- Maven 3.9+
- (선택) Docker / Docker Compose

### 로컬 실행 (Docker로 MySQL 띄우기)
`backend/pos-api`에서:

```bash
docker compose up -d
```

또는 루트에서:

```bash
make backend-up
```

### 로컬 실행 (API 서버)
`backend/pos-api`에서:

```bash
DB_HOST=localhost DB_PORT=3306 DB_NAME=judang DB_USER=pos DB_PASSWORD=pos mvn spring-boot:run
```

또는 루트에서:

```bash
make backend-run
```

로컬에 Maven(`mvn`)이 없으면 `make backend-run`은 자동으로 Docker(Maven 이미지)로 실행합니다.
종료는 다음으로 할 수 있습니다:

```bash
make backend-stop
```

기본 포트는 `8080`입니다. (`PORT` 환경변수로 변경 가능)

### 빠른 확인 (curl 예시)

```bash
curl -s http://localhost:8080/api/types
curl -s "http://localhost:8080/api/products?type=DRINK"

curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'content-type: application/json' \
  -d '{"id":"admin","password":"admin"}'

curl -s -X POST http://localhost:8080/api/bills \
  -H 'content-type: application/json' \
  -d '{"payMethod":"CASH","items":[{"productName":"Americano","quantity":2}]}'
```

### Swing 마이그레이션 치환표
`MIGRATION_SWING.md`를 참고하세요.

