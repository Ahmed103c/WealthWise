package com.Ahmed.Banking.controllers;


import com.Ahmed.Banking.services.Implementations.GoCardlessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banking")
public class GoCardlessController {

    private final GoCardlessService goCardlessService;

    public GoCardlessController(GoCardlessService goCardlessService) {
        this.goCardlessService = goCardlessService;
    }

    // ✅ Automatically Generate Authentication Link
    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticateUser() {
        String authLink = goCardlessService.authenticateUser();
        return ResponseEntity.ok(authLink);
    }
}
