package com.alfredo.proyectoDaw.controller;

import com.alfredo.proyectoDaw.entity.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import com.alfredo.proyectoDaw.service.CustomUserDetailsService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioController {

    private final CustomUserDetailsService usuarioService;

    @Autowired
    public UsuarioController(CustomUserDetailsService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/nuevoUsuario")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario-new";
    }

    @PostMapping("/nuevoUsuario")
    public String registerUser(@Valid @ModelAttribute("usuario") Usuario usuario,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "usuario-new"; // Devuelve al formulario con los mensajes de error
        }

        // Verificar si el email ya está en uso
        if (usuarioService.comprobarUsuario(usuario).isPresent()) {
            result.rejectValue("email", "error.usuario", "El email ya está en uso");
            return "usuario-new";
        }

        if (result.hasErrors()) {
            return "usuario-new"; // Devuelve al formulario con los mensajes de error
        }

        // Guardar el usuario
        //usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword())); // Cifrado de contraseña
        //usuario.setRol("USER");
        usuarioService.guardarUsuario(usuario);

        model.addAttribute("registroExitoso", true);
        return "redirect:/login";
    }
}
