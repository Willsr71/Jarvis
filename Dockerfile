FROM openjdk:8-alpine

USER root
ENV BUILD_NUMBER BUILD_NUMBER
ENV JENKINS_VERSION JENKINS_VERSION

RUN mkdir /usr/share/jarvis
WORKDIR /usr/share/jarvis

RUN mkdir config \
    && mkdir modules \
    && ln -s /usr/share/jarvis/config /config \
    && apk add --update --no-cache curl \
    && curl -sSLo jarvis.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/jarvis-$JENKINS_VERSION.jar \
    && cd modules \
    && curl -sSLo modules/admin.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/Admin-$JENKINS_VERSION.jar \
    && curl -sSLo modules/assistance.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/Assistance-$JENKINS_VERSION.jar \
    && curl -sSLo modules/chatbot.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/ChatBot-$JENKINS_VERSION.jar \
    && curl -sSLo modules/customcommands.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/CustomCommands-$JENKINS_VERSION.jar \
    && curl -sSLo modules/elections.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/Elections-$JENKINS_VERSION.jar \
    && curl -sSLo modules/flair.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/Flair-$JENKINS_VERSION.jar \
    && curl -sSLo modules/info.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/Info-$JENKINS_VERSION.jar \
    && curl -sSLo modules/levels.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/Levels-$JENKINS_VERSION.jar \
    && curl -sSLo modules/minecraft.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/Minecraft-$JENKINS_VERSION.jar \
    && curl -sSLo modules/ohno.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/OhNo-$JENKINS_VERSION.jar \
    && curl -sSLo modules/overwatch.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/Overwatch-$JENKINS_VERSION.jar \
    && apk del curl

VOLUME /config

ENTRYPOINT "java -jar jarvis.jar"