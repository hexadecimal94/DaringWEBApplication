package jp.co.poweredge.store.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.poweredge.store.domain.Article;
import jp.co.poweredge.store.domain.Purchase;
import jp.co.poweredge.store.repository.PurchaseRepository;
import jp.co.poweredge.store.service.ArticleService;
import jp.co.poweredge.store.service.PurchaseService;

@Controller
public class PurchaseController {

	private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);
	@Autowired
	private ArticleService articleService;

	@Autowired
	private PurchaseService purchaseService;
	@Autowired
	private PurchaseRepository purchaseRepository;

	@RequestMapping("/list-purchase")
	public String listOfPurchase(Model model) {
		List<Purchase> purchaselist = purchaseService.findAllPurchases();
		model.addAttribute("purchaselist",purchaselist);
		return "purchaselist";
	}

	@RequestMapping("/new-purchase")
	public String newPurchsae(@RequestParam("articleId") Long articleId,Model model) {
		Purchase purchase = new Purchase();
		purchase.setArticleId(articleId);
		purchase.setTitle(articleService.findArticleById(articleId).getTitle());
		//model.addAttribute("articleId",articleId);
		model.addAttribute("purchase",purchase);
		return "adminNewPurchase";
	}

	@RequestMapping("/new-purchase/add")
	public String addPurchaseItem(@ModelAttribute("purchase") Purchase purchase){
		logger.warn("purchase data, article id : " + purchase.getArticleId());
		logger.warn("purchase data, title : " + purchase.getTitle());
		logger.warn("purchase data, purchasePrice id : " + purchase.getPurchasePrice());
		logger.warn("purchase data, purchaseQty : " + purchase.getPurchaseQty());
		logger.warn("purchase data, purchaseDate : " + purchase.getPurchaseDate());


		//purchaserepositoryに保存する
		purchaseRepository.save(purchase);

		Article article = articleService.findArticleById(purchase.getArticleId());
		Integer currentStock = article.getStock();
		Integer purchaseStock = purchase.getPurchaseQty();

		//logger.warn("currentstock: " + currentStock);
		//logger.warn("purchaseStock: " + purchaseStock);

		//article_id でとってきた商品のstockを毎回purhcaserepositoryにデータ追加したら追加される
		article.setStock(currentStock+purchaseStock);
		articleService.saveArticle(article);
		//logger.warn("articlegetstock: " + article.getStock());

		return "redirect:/list-purchase";
	}


}
