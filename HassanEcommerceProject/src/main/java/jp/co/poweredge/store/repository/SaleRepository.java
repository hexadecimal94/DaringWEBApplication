package jp.co.poweredge.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.poweredge.store.domain.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long>{

}
