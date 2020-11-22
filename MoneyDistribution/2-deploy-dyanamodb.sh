#!/bin/bash

#!/bin/bash
sam_build() {
  sam build -t ${DDB_SAM_FILE} -b ./bin/ddb -s ./

  if [ $? -eq 0 ]; then
    echo "=== Done:  sam_build()"
  else
    echo "=== Failed: sam_build()"
    # shellcheck disable=SC2242
    exit -1
  fi
}

sam_deploy() {
  sam deploy --template-file bin/ddb/template.yaml --stack-name ${STACK_NAME} --s3-bucket ${BUCKET_NAME} --s3-prefix pccs-artifacts --region ${REGION} --capabilities CAPABILITY_IAM --tags '"Person In Charge"=jaeseung.bae'

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
  DDB_SAM_FILE=./sam-dynamodb.yaml
  STACK_NAME=money-distribution-ddb-stack

  sam_build
  sam_deploy
}

main
