package io.leavesfly.crawler.domain;

import java.util.List;

public class CommodityItem {
	private Long id;
	private String title;
	private Float price;
	private Float originalPrice;
	private Integer sales;
	private List<Long> shopIDList;
	private Float score;
	private Integer evaluateNum;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(Float originalPrice) {
		this.originalPrice = originalPrice;
	}

	public Integer getSales() {
		return sales;
	}

	public void setSales(Integer sales) {
		this.sales = sales;
	}

	public List<Long> getShopIDList() {
		return shopIDList;
	}

	public void setShopIDList(List<Long> shopIDList) {
		this.shopIDList = shopIDList;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public Integer getEvaluateNum() {
		return evaluateNum;
	}

	public void setEvaluateNum(Integer evaluateNum) {
		this.evaluateNum = evaluateNum;
	}

}
