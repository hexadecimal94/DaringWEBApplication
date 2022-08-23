package jp.co.poweredge.store.service;

import java.util.List;
import java.util.Optional;

import jp.co.poweredge.store.domain.CartItem;
import jp.co.poweredge.store.domain.Inventory;
import jp.co.poweredge.store.domain.Order;

public interface InventoryService {

	List<CartItem> findCartItemsById(Long id);

	Optional<Order> findOrderById(Long id);


	//Original InventoryRepository method

	List<Inventory> findInventoryByArticleId(Long articleId);

	Inventory saveInventory(Inventory inventory);

	Inventory findEndDateFromInventoryByArticleId(Long articleid);

}
