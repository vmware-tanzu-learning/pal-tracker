#!/bin/bash

UNIQUE_IDENTIFIER=$1
DOMAIN=$2

function hit_enter_key {
  echo ""
  echo ""
  read -p "     press the 'Enter' key to continue"
  clear
}

function run_command {
  clear
  echo $1..
  sleep $2
  echo ""
  echo ""
  eval "$3"
  echo ""
  echo ""
  echo "Done $1"
}

function run_and_pause_command {
  run_command "$1" "$2" "$3"
  hit_enter_key
}

function run_prod_load {
  echo "docker run -i -t --name prod-load -e DURATION=300 -e NUM_USERS=10 -e REQUESTS_PER_SECOND=5 -d -e URL=http://pal-tracker-${UNIQUE_IDENTIFIER}.${DOMAIN}  pivotaleducation/loadtest"
  echo ""
  docker run -i -t --name prod-load -e DURATION=300 -e NUM_USERS=10 -e REQUESTS_PER_SECOND=5 -d -e URL=http://pal-tracker-${UNIQUE_IDENTIFIER}.${DOMAIN}  pivotaleducation/loadtest
  echo ""
  echo ""
  echo "in a separate window, tail the load test logs by running 'docker logs prod-load -f"
}

function push_pal_tracker_v2 {
  echo "cf push pal-tracker-v2 -i 1 --no-start --no-route"
  echo ""
  cf push pal-tracker-v2 -i 1 --no-start --no-route

  echo "cf set-env pal-tracker-v2 WELCOME_MESSAGE \"Hello from the review environment v2\""
  echo ""
  cf set-env pal-tracker-v2 WELCOME_MESSAGE "Hello from the review environment v2"

  echo "cf map-route pal-tracker-v2 ${DOMAIN} --hostname pal-tracker-${UNIQUE_IDENTIFIER}-v2"
  echo ""
  cf map-route pal-tracker-v2 ${DOMAIN} --hostname pal-tracker-${UNIQUE_IDENTIFIER}-v2

  echo "cf start pal-tracker-v2"
  echo ""
  cf start pal-tracker-v2
}

function v2_smoke_test {
  echo "docker run -i -t --rm -e DURATION=30 -e NUM_USERS=1 -e REQUESTS_PER_SECOND=1 -e URL=http://pal-tracker-${UNIQUE_IDENTIFIER}-v2.${DOMAIN}  pivotaleducation/loadtest"
  echo ""
  docker run -i -t --rm -e DURATION=30 -e NUM_USERS=1 -e REQUESTS_PER_SECOND=1 -e URL=http://pal-tracker-${UNIQUE_IDENTIFIER}-v2.${DOMAIN}  pivotaleducation/loadtest
}

function start_v2_merge {
  echo "cf map-route pal-tracker-v2 ${DOMAIN} --hostname pal-tracker-${UNIQUE_IDENTIFIER}"
  echo ""
  cf map-route pal-tracker-v2 ${DOMAIN} --hostname pal-tracker-${UNIQUE_IDENTIFIER}
}

function merge_traffic_to_v2 {
  echo "cf scale pal-tracker-v2 -i 2"
  echo ""
  cf scale pal-tracker-v2 -i 2

  echo "cf scale pal-tracker -i 1"
  echo ""
  cf scale pal-tracker -i 1
}

function switch_all_traffic_to_v2 {
  echo "cf scale pal-tracker-v2 -i 3"
  echo ""
  cf scale pal-tracker-v2 -i 3

  echo "cf scale pal-tracker -i 0"
  echo ""
  cf scale pal-tracker -i 0
}

function switch_v2_to_prod {
  echo "cf delete pal-tracker -f"
  echo ""
  cf delete pal-tracker -f

  echo "cf rename pal-tracker-v2 pal-tracker"
  echo ""
  cf rename pal-tracker-v2 pal-tracker

  echo "cf delete-route ${DOMAIN} --hostname pal-tracker-${UNIQUE_IDENTIFIER}-v2 -f"
  echo ""
  cf delete-route ${DOMAIN} --hostname pal-tracker-${UNIQUE_IDENTIFIER}-v2 -f
}

function stop_prod_load {
  echo "docker rm prod-load -f"
  echo ""
  docker rm prod-load -f
}

clear
echo "Run the following command in a separate terminal window during the blue/green update: 'watch cf apps'"
hit_enter_key

run_and_pause_command "starting a load test for 5 minutes in the background to simulate production load." 0 run_prod_load
run_and_pause_command "pushing v2 application for blue/green" 5 push_pal_tracker_v2
run_and_pause_command "executing a smoke test for v2 for 30 seconds" 0 v2_smoke_test
run_and_pause_command "merging v2 traffic to production..." 5 start_v2_merge
run_and_pause_command "merging traffic from prod towards v2..." 0 merge_traffic_to_v2
run_and_pause_command "switching all traffic to v2..." 0 switch_all_traffic_to_v2
run_and_pause_command "switching v2 to the new prod." 0 switch_v2_to_prod
run_command "cleaning up load test..." 0 stop_prod_load
