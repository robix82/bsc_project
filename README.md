# bsc_project
Experimental Apparatus for a Digital Health Literacy Experiment

## HSE (Health Search Engine)

HSE is the core part of the apparatus, consisting in a special purpose Lucene based search engine. HSE is implemented as a Java SpringBoot web application, using Apache Maven for dependency management and build. Javadoc describing the individual classes is available under hse/doc.

### Run using docker-compose

The current version can be run by issuing
    docker-compose up
from the `hse` directory.

### Build with Maven

running
  mvn clean install  
from the `hse` creates a local build intended to be used during development. Running this build requires a local MySql instance running on port 3306 and containing a database named `hse_db`.

running
  `mvn -Pprod clean install`  
creates the final docker image.


