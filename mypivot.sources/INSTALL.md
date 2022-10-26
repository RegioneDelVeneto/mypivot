# MyPivot install guide

## Prerequisites

The following software components should be available:
- ActiveMQ Artemis
- Redis
- PostgreSQL database
- Java JDK v11
- Node.js v12 (LTS)
- any GIT client

The installation and configuration of such components is out of the scope of this document.

The workstation must have internet access (to allow the download of necessary dependencies during build process).

## Reference installation procedure

The installation procedure described in this document includes the basic steps to have the application up and running, typically for development purposes, on a single workstation; and should therefore considered just for reference.
In real-life scenarios it is strongly suggested the adoption of a container-based setup, to leverage the benefits of such architecures in terms of:

- deploy management
- scalability
- fault-tolerance
- high-availability

The installation procedure on container-based architecture is out of the scope of this document.

In the following paragraphs the standard linux shell syntax is used.

## Source code download

1. open a command-line shell
2. download source from GIT repository:
`git clone <MyPivot repo url>`

## Database module

MyPivot requires 1 database:

- `mypivot` database

The database structure and reference data could be created executing the following scripts inside source repository:

*__TO-DO__*

## Back-end module

### Build

1. open a command-line shell on folder where the source code repo has been cloned
2. go to back-end module root folder:
`cd mypivot4-be`
3. launch the build command (using embedded Gradle):
`./gradlew clean build`
4. in case of successful build, the binary artifact will be available at path:
`build/libs/mypivot4-be-<VERSION>.jar`

### Run

1. perform the above mentioned build procedure
2. go to folder containing the binary artifact:
`cd build/libs`
3. [optional] override the default application configuration parameters (file `mypivot4-be/src/main/resources/application.properties`) creating a file named `application.properties` in the same folder containg the binary `JAR` file
4. launch the Spring Boot application with command:
`java –cp mypivot4-be-<VERSION>.jar org.springframework.boot.loader.PropertiesLauncher`
5. verify on application log that the application starts correctly

In case of launching a (SpringBoot based) _batch_ process instead of the web application (default), the launch command shall include the main class of the _batch_ process. For instance, in case of the load-flussi-multibeneficiario-export _batch_ process, the launch command is:
`java –cp mypivot4-be-<VERSION>.jar -Dloader.main=it.regioneveneto.mygov.payment.mypivot4.scheduled.loadflussimultibeneficiarioexport.LoadFlussiMultibeneficiarioExportApplication org.springframework.boot.loader.PropertiesLauncher`

### Notes

- in case of Talend or Java7 based _batch_ processes, the build and launch process described in this document doesn't apply. Please consult the documentation inside `mypivot4-batch` folder for further details

## Front-end module

The front-end module of MyPivot consists of an Angular single page web application, aimed to PA officers

### Build

1. open a command-line shell on folder where the source code repo has been cloned
2. go to front-end module root folder:
`cd mypivot4-fe`
3. launch the command to initialize/update the build dependencies:
`npm install`
4. launch the build command:
`npm run build -- mypivot4-fe --base-href <context-root> --output-path <output-path> --configuration production`
	where:
  - the snippet `--configuration production` is optional and enable a series of optimization useful for production deployments
  - the `<context-root>` value should be set accordingly with the value used in configuration of back-end module
  - the `<output-path>` determine the folder where the deployable artifact are stored

### Run

1. perform the steps 1-3 of above build procedure
2. start (using ng internal web server) front-end:
`ng serve mypivot4-fe --base-href <context-root>`

## Talend and Java7 batch processes

*__TO-DO__*
