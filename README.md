# auth-server
인증 담당 서버

<br/>
<br/>
<br/>

> ## Redis, Redis-cli with Docker

로컬에서 도커로 redis, redis-cli 띄워두고 사용하세요

### docker 명령어
```
// redis, redis-cli 가 사용할 bridge 네트워크
docker network create redis-net

// redis 컨테이너 구동
docker run -d -p 6379:6379 --network redis-net --name my-redis redis

// redis 컨테이너와 소통홀 redis-cli 컨테이너 구동
docker run -it --network redis-net --link my-redis:redis --rm redis redis-cli -h redis -p 6379
```
<br/>

### redis-cli 명령어
auth-server에서는 Redis 이용시 key-value 형태의 String type collection만 사용중입니다.
```
// 모든 key 조회 (모든 키를 조회하므로 운영환경에서 사용시 성능부하가 걸릴 수 있음)
keys *

// 특정 key의 value 조회
get 키이름

// String key-value 데이터 삽입
set 키이름 값

// 수명 조회 (자동 삭제까지 남은시간)
ttl 키이름
```
