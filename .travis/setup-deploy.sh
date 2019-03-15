#!/bin/bash

#Pull request is a number or false
if [ "$TRAVIS_BRANCH" != 'master' ] || [ "$TRAVIS_PULL_REQUEST" != 'false' ]; then
    echo "Skipping env deployment setup for a non-release build"
    exit 0
fi

echo "Verifying environment variables"

SIGNING_VARS='SONATYPE_USERNAME SONATYPE_PASSWORD GPG_EXECUTABLE GPG_KEYNAME GPG_PASSPHRASE GPG_SECRETKEY GPG_OWNERTRUST'
for var in ${SIGNING_VARS[@]}
do
    if [ -z ${!var} ] ; then
        echo "Variable $var is not set cannot setup gpg signatures"
        exit 1
    fi
done

echo "Setting up env for deployment"
echo $GPG_SECRETKEY | base64 --decode | $GPG_EXECUTABLE --import
echo $GPG_OWNERTRUST | base64 --decode | $GPG_EXECUTABLE --import-ownertrust

echo "Configuring maven settings to sign jars and publish to sonatype"
cp ./.travis/settings.xml ${HOME}/.m2/settings.xml
echo "Maven settings setup completed"

echo "Environment setup for signing deployments"