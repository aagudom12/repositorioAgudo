package com.alfredo.proyectoDaw.controller;

import com.alfredo.proyectoDaw.entity.Comentario;
import com.alfredo.proyectoDaw.entity.Foto;
import com.alfredo.proyectoDaw.entity.Noticia;
import com.alfredo.proyectoDaw.entity.Usuario;
import com.alfredo.proyectoDaw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private ComentarioService comentarioService;
    private ComentarioLikeService comentarioLikeService;

    @Autowired
    public NoticiaController(NoticiaService noticiaService, CustomUserDetailsService usuarioService, FotoService fotoService, ComentarioService comentarioService, ComentarioLikeService comentarioLikeService) {
        this.noticiaService = noticiaService;
        this.usuarioService = usuarioService;
        this.fotoService = fotoService;
        this.comentarioService = comentarioService;
        this.comentarioLikeService = comentarioLikeService;
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
        noticia.setComentarios(noticiaExistente.getComentarios());

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

    @GetMapping("/noticia/{id}")
    public String verNoticia(@PathVariable Long id, Model model, Authentication auth) {
        Noticia noticia = noticiaService.buscarPorId(id);
        List<Comentario> comentarios = comentarioService.obtenerPorNoticia(noticia);

        Usuario usuario = null;
        List<Long> idsComentariosConLike = new ArrayList<>();

        if (auth != null) {
            usuario = usuarioService.obtenerUsuarioPorEmail(auth.getName());
            model.addAttribute("usuarioActual", usuario);
            // En lugar de reasignar, modificamos directamente la lista
            idsComentariosConLike.addAll(comentarioLikeService.obtenerIdsComentariosConLikeDelUsuario(usuario));
        }

        // Ahora esta variable es "efectivamente final"
        comentarios.forEach(c -> {
            c.setUsuarioLeDioLike(idsComentariosConLike.contains(c.getId()));
            c.setNumeroLikes(comentarioLikeService.contarLikesPorComentario(c));
        });

        model.addAttribute("noticia", noticia);
        model.addAttribute("comentarios", comentarios);
        model.addAttribute("nuevoComentario", new Comentario());

        return "noticia-detalle";
    }


    @PostMapping("/noticia/{id}/comentario")
    public String agregarComentario(@PathVariable Long id,
                                    @RequestParam("texto") String texto,
                                    Authentication auth) {

        Noticia noticia = noticiaService.buscarPorId(id);
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(auth.getName());

        Comentario comentario = new Comentario();
        comentario.setContenido(texto);
        comentario.setNoticia(noticia);
        comentario.setUsuario(usuario);
        comentario.setFechaComentario(LocalDateTime.now());

        comentarioService.guardar(comentario);

        return "redirect:/noticia/" + id;
    }

    @GetMapping("/comentario/editar/{id}")
    public String editarComentario(@PathVariable Long id, Model model, Authentication auth) {
        Comentario comentario = comentarioService.buscarPorId(id);
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(auth.getName());

        if (!comentario.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/inicio"; // O lanzar un 403 Forbidden
        }

        model.addAttribute("comentario", comentario);
        return "comentario-edit"; // Vista que crearás abajo
    }

    @PostMapping("/comentario/actualizar")
    public String actualizarComentario(@ModelAttribute Comentario comentarioForm, Authentication auth) {
        Comentario original = comentarioService.buscarPorId(comentarioForm.getId());
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(auth.getName());

        if (!original.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/inicio";
        }

        original.setContenido(comentarioForm.getContenido());
        comentarioService.guardar(original);

        return "redirect:/noticia/" + original.getNoticia().getId();
    }

    @PostMapping("/comentario/eliminar/{id}")
    public String eliminarComentario(@PathVariable Long id, Authentication auth) {
        Comentario comentario = comentarioService.buscarPorId(id);
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(auth.getName());

        boolean esPropietario = comentario.getUsuario().getId().equals(usuario.getId());
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (esPropietario || esAdmin) {
            comentarioService.eliminar(comentario);
        }

        return "redirect:/noticia/" + comentario.getNoticia().getId();

        /*if (!comentario.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/inicio";
        }

        Long noticiaId = comentario.getNoticia().getId();
        comentarioService.eliminar(comentario);

        return "redirect:/noticia/" + noticiaId;*/
    }


    @PostMapping("/comentario/{id}/like")
    public String toggleLike(@PathVariable Long id, Authentication auth) {
        Comentario comentario = comentarioService.buscarPorId(id);
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(auth.getName());

        if (comentarioLikeService.haDadoLike(comentario, usuario)) {
            comentarioLikeService.quitarLike(comentario, usuario);
        } else {
            comentarioLikeService.darLike(comentario, usuario);
        }

        return "redirect:/noticia/" + comentario.getNoticia().getId();
    }


    @GetMapping("/noticias/categoria/{categoria}")
    public String noticiasPorCategoria(@PathVariable String categoria,
                                       @RequestParam(defaultValue = "0") int page,
                                       Model model) {

        Page<Noticia> noticias = noticiaService.buscarPorCategoria(categoria, page);

        model.addAttribute("noticias", noticias.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", noticias.getTotalPages());
        model.addAttribute("categoriaSeleccionada", categoria);

        return "inicio"; // muy importante
    }


}
