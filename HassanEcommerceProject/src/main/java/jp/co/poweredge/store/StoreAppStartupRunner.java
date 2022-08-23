package jp.co.poweredge.store;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jp.co.poweredge.store.service.UserService;

@Component
public class StoreAppStartupRunner implements CommandLineRunner{

	@Autowired
	private UserService userService;

	//一番最初にプロジェクトスタートする時アドミンのユーザネームとパソワード設定できます。

	@Override
	public void run(String... args) throws Exception {
		userService.createUser("admin", "admin", "admin@admin.com", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
	}
}

