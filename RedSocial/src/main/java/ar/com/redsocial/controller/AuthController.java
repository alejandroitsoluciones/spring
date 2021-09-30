package ar.com.redsocial.controller;

import java.net.URI;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ar.com.redsocial.entity.Roles;
import ar.com.redsocial.entity.RolesNames;
import ar.com.redsocial.entity.Users;
import ar.com.redsocial.exception.AppException;
import ar.com.redsocial.payload.ApiResponse;
import ar.com.redsocial.payload.JwtAuthenticationResponse;
import ar.com.redsocial.payload.LoginRequest;
import ar.com.redsocial.payload.SignUpRequest;
import ar.com.redsocial.repository.RolesRepository;
import ar.com.redsocial.repository.UsersRepository;
import ar.com.redsocial.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UsersRepository usersRepository;
	
	@Autowired
	RolesRepository rolesRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	
	@PostMapping("/singin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
		
		// System.out.println(loginRequest.getUsernameOrEmail());
		// System.out.println(loginRequest.getPassword());

		Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginRequest.getUsernameOrEmail(),
							loginRequest.getPassword()
							)
				);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtTokenProvider.generateToken(authentication);
			
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}
	
	@PostMapping("/singup")
	public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest){
		
        if(usersRepository.existByUsername(signUpRequest.getUsername()) != null) {
            return new ResponseEntity(new ApiResponse(false, "¡Este nombre de usuario ya está tomado!"),
                    HttpStatus.BAD_REQUEST);
        }
        
        if(usersRepository.existByEmail(signUpRequest.getEmail()) != null) {
            return new ResponseEntity(new ApiResponse(false, "¡Dirección de correo electrónico ya está en uso!"),
                    HttpStatus.BAD_REQUEST);
        }
        
        Users user = new Users(
        		signUpRequest.getName(), 
        		signUpRequest.getUsername(), 
        		signUpRequest.getEmail(), 
        		signUpRequest.getPassword());
		
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Roles userRole = rolesRepository.findByName(RolesNames.ROLE_USER)
        		.orElseThrow(() -> new AppException("Función de usuario no establecida."));
        
        user.setRoles(Collections.singleton(userRole));
        
        Users result = usersRepository.save(user);
        
        URI location = ServletUriComponentsBuilder
        		.fromCurrentContextPath().path("/api/users/{username}")
        		.buildAndExpand(result.getUsername()).toUri();
        
		return ResponseEntity.created(location).body(new ApiResponse(true, "Usuario registrado exitosamente."));
	}
	
}
