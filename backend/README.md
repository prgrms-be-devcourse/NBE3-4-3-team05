# NBE3-4-3-Team05
데브코스 3기 4회차 2차프로젝트 오🌏구 팀

---
## 서비스 시작하기
### 1. 다운로드

해당 Github 에서 무료로 다운로드 가능합니다.
[최신 릴리즈 다운로드](https://github.com/prgrms-be-devcourse/NBE3-4-3-team05/archive/refs/heads/main.zip)

### 2. 환경변수 셋팅

이 프로젝트를 실행하기 위해서는, `.env` 파일이 루트 경로에 필요합니다!
`/backend/.env`
그리고, 필수 환경 변수를 지정해줘야 합니다!
아래에는 필수 환경변수와 예시입니다.

```env
Z9_DB_URL = "jdbc:mysql://localhost:3306/test?serverTimezone=Asia/Seoul"
Z9_DB_USERNAME = "root"
Z9_DB_PASSWORD = "1234"
JWT_SECRET = 'sdlkfnxcoisdfiojvclkmnflkni219o3uj908uxfjn1298i03ujiodusfj10928ijjsklfnwe0ijf1io2j3oi1kklasdf'
JWT_ACCESS_EXPIRATION = 300000
JWT_REFRESH_EXPIRATION = 30000000
OAUTH_KAKAO_CLIENT_ID = "YOUR_API_KEY"
OAUTH_KAKAO_REDIRECT-URI = "REDIRECT_URI"
OAUTH_KAKAO_CLIENT_SECRET = "YOUR_KAKAO_CLIENT_SECRET"
OAUTH_KAKAO_CONENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8"
Z9_REDIS_DB_HOST = "YOUR_REDIS_HOST"
Z9_REDIS_DB_PORT = "YOUR_REDIS_PORT"
Z9_REDIS_DB_PASSWORD = "YOUR_REDIS_PASSWORD"
```
- 이때, `JWT_SECRET` 값은, 최소 32바이트 이상 설정되어야 합니다. 즉, 최소 32개 문자 이상 들어가야 합니다!
- `JWT_ACCESS_EXPIRATION`, `JWT_REFRESH_EXPIRATION` 은, 각각의 token 의 유효 시간을 나타냅니다. 단위는 ms 입니다.
  - 즉, 3000 = 3초 
- oauth login 이 추가되면서, 환경변수가 추가되었습니다. (2025/02/02)
- redis 추가 구성되면서, 환경 변수가 추가되었습니다. (2025/02/03)

### 3. 프로젝트 사전 구성
- 기본적으로, MySQL 8.0v 이상이 설치되어 있어야 합니다.
- 또한, 설치된 MySQL 과 `.env` 파일 내용이 일치되어야 합니다.
- redis 가 설치되어야 합니다. 설치 방법은 공유된 [notion 페이지](https://www.notion.so/Redis-f6fd1bb01d994977b17a8fd5ffb86fe0?pvs=4) 참고 바랍니다.

### 4. 프로젝트 실행 방법
- Intellij 로 실행 시 최상단 프로젝트 실행 task에 옵션을 추가해줘야 합니다.
  1. task 화살표 클릭 후, `Edit Configurations...` 클릭
  2. 다음 창에서, 중간부분, `Modify options` 클릭
  3. 나오는 창들 중, `Enviorment variables` 클릭
  4. 중간 부분, `Enviroment variables` 에, 파일 아이콘 클릭해서, 생성한 .evn 파일 추가
  5. ![image](https://github.com/user-attachments/assets/e1b3497a-fc13-473e-92fa-c82ff8ec9cc6)
  6. 확인 버튼 후, 프로젝트 실행!


