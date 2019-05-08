# Distributed transaction - Saga

## Problem
TODO


## Big picture

![successful](ihttps://raw.githubusercontent.com/torczuk/distributed-transaction-saga/master/img/successful_saga.png | width=100)

## Testing

Solution contains different types of tests. Unit, integration and system tests. Last two categories of tests use kafka infrastructure inside docker.
Tests for `booking`, `order` and `payment` components are starting docker-compose before execution. Please take a look at the each `docker-compose.yml` in related module for more details.

`system-tests` module is dedicated to verify *saga* implementation against topology of components.
It means, kafka, zookeeper and all three components are started together.
Sample test looks like below.

#### all components are up and running
```
//given all components are up & running

//when transaction is started
POST /api/v1/bookins/{transaction-id}

//then
eventually after a quantum of time GET /api/v1/bookins/{transaction-id} will contain confimend booking
```


#### one component is unavailable
```
//given all components running except payment

//when transaction is started
POST /api/v1/bookins/{transaction-id}

// and when payment is startig recovery
...

//then
eventually after a quantum of time GET /api/v1/bookins/{transaction-id} will contain confimend booking
```

#### payment failed - transaction must be rollbacked
```
//given all components running except payment

//when transaction is started
POST /api/v1/bookins/{transaction-id}

// and when payment has failed
...

//then
eventually after a quantum of time GET /api/v1/bookins/{transaction-id} will contain cancelled booking
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
