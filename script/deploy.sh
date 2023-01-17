#! /bin/bash

ORGANIZATION=$1
REPOSITORY=$2
ABS_PATH=$(readlink -f "$0")
ABS_DIR=$(dirname "$ABS_PATH")
source "$ABS_DIR"/profile.sh

IDLE_CONTAINER=$(find_idle_profile)

echo "> MySQL, Redis, Nginx container 실행"
docker-compose -f docker-compose.prod.yml up mysql redis nginx -d --build

# $IDLE_CONTAINER의 컨테이너 ID를 찾고, 있다면 제거
if [ "$(docker ps -aqf name="^$IDLE_CONTAINER$")" ];
then
  echo "> $IDLE_CONTAINER container 제거"
  docker-compose -f docker-compose.prod.yml rm -s -v -f "$IDLE_CONTAINER"
else
  echo "> 구동 중인 유휴 spring container가 없으므로 종료하지 않습니다."
fi

if [[ "$(docker images -q ghcr.io/"$ORGANIZATION"/"$REPOSITORY":latest 2> /dev/null)" != "" ]]; then
  echo "> latest image tag를 old로 변경"
  docker rmi ghcr.io/"$ORGANIZATION"/"$REPOSITORY":old
  docker tag ghcr.io/"$ORGANIZATION"/"$REPOSITORY":latest ghcr.io/"$ORGANIZATION"/"$REPOSITORY":old
  docker rmi ghcr.io/"$ORGANIZATION"/"$REPOSITORY":latest
fi

echo "> $IDLE_CONTAINER container 실행"
docker-compose -f docker-compose.prod.yml up "$IDLE_CONTAINER" -d --build
