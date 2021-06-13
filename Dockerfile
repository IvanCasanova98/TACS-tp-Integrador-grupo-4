FROM hseeberger/scala-sbt:8u282_1.5.3_2.12.13

WORKDIR /root/build
ADD . /root/build

CMD sbt compile run