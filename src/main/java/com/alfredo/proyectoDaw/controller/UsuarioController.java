package com.alfredo.proyectoDaw.controller;

import com.alfredo.proyectoDaw.entity.Foto;
import com.alfredo.proyectoDaw.entity.Usuario;
import com.alfredo.proyectoDaw.service.FotoService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UsuarioController {

    private final CustomUserDetailsService usuarioService;

    private final FotoService fotoService;

    @Autowired
    public UsuarioController(CustomUserDetailsService usuarioService, FotoService fotoService) {
        this.usuarioService = usuarioService;
        this.fotoService = fotoService;
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
                                   @RequestParam("foto") MultipartFile archivoFoto,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        String email = auth.getName();
        Usuario usuarioActual = usuarioService.obtenerUsuarioPorEmail(email);

        // Comprobar si el nuevo email ya lo tiene otro usuario
        Optional<Usuario> usuarioExistente = usuarioService.comprobarUsuario(usuarioForm);
        if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuarioActual.getId())) {
            model.addAttribute("usuario", usuarioActual); // para mantener los datos actuales en el formulario
            model.addAttribute("errorEmail", "El email ya está en uso por otro usuario");
            return "perfil"; // Asumiendo que esa es tu vista del perfil
        }

        // Actualizar campos del formulario
        usuarioActual.setNombre(usuarioForm.getNombre());
        usuarioActual.setApellidos(usuarioForm.getApellidos());
        usuarioActual.setEmail(usuarioForm.getEmail());

        // Solo procesar la foto si se subió algo
        if (!archivoFoto.isEmpty()) {
            try {
                // Guarda el archivo en disco
                String nombreArchivo = UUID.randomUUID() + "_" + archivoFoto.getOriginalFilename();
                String rutaCarpeta = "uploads/fotosPerfil/";
                File directorio = new File(rutaCarpeta);
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }

                Path rutaCompleta = Paths.get(rutaCarpeta + nombreArchivo);
                Files.copy(archivoFoto.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

                // Si el usuario ya tenía foto previa, elimínala (opcional)
                if (usuarioActual.getFotoPerfil() != null) {
                    Foto fotoAnterior = usuarioActual.getFotoPerfil();

                    usuarioActual.setFotoPerfil(null);
                    fotoAnterior.setUsuario(null);

                    fotoService.eliminarFoto(fotoAnterior);
                }

                // Crear la entidad Foto
                Foto nuevaFoto = new Foto();
                nuevaFoto.setUrl("/" + rutaCarpeta + nombreArchivo); // para que se pueda mostrar como URL
                nuevaFoto.setUsuario(usuarioActual); // importante si tienes la relación @OneToOne con Usuario

                // Asignar la nueva foto al usuario
                usuarioActual.setFotoPerfil(nuevaFoto);

                // Guardar la nueva foto
                fotoService.guardarFoto(nuevaFoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        usuarioService.guardar(usuarioActual);
        redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado con éxito");
        return "redirect:/perfil";
    }





}
