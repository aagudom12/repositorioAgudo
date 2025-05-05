package com.alfredo.proyectoDaw.repository;

import com.alfredo.proyectoDaw.entity.Comentario;
import com.alfredo.proyectoDaw.entity.ComentarioLike;
import com.alfredo.proyectoDaw.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioLikeRepository extends JpaRepository<ComentarioLike, Long> {
    boolean existsByComentarioAndUsuario(Comentario comentario, Usuario usuario);
    int countByComentario(Comentario comentario);
    void deleteByComentarioAndUsuario(Comentario comentario, Usuario usuario);

    List<ComentarioLike> findByUsuario(Usuario usuario);

}

