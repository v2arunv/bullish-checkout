# Checkout System

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


## Authentication
Administrators and customers obviously require different mechanisms to authenticate themselves. Since no explicit requirements were given regarding authenticating admins, a simple Basic HTTP Auth is used to check if they're admin. The user name is `admin` and the password is `iamadmin` and based on [Basic Auth RFC](https://datatracker.ietf.org/doc/html/rfc7617), they need to be encoded in base64. You then need to add this as a header to your HTTP calls to authenticate yourself

```
Authorization: Basic YWRtaW46aWFtYWRtaW4=
```

Please include them in all your requests. Any **unauthenticated** requests will be treated as customer requests

## TBD

<TBD>
