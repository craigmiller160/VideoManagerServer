#!/bin/bash

raw_name=$(cat pom.xml | grep artifactId | head -n2 | tail -n1)
name=$(echo $raw_name | sed 's/^\s*<artifactId>//g' | sed 's/<\/artifactId>$//g')
raw_version=$(cat pom.xml | grep version | head -n3 | tail -n1)
version=$(echo $raw_version | sed 's/^\s*<version>//g' | sed 's/<\/version>$//g')
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