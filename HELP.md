# Getting Started

### Requirements

- Java 17
- Gradle 8

### Build

```shell
gradle clean build
```

### Run
```shell
gradle bootRun
```

### Test example

``shell
curl --location 'http://localhost:8081' \
--header 'Content-Type: application/json' \
--data '{
    "companyName" : "munslow",
    "companyNumber" : "06500246"
}'
``

