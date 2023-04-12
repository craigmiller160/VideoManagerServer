# VideoManagerServer

### Introduction

This is the server application for the VideoManager application.

## Terraform

For the Terraform script to run, the following environment variables must be present on the machine.

```
# The operator access token for communicating with 1Password
ONEPASSWORD_TOKEN=XXXXXXX
```

In addition to the `$ONEPASSWORD_TOKEN` needing to be available as an OS environment variable, this application expects that the `video-manager-converter` application is already configured in Keycloak via terraform. This allows for the composite role to be put together.

### Running Locally

The SSO OAuth Server must be running before this can run.

Please use the `run.sh` script to run it.

`sh run.sh`