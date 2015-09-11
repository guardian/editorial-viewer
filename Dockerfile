## Generic docker file for running Scala apps via SBT

FROM java:openjdk-8

# Install sbt
ENV SBT_VERSION 0.13.8
RUN apt-get update && apt-get install -y curl && \
    curl -L http://dl.bintray.com/sbt/debian/sbt-${SBT_VERSION}.deb > /tmp/sbt-${SBT_VERSION}.deb && \
    dpkg -i /tmp/sbt-${SBT_VERSION}.deb

# Install nodejs 0.10.x nodesource repo
RUN curl -sL https://deb.nodesource.com/setup_0.10 | bash -

# Install nodejs and other tools
RUN apt-get -y update && apt-get install -y --no-install-recommends build-essential nodejs git-core

EXPOSE 9000

RUN mkdir /opt/app

# Add project files so dependencies can be cached between builds
ADD build.sbt /opt/app/
ADD project /opt/app/project/

WORKDIR /opt/app

RUN sbt compile


# Build the app
ADD . /opt/app/
RUN npm install
RUN sbt stage

CMD ["/opt/app/target/universal/stage/bin/viewer"]
