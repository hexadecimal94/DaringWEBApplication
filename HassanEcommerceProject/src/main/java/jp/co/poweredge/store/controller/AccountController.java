package jp.co.poweredge.store.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.poweredge.store.domain.Address;
import jp.co.poweredge.store.domain.Order;
import jp.co.poweredge.store.domain.User;
import jp.co.poweredge.store.service.OrderService;
import jp.co.poweredge.store.service.UserService;
import jp.co.poweredge.store.service.impl.UserSecurityService;
import utility.SecurityUtility;

@Controller
public class AccountController {
	//ログ出すために持ってきたオブジェクトです。消しても本アプリケーションと問題ないです
	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private UserSecurityService userSecurityService;

	@Autowired
	private OrderService orderService;

	@RequestMapping("/login")
	public String log(Model model) {
		logger.warn("log method is called{/login} request mapping");

		//同じユーザネームとメールアドレスで新規登録した時エラーメッセージ出します。
		model.addAttribute("usernameExists", model.asMap().get("usernameExists"));
		model.addAttribute("emailExists", model.asMap().get("emailExists"));
		return "myAccount";
	}

	@RequestMapping("/my-profile")
	public String myProfile(Model model, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		//ログ
		logger.warn("myProfile method is called: " + authentication.getPrincipal());
		logger.warn("myProfile method is called: " + user.getAuthorities().toString());


		model.addAttribute("user", user);
		return "myProfile";
	}

	@RequestMapping("/my-orders")
	public String myOrders(Model model, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		model.addAttribute("user", user);

		//現在ログインしているユーザの注文すべて持ってくる。
		List<Order> orders = orderService.findByUser(user);

		//テーブルで表示するために注文リストを送る
		model.addAttribute("orders", orders);
		return "myOrders";
	}

	@RequestMapping("/my-address")
	public String myAddress(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		return "myAddress";
	}




	//ポイント機能
	@RequestMapping("/my-point")
	public String myPoint(Model model, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		List<Order> orders = orderService.findByUser(user);

		Integer totalmoneyspend = 0;
		for(Order order:orders) {
			BigDecimal bigDecimal = order.getOrderTotal();

			//converting to Integer
			totalmoneyspend = totalmoneyspend + bigDecimal.intValue();
			String bigDecimalString = String.valueOf(bigDecimal);

			logger.warn("ordertotal in orderlist: " + bigDecimalString);

			logger.warn("totalmoneyspend is: " + totalmoneyspend);

		}
		//calculating point
		Integer currentpoint = totalmoneyspend/100;
		logger.warn("currentpoint is: " + currentpoint);

		//ポイントオブジェクト追加
		model.addAttribute("currentpoint", currentpoint);

		//テーブルのためにordersも送る
		model.addAttribute("orders",orders);


		model.addAttribute("user", user);
		return "myPoint";
	}




	//アカウントページでユーザが住所をアップデートした時の処理
	@RequestMapping(value="/update-user-address", method=RequestMethod.POST)
	public String updateUserAddress(@ModelAttribute("address") Address address,
			Model model, Principal principal) throws Exception {
		User currentUser = userService.findByUsername(principal.getName());
		if(currentUser == null) {
			throw new Exception ("User not found");
		}
		currentUser.setAddress(address);
		userService.save(currentUser);
		return "redirect:/my-address";
	}

	//新規登録した時の処理
	@RequestMapping(value="/new-user", method=RequestMethod.POST)
	public String newUserPost(@Valid @ModelAttribute("user") User user, BindingResult bindingResults,
							  @ModelAttribute("new-password") String password,
							  RedirectAttributes redirectAttributes, Model model) {
		model.addAttribute("email", user.getEmail());
		model.addAttribute("username", user.getUsername());
		boolean invalidFields = false;
		if (bindingResults.hasErrors()) {
			return "redirect:/login";
		}
		if (userService.findByUsername(user.getUsername()) != null) {
			redirectAttributes.addFlashAttribute("usernameExists", true);
			invalidFields = true;
		}
		if (userService.findByEmail(user.getEmail()) != null) {
			redirectAttributes.addFlashAttribute("emailExists", true);
			invalidFields = true;
		}
		if (invalidFields) {
			return "redirect:/login";
		}

		//データベースに新しいユーザ情報を登録する
		//とりあえず、すべてのユーザに（"ROLE_USER")権限を与えるようにする


		user = userService.createUser(user.getUsername(), password, user.getEmail(), Arrays.asList("ROLE_USER"));

		//ユーザ情報データベースに挿入後、そのユーザを認証する必要があります。
		//そのために、spring securityのuserSecurityserviceのauthenticateUser メソッド使います。


		userSecurityService.authenticateUser(user.getUsername());
		return "redirect:/my-profile";
	}

