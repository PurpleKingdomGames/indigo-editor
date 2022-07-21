#!/bin/bash

set -e

PROJECT_ROOT=$(pwd);

sbt editor/fastLinkJS

cd editor
npm run build

cd app
npm run start

cd "$PROJECT_ROOT"

