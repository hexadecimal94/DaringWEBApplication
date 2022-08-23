package jp.co.poweredge.store.controller;

import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.poweredge.store.domain.Article;
import jp.co.poweredge.store.form.ArticleFilterForm;
import jp.co.poweredge.store.service.ArticleService;
import jp.co.poweredge.store.type.SortFilter;

@Controller
public class StoreController {
	//ログ出すために持ってきたオブジェクトです。消しても本アプリケーションと問題ないです
	private static final Logger logger = LoggerFactory.getLogger(StoreController.class);

	@Autowired
	private ArticleService articleService;

	@RequestMapping("/store")
	public String store(@ModelAttribute("filters") ArticleFilterForm filters, Model model) {
		//ログ
		logger.warn("StoreController.store メソッドを呼び出された");


		Integer page = filters.getPage();
		logger.warn("filters.getPage() is : " + page);


		int pagenumber = (page == null ||  page <= 0) ? 0 : page-1;
		SortFilter sortFilter = new SortFilter(filters.getSort());
		Page<Article> pageresult = articleService.findArticlesByCriteria(PageRequest.of(pagenumber,9, sortFilter.getSortType()),
																filters.getPricelow(),
																filters.getPricehigh(),
																filters.getSize(),
																filters.getCategory(),
																filters.getBrand(),
																filters.getSearch());


		logger.warn("pageresult.getContent(): " + pageresult.getContent());
		logger.warn("pageresult.getTotalElements(): " + pageresult.getTotalElements());
		logger.warn("allCategories: " + articleService.getAllCategories());
		logger.warn("allBrands: " + articleService.getAllBrands());
		logger.warn("allSizes: " + articleService.getAllSizes());



		model.addAttribute("allCategories", articleService.getAllCategories());
		model.addAttribute("allBrands", articleService.getAllBrands());
		model.addAttribute("allSizes", articleService.getAllSizes());

		//最初のぺーじでは9個の商品データを送ります。
		model.addAttribute("articles", pageresult.getContent());

		model.addAttribute("totalitems", pageresult.getTotalElements());
		model.addAttribute("itemsperpage",9);
		return "store";
	}


	@RequestMapping("/article-detail")
	public String articleDetail(@PathParam("id") Long id, Model model) {
		//ログ
		logger.warn("StoreController.articleDetail メソッドを呼び出された " + "article_id=" + id);


		Article article = articleService.findArticleById(id);
		model.addAttribute("article", article);
		model.addAttribute("notEnoughStock", model.asMap().get("notEnoughStock"));
		model.addAttribute("addArticleSuccess", model.asMap().get("addArticleSuccess"));

		return "articleDetail";
	}


}
