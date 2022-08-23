package jp.co.poweredge.store.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.poweredge.store.domain.Article;
import jp.co.poweredge.store.domain.ArticleBuilder;
import jp.co.poweredge.store.domain.Brand;
import jp.co.poweredge.store.domain.Category;
import jp.co.poweredge.store.domain.Order;
import jp.co.poweredge.store.domain.Size;
import jp.co.poweredge.store.domain.User;
import jp.co.poweredge.store.service.ArticleService;
import jp.co.poweredge.store.service.OrderService;
import jp.co.poweredge.store.service.UserService;

@Controller
@RequestMapping("/article")
public class ArticleController {
	//このページは管理者側の商品マスタのためのページです。


	//ログ出すために持ってきたオブジェクトです。
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ArticleController.class);

	//アドミンページの商品の取得、追加、編集、削除ためにarticleserviceを呼び出す
	@Autowired
	private ArticleService articleService;

	//売上情報のためにorderserviceを呼び出す
	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	//注文一覧ページを表示する
	@RequestMapping("/order-list")
	public String orderlistPost(Model model) {
		Order order = new Order();
		model.addAttribute("order",order);
		List<Order> orders = orderService.findAllOrders();
		orders.remove(0);


		//list for chart purpose
		List<String> totallist = new ArrayList<>();
		List<String> datelist = new ArrayList<>();

		//月別売上のために
		List<String> monthsaleslist = new ArrayList<>();
		monthsaleslist.add("0");
		monthsaleslist.add("0");
		monthsaleslist.add("0");
		monthsaleslist.add("0");
		monthsaleslist.add("0");


//		for(int i = 1; i<12; i++) {
//			logger.warn("i is " + i);
//			//Getting all the orders from a particular month
//			List<Order> imonthorderlist = orderService.findlistOfOrderByOrderDateandMonth(Integer.toString(i));
//			logger.warn("imonthorderlist is : " + imonthorderlist.size());
//			if(imonthorderlist.size()!=0) {
//				Integer ordertotal = 0;
//				for(Order orderitem: imonthorderlist) {
//					BigDecimal bd = orderitem.getOrderTotal();
//					ordertotal = ordertotal + bd.intValueExact();
//				}
//				monthsaleslist.add(Integer.toString(ordertotal));
//			}
//			else {
//				monthsaleslist.add("0");
//			}
//
//		}

		logger.warn("monthsaleslist is : " + monthsaleslist.size());



		Integer totalsales = 0;
		for(Order order1:orders) {
			//for calculating 合計売上
			BigDecimal bigdecima = order1.getOrderTotal();
			totalsales = totalsales + bigdecima.intValue();
			//チャートの情報
			totallist.add(bigdecima.toPlainString());

			//Date data to String
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    String strDate = dateFormat.format(order1.getOrderDate());
			datelist.add(strDate);
		}

		monthsaleslist.add(totalsales.toString());
		logger.warn("Totalsales is : " + totalsales);

		//ユーザテーブルにユーザ数を数える
		List<User> usercounts = userService.findAllUsers();


		model.addAttribute("label",datelist);
		model.addAttribute("point",totallist);


		model.addAttribute("monthsales",monthsaleslist);
		model.addAttribute("totalRevenue", totalsales);
		model.addAttribute("orders", orders);
		model.addAttribute("usercounts", usercounts);
		return "adminOrderList";
	}


