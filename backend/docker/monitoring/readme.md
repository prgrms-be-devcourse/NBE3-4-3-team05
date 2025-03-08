# Z9 Hobby project data base
## 설치 방법
### .env 생성
- `.env` 파일을 생성하고, 아래 필수 값들을 입력해줍니다.
- `.env` 파일은 docker-compose 파일과 같은 경로에 생성합니다.
```
MYSQL_ROOT_PASSWORD={패스워드}
MYSQL_OUT_PORT={외부 접근 포트 / Number}
MYSQL_IN_PORT={내부 접근 포트 / Number}
MYSQL_TIME_ZONE={시간대 / Asia/Seoul }
MYSQL_QUERY_LOG_PATH={HOST PC 로그 저장 경로}

EXPORTER_NAME={Exporter 계정명}
EXPORTER_PASSWORD={Exporter 계정 비밀번호}

REDIS_PASSWORD={REDIS 비밀번호}
REDIS_OUT_PORT={REDIS 외부 접근 포트 / Number}
REDIS_IN_PORT={REDIS 내부 접근 포트 / Number}
```

### Docker-compose 실행
- Docker Compose 위치에서 명령어를 실행합니다.
- `docker compose -f monitoring-docker-compose.yml -p monitoring up -d`

### 생성 확인
- Docker 생성과 실행을 확인합니다.
- `docker ps`

### 컨테이너 삭제
- `docker compose -f monitoring-docker-compose.yml -p monitoring down`