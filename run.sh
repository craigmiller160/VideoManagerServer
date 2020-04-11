#!/bin/sh

ARGS="-Dspring.config.location=classpath:/config/common/,classpath:/config/$1"

case $1 in
  dev) mvn clean spring-boot:run -Dspring-boot.run.arguments="$ARGS" ;;
  qa) mvn -P qa clean spring-boot:run -Dspring-boot.run.arguments="$ARGS" ;;
  prod) mvn -P prod clean spring-boot:run -Dspring-boot.run.arguments="$ARGS" ;;
  *)
    echo "Invalid option: $1"
    exit 1
  ;;
esac
