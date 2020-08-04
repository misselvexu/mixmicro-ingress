#!/bin/bash

nohup java -Dserver.port=4444 -Dsentinel.dashboard.auth.username=yunlsp -Dsentinel.dashboard.auth.password=VZEXF5Lro0JuIGfHrojDONc2eRkxMT8i -Dcsp.sentinel.dashboard.server=172.17.16.25:4444 -Dproject.name=ingress-dashboard -jar ingress-dashboard.jar > ingress-dashboard.log 2>&1 &