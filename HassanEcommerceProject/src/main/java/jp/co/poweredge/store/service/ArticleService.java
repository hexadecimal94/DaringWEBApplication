package jp.co.poweredge.store.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jp.co.poweredge.store.domain.Article;

public interface ArticleService {

	//商品をデータベースから読み取る
	List<Article> findAllArticles();

	Page<Article> findArticlesByCriteria(Pageable pageable, Integer priceLow, Integer priceHigh, List<String> sizes,
			List<String> categories, List<String> brands, String search);

	List<Article> findFirstArticles();

	//IDで商品をデータベースから探し出す
	Article findArticleById(Long id);

	//値段確認するために
	Article findArticlePriceById(Long id);

	//商品をデータベースに保存する
	Article saveArticle(Article article);

	//商品をデータベースから削除する
	void deleteArticleById(Long id);

	//("SELECT DISTINCT s.value FROM Size s")これを実行してDISTINCTサイズをリストに詰める
	List<String> getAllSizes();

	//("SELECT DISTINCT s.value FROM Size s")これを実行してDISTINCTカテゴリーをリストに詰める
	List<String> getAllCategories();

	//("SELECT DISTINCT s.value FROM Size s")これを実行してDISTINCTブランドをリストに詰める
	List<String> getAllBrands();
}
