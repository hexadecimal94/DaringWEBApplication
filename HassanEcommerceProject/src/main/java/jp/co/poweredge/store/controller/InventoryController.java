package jp.co.poweredge.store.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.poweredge.store.domain.Article;
import jp.co.poweredge.store.domain.CartItem;
import jp.co.poweredge.store.domain.Inventory;
import jp.co.poweredge.store.domain.Order;
import jp.co.poweredge.store.domain.Purchase;
import jp.co.poweredge.store.domain.Sale;
import jp.co.poweredge.store.service.ArticleService;
import jp.co.poweredge.store.service.InventoryService;
import jp.co.poweredge.store.service.OrderService;
import jp.co.poweredge.store.service.PurchaseService;

@Controller
public class InventoryController {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(InventoryController.class);

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ArticleService articleService;

//	@Autowired
//	private SaleRepository saleRepository;

	@Autowired
	private PurchaseService purchaseService;

	@RequestMapping("/inventory")
	public String allcartItem(@RequestParam("id") Long articleId,Model model) {
		logger.warn("request param is : " + articleId);

		Inventory newinventory = new Inventory();

		List<Inventory> inventoryList = inventoryService.findInventoryByArticleId(articleId);

	   /* for(Inventory inventory :inventoryList) {
				logger.warn("inventoryList testing,getArticleId : " + inventory.getArticleId());
				logger.warn("inventoryList testing,getEndDate : " + inventory.getEndDate());
				logger.warn("inventoryList testing,getStartDate : " + inventory.getStartDate());
				logger.warn("inventoryList testing,getQtyRemaining : " + inventory.getQtyRemaining());
			}*/



		List<CartItem> cartitemlist = inventoryService.findCartItemsById(articleId);
		Integer totalqtysold = 0;

		if(cartitemlist.size()==0) {
			//idで持ってきた商品がまだ売れていなかったらcart_item テーブルに入らないその時、cartitemlist == 0
			//その時また同じページにredirect するとりあえず
			Integer totalqtypurchased = 0;
			List<Purchase> purchaselist = purchaseService.findPuchaseByArticleId(articleId);
			for(Purchase purchase:purchaselist) {
				logger.warn("purchase article_id: "+ purchase.getArticleId());
				logger.warn("purchase title is : " + purchase.getTitle());
				logger.warn("purchase date is : " + purchase.getPurchaseDate());
				logger.warn("purchase price is : " + purchase.getPurchasePrice());
				logger.warn("purchase quantity is : " + purchase.getPurchaseQty());
				//for thymeleaf totalqtypurchased attribute
				totalqtypurchased = totalqtypurchased + purchase.getPurchaseQty();
			}

			List<Sale> salelist = new ArrayList<>();
			Article article = articleService.findArticlePriceById(articleId);


			model.addAttribute("articlePrice",article.getPrice());
			model.addAttribute("totalqtysold",totalqtysold);
			model.addAttribute("totalqtypurchased",totalqtypurchased);
			model.addAttribute("salelist",salelist);
			model.addAttribute("cartitemlist", cartitemlist);
			model.addAttribute("purchaselist",purchaselist);
			model.addAttribute("inventoryList", inventoryList);
			model.addAttribute("inventory", newinventory);
			return "adminInventory";
		}


		//
		List<Sale> salelist = new ArrayList<>();
		logger.warn("inventory mapper called : " + cartitemlist.size());

		for(CartItem cartitem: cartitemlist) {
			logger.warn("cartitem article_id : " + cartitem.getArticle().getId());
			logger.warn("cartitem quantity is : " + cartitem.getQty());
			logger.warn("cartitem order_id is : " + cartitem.getOrder().getId());
			Order oneorder = orderService.findOrderWithDetails(cartitem.getOrder().getId());

			logger.warn("oneorder getorderdate is : " + oneorder.getOrderDate());
			logger.warn("sellingPrice total: " + oneorder.getOrderTotal());

			//Sale
			Sale sale = new Sale();
			sale.setArticleId(cartitem.getArticle().getId());
			sale.setOrderDate(oneorder.getOrderDate());
			sale.setQty(cartitem.getQty());
			sale.setSellingPrice(oneorder.getOrderTotal());
			salelist.add(sale);
			//saleRepository.save(sale);
		}


		//looping the sale list for finding the total quantity sold for that particular product
		for(Sale sale:salelist) {
			totalqtysold = totalqtysold + sale.getQty();
		}
		logger.warn("totalquantity sold is: " + totalqtysold);
		List<Purchase> purchaselist = purchaseService.findPuchaseByArticleId(articleId);
		Integer totalqtypurchased = 0;
		for(Purchase purchase:purchaselist) {
			logger.warn("purchase article_id: "+ purchase.getArticleId());
			logger.warn("purchase title is : " + purchase.getTitle());
			logger.warn("purchase date is : " + purchase.getPurchaseDate());
			logger.warn("purchase price is : " + purchase.getPurchasePrice());
			logger.warn("purchase quantity is : " + purchase.getPurchaseQty());
			//for thymeleaf totalqtypurchased attribute
			totalqtypurchased = totalqtypurchased + purchase.getPurchaseQty();
		}
		Article article = articleService.findArticlePriceById(articleId);
		model.addAttribute("articlePrice",article.getPrice());
		model.addAttribute("totalqtysold",totalqtysold);
		model.addAttribute("totalqtypurchased",totalqtypurchased);
		model.addAttribute("salelist",salelist);
		model.addAttribute("cartitemlist", cartitemlist);
		model.addAttribute("purchaselist",purchaselist);
		model.addAttribute("inventoryList", inventoryList);
		model.addAttribute("inventory", newinventory);
		return "adminInventory";
	}


