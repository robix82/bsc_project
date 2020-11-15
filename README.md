# bsc_project
Experimental Apparatus for a Digital Health Literacy Experiment

## HSE (Health Search Engine)

HSE is the core part of the apparatus, consisting in a special purpose Lucene based search engine. HSE is implemented as a Java SpringBoot web application, using Apache Maven for dependency management and build. Javadoc describing the individual classes an methods is available under [hse_javadoc](hse_javadoc).

### Run using docker-compose

The current version can be run by issuing `docker-compose up`
from the `hse` directory.
If the images are not already installed on the system, the latest hse image as well as
the required MySql image will be downloaded from DockerHub.
The application's user interface runs on tcp port 80.

### Build with Maven

running `mvn clean install`
from the `hse` directory creates a local build intended to be used during development. Running this build requires a local MySql instance running on port 3306 and containing a database named `hse_db`.
The development version runs on tcp port 8080.

running 
  `mvn -Pprod clean install` creates the final docker image.

### Default login

In order to use the application it is required to log in.
When no administrator is configured (e.g. when the system is newly installed) a defult user with the following credentials
is available:

**user name:** admin  
**password:** admin
