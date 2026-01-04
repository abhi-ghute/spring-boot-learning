package com.hospital.management.controller;

import com.hospital.management.entity.Patient;
import com.hospital.management.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping(value = "/patient/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Patient getPatient(@PathVariable("id") Long id) throws InterruptedException {
        return patientService.getPatient(id);
    }
}
