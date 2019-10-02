package com.redhat.samples.camel.spring.boot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name) {
        System.out.println("received: name = " + name);
        return String.format("Hello, %s!", name);
    }

}
