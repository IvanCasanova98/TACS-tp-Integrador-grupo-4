FROM hseeberger/scala-sbt:8u282_1.5.3_2.12.13

WORKDIR /root/build
ADD . /root/build
COPY tacs-tp-integrador-grupo-4-assembly-0.1.jar .
CMD java -jar tacs-tp-integrador-grupo-4-assembly-0.1.jar