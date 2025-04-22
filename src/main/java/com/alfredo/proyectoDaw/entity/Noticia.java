package com.alfredo.proyectoDaw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "noticias")
public class Noticia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idAdmin", nullable = false)
    private Usuario admin;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false)
    private LocalDateTime fechaPublicacion = LocalDateTime.now();

    @OneToMany(mappedBy = "noticia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> fotos;

    @OneToMany(mappedBy = "noticia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

}