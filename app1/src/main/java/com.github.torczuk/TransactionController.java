package com.github.torczuk;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/transaction")
public class TransactionController {

    @GetMapping
    public String hello() {
        return "hello";
    }

}
