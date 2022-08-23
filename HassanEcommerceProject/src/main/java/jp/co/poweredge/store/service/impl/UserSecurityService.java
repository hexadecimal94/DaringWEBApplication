package jp.co.poweredge.store.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jp.co.poweredge.store.domain.User;
import jp.co.poweredge.store.repository.UserRepository;

@Service
public class UserSecurityService implements UserDetailsService {
	//ログ出すために持ってきたオブジェクトです。消しても本アプリケーションと問題ないです
	private static final Logger logger = LoggerFactory.getLogger(UserSecurityService.class);

	@Autowired
	private UserRepository userRepository;

	@SuppressWarnings("unused")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		logger.warn("loadby called: " + user.getUsername());
		logger.warn("loadby called: " + user.getAccountstatus());

		if (user == null) {
			throw new UsernameNotFoundException("Username not found");
		}

		String acstatus = user.getAccountstatus();
		if(acstatus.equals("INACTIVE")) {
			//logger.warn("testing acstatus" + user.isEnabled());
		}
		return user;
	}

	public void authenticateUser(String username) {
		UserDetails userDetails = loadUserByUsername(username);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
