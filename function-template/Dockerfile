ARG IMAGE
FROM ${IMAGE}

ARG HANDLER
ENV HANDLER=$HANDLER

RUN ([ -n "${HANDLER}" ] || (echo "HANDLER is required for Java" >&2 && exit 1))

WORKDIR ${WORKDIR}

COPY . .

RUN mvn compile -Dmaven.test.skip=true -Dproject.build.sourceEncoding=UTF-8 -Dmaven.compiler.source=10 -Dmaven.compiler.target=10 dependency:build-classpath -Dmdep.outputFile=./cp.txt

# Validate function file and given handler
WORKDIR /validator

RUN mvn install dependency:build-classpath -Dmdep.outputFile=./cp.txt && \
    java -cp target/classes:${WORKDIR}/target/classes:$(<./cp.txt):$(<${WORKDIR}/cp.txt) io.dispatchframework.javabaseimage.Validator "${HANDLER}" || \
    (echo "Invalid function file or handler" >&2 && exit 1)

WORKDIR ${WORKDIR}