package jp.co.poweredge.store.service;


import java.util.List;

import jp.co.poweredge.store.domain.User;

public interface UserService {

	User findById(Long id);

	User findByUsername(String username);

	User findByEmail(String email);

	void save(User user);

	User createUser(String username, String email,  String password, List<String> roles);

	List<User>findAllUsers();

}
