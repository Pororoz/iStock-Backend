#! /bin/bash

ABS_PATH=$(readlink -f "$0")
ABS_DIR=$(dirname "$ABS_PATH")
source "$ABS_DIR"/profile.sh

function switch_proxy(){
  IDLE_CONTAINER=$(find_idle_profile)

  echo "> 전환할 컨테이너: $IDLE_CONTAINER"
  docker exec nginx /bin/sh -c "echo set '\$service_url http://$IDLE_CONTAINER:8080;' | tee /etc/nginx/conf.d/service-url.inc"

  echo "Nginx container restart"
  docker exec -i nginx nginx -s reload

  for RETRY_COUNT in $(seq 1 5)
  do
    ACTIVE_CONTAINER=$(find_idle_profile)
    if [ "$IDLE_CONTAINER" != "$ACTIVE_CONTAINER" ]
    then
      break
    fi
    echo "Switch delayed"
    sleep 0.3
    if [ "$RETRY_COUNT" -eq 5 ]
    then
      echo "Nginx 전환에 실패했습니다."
      exit 1
    fi
  done
}