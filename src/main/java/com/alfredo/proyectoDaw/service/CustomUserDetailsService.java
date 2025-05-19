package com.alfredo.proyectoDaw.service;

import com.alfredo.proyectoDaw.entity.Usuario;
import com.alfredo.proyectoDaw.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        //Configurar roles
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(usuario.getRol())
                .build();
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }


    /*public Usuario registrarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // Encriptar antes de guardar
        return usuarioRepository.save(usuario);
    }*/

    public Optional<Usuario> comprobarUsuario(Usuario usuario) {
        return usuarioRepository.findByEmail(usuario.getEmail());
    }

    public void guardarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // Cifrar la contraseña
        usuario.setRol("USER"); // Asignar el rol por defecto
        usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void guardar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public String guardarFotoPerfil(MultipartFile archivo) throws IOException {
        Path directorioFotosPerfil = Paths.get("uploads/fotosPerfil");
        if (!Files.exists(directorioFotosPerfil)) {
            Files.createDirectories(directorioFotosPerfil);
        }

        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        Path destino = directorioFotosPerfil.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/fotosPerfil/" + nombreArchivo;
    }

}
