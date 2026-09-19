package com.example.demo.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SystemController {

    // ==========================================
    // RECORD ENDPOINTS
    // ==========================================

    @GetMapping("/records/{id}")
    public ResponseEntity<String> readRecord(
            @PathVariable String id) {

        return ResponseEntity.ok(
                "Reading record " + id + " - Access Granted."
        );
    }

    @PostMapping("/records")
    public ResponseEntity<String> createRecord(
            @RequestBody String data) {

        return ResponseEntity.ok(
                "Record created successfully - Admin Access Granted."
        );
    }

    // ==========================================
    // MODULE FIELD DETAILS ENDPOINTS
    // ==========================================

    @GetMapping("/modules/{id}")
    public ResponseEntity<String> readModule(
            @PathVariable String id) {

        return ResponseEntity.ok(
                "Reading module field details " + id + " - Access Granted."
        );
    }

    @PutMapping("/modules/{id}")
    public ResponseEntity<String> updateModule(
            @PathVariable String id,
            @RequestBody String data) {

        return ResponseEntity.ok(
                "Module field details updated successfully - Admin Access Granted."
        );
    }
}