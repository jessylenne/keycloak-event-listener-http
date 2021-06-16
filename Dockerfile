#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim

WORKDIR /

# Copy dependencies for the build
COPY src /home/app/src
COPY pom.xml /home/app

# Download dependencies and package the app
RUN mvn -f /home/app/pom.xml clean package

# Generate destination folder
RUN mkdir /output
RUN chmod +w /output

# We sleep for a few seconds, just enough time for the `make package-image` command
# to successfully to copy out the files from the running container.
CMD sleep 5