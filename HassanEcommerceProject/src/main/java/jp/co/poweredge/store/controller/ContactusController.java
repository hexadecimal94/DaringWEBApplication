package jp.co.poweredge.store.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.poweredge.store.domain.Contact;
import jp.co.poweredge.store.service.ContactService;

@Controller
public class ContactusController {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ContactusController.class);

	@Autowired
	private ContactService contactService;


	@RequestMapping("/contactus")
	public String contactus(Model model) {
		Contact contact = new Contact();
		model.addAttribute("contact",contact);
		return "contact-us";
	}

	@PostMapping("/contactus/add")
	public String addinfo(@ModelAttribute("contact") Contact contact) {


		//logger.warn("Contact us firstname: " + contact.getFirstName());

		//logger.warn("Contact us calling: " + contact.getLastName());
		//logger.warn("Contact us calling: " + contact.getEmail());
		//logger.warn("Contact us calling: " + contact.getContents());

		//データベースに保存する
		contact.setAnswerstatus("未回答");
		contactService.saveInquiry(contact);
		return "index";
	}


	//アドミン側からお問い合わせのリストを見るとき
	@RequestMapping("/contactus/inquirylist")
	public String contactusInquiryList(Model model) {
		List<Contact> contactList = contactService.findAllInquiry();
		model.addAttribute("contactlist",contactList);
		return "adminContactusList";
	}

	@RequestMapping("/contact-complete")
	public String contactCompletecheck(@RequestParam("contactId") Long contactId, Model model) {
		logger.warn("contactusID is : " + contactId);

		Contact contactobj = contactService.findBycontactId(contactId);

		logger.warn("contactusObj data is : " + contactobj.getId());
		logger.warn("contactusObj data is : " + contactobj.getEmail());
		logger.warn("contactusObj data is : " + contactobj.getFirstName());
		logger.warn("contactusObj data is : " + contactobj.getContents());
		logger.warn("contactusObj data is : " + contactobj.getAnswerstatus());


		contactobj.setAnswerstatus("回答済み");
		logger.warn("after " + contactobj.getAnswerstatus());

		contactService.saveInquiry(contactobj);


		List<Contact> contactList = contactService.findAllInquiry();
		model.addAttribute("contactlist",contactList);
		return "adminContactusList";
	}
}
