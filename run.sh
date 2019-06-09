#!/bin/bash

bash docker/setup.sh
sudo -E docker-compose -f docker/docker-compose.yml $@