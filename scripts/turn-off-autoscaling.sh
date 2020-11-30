#!/bin/bash

cf disable-autoscaling pal-tracker
cf unbind-service pal-tracker pal-tracker-autoscaler
cf ds pal-tracker-autoscaler -f
cf scale pal-tracker -i 3
