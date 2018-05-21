FROM vmware/photon2:20180424

RUN tdnf install -yq tar gzip apache-maven-3.5.0-5.ph2 && curl -f https://cdn.azul.com/zulu/bin/zulu10.1+11-jdk10-linux_x64.tar.gz | tar -xzf -
ENV PATH=/zulu10.1+11-jdk10-linux_x64/bin:${PATH}

ENV PATH /var/opt/apache-maven/bin:$PATH

ARG IMAGE_TEMPLATE=/image-template
ARG FUNCTION_TEMPLATE=/function-template

LABEL io.dispatchframework.imageTemplate="${IMAGE_TEMPLATE}" \
      io.dispatchframework.functionTemplate="${FUNCTION_TEMPLATE}"

COPY image-template ${IMAGE_TEMPLATE}/
COPY function-template ${FUNCTION_TEMPLATE}/

COPY function-server /function-server/

ENV WORKDIR=/function-server PORT=8080

EXPOSE ${PORT}
WORKDIR ${WORKDIR}

RUN mvn dependency:copy-dependencies -DoutputDirectory=target/lib -DincludeScope=runtime

CMD java -cp target/classes:target/*:target/lib/* io.dispatchframework.javabaseimage.Entrypoint "${HANDLER%.*}" "${HANDLER##*.}"
