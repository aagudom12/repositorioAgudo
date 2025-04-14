package com.alfredo.proyectoDaw.service;

import com.alfredo.proyectoDaw.entity.Noticia;
import com.alfredo.proyectoDaw.repository.NoticiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NoticiaService {

    @Autowired
    private NoticiaRepository noticiaRepository;

    public void guardar(Noticia noticia) {
        noticiaRepository.save(noticia);
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


}
