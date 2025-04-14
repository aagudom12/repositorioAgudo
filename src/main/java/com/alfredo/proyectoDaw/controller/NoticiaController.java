package com.alfredo.proyectoDaw.controller;

import com.alfredo.proyectoDaw.entity.Noticia;
import com.alfredo.proyectoDaw.entity.Usuario;
import com.alfredo.proyectoDaw.service.CustomUserDetailsService;
import com.alfredo.proyectoDaw.service.NoticiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class NoticiaController {

    private NoticiaService noticiaService;
    private CustomUserDetailsService usuarioService;

    @Autowired
    public NoticiaController(NoticiaService noticiaService, CustomUserDetailsService usuarioService) {
        this.noticiaService = noticiaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/nuevaNoticia")
    public String newNoticia(Model model){
        model.addAttribute("noticia", new Noticia());
        return "noticia-new";
    }

    @PostMapping("/guardarNoticia")
    public String guardarNoticia(@ModelAttribute Noticia noticia) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Usuario admin = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());

        noticia.setAdmin(admin);
        noticia.setFechaPublicacion(LocalDateTime.now());

        noticiaService.guardar(noticia);

        return "redirect:/inicio";
    }

    @GetMapping("/editarNoticia/{id}")
    public String editarNoticia(@PathVariable Long id, Model model) {
        Noticia noticia = noticiaService.buscarPorId(id);
        model.addAttribute("noticia", noticia);
        return "noticia-edit";
    }

    @PostMapping("/actualizarNoticia")
    public String actualizarNoticia(@ModelAttribute Noticia noticia) {
        // Conservamos el admin original
        Noticia noticiaExistente = noticiaService.buscarPorId(noticia.getId());
        noticia.setAdmin(noticiaExistente.getAdmin());
        noticia.setFechaPublicacion(noticiaExistente.getFechaPublicacion()); // O puedes actualizar si prefieres

        noticiaService.guardar(noticia);
        return "redirect:/inicio";
    }

    @GetMapping("/eliminarNoticia/{id}")
    public String eliminarNoticia(@PathVariable Long id) {
        noticiaService.eliminarPorId(id);
        return "redirect:/inicio";
    }


}
