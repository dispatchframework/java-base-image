ARG IMAGE
FROM ${IMAGE}

ARG HANDLER
ENV HANDLER=$HANDLER

WORKDIR ${WORKDIR}

COPY . .

RUN mvn compile -Dmaven.test.skip=true -Dproject.build.sourceEncoding=UTF-8 -Dmaven.compiler.source=10 -Dmaven.compiler.target=10 dependency:build-classpath -Dmdep.outputFile=./cp.txt

# Create handler file if passed in otherwise extract handler info
RUN echo -n ${HANDLER} > /tmp/handler
RUN if ([ -z "${HANDLER}" ] && [ $(ls *.java | wc -l) -eq "1" ]); then \
    javac -cp .:$(<./cp.txt) -d . $(ls *.java) && \
    dir=$(ls -d */) && \
    package=$(find ${dir} -type d | tail -n 1 | sed 's:\/:\.:g') && \
    class=$(ls *.java) && \
    class="${class%.*}" && \
    mkdir -p target/classes && \
    mv ${dir} target/classes && \
    echo -n "${package}.${class}" > /tmp/handler; fi

# If single file and handler arg is passed in
RUN if ([ -n "${HANDLER}" ] && [ $(ls *.java | wc -l) -eq "1" ]); then \
    javac -cp .:$(<./cp.txt) "${HANDLER##*.}".java && \
    package=$(echo -n "${HANDLER%.*}" | sed 's:\.:\/:g') && \
    mkdir -p target/classes/${package} && \
    mv *.class target/classes/${package}; fi

# Validate function file and given handler
WORKDIR /validator

RUN mvn install dependency:build-classpath -Dmdep.outputFile=./cp.txt && \
    java -cp target/classes:${WORKDIR}/target/classes:$(<./cp.txt):$(<${WORKDIR}/cp.txt) io.dispatchframework.javabaseimage.Validator $(cat /tmp/handler) || \
    (echo "Invalid function file or handler" >&2 && exit 1)

WORKDIR ${WORKDIR}