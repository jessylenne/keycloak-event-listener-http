
IMAGE=jessylenne/keycloak-event-listener-http
VERSION=latest
CONTAINER_NAME=keycloak-listener-war-builder
JAR_ONE=event-listener-http-jar-with-dependencies.jar
JAR_TWO=event-listener-http.jar

package-image:
# Build the docker image that we'll use to constrct the maven package
	docker build . -t "${IMAGE}:${VERSION}"
# Build the maven package, then save the resulting files into `mvn-output`
	docker run -d --rm --name ${CONTAINER_NAME} -v ${PWD}/mvn-output:/output ${IMAGE}:${VERSION}
	mkdir -p mvn-output
# Ensure that you have proper write permissions to write on the `mvn-output` folder.
# See: https://stackoverflow.com/a/45276559/1323398
	docker cp ${CONTAINER_NAME}:/home/app/target/event-listener-http-jar-with-dependencies.jar ${PWD}/mvn-output/event-listener-http-jar-with-dependencies.jar
	docker cp ${CONTAINER_NAME}:/home/app/target/event-listener-http.jar ${PWD}/mvn-output/event-listener-http.jar