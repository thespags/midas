#!/bin/bash
cd `dirname $0`/..

if [ -z "${SONATYPE_USERNAME}" ]
then
    echo "error: please set SONATYPE_USERNAME and SONATYPE_PASSWORD environment variable"
    exit 1
fi

if [ -z "${SONATYPE_PASSWORD}" ]
then
    echo "error: please set SONATYPE_PASSWORD environment variable"
    exit 1
fi

if [ ! -z "${TRAVIS_TAG}" ]
then
    echo "on a tag -> set pom.xml <version> to ${TRAVIS_TAG}"
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DgenerateBackupPoms=false -DnewVersion=${TRAVIS_TAG} 1>/dev/null 2>/dev/null
    mvn --batch-mode --settings .travis/settings.xml --update-snapshots clean deploy -DdeploySpals -DskipTests

    # Create new SNAPSHOT version by incrementing the patch
    TRAVIS_TAG_ARRAY=( ${TRAVIS_TAG//./ } )
    ((TRAVIS_TAG_ARRAY[2]++))
    NEW_SNAPSHOT_VERSION="${TRAVIS_TAG_ARRAY[0]}.${TRAVIS_TAG_ARRAY[1]}.${TRAVIS_TAG_ARRAY[2]}-SNAPSHOT"

    echo "setting new SNAPSHOT version to ${NEW_SNAPSHOT_VERSION}"
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DgenerateBackupPoms=false -DnewVersion=${NEW_SNAPSHOT_VERSION} 1>/dev/null 2>/dev/null
    git commit -a -m "[ci skip] Bump version to ${NEW_SNAPSHOT_VERSION}"
    git push origin master
else
    echo "not on a tag -> keep snapshot version in pom.xml"
    mvn --batch-mode --settings .travis/settings.xml --update-snapshots clean deploy -DdeploySpals -DskipTests
fi
