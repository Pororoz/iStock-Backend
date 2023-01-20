#! /bin/bash

ABS_PATH=$(readlink -f "$0")
ABS_DIR=$(dirname "$ABS_PATH")
CLOSE=$1 # 어떤 인자라도 있으면 docker-compose down

cd "$ABS_DIR"/../ || return

docker-compose -f docker-compose.test.yml up -d --build

# mysql이 켜질 때 까지 대기
while ! mysqladmin ping --host=127.0.0.1 --port=3399 --password=1234 --silent; do
  sleep 1
done

./gradlew clean build

if [ "$CLOSE" ]
then
  docker-compose -f docker-compose.test.yml down
fi