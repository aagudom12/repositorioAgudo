package com.alfredo.proyectoDaw.repository;

import com.alfredo.proyectoDaw.entity.Noticia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
    Page<Noticia> findByCategoriaIgnoreCase(String categoria, Pageable pageable);
}
