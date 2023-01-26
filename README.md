# VideoManagerServer

### Introduction

This is the server application for the VideoManager application.

## Terraform

For the Terraform script to run, the following environment variables must be present on the machine.

```
# The operator access token for communicating with 1Password
ONEPASSWORD_TOKEN=XXXXXXX
```

### Running Locally

The SSO OAuth Server must be running before this can run.

Please use the `run.sh` script to run it. The name of the environment it is being run in (dev/qa/prod) is a requirement. For example:

`sh run.sh dev`