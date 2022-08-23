package jp.co.poweredge.store.repository;

import org.springframework.data.repository.CrudRepository;

import jp.co.poweredge.store.domain.security.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	
	Role findByName(String name);

}
