# VideoManagerServer

### Introduction

This is the server application for the VideoManager application.

### Related Projects

VideoManagerClient - the client application.

### Environment Variables

VIDEO_MANAGER_DIR - The directory with videos for the dev environment. It should be set on the OS itself for development, and via Docker for production deployment.

### Setup

Make sure Postgres is running. If it has not already been done, please run the "sql/init.sql" script to set up the database before running this application.