package jp.co.poweredge.store.service;

import java.util.Date;
import java.util.List;

import jp.co.poweredge.store.domain.Purchase;

public interface PurchaseService {
	List<Purchase> findAllPurchases();

	List<Purchase> findPuchaseByArticleId(Long articleId);

	//using query annotation
	List<Purchase> findAllByArticleIdandDate(Date startDate,Date endDate);
}
