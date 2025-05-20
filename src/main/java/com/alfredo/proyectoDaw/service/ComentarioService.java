package com.alfredo.proyectoDaw.service;

import com.alfredo.proyectoDaw.entity.Comentario;
import com.alfredo.proyectoDaw.entity.Noticia;
import com.alfredo.proyectoDaw.repository.ComentarioLikeRepository;
import com.alfredo.proyectoDaw.repository.ComentarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private ComentarioLikeRepository comentarioLikeRepository;

    public List<Comentario> obtenerPorNoticia(Noticia noticia) {
        return comentarioRepository.findByNoticiaOrderByFechaComentarioAsc(noticia);
    }

    public void guardar(Comentario comentario) {
        comentarioRepository.save(comentario);
    }

    public Comentario buscarPorId(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
    }

    @Transactional
    public void eliminar(Comentario comentario) {
        // 1. Eliminar likes asociados al comentario
        comentarioLikeRepository.deleteByComentario(comentario);

        // 2. Eliminar el comentario
        comentarioRepository.delete(comentario);
    }

}

