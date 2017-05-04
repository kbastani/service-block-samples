#!/bin/bash

error_exit() {
  msg="$1"
  if [ -z "$1" ]
    then
      exit 1
  else
      echo -e ""
      echo -e "\x1B[31m$msg\x1B[0m"
  	  exit 1
  fi
}

print_help() {
  echo -e "Usage: $ ./deploy-function.sh bucket_name"
}

if [ "$1" = 'help' ]
  then
    print_help
    exit 1
fi

if [ $# -eq 0 ]
  then
    echo -e "An Amazon S3 bucket name is required as an argument"
    print_help
    error_exit "Deployment failed..."
fi

if [ -z "$1" ]
  then
    echo -e "The supplied S3 bucket name is not valid"
    print_help
    error_exit "Deployment failed..."
fi

bucket_name="$1"

package() {
  # Create a CloudFormation package for this AWS Lambda function
  echo -e "Packaging deployment..."
  echo ""

  aws cloudformation package \
     --template-file package.yaml \
     --output-template-file deployment.yaml \
     --s3-bucket $bucket_name || error_exit "Packaging failed: Could not access the S3 bucket..."

     echo ""

     deploy
}

deploy() {
  # Deploy the CloudFormation package
  echo -e "Deploying package from s3://$bucket_name..."
  echo ""

  aws cloudformation deploy \
     --template-file deployment.yaml \
     --stack-name accounts || error_exit "Deployment failed..."

  # Remove the deployment package
  rm ./deployment.yaml
}

install_aws_cli() {
    apk --no-cache update && \
        apk --no-cache add python py-pip py-setuptools ca-certificates groff less && \
        pip --no-cache-dir install awscli && \
        rm -rf /var/cache/apk/*

    package
}

install_aws_cli