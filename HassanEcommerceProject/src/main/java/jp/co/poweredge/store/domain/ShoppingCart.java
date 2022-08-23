package jp.co.poweredge.store.domain;

import java.math.BigDecimal;
import java.util.List;

public class ShoppingCart {

	private List<CartItem> cartItems;

	//コンストラクタ
	public ShoppingCart(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}
	//ショッピングカートの中にあるすべてのアイテムのトータルをsumする
	public BigDecimal getGrandTotal() {
		BigDecimal cartTotal = new BigDecimal(0);
		for (CartItem item : this.cartItems) {
			cartTotal = cartTotal.add(item.getSubtotal());
		}
		return cartTotal;
	}

	//cartitems リストからかどうか確認する、戻り値boolean
	public boolean isEmpty() {
		return cartItems.isEmpty();
	}

	public void removeCartItem(CartItem cartItem) {
		cartItems.removeIf(item -> item.getId() == cartItem.getId());
	}

	public void clearItems() {
		cartItems.clear();
	}

	public CartItem findCartItemByArticleAndSize(Long id, String size) {
		for (CartItem item : this.cartItems) {
			if (item.getArticle().getId().equals(id) && item.getSize().equals(size)) {
				return item;
			}
		}
		return null;
	}

	public int getItemCount() {
		return this.cartItems.size();
	}

	public List<CartItem> getCartItems() {
		return cartItems;
	}
	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}


}
