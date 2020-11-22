#!/bin/bash

make_s3_bucket() {
  aws s3api create-bucket --acl private --bucket $BUCKET_NAME --region $REGION --create-bucket-configuration LocationConstraint=$REGION
  if [ $? -eq 0 ]; then
    echo "=== Done:  make_s3_bucket()"
  else
    echo "=== Failed: make_s3_bucket()"
    # shellcheck disable=SC2242
    exit -1
  fi
}

main() {
  # Env
  REGION=ap-northeast-2
  BUCKET_NAME=money-distribution-aritifact-scottbae1101

  make_s3_bucket
}

main
