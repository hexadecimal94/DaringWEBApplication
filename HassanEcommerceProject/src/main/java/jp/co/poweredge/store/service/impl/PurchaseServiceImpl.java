package jp.co.poweredge.store.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.poweredge.store.domain.Purchase;
import jp.co.poweredge.store.repository.PurchaseRepository;
import jp.co.poweredge.store.service.PurchaseService;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {
	@Autowired
	private PurchaseRepository purchaseRepository;

	@Override
	public List<Purchase>findAllPurchases() {
		return purchaseRepository.findAll();
	}

	@Override
	public List<Purchase> findPuchaseByArticleId(Long articleId){
		return purchaseRepository.findPurchaseByArticleId(articleId);
	}


	@Override
	public List<Purchase> findAllByArticleIdandDate(Date startDate, Date endDate){
		return purchaseRepository.findAllByArticleIdAndDate(startDate,endDate);
	}
}
