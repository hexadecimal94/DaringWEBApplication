package jp.co.poweredge.store.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.poweredge.store.domain.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

	List<Purchase> findPurchaseByArticleId(Long articleId);

	@Query("SELECT p FROM Purchase p WHERE purchaseDate BETWEEN :startDate AND :endDate")
	List<Purchase> findAllByArticleIdAndDate(@Param("startDate")Date startDate,@Param("endDate")Date endDate);

}
