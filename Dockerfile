FROM openjdk:11
MAINTAINER baeldung.com
COPY Peer/target/Peer-1.0-SNAPSHOT.jar peer.jar
COPY server/target/server-1.0-SNAPSHOT.jar server.jar
RUN apt-get update &&\
    apt-get upgrade -y &&\
    apt-get install tcpdump -y &&\
    apt-get install iputils-ping -y

