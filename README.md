# VideoManagerServer

### Introduction

This is the server application for the VideoManager application.

### Related Projects

VideoManagerClient - the client application.
VideoManagerCypress - the automated testing application.

### Setup

1. Make sure Postgres is running.
2. Create the following three databases:
    1. vm_dev
    2. vm_qa
    3. vm_prod
3. Schema generation
    1. Dev & QA will have auto-generated schemas.
    2. Prod schema needs to be executed manually.
        1. Run the project for the first time (instructions below)
        2. Create and drop SQL files will be generated in the sql/ directory.

### Running Locally

Please use the `run.sh` script to run it. The name of the environment it is being run in (dev/qa/prod) is a requirement. For example:

`sh run.sh dev`

### Deploying to Production

Any environment in production will need the following environment variables to be set. If values are not provided, it is because they are expected to be provided by the prod environment.

```
spring.cloud.config.username=
spring.cloud.config.password=
spring.cloud.config.retry.max-attempts=
spring.profiles.active=prod
spring.config.location=classpath:/config/common/,classpath:/config/prod/
```

To build the production artifact and automatically copy it to the staging deploy/build directory, run `mvn clean verify`.

Lastly, we need to have directories to mount. Create `/opt/kubernetes/data/video-manager-server/homeDir`, because that's where it'll be setup to search for the video files directory. Make sure to symlink this to the actual location of the files on the host system.