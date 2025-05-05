package com.alfredo.proyectoDaw.service;

import com.alfredo.proyectoDaw.entity.Comentario;
import com.alfredo.proyectoDaw.entity.ComentarioLike;
import com.alfredo.proyectoDaw.entity.Usuario;
import com.alfredo.proyectoDaw.repository.ComentarioLikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComentarioLikeService {

    @Autowired
    private ComentarioLikeRepository likeRepository;

    public boolean haDadoLike(Comentario comentario, Usuario usuario) {
        return likeRepository.existsByComentarioAndUsuario(comentario, usuario);
    }

    public void darLike(Comentario comentario, Usuario usuario) {
        if (!haDadoLike(comentario, usuario)) {
            ComentarioLike like = new ComentarioLike();
            like.setComentario(comentario);
            like.setUsuario(usuario);
            likeRepository.save(like);
        }
    }

    @Transactional
    public void quitarLike(Comentario comentario, Usuario usuario) {
        likeRepository.deleteByComentarioAndUsuario(comentario, usuario);
    }

    public long contarLikes(Comentario comentario) {
        return likeRepository.countByComentario(comentario);
    }

    public List<Long> obtenerIdsComentariosConLikeDelUsuario(Usuario usuario) {
        return likeRepository.findByUsuario(usuario)
                .stream()
                .map(cl -> cl.getComentario().getId())
                .collect(Collectors.toList());
    }

    public int contarLikesPorComentario(Comentario comentario) {
        return likeRepository.countByComentario(comentario);
    }


}

