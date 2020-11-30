#!/bin/bash

cf cs app-autoscaler standard pal-tracker-autoscaler
cf bs pal-tracker pal-tracker-autoscaler
cf update-autoscaling-limits pal-tracker 3 5
cf create-autoscaling-rule pal-tracker http_throughput 5 10
cf enable-autoscaling pal-tracker
