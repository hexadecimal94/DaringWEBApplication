package jp.co.poweredge.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import jp.co.poweredge.store.domain.CartItem;
import jp.co.poweredge.store.domain.User;

public interface CartItemRepository extends CrudRepository<CartItem, Long> {

	@EntityGraph(attributePaths = { "article" })
	List<CartItem> findAllByUserAndOrderIsNull(User user);

	void deleteAllByUserAndOrderIsNull(User user);

	int countDistinctByUserAndOrderIsNull(User user);

	//@Query("Select * FROM CartItem where article_id = 424 and is not NULL")
	List<CartItem>findAllByArticleIdAndOrderIsNotNull(Long id);
}
