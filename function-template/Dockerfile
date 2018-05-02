ARG IMAGE
FROM ${IMAGE}

ARG FUNCTION_SRC=function.txt

COPY ${FUNCTION_SRC} .

# Package function server
RUN mvn package

# Package name
RUN while read -r _ a _; do echo "$a" ; done <<< $(cat ${FUNCTION_SRC} | grep "^[ $(printf '\t')]*package" | head -1) | sed s'/;*$//' > /tmp/package

# Class name
RUN while read -r _ _ a _; do echo "$a" ; done <<< $(cat ${FUNCTION_SRC} | sed -e "s/static//" | grep "^[ $(printf '\t')]*public[ $(printf '\t')]*class" | head -1) > /tmp/class

# Copy source file
RUN mkdir -p src/main/java/$(cat /tmp/package | sed 's:\.:\/:g')
RUN mv ${FUNCTION_SRC} src/main/java/$(cat /tmp/package | sed 's:\.:\/:g')/$(cat /tmp/class).java

# Compile source files
RUN mkdir -p target/classes
RUN javac -d "target/classes" -cp "target/lib/*" src/main/java/$(cat /tmp/package | sed 's:\.:\/:g')/$(cat /tmp/class).java
