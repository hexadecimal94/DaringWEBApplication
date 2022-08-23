package jp.co.poweredge.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jp.co.poweredge.store.domain.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory,Long>{

	List<Inventory> findAllByArticleId(Long articleId);

//	@Query("select a from Inventory inventory where inventory.endDate <= :creationDateTime")
//    List<Inventory> findAllWithCreationDateTimeBefore(
//      @Param("creationDateTime") Date creationDateTime);


	@Query(value ="SELECT * FROM inventory i WHERE i.article_id = ?1 and i.end_date = (SELECT MAX(i.end_date) FROM inventory i)", nativeQuery=true)
	Inventory findlastInventory(Long articleid);




}
