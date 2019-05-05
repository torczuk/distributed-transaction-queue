# Distributed transaction - Saga

## Problem
TODO


## Big picture
TODO


## Testing

Solution contains different types of tests. Unit, integration and system tests. Last two categories of tests use kafka, kafka + zookeeper, environment inside docker.
Tests for `booking`, `order` and `payment` components are starting docker-compose before execution. Please take a look at the each `docker-compose.yml` in related module.

`system-tests` module is dedicated to verify *saga* against topology of example.
It means, kafka, zookeeper and all three components are started together.
Sample test looks like below.

#### all components are up and running
```
//given all components running

//when transaction is started
POST /api/v1/bookins/{transaction-id}

//then
wait until GET /api/v1/bookins/{transaction-id} contains confimend booking
```


#### one component is unavailable
```
//given all components running except payment

//when transaction is started
POST /api/v1/bookins/{transaction-id}

// and when payment is startig recovery
...

//then
wait until GET /api/v1/bookins/{transaction-id} contains confimend booking
```

#### payment failed - transaction must be rollbacked
```
//given all components running except payment

//when transaction is started
POST /api/v1/bookins/{transaction-id}

// and when payment has failed
...

//then
wait until GET /api/v1/bookins/{transaction-id} contains cancelled booking
```


#### Test all together
```
./gradlew clean \
         :booking:build \
         :order:build \
         :payment:build \
         systemTest --info
```

or one by one

##### Booking
```
./gradlew clean :booking:build --info
```

##### Order
```
./gradlew clean :order:build --info
```

##### Payment
```
./gradlew clean :payment:build --info
```

#### System test

All components must be build before running this comand
```
./gradlew clean :booking:build systemTest --info
```


## Trade offs
* Ports during tests are not randomized