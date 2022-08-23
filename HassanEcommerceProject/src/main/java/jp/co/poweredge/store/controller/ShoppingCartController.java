package jp.co.poweredge.store.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.poweredge.store.domain.Article;
import jp.co.poweredge.store.domain.CartItem;
import jp.co.poweredge.store.domain.ShoppingCart;
import jp.co.poweredge.store.domain.User;
import jp.co.poweredge.store.service.ArticleService;
import jp.co.poweredge.store.service.ShoppingCartService;

@Controller
@RequestMapping("/shopping-cart")
public class ShoppingCartController {
	//ログ出すために持ってきたオブジェクトです。消しても本アプリケーションと問題ないです
	private static final Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);

	@Autowired
	private ArticleService articleService;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@RequestMapping("/cart")
	public String shoppingCart(Model model, Authentication authentication) {
		//ログ
		logger.warn("ShoppingCartController.shoppingCart メソッドを呼び出された");


		User user = (User) authentication.getPrincipal();
		ShoppingCart shoppingCart = shoppingCartService.getShoppingCart(user);
		model.addAttribute("cartItemList", shoppingCart.getCartItems());
		model.addAttribute("shoppingCart", shoppingCart);
		return "shoppingCart";
	}

	@RequestMapping("/add-item")
	public String addItem(@ModelAttribute("article") Article article, @RequestParam("qty") String qty,
						  @RequestParam("size") String size, RedirectAttributes attributes, Model model, Authentication authentication) {
		//ログ
		logger.warn("ShoppingCartController.addItem メソッドを呼び出された" + "article_id is="+article.getId()+ " quantity is=" + qty + " size is=" + size + "product is=" + article.toString());


		article = articleService.findArticleById(article.getId());
		//先にその商品の在庫あるかどうか確認する、在庫足りなかったら同じページにredirect する。
		
		if (!article.hasStock(Integer.parseInt(qty))) {
			attributes.addFlashAttribute("notEnoughStock", true);
			return "redirect:/article-detail?id="+article.getId();
		}
		User user = (User) authentication.getPrincipal();
		shoppingCartService.addArticleToShoppingCart(article, user, Integer.parseInt(qty), size);
		attributes.addFlashAttribute("addArticleSuccess", true);
		return "redirect:/article-detail?id="+article.getId();
	}

	@RequestMapping("/update-item")
	public String updateItemQuantity(@RequestParam("id") Long cartItemId,
									 @RequestParam("qty") Integer qty, Model model) {
		CartItem cartItem = shoppingCartService.findCartItemById(cartItemId);
		if (cartItem.canUpdateQty(qty)) {
			shoppingCartService.updateCartItem(cartItem, qty);
		}
		return "redirect:/shopping-cart/cart";
	}

	@RequestMapping("/remove-item")
	public String removeItem(@RequestParam("id") Long id) {
		shoppingCartService.removeCartItem(shoppingCartService.findCartItemById(id));
		return "redirect:/shopping-cart/cart";
	}
}
