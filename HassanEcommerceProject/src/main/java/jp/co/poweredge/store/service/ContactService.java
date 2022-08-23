package jp.co.poweredge.store.service;

import java.util.List;

import jp.co.poweredge.store.domain.Contact;

public interface ContactService {

	List<Contact> findAllInquiry();

	Contact saveInquiry(Contact contact);

	Contact findBycontactId(Long contactId);
}
