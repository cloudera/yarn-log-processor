#!/bin/bash
POSITIONAL=()
while [[ $# -gt 0 ]]; do
key="$1"
LOG4J_CONFIG_DIR="src/main/resources/"
LOG4J_CONFIG_FILE="log4j.properties"
case $key in
    -b|--build)
    BUILD=YES
    shift # past argument
    ;;
    -d|--dev)
    LOG4J_CONFIG_FILE="log4j.dev.properties"
    shift
    ;;
    *)    # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift # past argument
    ;;
esac
done
set -- "${POSITIONAL[@]}" # restore positional parameters
if [ ! -d "target" ] || [ $BUILD ]; then
  echo "Building project..."
  mvn clean package -DskipTests
else
  echo "The --build option is not provided and the target/ directory exists - skipping build"
fi
echo "Starting the tool"
java -Dlog4j.configuration=file:"$LOG4J_CONFIG_DIR""$LOG4J_CONFIG_FILE" -jar target/bundle-log-processor-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"