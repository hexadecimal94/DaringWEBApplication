package jp.co.poweredge.store.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.poweredge.store.domain.CartItem;
import jp.co.poweredge.store.domain.Inventory;
import jp.co.poweredge.store.domain.Order;
import jp.co.poweredge.store.repository.CartItemRepository;
import jp.co.poweredge.store.repository.InventoryRepository;
import jp.co.poweredge.store.repository.OrderRepository;
import jp.co.poweredge.store.service.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService{
	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private CartItemRepository cartitemRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public List<CartItem> findCartItemsById(Long id){
		return cartitemRepository.findAllByArticleIdAndOrderIsNotNull(id);
	}

	@Override
	public Optional<Order> findOrderById(Long id) {
		return orderRepository.findById(id);
	}


	//Original InventoryRepository Method
	@Override
	public List<Inventory> findInventoryByArticleId(Long articleId){
		List<Inventory> inventoryList = inventoryRepository.findAllByArticleId(articleId);
		return inventoryList;
	}

	@Override
	public Inventory saveInventory(Inventory inventory) {
		return inventoryRepository.save(inventory);
	}

	@Override
	public Inventory findEndDateFromInventoryByArticleId(Long articleid) {
		return inventoryRepository.findlastInventory(articleid);
	}
}
