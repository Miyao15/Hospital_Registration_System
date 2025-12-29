package com.hospital.registration.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        // This is a simple endpoint for debugging network connectivity.
        // It has no security, no database access, and no complex logic.
        return "pong";
    }
}
