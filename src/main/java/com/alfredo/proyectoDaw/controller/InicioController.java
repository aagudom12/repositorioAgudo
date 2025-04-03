package com.alfredo.proyectoDaw.controller;

import com.alfredo.proyectoDaw.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class InicioController {

    private CustomUserDetailsService usuarioService;

    @Autowired
    public InicioController(CustomUserDetailsService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/inicio")
    public String index(Model model){
        return "inicio";
    }
}
