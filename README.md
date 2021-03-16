# Elide Spring Boot Example

An archetype Elide project using Spring Boot.

## Background

This project is the sample code for [Elide's Getting Started documentation](https://elide.io/pages/guide/01-start.html).

## Install

To build and run:

1. mvn clean install
2. java -jar target/elide-spring-boot-1.0.jar
3. Browse http://localhost:8080/

To run from Heroku:

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/yahoo/elide-spring-boot-example)

## Docker and Containerize

To containerize and run elide project locally

1. Build Docker Image from Root of the project 
   docker build -t elide/elide-spring-boot-example .
2. Run docker container locally
   docker run -p 8081:8080 elide/elide-spring-boot-example
   https://localhost:8081/
   
# Run from cloud (AWS)


1. ECR 
   i. Create a repository in Elastic Container Registry with name elide-spring-boot-example
   ii. aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin xxx.dkr.ecr.us-east-1.amazonaws.com 
   iii. docker tag elide-spring-boot-example:latest xxx.dkr.ecr.us-east-1.amazonaws.com/elide-spring-boot-example:latest
   iv. docker push xxx.dkr.ecr.us-east-1.amazonaws.com/elide-spring-boot-example
2. Fargate
   i. With the image on AWS ECR, you can now define your deployment on AWS ECS/Fargate.
   ii. Make sure to mention ECR details
   iii. Port mapping is needed in the task definition. If you open the json definition of the task, you can edit to add this.
   "portMappings": [
         {
         "hostPort": 8080,
         "protocol": "tcp",
         "containerPort": 8080
         }
      ],
    iv. The security group created or used for the task , needs to have inbound rule for TCP custom port 8080 , CIDR block 0.0.0.0/0(open to everyone, or you can add your company's CIDR block if its an internal service)
   



## Usage

See [Elide's Getting Started documentation](https://elide.io/pages/guide/01-start.html).

## Contribute
Please refer to [the contributing.md file](CONTRIBUTING.md) for information about how to get involved. We welcome issues, questions, and pull requests.

## License
This project is licensed under the terms of the [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) open source license.
Please refer to [LICENSE](LICENSE.txt) for the full terms.
