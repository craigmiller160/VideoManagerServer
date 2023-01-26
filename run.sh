#!/bin/sh

ARGS="-Dspring.config.location=classpath:/config/common/,classpath:/config/dev/ -Dspring.profiles.active=dev"

mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="$ARGS"
