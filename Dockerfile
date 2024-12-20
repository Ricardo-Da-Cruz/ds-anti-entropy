FROM openjdk:11
MAINTAINER baeldung.com
COPY /target/ds-entropy.jar peer.jar
COPY words.txt words.txt
RUN apt-get update &&\
    apt-get upgrade -y &&\
    apt-get install tcpdump -y &&\
    apt-get install iputils-ping -y