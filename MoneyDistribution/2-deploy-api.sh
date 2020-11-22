#!/bin/bash

sam_build() {
  sam build -t $SAM_FILE -s ./

  if [ $? -eq 0 ]; then
    echo "=== Done:  sam_build()"
  else
    echo "=== Failed: sam_build()"
    # shellcheck disable=SC2242
    exit -1
  fi
}

sam_deploy() {
  sam deploy --guided
  if [ $? -eq 0 ]; then
    echo "=== Done:  sam_deploy()"
  else
    echo "=== Failed: sam_deploy()"
    # shellcheck disable=SC2242
    exit -1
  fi
}

main() {
  # Env
  REGION=ap-northeast-2
  BUCKET_NAME=money-distribution-aritifact-scottbae1101
  SAM_FILE=./template.yaml

  sam_build
  sam_deploy
}

main
