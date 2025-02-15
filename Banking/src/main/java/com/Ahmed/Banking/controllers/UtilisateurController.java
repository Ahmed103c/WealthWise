package com.Ahmed.Banking.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Ahmed.Banking.dto.UtilisateurDto;
import com.Ahmed.Banking.services.UtilisateurService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService service;

    @PostMapping("/")
    public ResponseEntity<Integer> save(@Valid @RequestBody UtilisateurDto utilisateurDto) {
        return ResponseEntity.ok(service.save(utilisateurDto));
    }

    @GetMapping("/")
    public ResponseEntity<List<UtilisateurDto>> findAll()
    {
        return ResponseEntity.ok(service.findAll());
    }
    @GetMapping("/{utilisateur-id}")
    public ResponseEntity<UtilisateurDto> findById(@PathVariable("utilisateur-id") Integer id )
    {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{utilisateur-id}")
    public ResponseEntity<UtilisateurDto> delete(@PathVariable("utilisateur-id") Integer id )
    {
        service.delete(id);
        return ResponseEntity.accepted().build();
    }
}
