# Bullish Checkout

Refer to **Bullish Technical Assessment.pdf** for the original requirements. This is a Spring Boot application that utilises Spring Security and in-memory DB to achieve the provided requirements. 

For your convinience, you can run `setup.sh` which is a helper script that can add products and deals into a running instance of the web application. Please note you will have to run this every time you stop & start the app since none of the data is persisted.



## Running the application
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



## Authentication
Administrators and customers obviously require different mechanisms to authenticate themselves. Since no explicit requirements were given regarding authenticating admins, a simple Basic HTTP Auth is used to check if they're admin. The user name is `admin` and the password is `iamadmin` and based on [Basic Auth RFC](https://datatracker.ietf.org/doc/html/rfc7617), they need to be encoded in base64. You then need to add this as a header to your HTTP calls to authenticate yourself

```
Authorization: Basic YWRtaW46aWFtYWRtaW4=
```

Please include them in all your requests. Any **unauthenticated** requests will be treated as customer requests

## TBD

<TBD>
