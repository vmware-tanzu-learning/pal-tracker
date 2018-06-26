#!/usr/bin/env bash
set -e


app_guid=`cf app $1 --guid`
credentials=`cf curl /v2/apps/$app_guid/env | jq '.system_env_json.VCAP_SERVICES' | jq '.["p-mysql"][0].credentials'`

ip_address=`echo $credentials | jq -r '.hostname'`
db_name=`echo $credentials | jq -r '.name'`
db_username=`echo $credentials | jq -r '.username'`
db_password=`echo $credentials | jq -r '.password'`

echo "Opening ssh tunnel to $ip_address"
cf ssh -N -L 63306:$ip_address:3306 pal-tracker &
cf_ssh_pid=$!

echo "Waiting for tunnel"
sleep 5

flyway-*/flyway -url="jdbc:mysql://127.0.0.1:63306/$db_name" -locations=filesystem:$2/databases/tracker -user=$db_username -password=$db_password migrate

kill -STOP $cf_ssh_pid