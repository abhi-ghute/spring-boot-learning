package com.hospital.management.service;

import com.hospital.management.entity.Patient;
import com.hospital.management.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public Patient getPatient(Long id) throws InterruptedException {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        Thread.sleep(2000);
        Patient patient2 = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        System.out.println(patient == patient2);
        patient.setEmail("ajay@gmail.com");

        return patient;
    }
}
