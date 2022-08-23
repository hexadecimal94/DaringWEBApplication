package jp.co.poweredge.store.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.poweredge.store.domain.Address;
import jp.co.poweredge.store.domain.Order;
import jp.co.poweredge.store.domain.Payment;
import jp.co.poweredge.store.domain.Shipping;
import jp.co.poweredge.store.domain.ShoppingCart;
import jp.co.poweredge.store.domain.User;
import jp.co.poweredge.store.service.OrderService;
import jp.co.poweredge.store.service.ShoppingCartService;

@Controller
public class CheckoutControler {

	//コンソールにログ出すために
	private static final Logger logger = LoggerFactory.getLogger(CheckoutControler.class);

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private OrderService orderService;

	@RequestMapping("/checkout")
	public String checkout( @RequestParam(value="missingRequiredField", required=false) boolean missingRequiredField,
							Model model, Authentication authentication) {
		User user = (User) authentication.getPrincipal();

		logger.warn("this is user: " + user.getUsername());
		ShoppingCart shoppingCart = shoppingCartService.getShoppingCart(user);


		//カートの中身空かどうかを確認する
		if(shoppingCart.isEmpty()) {
			model.addAttribute("emptyCart", true);
			return "redirect:/shopping-cart/cart";
		}
		//List<CartItem>をカートページに送る
		model.addAttribute("cartItemList", shoppingCart.getCartItems());
		model.addAttribute("shoppingCart", shoppingCart);
		if(missingRequiredField) {
			model.addAttribute("missingRequiredField", true);
		}
		return "checkout";
	}

	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public String placeOrder(@ModelAttribute("shipping") Shipping shipping,
							@ModelAttribute("address") Address address,
							@ModelAttribute("payment") Payment payment,
							RedirectAttributes redirectAttributes, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		ShoppingCart shoppingCart = shoppingCartService.getShoppingCart(user);
		if (!shoppingCart.isEmpty()) {
			shipping.setAddress(address);
			//新規注文する
			//注文テーブルにデータを保存する
			Order order = orderService.createOrder(shoppingCart, shipping, payment, user);
			redirectAttributes.addFlashAttribute("order", order);
		}
		return "redirect:/order-submitted";
	}

	@RequestMapping(value = "/order-submitted", method = RequestMethod.GET)
	public String orderSubmitted(Model model) {
		Order order = (Order) model.asMap().get("order");
		if (order == null) {
			return "redirect:/";
		}
		model.addAttribute("order", order);
		return "orderSubmitted";
	}

}
