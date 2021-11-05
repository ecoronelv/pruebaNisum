package com.springboot.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.springboot.app.entity.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long>{
	
	@Query("SELECT u FROM Usuario u WHERE u.email = ?1")
	public Usuario getUserByMail(String mail);

}
