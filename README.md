# Bullish Checkout

Refer to **Bullish Technical Assessment.pdf** for the original requirements. This is a Spring Boot application that utilises Spring Security and am in-memory DB to achieve the provided requirements. 

For your convenience, you can run `run.sh` which is a helper script that can add products and deals into a running instance of the web application. 
Please note you will have to run this every time you stop & start the app since none of the data is persisted.

You can find the pre-loaded products and deals by looking at `src/main/resources/data.sql`. You can use them for your testing or you can also just use the Postman collection directly.

## Running the application

The Java version used is Java 17 and the gradle version used 7.6.1 (the bundled bin is that version)

This project uses docker and [docker-compose](https://docs.docker.com/compose/install/), so please have that installed prior to running the command. The shell script, uses the attached `gradlew` executable to create a docker image and then uses docker-compose to attach the container's port to your `localhost` 's 8080

```
./run.sh
```

## Testing the application
```
./test.sh
```

# Assumptions 

## Design choices

**Request, Responses & DTOs**: In an ideal world, we could use a OpenAPI contract and its codegen tools to create controllers and POJOs that represent the API contract. However, for this challenge, I've decided to just create Request, Response and DTO objects myself with mappers that can convert them to and from the domain object as required

**Deals**: There are several ways to structure discounts/deals (such as a flat discount or a percentage amount for a given product) and there are several ways to apply them. We have to also consider what needs to be done when a product has multiple applicable/eligible deals. There needs to be a mechanism that governs how they are applied at checkout. Refer to `dealapplicator` to understand how this has been structured

**Money/Amounts/Price**: Money here is represented using [Java Money](https://javamoney.github.io/ri.html) and a reasonable assumption is being made that the currency of choice is Hong Kong Dollar. So, all products and deals are assumed to have prices in HKD


## Authentication
Administrators and customers obviously require different mechanisms to authenticate themselves. Since no explicit requirements were given regarding authenticating admins, a simple Basic HTTP Auth is used to check if they're admin. The user name is `admin` and the password is `iamadmin` and based on [Basic Auth RFC](https://datatracker.ietf.org/doc/html/rfc7617), they need to be encoded in base64. You then need to add this as a header to your HTTP calls to authenticate yourself

```
Authorization: Basic YWRtaW46aWFtYWRtaW4=
```

Please include them in all your requests. Any **unauthenticated** requests will be treated as customer requests. Refer to either the Postman collection of requests or refer to requests such as `AddPercentageDiscountDealRequest`in `Requests` which include the Basic HTTP Auth in the headers 

## Postman Collection

You can import `Bullish Assignment.postman_collection.json` into your local Postman to get a collection of requests that can be used to interact with the system


## Areas for improvement

1) When I started the `xxxOperations` where pretty lightweight and I liked how clear it was of additional logic, but as time went on, it's become a little too meaty.
2) I have to rely on `@DirtiesContext` since the data needs to be reset and I need JPA/Hibernate to flush/reset everytime. If we're expanding this further, I think I need to break it down more to allow for fewer resets
3) The tests are huge and yes, there are pieces that are duplicated, but as mentioned in the requirements as well, I wanted the tests to be self-descriptive without having to surf through a lot of files to understand what the request/response is
4) I prefer Integration Tests over unit tests because it allows you to test the state of the entire application and provide the exact input a user of the service would provide and validate against the output they would receive. That being said, there is a lot of value in Unit Testing and if I had a little more time and room, I would've invested in that as well
5) The entities are immutable outside its own class and only updated/constructed through builder patterns - but I wonder if there's a better way to keep the internal data/state abstracted hidden away.
6) The DB setups for the integration tests are becoming cumbersome, but in order for the tests to be precise, I needed them to be deterministic and hence had to create a DB that represents a point-in-time. If the application gets bigger, it will become unwieldy as well.
7) The Spring JPA/DDL Initialisation does a little too much magic for my taste, I'd prefer the good-old flyway scripts and will choose that should we need to productionise something
8) I would've liked to harden the Docker container a little more i.e. set the correct linux user and group and then give it only the required rights and also maybe make it more multi-stage so that only the `JRE` is required at execution time
