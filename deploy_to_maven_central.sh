#!/bin/bash
# Deploy swookiee to Maven Central staging. GPG and ./m2/settings.xml setup required.

read -p "Really deploy to maven cetral repository (yes/NO)? " -r REPLY

if ( [ "$REPLY" == "yes" ] ) then
    read -p "Version Number (0.0.42) " -r REPLY
    mvn -PsetVersion -DnewVersion=$REPLY initialize
    mvn clean deploy -PaddJavadocSourcesAndSign
    STATUS=$?
    if [ $STATUS -eq 0 ]; then
        echo "Pushing Tag to github"
        git tag v$REPLY
        git push --tags
    else
        echo "#################"
        echo "## BUILD ERROR ##"
        echo "#################"
    fi
    git reset --hard HEAD~2
else
    echo 'No yes, No deploy... :)'
fi
