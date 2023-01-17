#! /bin/bash

ABS_PATH=$(readlink -f "$0")
ABS_DIR=$(dirname "$ABS_PATH")
source "$ABS_DIR"/profile.sh

function switch_proxy(){
  IDLE_CONTAINER=$(find_idle_profile)

  echo "> 전환할 컨테이너: $IDLE_CONTAINER"
  echo "set \$service_url http://$IDLE_CONTAINER:8080;" | sudo tee ~/nginx/conf.d/service-url.inc

  echo "Nginx container restart"
  docker restart nginx
}