-- phpMyAdmin SQL Dump
-- version 5.1.1deb5ubuntu1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost:3306
-- Tiempo de generación: 06-05-2025 a las 19:29:33
-- Versión del servidor: 8.0.34-0ubuntu0.22.04.1
-- Versión de PHP: 8.1.2-1ubuntu2.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `futbolbd`
--
CREATE DATABASE IF NOT EXISTS `futbolbd` DEFAULT CHARACTER SET utf8mb3 COLLATE utf8mb3_spanish_ci;
USE `futbolbd`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comentarios`
--

DROP TABLE IF EXISTS `comentarios`;
CREATE TABLE `comentarios` (
  `id` bigint NOT NULL,
  `contenido` text COLLATE utf8mb3_spanish_ci NOT NULL,
  `fecha_comentario` datetime(6) NOT NULL,
  `noticia_id` bigint NOT NULL,
  `usuario_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comentario_likes`
--

DROP TABLE IF EXISTS `comentario_likes`;
CREATE TABLE `comentario_likes` (
  `id` bigint NOT NULL,
  `comentario_id` bigint NOT NULL,
  `usuario_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `fotos`
--

DROP TABLE IF EXISTS `fotos`;
CREATE TABLE `fotos` (
  `id` bigint NOT NULL,
  `url` varchar(255) COLLATE utf8mb3_spanish_ci NOT NULL,
  `id_noticia` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `noticias`
--

DROP TABLE IF EXISTS `noticias`;
CREATE TABLE `noticias` (
  `id` bigint NOT NULL,
  `contenido` text COLLATE utf8mb3_spanish_ci NOT NULL,
  `fecha_publicacion` datetime(6) NOT NULL,
  `titulo` varchar(255) COLLATE utf8mb3_spanish_ci NOT NULL,
  `id_admin` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id` bigint NOT NULL,
  `password` varchar(500) COLLATE utf8mb3_spanish_ci DEFAULT NULL,
  `apellidos` varchar(255) COLLATE utf8mb3_spanish_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb3_spanish_ci NOT NULL,
  `nombre` varchar(255) COLLATE utf8mb3_spanish_ci DEFAULT NULL,
  `rol` varchar(255) COLLATE utf8mb3_spanish_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_spanish_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `comentarios`
--
ALTER TABLE `comentarios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK7ug5sge4353sonf8540pdujaq` (`noticia_id`),
  ADD KEY `FKdts62yj83qe3k748cgcjvm48r` (`usuario_id`);

--
-- Indices de la tabla `comentario_likes`
--
ALTER TABLE `comentario_likes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKhmrcv22706ndp51xaqkoiwxr0` (`comentario_id`,`usuario_id`),
  ADD KEY `FK5weqhlqog2283srp4v463q9dy` (`usuario_id`);

--
-- Indices de la tabla `fotos`
--
ALTER TABLE `fotos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKo3imue65pkya2a9rdn7ybxfcx` (`id_noticia`);

--
-- Indices de la tabla `noticias`
--
ALTER TABLE `noticias`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK8dh3tyuu9spe34g3ueu2fj2na` (`id_admin`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKkfsp0s1tflm1cwlj8idhqsad0` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `comentarios`
--
ALTER TABLE `comentarios`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `comentario_likes`
--
ALTER TABLE `comentario_likes`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `fotos`
--
ALTER TABLE `fotos`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `noticias`
--
ALTER TABLE `noticias`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `comentarios`
--
ALTER TABLE `comentarios`
  ADD CONSTRAINT `FK7ug5sge4353sonf8540pdujaq` FOREIGN KEY (`noticia_id`) REFERENCES `noticias` (`id`),
  ADD CONSTRAINT `FKdts62yj83qe3k748cgcjvm48r` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `comentario_likes`
--
ALTER TABLE `comentario_likes`
  ADD CONSTRAINT `FK5weqhlqog2283srp4v463q9dy` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `FKktyj5fkb3xd09y6iru22gr7i1` FOREIGN KEY (`comentario_id`) REFERENCES `comentarios` (`id`);

--
-- Filtros para la tabla `fotos`
--
ALTER TABLE `fotos`
  ADD CONSTRAINT `FKo3imue65pkya2a9rdn7ybxfcx` FOREIGN KEY (`id_noticia`) REFERENCES `noticias` (`id`);

--
-- Filtros para la tabla `noticias`
--
ALTER TABLE `noticias`
  ADD CONSTRAINT `FK8dh3tyuu9spe34g3ueu2fj2na` FOREIGN KEY (`id_admin`) REFERENCES `usuarios` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
