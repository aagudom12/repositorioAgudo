package com.alfredo.proyectoDaw.repository;

import com.alfredo.proyectoDaw.entity.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long> {
}

