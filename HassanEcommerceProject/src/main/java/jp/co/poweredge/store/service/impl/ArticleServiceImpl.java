package jp.co.poweredge.store.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.poweredge.store.domain.Article;
import jp.co.poweredge.store.repository.ArticleRepository;
import jp.co.poweredge.store.repository.ArticleSpecification;
import jp.co.poweredge.store.service.ArticleService;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

	@Autowired
	private ArticleRepository articleRepository;

	//index ページのfeatured 商品の数を指定できるapplication.properties	のところから
	@Value("${articleservice.featured-items-number}")
	private int featuredArticlesNumber;

	@Override
	public List<Article> findAllArticles() {
		return (List<Article>) articleRepository.findAllEagerBy();
	}

	@Override
	public Page<Article> findArticlesByCriteria(Pageable pageable, Integer priceLow, Integer priceHigh,
										List<String> sizes, List<String> categories, List<String> brands, String search) {
		Page<Article> page = articleRepository.findAll(ArticleSpecification.filterBy(priceLow, priceHigh, sizes, categories, brands, search), pageable);
        return page;
	}

	@Override
	public List<Article> findFirstArticles() {
		return articleRepository.findAll(PageRequest.of(0,featuredArticlesNumber)).getContent();
	}

	@Override
	public Article findArticleById(Long id) {
		Optional<Article> opt = articleRepository.findById(id);
		return opt.get();
	}

	@Override
	public Article findArticlePriceById(Long id) {
		Article article = articleRepository.findArticlePriceById(id);
		return article;
	}

	@Override
	@CacheEvict(value = { "sizes", "categories", "brands" }, allEntries = true)
	public Article saveArticle(Article article) {
		return articleRepository.save(article);
	}

	@Override
	@CacheEvict(value = { "sizes", "categories", "brands" }, allEntries = true)
	public void deleteArticleById(Long id) {
		articleRepository.deleteById(id);
	}

	//すべてのサイズとりあえずキャッシュにいれて置く［38,39,40,41,42,43,44,45]
	//メソッドの戻り値がキャッシュに入れて置く
	@Override
	@Cacheable("sizes")
	public List<String> getAllSizes() {
		return articleRepository.findAllSizes();
	}

	//すべてのカテゴリーもとりあえずキャッシュにいれて置く
	@Override
	@Cacheable("categories")
	public List<String> getAllCategories() {
		return articleRepository.findAllCategories();
	}

	//すべてのブランドもとりあえずキャッシュにいれて置く[John Foos, Other, Adidas,Topper,Nazerana,Fila,New Balance, Puma, Footy, Rave, Reebok, Savage, Isabella]
	@Override
	@Cacheable("brands")
	public List<String> getAllBrands() {
		return articleRepository.findAllBrands();
	}

}
