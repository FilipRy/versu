# Versu Backend

This is backend application of versu social network. It provides a set of REST endpoints. It's a spring boot application, developed in Java, utilizes MySQL 5.7 database. Swagger 2.0 has been used to document the endpoints.

## Getting Started

These instructions will guide you through the process of installation and configuration of the application. To run the application locally for testing or development purposes, see Running section.

### Prerequisites
1. Installed [Java 8](https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)
2. Installed [Maven](https://maven.apache.org/)

OR

1. Installed [Docker](https://www.docker.com/)

### Installing
#### Locally
1. Package the application into a .jar file
```
$ mvn -Dmaven.test.skip=true package
```
#### In Docker container
1. Build a docker image
```
$ docker build . -t versu_backend
```
### Configuration
Use the EXAMPLE-application-\*.properties when configuring the application (* stands for profile).  You should override the `google.apikey` property to set an api key used to access Google Geolocation API. Furthermore, you should add your `firebase.api_key` and `firebase.sender_id` to use Firebase Cloud Messaging. If you run the application in 'local' or 'test' profile you have to provide an url path to a running instance of a MySQL database. You can run the instance in docker container by executing the `start-db-docker.sh` script in `/database`. If you run the application in 'docker' profile the database instance is started automatically by `docker-compose`. See Running for further information.

P.S Don't forget to remove the EXAMPLE- prefix before running the app!

## Running the tests
1. Run an instanse of MySQL database (see Configuration Section)
2. Execute the tests
```
$ mvn clean verify -Dspring.profiles.active=test
```

## Running
### Locally
1. Run an instanse of MySQL database (see Configuration Section)
2. Execute the packaged application
```
$ java -jar target/backend-0.8.0.jar --spring.profiles.active=local
```

### In Docker container
1. Navigate to upper directory
```
$ cd ..
```
2. Run the application and its dependencies via docker-compose
```
$ docker-compose up
```

## Documentation
When the application is running open your browser and enter the following url http://localhost:8081/swagger-ui.html#/. It's a Swagger UI interface, documentating the used endpoints. All api endpoints (beside singup-controller) are secured by spring security. If you want to play around with the endpoints you have to authorize yourself:
1. Create a user account via the `/singup` endpoint of `singup-controller`.
2. Press the authorize button and scroll down to oauth2 (OAuth2, password) authorization.
3. Enter the username and password you chose at step 1). This will return your access token.
4. From now on every request, you execute via Swagger, will have your JWT access token in the request's header.
## Built With

* [MVN](https://maven.apache.org/) - Build & Dependecny Management
* [Spring Boot](https://spring.io/projects/spring-boot) - Framework
* [Java 8](https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) - Programming language
* [Docker](https://www.docker.com/) - Execution Environment

## License
Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0).
