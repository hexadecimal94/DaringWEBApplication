package jp.co.poweredge.store.service;

import jp.co.poweredge.store.domain.Article;
import jp.co.poweredge.store.domain.CartItem;
import jp.co.poweredge.store.domain.ShoppingCart;
import jp.co.poweredge.store.domain.User;


public interface ShoppingCartService {

	ShoppingCart getShoppingCart(User user);
	
	int getItemsNumber(User user);
	
	CartItem findCartItemById(Long cartItemId);
	
	CartItem addArticleToShoppingCart(Article article, User user, int qty, String size);
		
	void clearShoppingCart(User user);
	
	void updateCartItem(CartItem cartItem, Integer qty);

	void removeCartItem(CartItem cartItem);
	
}
