package jp.co.poweredge.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.poweredge.store.domain.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {

}
