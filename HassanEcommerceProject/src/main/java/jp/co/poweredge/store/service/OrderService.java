package jp.co.poweredge.store.service;

import java.util.List;

import jp.co.poweredge.store.domain.Order;
import jp.co.poweredge.store.domain.Payment;
import jp.co.poweredge.store.domain.Shipping;
import jp.co.poweredge.store.domain.ShoppingCart;
import jp.co.poweredge.store.domain.User;

public interface OrderService {

	//ユーザが注文する時の新規オーダー情報が作成する
	Order createOrder(ShoppingCart shoppingCart, Shipping shippingAddress, Payment payment, User user);

	//user_idによってオーダー情報を持って来る
	List<Order> findByUser(User user);

	Order findOrderWithDetails(Long id);

	List<Order> findAllOrders();

//	public void  updateOrderRecord(Long id, String haitatsu);

	List<Order> findlistOfOrderByOrderDateandMonth(String month);
}
