package com.alfredo.proyectoDaw.controller;

import com.alfredo.proyectoDaw.entity.Noticia;
import com.alfredo.proyectoDaw.service.CustomUserDetailsService;
import com.alfredo.proyectoDaw.service.NoticiaService;
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
    private NoticiaService noticiaService;

    @Autowired
    public InicioController(CustomUserDetailsService usuarioService, NoticiaService noticiaService) {
        this.usuarioService = usuarioService;
        this.noticiaService = noticiaService;
    }

    @GetMapping("/inicio")
    public String mostrarInicio(Model model) {
        List<Noticia> noticias = noticiaService.listarTodas();
        model.addAttribute("noticias", noticias);
        return "inicio";
    }

}