	//新しい棚卸するマッピング
	@RequestMapping("/inventory/new")
	public String newTanaoroshi(@RequestParam("articleId") Long articleId) {
		List<Inventory> inventoryList = inventoryService.findInventoryByArticleId(articleId);

		//Date inventoryEndDdate = "2022-05-31";
		//LocalDate inventoryActionDate = LocalDate.now();
		//Date.from(inventoryActionDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


		//End date+1day should be the first date of todays inventory
		//But before that we need to get all the inventory by articleid and sort it out according to End date
		// List<Inventory> inventorylist = {Inventory1.getEndDate(),Inventory2.getEndDate(), Inventory3.getEndDate(), Inventory4.getEndDate(),Inventory5.getEndDate()}

		for(Inventory inventory: inventoryList){
			inventory.getEndDate();
		}


		return "redirect:adminInventory";
	}

	//inventory new form mapping
	@RequestMapping("/inventory/new-form")
	public String newTanaoroshi(@ModelAttribute("inventory") Inventory forminventory, Model model) {
		logger.warn("forminventory : " + forminventory.getArticleId());
		logger.warn("forminventory : " + forminventory.getEndDate());
		logger.warn("forminventory Tanaoroshizaiko : " + forminventory.getTanaoroshizaiko());
		logger.warn("forminventory QtyRemaining : " + forminventory.getQtyRemaining());
		logger.warn("forminventory Reason : " + forminventory.getReason());

		//Inventory inventorylast = inventoryService.findlastInventory(forminventory.getArticleId());

		Inventory inventorylast = inventoryService.findEndDateFromInventoryByArticleId(forminventory.getArticleId());
		logger.warn("inventorylast is : " + inventorylast.getEndDate());
		inventoryService.saveInventory(forminventory);
		return "redirect:/list-purchase";

	}
}

//List<Purchase> purchaseListDatebetweenStartDateandEndDate = purchaseService.findAllByArticleIdandDate(forminventory.getStartDate(),forminventory.getEndDate());
//List<Purchase> purchaseListWithArticleIdandDate = new ArrayList<>();
//Long inventoryarticleId = forminventory.getArticleId();
//for(Purchase purchaseitem: purchaseListDatebetweenStartDateandEndDate) {
//	logger.warn("purchaseList is purchase.getArticleId() : " + purchaseitem.getArticleId());
//	logger.warn("purchaseList is purchase.getArticleId() : " + purchaseitem.getPurchaseQty());
//	logger.warn("purchaseList is purchase.getArticleId() : " + purchaseitem.getPurchaseDate());
//	logger.warn("purchaseList is purchase.getArticleId() : " + purchaseitem.getTitle());
//
//	if(purchaseitem.getArticleId().equals(inventoryarticleId)) {
//		logger.warn("true");
//		purchaseListWithArticleIdandDate.add(purchaseitem);
//	}
//}
//logger.warn("size of the list: " + purchaseListWithArticleIdandDate.size());
//
//
////Salelist 処理
//List<CartItem> cartitemlist = inventoryService.findCartItemsById(forminventory.getArticleId());
//List<Sale> salelist = new ArrayList<>();
//List<Sale> finalsaleListwithDateBetween = new ArrayList<>();
//logger.warn("inventory mapper called : " + cartitemlist.size());
//
//for(CartItem cartitem: cartitemlist) {
//	logger.warn("cartitem article_id : " + cartitem.getArticle().getId());
//	logger.warn("cartitem quantity is : " + cartitem.getQty());
//	logger.warn("cartitem order_id is : " + cartitem.getOrder().getId());
//	Order oneorder = orderService.findOrderWithDetails(cartitem.getOrder().getId());
//
//	logger.warn("oneorder getorderdate is : " + oneorder.getOrderDate());
//	logger.warn("sellingPrice total: " + oneorder.getOrderTotal());
//
//	//Sale
//	Sale sale = new Sale();
//	sale.setArticleId(cartitem.getArticle().getId());
//	sale.setOrderDate(oneorder.getOrderDate());
//	sale.setQty(cartitem.getQty());
//	sale.setSellingPrice(oneorder.getOrderTotal());
//	salelist.add(sale);
//	//saleRepository.save(sale);
//}
//
//logger.warn("salelist size is : " + salelist.size());
//for(Sale saleitem: salelist) {
//	logger.warn("salelist.getorderdate is: " + saleitem.getOrderDate());
//	logger.warn("salelist.articleId is: " + saleitem.getArticleId());
//	logger.warn("salelist.qtysold is: " + saleitem.getQty());
//	//historydate == startdate , endate == futuredate
//	if(saleitem.getOrderDate().after(forminventory.getStartDate())&&saleitem.getOrderDate().before(forminventory.getEndDate())) {
//		finalsaleListwithDateBetween.add(saleitem);
//	}
//}
//
//logger.warn("finalsaleListwithDateBetween size is : " + finalsaleListwithDateBetween.size());
//model.addAttribute("finalsaleListwithDateBetween", finalsaleListwithDateBetween);
//model.addAttribute("purchaseListWithArticleIdandDate",purchaseListWithArticleIdandDate);
//return "adminNewInventoryForm";