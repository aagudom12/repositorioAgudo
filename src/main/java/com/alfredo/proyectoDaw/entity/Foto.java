package com.alfredo.proyectoDaw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "fotos")
public class Foto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idNoticia", nullable = true)
    private Noticia noticia;

    @Column(nullable = false)
    private String url;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;
}
