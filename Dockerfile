FROM vmware/photon2:20180302

RUN tdnf install -y openjdk8-1.8.0.152-1.ph2 apache-maven-3.5.0-5.ph2

ENV PATH /var/opt/apache-maven/bin:$PATH

ARG IMAGE_TEMPLATE=/image-template
ARG FUNCTION_TEMPLATE=/function-template

LABEL io.dispatchframework.imageTemplate="${IMAGE_TEMPLATE}" \
      io.dispatchframework.functionTemplate="${FUNCTION_TEMPLATE}"

COPY image-template ${IMAGE_TEMPLATE}/
COPY function-template ${FUNCTION_TEMPLATE}/

ENV PORT=8080
EXPOSE ${PORT}

COPY function-server /function-server/

WORKDIR /function-server
RUN mvn dependency:copy-dependencies -DoutputDirectory=target/lib -DincludeScope=runtime

CMD java -cp target/classes:target/*:target/lib/* io.dispatchframework.javabaseimage.Entrypoint $(cat /tmp/package) $(cat /tmp/class)
