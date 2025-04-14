package com.alfredo.proyectoDaw.repository;

import com.alfredo.proyectoDaw.entity.Noticia;
import com.alfredo.proyectoDaw.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
}
