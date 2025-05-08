package com.alfredo.proyectoDaw.controller;

import com.alfredo.proyectoDaw.entity.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import com.alfredo.proyectoDaw.service.CustomUserDetailsService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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

    @GetMapping("/admin/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuario-list";
    }

    @GetMapping("/admin/eliminarUsuario/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/admin/usuarios";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailLogueado = auth.getName();

        if (usuario.getEmail().equals(emailLogueado)) {
            redirectAttributes.addFlashAttribute("error", "No puedes eliminar tu propio usuario.");
            return "redirect:/admin/usuarios";
        }

        usuarioService.eliminarUsuario(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
        return "redirect:/admin/usuarios";
    }


    @PostMapping("/admin/usuarios/cambiarRol/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String cambiarRol(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/admin/usuarios";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailLogueado = auth.getName();

        // Evita que un admin se cambie su propio rol
        if (usuario.getEmail().equals(emailLogueado)) {
            redirectAttributes.addFlashAttribute("error", "No puedes cambiar tu propio rol.");
            return "redirect:/admin/usuarios";
        }

        // Cambia el rol
        if ("ADMIN".equals(usuario.getRol())) {
            usuario.setRol("USER");
            redirectAttributes.addFlashAttribute("mensaje", "El usuario ahora tiene rol USER.");
        } else {
            usuario.setRol("ADMIN");
            redirectAttributes.addFlashAttribute("mensaje", "El usuario ahora tiene rol ADMIN.");
        }

        usuarioService.guardar(usuario);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public String verPerfil(Model model, Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public String actualizarPerfil(@ModelAttribute("usuario") Usuario usuarioForm,
                                   Authentication auth, RedirectAttributes redirectAttributes) {
        String email = auth.getName();
        Usuario usuarioActual = usuarioService.obtenerUsuarioPorEmail(email);

        // Actualizar campos editables
        usuarioActual.setNombre(usuarioForm.getNombre());
        usuarioActual.setApellidos(usuarioForm.getApellidos());
        usuarioActual.setEmail(usuarioForm.getEmail()); // solo si lo permites

        usuarioService.guardar(usuarioActual);
        redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado con éxito");

        return "redirect:/perfil";
    }



}
