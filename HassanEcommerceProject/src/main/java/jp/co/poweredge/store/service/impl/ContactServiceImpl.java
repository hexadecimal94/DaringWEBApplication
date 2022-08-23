package jp.co.poweredge.store.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.poweredge.store.domain.Contact;
import jp.co.poweredge.store.repository.ContactRepository;
import jp.co.poweredge.store.service.ContactService;

@Service
public class ContactServiceImpl implements ContactService {
	@Autowired
	private ContactRepository contactRepository;

	@Override
	public List<Contact> findAllInquiry(){
		return contactRepository.findAll();
	}

	@Override
	public Contact saveInquiry(Contact contact) {
		return contactRepository.save(contact);
	}

	@Override
	public Contact findBycontactId(Long contactId) {
		Optional<Contact> conta = contactRepository.findById(contactId);
		return conta.get();
	}

}
