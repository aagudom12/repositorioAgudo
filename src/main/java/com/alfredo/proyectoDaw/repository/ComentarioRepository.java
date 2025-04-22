package com.alfredo.proyectoDaw.repository;

import com.alfredo.proyectoDaw.entity.Comentario;
import com.alfredo.proyectoDaw.entity.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByNoticiaOrderByFechaComentarioAsc(Noticia noticia);
}

