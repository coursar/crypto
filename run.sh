#!/bin/bash

docker container run \
  -p 5432:5432 \
  -e POSTGRES_USER=app \
  -e POSTGRES_PASSWORD=pass \
  -e POSGRES_DB=app \
  -v "$PWD/docker-entrypoint-initdb.d":/docker-entrypoint-initdb.d:ro \
  postgres