FROM hseeberger/scala-sbt
WORKDIR /Phase10
ADD . /Phase10
CMD sbt run
