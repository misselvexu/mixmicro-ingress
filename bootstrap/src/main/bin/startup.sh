#!/bin/bash

SERVICE_NAME=mixmicro-ingress-bootstrap
SERVICE_VERSION=1.0.0.RC1

error_exit ()
{
    echo "ERROR: $1 !!"
    exit 1
}

## check java runtime env
[[ ! -e "$JAVA_HOME/bin/java" ]] && JAVA_HOME=$HOME/jdk/java
[[ ! -e "$JAVA_HOME/bin/java" ]] && JAVA_HOME=/usr/java
[[ ! -e "$JAVA_HOME/bin/java" ]] && JAVA_HOME=/opt/taobao/java
[[ ! -e "$JAVA_HOME/bin/java" ]] && error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk8 or later is better!"

## check `SW_AGENT_COLLECTOR_BACKEND_SERVICES`

if [[ ! ${SW_AGENT_COLLECTOR_BACKEND_SERVICES} ]];then
	echo "INFO: Apache skywalking server address is not set, will be ignore ."
else
    echo $SW_AGENT_COLLECTOR_BACKEND_SERVICES
fi

## check `SERVER_ENV`
if [[ ! ${SERVER_ENV} ]];then
	echo "INFO: server running env is not set, will use default env=production ."
    export ENV="production"
else
    export ENV=${SERVER_ENV}
fi

## run mode [standalone | daemon]
export MODE="standalone"
export DEBUG="false"
while getopts ":m:e:debug:" opt
do
    case ${opt} in
        m)
            MODE=$OPTARG;;
        e)
            ENV=$OPTARG;;
        debug)
            DEBUG=$OPTARG;;
        ?)
        echo "未知参数"
        exit 1;;
    esac
done

export JAVA_HOME
export JAVA="$JAVA_HOME/bin/java"
export BASE_DIR=`cd $(dirname $0)/..; pwd`
export DEFAULT_SEARCH_LOCATIONS="classpath:/,classpath:/config/,file:./,file:./config/"
export CUSTOM_SEARCH_LOCATIONS="${DEFAULT_SEARCH_LOCATIONS},file:${BASE_DIR}/config/"
export LOG_DIR="${BASE_DIR}/logs"

#===========================================================================================
# JVM Configuration
#===========================================================================================

## check `JVM_PARAM`
if [[ ! ${JVM_PARAM} ]];then
	echo "INFO: jvm param is not assigned , will use application default config ."
	JAVA_OPT="${JAVA_OPT} -server -Xms512m -Xmx512m -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
else
    JAVA_OPT="${JAVA_OPT} -server ${JVM_PARAM} -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
fi

#JAVA_OPT="${JAVA_OPT} -server -Xms512m -Xmx512m -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"

if [[ "${DEBUG}" == "true" ]]; then
    JAVA_OPT="${JAVA_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"
fi

JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8  -XX:-UseParNewGC"
JAVA_OPT="${JAVA_OPT} -Djava.net.preferIPv4Stack=true -Dlogging.file=$LOG_DIR/$SERVICE_NAME.log -verbose:gc -Xloggc:${BASE_DIR}/logs/${SERVICE_NAME}_gc.log -XX:HeapDumpPath=$LOG_DIR/HeapDumpOnOutOfMemoryError/ -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -XX:+PrintAdaptiveSizePolicy"
JAVA_OPT="${JAVA_OPT} -Dmixmicro.server.home=${BASE_DIR}"
JAVA_OPT="${JAVA_OPT} -Druntime.env=${ENV}"
JAVA_OPT="${JAVA_OPT} -Dspring.profiles.active=${ENV}"

## check skywalking config
if [[ "${ENV}" == "production" && ${SW_AGENT_COLLECTOR_BACKEND_SERVICES} ]]; then
    # enabled monitor
    JAVA_OPT="${JAVA_OPT} -javaagent:/data/skywalking-agent/skywalking-agent.jar -Dskywalking.agent.service_name=${SERVICE_NAME} -Dskywalking.collector.backend_service=${SW_AGENT_COLLECTOR_BACKEND_SERVICES}"
fi

JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
JAVA_OPT="${JAVA_OPT} -XX:-UseLargePages"
JAVA_OPT="${JAVA_OPT} -Xbootclasspath/a:./:${BASE_DIR}/config/"
JAVA_OPT="${JAVA_OPT} -jar ${BASE_DIR}/${SERVICE_NAME}-${SERVICE_VERSION}.jar"
JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
JAVA_OPT="${JAVA_OPT} -Dspring.config.location=${CUSTOM_SEARCH_LOCATIONS}"
JAVA_OPT="${JAVA_OPT} -Dlogging.config=${BASE_DIR}/config/logback.xml"

## check `SERVER_PORT`
if [[ ! ${SERVER_PORT} ]];then
	echo "INFO: server port is not assigned , will use application default port ."
else
    JAVA_OPT="${JAVA_OPT} -Dserver.port=${SERVER_PORT} "
fi

## check log dir
if [[ ! -d "${BASE_DIR}/logs" ]]; then
  mkdir ${BASE_DIR}/logs
fi

if [[ "${MODE}" == "standalone" ]]; then
    echo "Mixmicro+ Application Server is starting"
    ${JAVA} ${JAVA_OPT}
else
    if [[ ! -f "${BASE_DIR}/logs/start.log" ]]; then
      touch "${BASE_DIR}/logs/start.log"
    fi

    echo ${JAVA} ${JAVA_OPT} > ${BASE_DIR}/logs/start.log 2>&1 &
    nohup ${JAVA} ${JAVA_OPT} > ${BASE_DIR}/logs/start.log 2>&1 &
    echo "Mixmicro+ Application Server is starting，you can check the ${BASE_DIR}/logs/start.log"
fi