# Posidon
This repo is the code of Posidon Project, which is an unique identifier generator.
# Steps to build and deploy this application to Docker
1. Run cd posidion-server to enter the root folder of the posidon-server project
2. Run mvn package spring-boot:repackage
3. Run docker build -t posidon/posidon-server to create docker image
4. Run docker run -dp 8000:8000 posidon-server to start the container with the image create in step 2.
5. Run curl http://localhost:8000/posidon/hello to test whether the application starts successfully.
6. Run http://localhost:8000/posidon/id?serviceName=cloudlicense to see if the id was generated successsfully.
