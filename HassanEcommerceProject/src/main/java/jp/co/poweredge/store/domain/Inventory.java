package jp.co.poweredge.store.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Inventory {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	private Long articleId;
	private int qtyRemaining;
	private int tanaoroshizaiko;
	private String reason;


	public Inventory() {

	}

	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Long getArticleId() {
		return articleId;
	}
	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
	public int getQtyRemaining() {
		return qtyRemaining;
	}
	public void setQtyRemaining(int qtyRemaining) {
		this.qtyRemaining = qtyRemaining;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getTanaoroshizaiko() {
		return tanaoroshizaiko;
	}

	public void setTanaoroshizaiko(int tanaoroshizaiko) {
		this.tanaoroshizaiko = tanaoroshizaiko;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
