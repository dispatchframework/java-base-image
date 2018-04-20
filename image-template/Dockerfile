ARG BASE_IMAGE
FROM ${BASE_IMAGE}

ARG SYSTEM_PACKAGES_FILE=system-packages.txt
COPY ${SYSTEM_PACKAGES_FILE} .

RUN packages=$(cat ${SYSTEM_PACKAGES_FILE} | sed 's/ /-/' | uniq | paste -d' ' -); \
    [ -n "${packages}" ] && tdnf install -y ${packages} || :

ARG PACKAGES_FILE=packages.txt
COPY ${PACKAGES_FILE} pom2.xml

RUN pj=$(cat pom2.xml); [ -n "${pj}" ] && mvn -f pom2.xml dependency:copy-dependencies -DoutputDirectory=target/lib || :