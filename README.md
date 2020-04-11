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
3. Dev & QA will have auto-generated schemas initially.
4. Prod schema needs to be executed manually.
    1. Run the project for the first time with: `mvn spring-boot:run`
    2. Create and drop SQL files will be generated in the sql/ directory.
        1. Be warned that the default behavior is to append to existing files, so if you want a complete, non-repetitive schema you'll need to delete the files before running.
        2. Alternatively, mvn clean will delete them for you.
        
TODO
ADD REFERENCE TO ENVIRONMENT VARIABLES FOR DEV,QA,PROD
CONFIG_SERVER_USER
CONFIG_SERVER_PASSWORD
