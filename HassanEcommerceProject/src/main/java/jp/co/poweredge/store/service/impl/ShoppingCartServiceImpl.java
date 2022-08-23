package jp.co.poweredge.store.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jp.co.poweredge.store.domain.Article;
import jp.co.poweredge.store.domain.CartItem;
import jp.co.poweredge.store.domain.ShoppingCart;
import jp.co.poweredge.store.domain.User;
import jp.co.poweredge.store.repository.CartItemRepository;
import jp.co.poweredge.store.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Override
	public ShoppingCart getShoppingCart(User user) {
		return new ShoppingCart(cartItemRepository.findAllByUserAndOrderIsNull(user));
	}

	@Override
	@Cacheable("itemcount")
	public int getItemsNumber(User user) {
		return cartItemRepository.countDistinctByUserAndOrderIsNull(user);
	}

	@Override
	public CartItem findCartItemById(Long cartItemId) {

		Optional<CartItem> opt = cartItemRepository.findById(cartItemId);
		return opt.get();
	}

	//商品とユーザと個数とサイズをデータベースに保存する

	@Override
	@CacheEvict(value = "itemcount", allEntries = true)
	public CartItem addArticleToShoppingCart(Article article, User user, int qty, String size) {
		ShoppingCart shoppingCart = this.getShoppingCart(user);
		CartItem cartItem = shoppingCart.findCartItemByArticleAndSize(article.getId(), size);
		if (cartItem != null && cartItem.hasSameSizeThan(size)) {
			cartItem.addQuantity(qty);
			cartItem.setSize(size);
			cartItem = cartItemRepository.save(cartItem);
		} else {
			cartItem = new CartItem();
			cartItem.setUser(user);
			cartItem.setArticle(article);
			cartItem.setQty(qty);
			cartItem.setSize(size);
			cartItem = cartItemRepository.save(cartItem);
		}
		return cartItem;
	}

	//パラメータのcartItem.getId()でデータベースのカートアイテムテーブルから一つのアイテムを削除する
	@Override
	@CacheEvict(value = "itemcount", allEntries = true)
	public void removeCartItem(CartItem cartItem) {
		cartItemRepository.deleteById(cartItem.getId());
	}

	@Override
	@CacheEvict(value = "itemcount", allEntries = true)
	public void updateCartItem(CartItem cartItem, Integer qty) {
		if (qty == null || qty <= 0) {
			this.removeCartItem(cartItem);
		} else if (cartItem.getArticle().hasStock(qty)) {
			cartItem.setQty(qty);
			cartItemRepository.save(cartItem);
		}
	}

	@Override
	@CacheEvict(value = "itemcount", allEntries = true)
	public void clearShoppingCart(User user) {
		cartItemRepository.deleteAllByUserAndOrderIsNull(user);
	}
}
