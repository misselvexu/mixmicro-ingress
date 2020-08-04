#!/bin/bash

nohup java -Dserver.port=4444 -Dcsp.sentinel.dashboard.server=172.17.16.25:4444 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard.jar > sentinel-dashboard.log 2>&1 &