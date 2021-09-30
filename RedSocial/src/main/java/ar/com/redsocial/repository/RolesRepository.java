package ar.com.redsocial.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.redsocial.entity.Roles;
import ar.com.redsocial.entity.RolesNames;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

	Optional<Roles> findByName(RolesNames rolesNames);
	
}
