package ar.com.redsocial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ar.com.redsocial.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

	Optional<Users> findByEmail(String email);
	
	Optional<Users> findByUsernameOrEmail(String username, String email);
	
	List<Users> findByIdIn(List<Long> usersId);
	
	@Query("SELECT u FROM Users u WHERE u.username = ?1")
	Users existByUsername(String username);
	
	@Query("SELECT u FROM Users u WHERE u.email = ?1")
	Users existByEmail(String email);
	
}
