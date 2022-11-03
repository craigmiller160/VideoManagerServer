#!/bin/sh

ARGS="-Dspring.config.location=classpath:/config/common/,classpath:/config/$1/ -Dspring.profiles.active=$1"

case $1 in
  dev|qa|prod) mvnd clean spring-boot:run -Dspring-boot.run.jvmArguments="$ARGS" ;;
  *)
    echo "Invalid option: $1"
    exit 1
  ;;
esac
