package jp.co.poweredge.store.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.poweredge.store.domain.Article;
import jp.co.poweredge.store.domain.CartItem;
import jp.co.poweredge.store.domain.Order;
import jp.co.poweredge.store.domain.Payment;
import jp.co.poweredge.store.domain.Shipping;
import jp.co.poweredge.store.domain.ShoppingCart;
import jp.co.poweredge.store.domain.User;
import jp.co.poweredge.store.repository.ArticleRepository;
import jp.co.poweredge.store.repository.CartItemRepository;
import jp.co.poweredge.store.repository.OrderRepository;
import jp.co.poweredge.store.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	CartItemRepository cartItemRepository;

	@Autowired
	ArticleRepository articleRepository;

	@Override
	@Transactional
	@CacheEvict(value = "itemcount", allEntries = true)
	public synchronized Order createOrder(ShoppingCart shoppingCart, Shipping shipping, Payment payment, User user) {
		Order order = new Order();
		order.setUser(user);
		order.setPayment(payment);
		order.setShipping(shipping);
		order.setOrderTotal(shoppingCart.getGrandTotal());
		shipping.setOrder(order);
		payment.setOrder(order);
		LocalDate today = LocalDate.now();
		LocalDate estimatedDeliveryDate = today.plusDays(5);

		order.setOrderDate(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));


		order.setShippingDate(Date.from(estimatedDeliveryDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		order.setOrderStatus("In Progress");

		order = orderRepository.save(order);

		//カートに入っているcartItemのリストもらって
		List<CartItem> cartItems = shoppingCart.getCartItems();
		for (CartItem item : cartItems) {
			Article article = item.getArticle();
			//article テーブルから在庫を減らす
			article.decreaseStock(item.getQty());
			//在庫減らしてそのarticle またデータベースに保存する
			articleRepository.save(article);
			item.setOrder(order);
			cartItemRepository.save(item);
		}
		return order;
	}

	@Override
	public Order findOrderWithDetails(Long id) {
		return orderRepository.findEagerById(id);
	}

	public List<Order> findByUser(User user) {
		return orderRepository.findByUser(user);
	}



	//admin page orderlist method
	@Override
	public List<Order> findAllOrders(){
		return (List<Order>) orderRepository.findAll();
	}

//	@Override
//	public void updateOrderRecord(Long id, String haitatsu) {
//		orderRepository.sa
//	}

	@Override
	public List<Order>findlistOfOrderByOrderDateandMonth(String month) {
		return orderRepository.findOrderByOrderDateandMonth(month);
	}

}
