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
- `docker compose -f db-docker-compose.yml -p database up -d`

### 생성 확인
- Docker 생성과 실행을 확인합니다.
- `docker ps`

### 컨테이너 삭제
- `docker compose -f db-docker-compose.yml -p database down`
- 만약, table 의 구성(속성 등)을 변경 하거나, 신규 테이블 생성을 하려면, `init.sh` 의 내용을 수정하고, 볼륨을 삭제해야 합니다.
- 단, 볼륨을 삭제하게 되면, 기존 데이터는 모두 삭제되므로, 백업을 잘 처리해야 합니다.
- `docker compose -f db-docker-compose.yml -p database down -v`

### 옵션들
- 기본적으로, `Slow Query Logging` 옵션이 1초로 켜져있습니다.
- 만약, `.env` 파일에 `MYSQL_QUERY_LOG_PATH` 에 설정한 곳으로 호스트 PC 에서 로그 확인이 가능합니다.
- 또한, 기본적으로, `log_queries_not_using_indexes` 옵션이 켜져있어, 같은 곳으로 인덱스를 사용하지 않은 쿼리가 로깅됩니다.
- 만약, 해당 기능들을 끄거나, 변경하시려면, `my.cnf` 파일을 변경 후, 컨테이너를 재실행 하시길 바랍니다.
