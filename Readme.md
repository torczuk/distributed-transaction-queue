# Distributed transaction - Saga

## Problem
How would you model distributed transaction where steps (transactions) are sequential, and current state depends on before.
e.g. you are buying tickets to cinema. If you select places and next during payment you are run ot of money, booked sits should
return to the common pool and be available. Bossiness logic responsible for booking sits is handled by different process than payment.
They are deployed on different hosts, possibly different availability zones.
Saga for the rescue: [link](http://www.cs.cornell.edu/andru/cs711/2002fa/reading/sagas.pdf)

## Big picture

![successful](https://raw.githubusercontent.com/torczuk/distributed-transaction-saga/master/img/successful_saga.png)

## Testing

Solution contains different types of tests. Unit, integration and system tests. Last two categories of tests use kafka infrastructure inside docker.
Tests for `booking`, `order` and `payment` components are starting docker-compose before execution. Please take a look at the each `docker-compose.yml` in related module for more details.

`system-tests` module is dedicated to verify *saga* implementation against topology of components.
It means, kafka, zookeeper and all three components are started together.
Sample test looks like below.

#### all components are up and running
```kotlin
    @SystemTest
    fun `distributed transaction should run successfully when all components are up and running`() {
        logContainers()

        val transactionId = uuid()
        val response = POST("http://$bookingHost:$bookingPort/api/v1/transaction/$transactionId")

        await("booking is confirmed").pollDelay(ONE_SECOND).atMost(ONE_MINUTE).until {
            val statuses = GET("http://$bookingHost:$bookingPort/${location(response.body)}")
            log.info("status for {}: {}", transactionId, statuses.body)
            isConfirmed(statuses.body, transactionId)
        }
    }
```


#### one component is unavailable
```kotlin
    @SystemTest
    fun `should book successfully order even when payment component is not available for defined number of time`() {
        docker.pause("system_test_payment")
        logContainers()

        val transactionId = uuid()
        val response = POST("http://$bookingHost:$bookingPort/api/v1/transaction/$transactionId")
        simulateUnavailability("system_test_payment")

        await("booking is confirmed").pollDelay(ONE_SECOND).atMost(ONE_MINUTE).until {
            val statuses = GET("http://$bookingHost:$bookingPort/${location(response.body)}")
            log.info("status for {}: {}", transactionId, statuses.body)
            isConfirmed(statuses.body, transactionId)
        }
    }
```

#### payment failed - transaction must be rollbacked
```
TODO
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
