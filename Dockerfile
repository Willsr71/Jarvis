FROM openjdk:8-alpine

WORKDIR /usr/share/jarvis

RUN mkdir config \
    && mkdir modules \
    && ln -s /usr/share/jarvis/config /config \
    && ln -s /usr/share/jarvis/modules /modules \
    && apk add --update --no-cache curl ttf-dejavu \
    && export BUILD_NUMBER=$(curl -sSLN https://noxal.net/ci/lastBuild.php?job=Jarvis) \
    && curl -sSLo jarvis.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/jarvis-1.0-$BUILD_NUMBER.jar \
    && apk del curl

VOLUME /config
VOLUME /modules

CMD ["/usr/bin/java", "-jar", "jarvis.jar"]
