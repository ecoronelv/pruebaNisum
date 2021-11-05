package com.springboot.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.app.entity.Telefono;

public interface TelefonoRepositorio extends JpaRepository<Telefono, Long> {

}
