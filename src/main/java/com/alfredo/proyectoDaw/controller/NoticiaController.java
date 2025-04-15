package com.alfredo.proyectoDaw.controller;

import com.alfredo.proyectoDaw.entity.Foto;
import com.alfredo.proyectoDaw.entity.Noticia;
import com.alfredo.proyectoDaw.entity.Usuario;
import com.alfredo.proyectoDaw.service.CustomUserDetailsService;
import com.alfredo.proyectoDaw.service.FotoService;
import com.alfredo.proyectoDaw.service.NoticiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class NoticiaController {

    private NoticiaService noticiaService;
    private FotoService fotoService;
    private CustomUserDetailsService usuarioService;

    @Autowired
    public NoticiaController(NoticiaService noticiaService, CustomUserDetailsService usuarioService, FotoService fotoService) {
        this.noticiaService = noticiaService;
        this.usuarioService = usuarioService;
        this.fotoService = fotoService;
    }

    @GetMapping("/nuevaNoticia")
    public String newNoticia(Model model){
        model.addAttribute("noticia", new Noticia());
        return "noticia-new";
    }

    @PostMapping("/guardarNoticia")
    public String guardarNoticia(@ModelAttribute Noticia noticia, @RequestParam("imagenes") MultipartFile[] imagenes) throws IOException {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Usuario admin = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());

        noticia.setAdmin(admin);
        noticia.setFechaPublicacion(LocalDateTime.now());

        // Guarda la noticia primero para tener el ID
        Noticia noticiaGuardada = noticiaService.guardar(noticia);

        // Manejo de imágenes
        List<Foto> fotos = new ArrayList<>();
        for (MultipartFile imagen : imagenes) {
            if (!imagen.isEmpty()) {
                // Ruta donde guardamos la imagen (ej: /uploads/nombre.jpg)
                String nombreArchivo = UUID.randomUUID() + "_" + imagen.getOriginalFilename();
                Path ruta = Paths.get("uploads/imagesNoticias", nombreArchivo);
                Files.createDirectories(ruta.getParent());
                //Files.write(ruta, imagen.getBytes());
                Files.copy(imagen.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

                // Creamos la entidad Foto con la URL relativa
                Foto foto = Foto.builder()
                        .noticia(noticiaGuardada)
                        .url("/imagesNoticias/" + nombreArchivo)
                        .build();

                fotos.add(foto);
            }
        }

        if (!fotos.isEmpty()) {
            noticiaGuardada.setFotos(fotos);
            noticiaService.guardar(noticiaGuardada); // Actualiza con las fotos
        }

        return "redirect:/inicio";
    }

    @GetMapping("/editarNoticia/{id}")
    public String editarNoticia(@PathVariable Long id, Model model) {
        Noticia noticia = noticiaService.buscarPorId(id);
        model.addAttribute("noticia", noticia);
        return "noticia-edit";
    }

    @PostMapping("/actualizarNoticia")
    public String actualizarNoticia(@ModelAttribute Noticia noticia,
                                    @RequestParam("imagenes") MultipartFile[] imagenes) throws IOException  {
        // Conservamos el admin original
        Noticia noticiaExistente = noticiaService.buscarPorId(noticia.getId());
        noticia.setAdmin(noticiaExistente.getAdmin());
        noticia.setFechaPublicacion(noticiaExistente.getFechaPublicacion()); // O puedes actualizar si prefieres
        noticia.setFotos(noticiaExistente.getFotos()); // Conservamos las fotos

        Noticia noticiaActualizada = noticiaService.guardar(noticia);

        // Guardar nuevas imágenes si hay
        for (MultipartFile imagen : imagenes) {
            if (!imagen.isEmpty()) {
                String nombreArchivo = UUID.randomUUID() + "_" + imagen.getOriginalFilename();
                Path ruta = Paths.get("uploads/imagesNoticias", nombreArchivo);
                Files.createDirectories(ruta.getParent());
                Files.copy(imagen.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

                Foto foto = Foto.builder()
                        .noticia(noticiaActualizada)
                        .url("/imagesNoticias/" + nombreArchivo)
                        .build();

                fotoService.guardarFoto(foto);
            }
        }
        return "redirect:/inicio";
    }

    @GetMapping("/eliminarNoticia/{id}")
    public String eliminarNoticia(@PathVariable Long id) {
        noticiaService.eliminarPorId(id);
        return "redirect:/inicio";
    }

    @GetMapping("/eliminarFoto/{id}")
    public String eliminarFoto(@PathVariable Long id) {
        Foto foto = fotoService.buscarFotoPorId(id);
        fotoService.eliminarFoto(foto);
        return "redirect:/editarNoticia/" + foto.getNoticia().getId();
    }



}
