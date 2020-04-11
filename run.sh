#!/bin/sh

ARGS="-Dspring.config.location=classpath:/config/common/,classpath:/config/$1/"

case $1 in
  dev) mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="$ARGS" ;;
  qa) mvn -P qa clean spring-boot:run -Dspring-boot.run.jvmArguments="$ARGS" ;;
  prod) mvn -P prod clean spring-boot:run -Dspring-boot.run.jvmArguments="$ARGS" ;;
  *)
    echo "Invalid option: $1"
    exit 1
  ;;
esac
