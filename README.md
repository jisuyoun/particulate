# TCP 기반 미세먼지 데이터 처리 애플리케이션

## 프로젝트 설명
이 프로젝트는 TCP 통신을 통해 미세먼지 및 초미세먼지 데이터를 클라이언트에서 서버를 전송하고, 서버에서 이를 처리하는 애플리케이션입니다. TCP 소켓 통신을 이해하고, 데이터 전송 및 처리 과정을 학습하기 위해 시작되었습니다.

## 목표
- TCP 소켓 통신 이해
- 클라이언트-서버 아키텍처 구현
- CSV 파일에서 데이터를 읽어 서버로 전송
- 서버에서 데이터 처리 및 응답 생성

## 기술 스택
- Java 17
- Spring Boot 3.4
- SLF4J
- Gradle

## 설치 및 실행 방법
1. 이 저장소를 클론합니다.
```bash
https://github.com/jisuyoun/particulate.git

```
2. 필요한 라이브러리를 설치합니다.
```bash
./gradlew build
```

3. 서버를 실행시킵니다.
```bash
mvn spring-boot:run
```

4. 클라이언트를 컴파일합니다.
```bash
javac -cp target/particulate-0.0.1-SNAPSHOT.jar src/main/java/com/mypro/particulate/main/client/TcpClient.java
```

5. 클라이언트를 실행시킵니다.
```bash
java -cp target/particulate-0.0.1-SNAPSHOT.jar:src/main/java com.mypro.particulate.main.client.TcpClient
<<<<<<< HEAD
```
=======
```
>>>>>>> 4ec1f153ead69321d604461a25cf00daf10f8060
