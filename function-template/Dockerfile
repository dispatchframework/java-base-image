ARG IMAGE
FROM ${IMAGE}

ARG HANDLER
ENV HANDLER=$HANDLER

WORKDIR ${WORKDIR}

COPY . .

# Check if handler is in directory, if not (single file) create directory and move it
RUN [ -f src/main/java/$(echo ${HANDLER} | sed 's:\.:\/:g').java ] || \ 
    (mkdir -p src/main/java/$(echo "${HANDLER%.*}" | sed 's:\.:\/:g') && \
    mv "${HANDLER##*.}".java src/main/java/$(echo ${HANDLER} | sed 's:\.:\/:g').java)

# Package function server
RUN mvn package -Dmaven.test.skip=true

# Compile source files
RUN mkdir -p target/classes
RUN javac -d "target/classes" -cp "target/lib/*" src/main/java/$(echo ${HANDLER} | sed 's:\.:\/:g').java