	//ユーザ情報をアップデートするときの処理
	@RequestMapping(value="/update-user-info", method=RequestMethod.POST)
	public String updateUserInfo( @ModelAttribute("user") User user,
								  @RequestParam("newPassword") String newPassword,
								  Model model, Principal principal) throws Exception {
		User currentUser = userService.findByUsername(principal.getName());
		if(currentUser == null) {
			throw new Exception ("User not found");
		}
		/*アップデートする時、新しく書いたユーザネームが既に登録されていたらreturn する*/
		User existingUser = userService.findByUsername(user.getUsername());
		if (existingUser != null && !existingUser.getId().equals(currentUser.getId()))  {
			model.addAttribute("usernameExists", true);
			return "myProfile";
		}
		/*アップデートする時、新しく書いたメールアドレスが既に登録されていたらreturn する*/
		existingUser = userService.findByEmail(user.getEmail());
		if (existingUser != null && !existingUser.getId().equals(currentUser.getId()))  {
			model.addAttribute("emailExists", true);
			return "myProfile";
		}
		/*パソワードアップデートする*/
		if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals("")){
			BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
			String dbPassword = currentUser.getPassword();
			if(passwordEncoder.matches(user.getPassword(), dbPassword)){
				currentUser.setPassword(passwordEncoder.encode(newPassword));
			} else {
				model.addAttribute("incorrectPassword", true);
				return "myProfile";
			}
		}
		currentUser.setFirstName(user.getFirstName());
		currentUser.setLastName(user.getLastName());
		currentUser.setUsername(user.getUsername());
		currentUser.setEmail(user.getEmail());
		userService.save(currentUser);


		model.addAttribute("updateSuccess", true);
		model.addAttribute("user", currentUser);

		//もう一度認証しないと
		userSecurityService.authenticateUser(currentUser.getUsername());
		return "myProfile";
	}









	//注文詳細
	@RequestMapping("/order-detail")
	public String orderDetail(@RequestParam("order") Long id, Model model) {
		Order order = orderService.findOrderWithDetails(id);
		model.addAttribute("order", order);
		return "orderDetails";
	}

	//退会リクエスト
	@RequestMapping("/withdraw")
	public String withdraw(Model model, Authentication authentication)throws Exception {
		User user = (User) authentication.getPrincipal();
		logger.warn("withdraw is called, currentUserId is : " + user.getId());
		logger.warn("withdraw is called, currentUsername is : " + user.getUsername());

		model.addAttribute("user", user);
		return "myWithdraw";
	}

	@RequestMapping("/withdrawConfirm")
	public String deactivateUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes,Authentication authentication) {
		//ユーザが退会すると決定したらusertable のaccountstatus inactiveにする
		User currentUser = (User) authentication.getPrincipal();
		logger.warn("existing username is: " + currentUser.getUsername());

		User deactivatingUser = userService.findByUsername(currentUser.getUsername());

		logger.warn("deactivatingUser username is: " + deactivatingUser.getUsername());
		logger.warn("deactivatingUser username is: " + deactivatingUser.getAccountstatus());
		logger.warn("deactivatingUser username is: " + deactivatingUser.getEmail());
		logger.warn("deactivatingUser username is: " + deactivatingUser.getPassword());

		//accountstatusをnullに設定したら再度ログイン不可能
		deactivatingUser.setAccountstatus(null);
		logger.warn("deactivatingUser AccountStatus is: " + deactivatingUser.getAccountstatus());
		userService.save(deactivatingUser);


		//ログアウトする必要もあるので、リダイレクトする時{/logout} リクエストマッピングにするとlogout 成功します。
		return "redirect:/logout";
	}

}
