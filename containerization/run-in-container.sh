#!/usr/bin/env sh

# the script is to be run from the project's root.

__catalina_options="\
-Xmx1G -Duser.timezone=GMT+1 \
-Dspring.jndi.ignore=true \
-Dlogback.configurationFile=/root/logback.xml"

__container_name='mercury-tmp'
__deployable=$(ls ./target/mercury-*.war)

docker build \
    -t ${__container_name} \
    --build-arg WAR_FILE=${__deployable} \
    -f containerization/run-dockerfile . &&
docker run \
    --rm -it -p 9001:9001 \
    -e CATALINA_OPTS="${__catalina_options}" \
    -e JWT_SECRET \
    -e JWT_VALIDITY_MINUTES \
    --log-driver none \
    ${__container_name}
