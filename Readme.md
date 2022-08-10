
# Image service

The Image service exposes two apis to download and delete the images

- /image/show/<predefined-type-name>/<dummy-seo-name>/?
  reference=<unique-original-image-filename>
- /image/flush/<predefined-image-type>/reference=<unique-original-image
  -filename>


## Requirements

For building and running the application you need:

- JDK 1.8
- Docker
- Maven 3

##Configuration

Below config needs to be passed to the application 

- ACCESS_KEY = Aws access key
- SECRET_KEY = Aws secret Key
- REGION = Bucket region
- BUCKET_NAME= Bucket name 
- SOURCE_ROOT = Root url for the image

Note: Currently the application works for classpath as source root only .

## Running the application locally

There is docker file in the repo
Currently `dev` profile has been marked active , in order to change the 
profile pass the pass while running the docker image
`"SPRING_PROFILES_ACTIVE=test"`


```shell
mvn clean install

docker build -t image-service .   

docker run -it \
-e SPRING_PROFILES_ACTIVE="test" \
-e ACCESS_KEY="" \
-e SECRET_KEY="" \
-e REGION="eu-central-1" \
-e BUCKET_NAME="" \
-e SOURCE_ROOT="classpath" \
-p 8080:8080 image-service
```
