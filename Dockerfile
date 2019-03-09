FROM openjdk:8-alpine

WORKDIR /usr/share/jarvis

RUN mkdir config \
    && mkdir modules \
    && ln -s /usr/share/jarvis/config /config \
    && apk add --update --no-cache curl ttf-dejavu \
    && export BUILD_NUMBER=$(curl -sSLN https://noxal.net/ci/lastBuild.php?job=Jarvis) \
    && curl -sSLo jarvis.jar https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/build/libs/jarvis-1.0-$BUILD_NUMBER.jar \
    && cd modules \
    && curl -sSLO https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/modules/admin/build/libs/Admin-1.0-$BUILD_NUMBER.jar \
    && curl -sSLO https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/modules/assistance/build/libs/Assistance-1.0-$BUILD_NUMBER.jar \
    && curl -sSLO https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/modules/chatbot/build/libs/ChatBot-1.0-$BUILD_NUMBER.jar \
    && curl -sSLO https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/modules/customcommands/build/libs/CustomCommands-1.0-$BUILD_NUMBER.jar \
    && curl -sSLO https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/modules/elections/build/libs/Elections-1.0-$BUILD_NUMBER.jar \
    && curl -sSLO https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/modules/flair/build/libs/Flair-1.0-$BUILD_NUMBER.jar \
    && curl -sSLO https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/modules/info/build/libs/Info-1.0-$BUILD_NUMBER.jar \
    && curl -sSLO https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/modules/levels/build/libs/Levels-1.0-$BUILD_NUMBER.jar \
    && curl -sSLO https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/modules/minecraft/build/libs/Minecraft-1.0-$BUILD_NUMBER.jar \
    && curl -sSLO https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/modules/ohno/build/libs/OhNo-1.0-$BUILD_NUMBER.jar \
    && curl -sSLO https://ci.noxal.net/job/Jarvis/$BUILD_NUMBER/artifact/modules/overwatch/build/libs/Overwatch-1.0-$BUILD_NUMBER.jar \
    && export VEXBOT_BUILD_NUMBER=$(curl -sSLN https://noxal.net/ci/lastBuild.php?job=VexBot) \
    && curl -sSLO https://ci.noxal.net/job/VexBot/$VEXBOT_BUILD_NUMBER/artifact/build/libs/vexbot-1.0-$VEXBOT_BUILD_NUMBER.jar \
    && apk del curl

VOLUME /config
VOLUME /modules

CMD ["/usr/bin/java", "-jar", "jarvis.jar"]