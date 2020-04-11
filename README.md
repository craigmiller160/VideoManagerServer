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

To run this project locally, first please set up the following system environment variables with the correct values:

```
CONFIG_SERVER_USER
CONFIG_SERVER_PASSWORD
```

Then, please use the `run.sh` script to run it. The name of the environment it is being run in (dev/qa/prod) is a requirement. For example:

`sh run.sh dev`

### Deploying to Production

Any environment in production will need the following environment variables to be set. If values are not provided, it is because they are expected to be provided by the prod environment.

```
spring.cloud.config.username=
spring.cloud.config.password=
spring.profiles.active=prod
spring.config.location=classpath:/config/common/,classpath:/config/prod/
```
