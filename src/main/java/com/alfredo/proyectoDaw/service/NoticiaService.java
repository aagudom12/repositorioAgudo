package com.alfredo.proyectoDaw.service;

import com.alfredo.proyectoDaw.entity.Noticia;
import com.alfredo.proyectoDaw.repository.NoticiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NoticiaService {

    @Autowired
    private NoticiaRepository noticiaRepository;

    public Noticia guardar(Noticia noticia) {
        return noticiaRepository.save(noticia);
    }

    public List<Noticia> listarTodas() {
        return noticiaRepository.findAll();
    }

    public Noticia buscarPorId(Long id) {
        return noticiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Noticia no encontrada"));
    }

    public void eliminarPorId(Long id) {
        noticiaRepository.deleteById(id);
    }

    public Page<Noticia> listarNoticiasPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaPublicacion").descending());
        return noticiaRepository.findAll(pageable);
    }

}
