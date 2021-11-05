package com.springboot.app.controller;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.app.dto.UsuarioRequest;
import com.springboot.app.dto.MensajeResponse;
import com.springboot.app.entity.Usuario;
import com.springboot.app.repository.TelefonoRepositorio;
import com.springboot.app.repository.UsuarioRepositorio;

@RestController
public class UsuarioController {
	@Autowired
	private TelefonoRepositorio telefonoRepositorio;
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", Pattern.CASE_INSENSITIVE);
	public static final Pattern VALID_PASS_ADDRESS_REGEX = Pattern.compile("(?=\\w*[0-9])(?=\\w*[A-Z])(?=\\w*[a-z])\\S{8,16}");
	
	
	@PostMapping("/usuarioReq")
	public ResponseEntity<MensajeResponse> guardarUsuario(@RequestBody UsuarioRequest request) {
		Usuario datosReq = request.getUsuario();
		MensajeResponse msj = new MensajeResponse();
		
		if (datosReq == null || datosReq.getEmail() == null) {
			msj = setDataResponse(datosReq,"Correo es obligatorio");
			return new ResponseEntity<>(msj, HttpStatus.BAD_REQUEST);	
		}
		
		Usuario datosUsuario = usuarioRepositorio.getUserByMail(datosReq.getEmail());
		if (datosUsuario != null) {
			msj = setDataResponse(datosUsuario,"Usuario ya existe");
			return new ResponseEntity<>(msj, HttpStatus.OK);
		}
		
		if (!validaMail(datosReq.getEmail())) {
			msj = setDataResponse(datosReq,"Formato de correo invalido");
			return new ResponseEntity<>(msj, HttpStatus.BAD_REQUEST);
		}
		
		if (!validaClave(datosReq.getClave())) {
			msj = setDataResponse(datosReq,"Clave inválida. Debe tener entre 8 y 16 caracteres, al menos una letra mayúscula, una minuscula y un número");
			return new ResponseEntity<>(msj, HttpStatus.BAD_REQUEST);
		}
		
		datosReq.setFechaCreacion(new Date());
		datosReq.setFechaModificacion(new Date());
		datosReq.setFechaUltimoLogin(new Date());
		datosReq.setEstado("A");
		
		UUID uuid = UUID.randomUUID();
		datosReq.setToken(uuid.toString());
		
		try{
			
			Usuario datosResp = usuarioRepositorio.save(request.getUsuario());
			
			if (datosResp != null) {
				msj = setDataResponse(datosResp, "Transacción exitosa");
			}
			
		}catch(Exception e) {
			msj.setMensaje("Error: "+e.getMessage());
			return new ResponseEntity<>(msj, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(msj, HttpStatus.OK);
	}
	
	public MensajeResponse setDataResponse(Usuario datosUsuario, String mensaje) {
		MensajeResponse msj = new MensajeResponse();
		msj.setIdUsuario(datosUsuario.getId());
		msj.setFechaCreacion(datosUsuario.getFechaCreacion());
		msj.setFechaModificacion(datosUsuario.getFechaModificacion());
		msj.setFechaUltimoLogin(datosUsuario.getFechaUltimoLogin());
		msj.setToken(datosUsuario.getToken());
		msj.setActive(false); 
		if (datosUsuario.getEstado() != null && datosUsuario.getEstado().equals("A")) {
			msj.setActive(true);
		}
		msj.setMensaje(mensaje);
		return msj;
		
	}
	

	public static boolean validaMail(String emailStr) {
		if (emailStr == null) {
			return false;
		}
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
	
	public static boolean validaClave(String claveStr) {
		if (claveStr == null) {
			return false;
		}
		Matcher matcher = VALID_PASS_ADDRESS_REGEX.matcher(claveStr);
		return matcher.find();
	}

}
