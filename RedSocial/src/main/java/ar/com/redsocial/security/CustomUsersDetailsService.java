package ar.com.redsocial.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.redsocial.entity.Users;
import ar.com.redsocial.repository.UsersRepository;

@Service
public class CustomUsersDetailsService implements UserDetailsService{

	@Autowired
	UsersRepository usersRepository;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Users user = usersRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
				.orElseThrow(() ->
					new UsernameNotFoundException("Usuario no encontrado con nombre de usuario o correo electrónico: " + usernameOrEmail)
				);
		return UsersPrincipal.create(user);
	}
	
	@Transactional
	public UserDetails loadUsersById(Long id) {
		Users user = usersRepository.findById(id).orElseThrow(
				() -> new UsernameNotFoundException("Usuario no encontrado con id: " + id)
		);
		return UsersPrincipal.create(user);
	}
	
}
