#!/bin/sh

ARGS="-Dspring.config.location=classpath:/config/common/,classpath:/config/$1/ -Djavax.net.ssl.trustStore=truststore.jks -Djavax.net.ssl.trustStorePassword=SdLol?I#7p=Lz#i8BqE"

case $1 in
  dev) mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="$ARGS" ;;
  qa) mvn -P qa clean spring-boot:run -Dspring-boot.run.jvmArguments="$ARGS" ;;
  prod) mvn -P prod clean spring-boot:run -Dspring-boot.run.jvmArguments="$ARGS" ;;
  *)
    echo "Invalid option: $1"
    exit 1
  ;;
esac
