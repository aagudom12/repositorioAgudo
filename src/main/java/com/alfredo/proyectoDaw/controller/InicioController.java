package com.alfredo.proyectoDaw.controller;

import com.alfredo.proyectoDaw.entity.Noticia;
import com.alfredo.proyectoDaw.entity.Usuario;
import com.alfredo.proyectoDaw.service.CustomUserDetailsService;
import com.alfredo.proyectoDaw.service.NoticiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
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

    /*@GetMapping("/inicio")
    public String mostrarInicio(Model model) {
        List<Noticia> noticias = noticiaService.listarTodas();
        model.addAttribute("noticias", noticias);
        return "inicio";
    }*/

    @GetMapping({"/", "/inicio"})
    public String listarNoticias(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "6") int size,
                                 Model model,
                                 Principal principal) {

        Page<Noticia> noticiasPage = noticiaService.listarNoticiasPaginadas(page, size);
        long totalNoticias = noticiaService.contarNoticias();

        model.addAttribute("noticias", noticiasPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", noticiasPage.getTotalPages());
        model.addAttribute("totalNoticias", totalNoticias);

        // Agregar el usuario autenticado al modelo, si existe
        if (principal != null) {
            Usuario usuario = usuarioService.obtenerUsuarioPorEmail(principal.getName());
            model.addAttribute("usuarioAutenticado", usuario);
        }

        return "inicio";
    }

    @GetMapping("/sobreMi")
    public String mostrarSobreMi() {
        return "sobre-mi";
    }

}
