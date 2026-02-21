## POS Monorepo

이 레포는 **Swing POS(프론트)**와 **Spring Boot API(백엔드)**를 한 레포에서 관리합니다.

### 디렉토리 구조
- `frontend/`: 기존 Swing POS 클라이언트 (이제 DB 직결 대신 HTTP로 백엔드 호출)
  - `frontend/src/`: Java 소스
  - `frontend/lib/`: Swing에서 쓰는 벤더 JAR (Eclipse `.classpath`는 상대경로로 설정)
- `backend/pos-api/`: Spring Boot 백엔드 API

### 실행(개발)

#### Backend
`backend/pos-api/README.md` 참고

또는 루트에서:

```bash
make backend-up
make backend-run         # 포그라운드(터미널 점유)
make backend-run-docker-bg # 백그라운드(터미널 반환)
make backend-stop
```

#### Frontend (Swing)
- 기본 API 주소: `http://localhost:8080`
- 변경 시:
  - JVM 옵션: `-DPOS_API_BASE_URL=http://localhost:8080`
  - 또는 환경변수: `POS_API_BASE_URL=http://localhost:8080`

루트에서 실행:

```bash
make frontend-run
```

