#!/bin/bash
# Deploy swookiee to Maven Central staging. GPG and ./m2/settings.xml setup required.

read -p "Really deploy to maven cetral repository (yes/NO)? " -r REPLY

if ( [ "$REPLY" == "yes" ] ) then
    read -p "Version Number (0.0.42) " -r REPLY
    mvn -PsetVersion -DnewVersion=$REPLY initialize
    mvn clean deploy -PaddJavadocSourcesAndSign
    git tag v$REPLY
    git push --tags
    git reset --hard HEAD~2
else
    echo 'No yes, No deploy... :)'
fi
