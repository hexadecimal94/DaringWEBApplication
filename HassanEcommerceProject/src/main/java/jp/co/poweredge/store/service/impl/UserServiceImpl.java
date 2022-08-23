package jp.co.poweredge.store.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.poweredge.store.domain.User;
import jp.co.poweredge.store.domain.security.Role;
import jp.co.poweredge.store.domain.security.UserRole;
import jp.co.poweredge.store.repository.RoleRepository;
import jp.co.poweredge.store.repository.UserRepository;
import jp.co.poweredge.store.service.UserService;
import utility.SecurityUtility;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public User findById(Long id) {
		Optional<User> opt = userRepository.findById(id);
		return opt.get();
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}


	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void save(User user) {
		userRepository.save(user);
	}

	@Override
	@Transactional
	public User createUser(String username, String password, String email, List<String> roles) {
		User user = findByUsername(username);
		if (user != null) {
			return user;
		} else {
			user = new User();
			user.setUsername(username);

			//SecurityUtilityクラスで生のパソワードのハッシュ値を生成する
			user.setPassword(SecurityUtility.passwordEncoder().encode(password));
			user.setEmail(email);

			//デフォルトですべてのユーザのアカウントをActiveをにする
			user.setAccountstatus("ACTIVE");
			Set<UserRole> userRoles = new HashSet<>();
			for (String rolename : roles) {
				Role role = roleRepository.findByName(rolename);
				if (role == null) {
					role = new Role();
					role.setName(rolename);
					roleRepository.save(role);
				}
				userRoles.add(new UserRole(user, role));
			}
			user.setUserRoles(userRoles);
			return userRepository.save(user);
		}
	}


	//Admin 側のuser一覧表示する時のメソッド
	@Override
	public List<User>findAllUsers() {
		return (List<User>) userRepository.findAll();
	}

}
