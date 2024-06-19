# Weather API

This code is a solution for a code test from Vanguard.

## Problem

Develop SpringBoot application and test a HTTP REST API in that fronts the OpenWeatherMap service: OpenWeatherMap name service
guide: http://openweathermap.org/current#name . (Example: http://samples.openweathermap.org/data/2.5/weather?q=London,uk)
Your service should:

1. Enforce API Key scheme. An API Key is rate limited to 5 weather reports an hour. After that your service should respond in a way which
   communicates that the hourly limit has been exceeded. Create 5 API Keys. Pick a convention for handling them that you like; using simple
   string constants is fine. This is NOT an exercise about generating and distributing API Keys. Assume that the user of your service knows
   about them.
2. Have a URL that accepts both a city name and country name. Based upon these inputs, and the API Key, your service should decide whether
   or not to call the OpenWeatherMap name service. If it does, the only weather data you need to return to the client is the description
   field from the weather JSON result. Whether it does or does not, it should respond appropriately to the client.
3. Reject requests with invalid input or missing API Keys.
4. Store the data from openweathermap.org into H2 DB.
5. The API will query the data from H2
6. Clear Spring Layers are needed.
7. Follow Rest API convention.

## Solution

This project was created using Spring Boot 3.3.

### Minimum requirements

* This project was built on Java 17.
* To run this project, please use your own custom `appid` from [API Keys](https://home.openweathermap.org/api_keys) tab on your Open
  Weather Map Account. This `appid` should be placed in [application.yml](/src/main/resources/application.yml) file.

### Build

To build this application, run:

```bash
./mvnw clean install -DskipTests
```

### Test

To run automated tests, run

```bash
./mvnw verify
```

### Run

To run this application in an IDE, run
the [WeatherApiApplication](src/main/java/com/vanguard/codetest/weatherapi/WeatherApiApplication.java) class' main() method.

To run this application from terminal, run

```bash
./mvnw spring-boot:run
```

## Future improvements

* Better concurrency handling on DB access
* API KEY Auth only for `/weather` path. Currently `/h2-console` is blocked due to this
