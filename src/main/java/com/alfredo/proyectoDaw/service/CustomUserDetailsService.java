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

import java.util.Optional;

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

}
