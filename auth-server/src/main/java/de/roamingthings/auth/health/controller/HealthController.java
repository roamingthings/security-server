package de.roamingthings.auth.health.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/04/10
 */
@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping("/status")
    public String status() {
        return "running";
    }
}
