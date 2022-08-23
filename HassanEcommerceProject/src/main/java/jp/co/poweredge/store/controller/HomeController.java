package jp.co.poweredge.store.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.poweredge.store.domain.Article;
import jp.co.poweredge.store.service.ArticleService;

@Controller
public class HomeController {
	//ログ出すために持ってきたオブジェクトです。消しても本アプリケーションと問題ないです
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private ArticleService articleService;


	@RequestMapping("/")
	public String index(Model model) {
		//ログ
		logger.warn("HomeController.index メソッドを呼び出された");

		List<Article> articles = articleService.findFirstArticles();
		model.addAttribute("articles", articles);
		//最初ページに8-9個ぐらいの商品を送る
		return "index";
	}


}
