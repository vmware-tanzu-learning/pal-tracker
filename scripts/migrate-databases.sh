#!/usr/bin/env bash

set -e

app_name="${1:-pal-tracker}"
script_dir="${2:-.}"
service_name="${3:-tracker-database}"
service_key="${4:-flyway-migration-key}"

function main() {

    echo "Creating service key, if necessary ..."

    cf create-service-key $service_name $service_key > /dev/null

    echo "Retrieving target database parameters ..."

    credentials=$(cf service-key $service_name $service_key | sed -ne '/{/,$p')

    db_host=$(echo $credentials | jq -r '.hostname')
    db_name=$(echo $credentials | jq -r '.name')
    db_username=$(echo $credentials | jq -r '.username')
    db_password=$(echo $credentials | jq -r '.password')
    db_port=$(echo $credentials | jq -r '.port')

    test -n "$db_host" || exit 1

    echo "Opening ssh tunnel to $db_host:$db_port ..."

    cf ssh -N -L 63306:$db_host:$db_port $app_name &
    cf_ssh_pid=$!

    echo "Waiting for tunnel ..."

    wait_for_tunnel

    echo "Running migration ..."

    flyway-*/flyway -url="jdbc:mysql://127.0.0.1:63306/$db_name" \
        -locations=filesystem:"$script_dir"/databases/tracker \
        -user="$db_username" \
        -password="$db_password" \
        migrate

    echo "Migration complete"

}

function pre_tunnel_exit() {
    echo "ERROR: No suitable credentials found for application '$app_name' and service '$service_name'" >&2
}

trap pre_tunnel_exit EXIT

function close_tunnel_at_exit() {
    echo "Closing tunnel"
    kill -KILL $cf_ssh_pid
}

trap close_tunnel_at_exit EXIT


function wait_for_tunnel() {

    set +e

    for x in {1..10}; do
        $(nc -z localhost 63306)
        nc_status=$?
        if [[ "$nc_status" == "0" ]]; then
            echo "Ssh tunnel success."
            break
        else
            echo "Ssh tunnel not detected yet..."
            sleep 3
        fi
    done

    if [[ "$nc_status" != "0" ]] ; then
        echo "Failed to detect ssh tunnel. Exiting."
        exit 1
    fi

    set -e
}

main
