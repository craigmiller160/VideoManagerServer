#!/bin/bash

name=$(cat package.json | grep name | sed 's/^\s*"name":\s\?"//g' | sed 's/",$//g' | sed 's/\@craigmiller160\///g')
version=$(cat package.json | grep version | sed 's/^\s*"version":\s\?"//g' | sed 's/",$//g')
registry=localhost:32000
tag=$registry/$name:$version

echo "Building $name:$version"

cd deploy
sudo docker build \
  --network=host \
  -t $tag \
  .
sudo docker push $tag

sudo microk8s kubectl apply -f configmap.yml
sudo microk8s kubectl apply -f deployment.yml
sudo microk8s kubectl rollout restart deployment $name