//	//管理者側から注文完了ボタン押された時の処理
//	@RequestMapping("/order/confirm")
//	public String orderConfirmed(@RequestParam("id") Long id) {
//		logger.warn("order_id is : " + id);
//
//		Order order = orderService.findOrderWithDetails(id);
//		order.setOrderStatus("注文配達完了");
//
////		orderService.updateOrderRecord(id,"注文配達完了");
//		return "redirect:/order-list";
//	}


	//Admin側のユーザ情報を管理
	@RequestMapping("/user-list")
	public String userlistPost(Model model) {
		User user = new User();
		model.addAttribute("user",user);
		List<User> users = userService.findAllUsers();

		for(@SuppressWarnings("unused") User useritem: users) {
			logger.warn("user accountstatus : " + user.getAccountstatus());
		}

		model.addAttribute("users", users);
		return "adminUserList";
	}

	//Admin側の商品のCRUD操作
	@RequestMapping("/add")
	public String addArticle(Model model) {
		Article article = new Article();
		model.addAttribute("article", article);
		model.addAttribute("allSizes", articleService.getAllSizes());
		model.addAttribute("allBrands", articleService.getAllBrands());
		model.addAttribute("allCategories", articleService.getAllCategories());
		return "addArticle";
	}

	//Admin側が商品追加したらデータベースにその商品をinsertする(PRG)post,redirect,get式
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String addArticlePost(@ModelAttribute("article") Article article, HttpServletRequest request) {
		Article newArticle = new ArticleBuilder()
				.withTitle(article.getTitle())
				.stockAvailable(article.getStock())
				.withPrice(article.getPrice())
				.imageLink(article.getPicture())
//				.withPurchasePrice(article.getPurchasePrice())
//				.withPurchaseDate(article.getPurchaseDate())
				.sizesAvailable(Arrays.asList(request.getParameter("size").split("\\s*,\\s*")))
				.ofCategories(Arrays.asList(request.getParameter("category").split("\\s*,\\s*")))
				.ofBrand(Arrays.asList(request.getParameter("brand").split("\\s*,\\s*")))
				.build();
		articleService.saveArticle(newArticle);
		return "redirect:article-list";
	}

	//商品一覧ページ
	@RequestMapping("/article-list")
	public String articleList(Model model) {
		List<Article> articles = articleService.findAllArticles();

		List<String> brands = articleService.getAllBrands();
		List<String> sizes = articleService.getAllSizes();
		List<String> categories = articleService.getAllCategories();

		//zaiko no sukunai shouhin nado miseru tameni
		List<Article> stockless5 = new ArrayList<>();
		List<Article> stockless5to20 = new ArrayList<>();
		List<Article> stockless20to40 = new ArrayList<>();
		List<Article> stockgreaterthan40 = new ArrayList<>();

		for(Article article: articles) {
			if(article.getStock()<5) {
				stockless5.add(article);
			}
			else if(article.getStock()>=5 && article.getStock()<20){
				stockless5to20.add(article);
			}
			else if(article.getStock()>=20 && article.getStock()<40){
				stockless20to40.add(article);
			}
			else if(article.getStock()>40){
				stockgreaterthan40.add(article);
			}
		}



		model.addAttribute("stockgreaterthan40",stockgreaterthan40);
		model.addAttribute("stockless20to40",stockless20to40);
		model.addAttribute("stockless5to20",stockless5to20);
		model.addAttribute("stockless5list",stockless5);


		model.addAttribute("articles", articles);

		//総ブランド数のdiv
		model.addAttribute("brandsize",brands);

		//総サイズ数のdiv
		model.addAttribute("sizes",sizes);

		//総カテゴリー数のdiv
		model.addAttribute("categories",categories);

		return "articleList";
	}



	//商品編集ページ
	@RequestMapping("/edit")
	public String editArticle(@RequestParam("id") Long id, Model model) {
		Article article = articleService.findArticleById(id);
		String preselectedSizes = "";
		for (Size size : article.getSizes()) {
			preselectedSizes += (size.getValue() + ",");
		}
		String preselectedBrands = "";
		for (Brand brand : article.getBrands()) {
			preselectedBrands += (brand.getName() + ",");
		}
		String preselectedCategories = "";
		for (Category category : article.getCategories()) {
			preselectedCategories += (category.getName() + ",");
		}

		logger.warn("Article edit-sizes : " + preselectedSizes);
		logger.warn("Article edit-brands : " + preselectedBrands);
		logger.warn("Article edit-categories : " + preselectedCategories);


		model.addAttribute("article", article);
		model.addAttribute("preselectedSizes", preselectedSizes);
		model.addAttribute("preselectedBrands", preselectedBrands);
		model.addAttribute("preselectedCategories", preselectedCategories);
		model.addAttribute("allSizes", articleService.getAllSizes());
		model.addAttribute("allBrands", articleService.getAllBrands());
		model.addAttribute("allCategories", articleService.getAllCategories());
		return "editArticle";
	}

	//Admin側が商品編集したらデータベースにその商品をアップデートする(PRG)post,redirect,get式
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String editArticlePost(@ModelAttribute("article") Article article, HttpServletRequest request) {

//		logger.warn("edit method called, purchaseprice is: " + article.getPurchasePrice());
		logger.warn("edit method called, hanbai price is: " + article.getPrice());
		logger.warn("edit method called, stock is: " + article.getStock());
		logger.warn("edit method called, picture path is: " + article.getPicture());
//		logger.warn("edit method called, purchaseDate is : " + article.getPurchaseDate());



		Article newArticle = new ArticleBuilder()
				.withTitle(article.getTitle())
				.stockAvailable(article.getStock())
				.withPrice(article.getPrice())
//				.withPurchasePrice(article.getPurchasePrice())
//				.withPurchaseDate(article.getPurchaseDate())
				.imageLink(article.getPicture())
				.sizesAvailable(Arrays.asList(request.getParameter("size").split("\\s*,\\s*")))
				.ofCategories(Arrays.asList(request.getParameter("category").split("\\s*,\\s*")))
				.ofBrand(Arrays.asList(request.getParameter("brand").split("\\s*,\\s*")))
				.build();
		newArticle.setId(article.getId());
		articleService.saveArticle(newArticle);
		return "redirect:article-list";
	}

	@RequestMapping("/delete")
	public String deleteArticle(@RequestParam("id") Long id) {
		articleService.deleteArticleById(id);
		return "redirect:article-list";
	}


	@RequestMapping("/admin-dashboard")
	public String adminDashboard() {
		return "adminDashboard";
	}

}
