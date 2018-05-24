ARG IMAGE
FROM ${IMAGE}

ARG HANDLER
ENV HANDLER=$HANDLER

WORKDIR ${WORKDIR}

COPY . .

RUN mvn compile -Dmaven.test.skip=true -Dproject.build.sourceEncoding=UTF-8 -Dmaven.compiler.source=10 -Dmaven.compiler.target=10 dependency:build-classpath -Dmdep.outputFile=./cp.txt
