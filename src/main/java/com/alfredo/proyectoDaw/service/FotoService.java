package com.alfredo.proyectoDaw.service;

import com.alfredo.proyectoDaw.entity.Foto;
import com.alfredo.proyectoDaw.repository.FotoRepository;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FotoService {

    @Autowired
    private FotoRepository fotoRepository;

    public Foto buscarFotoPorId(Long id) {
        return fotoRepository.findById(id).orElseThrow(() -> new RuntimeException("Foto no encontrada"));
    }

    public void eliminarFoto(Foto foto) {
        // Eliminar archivo físico también si quieres
        Path path = Paths.get("uploads/imagesNoticias").resolve(Paths.get(foto.getUrl().replace("/imagesNoticias/", ""))).normalize();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fotoRepository.delete(foto);
    }

    public void guardarFoto(Foto foto) {
        fotoRepository.save(foto);
    }
}
