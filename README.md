# VideoManagerServer

### Introduction

This is the server application for the VideoManager application.

### Auth Server Setup

1. Create a client for this app in the Auth Server UI
    1. Update the properties of this app with the client key/secret.
    1. Add the ADMIN, EDIT, and SCAN roles.

### Running Locally

The SSO OAuth Server must be running before this can run.

Please use the `run.sh` script to run it. The name of the environment it is being run in (dev/qa/prod) is a requirement. For example:

`sh run.sh dev`