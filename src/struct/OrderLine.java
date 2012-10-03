package struct;

import java.util.List;

/**
 * Input
 * 
 * @author kyle
 */
public class OrderLine {

	/**
	 * Incrementing number for each order line
	 */
	private int orderLineNo;

	/**
	 * The Type of Article ordered
	 */
	private Article article;

	/**
	 * Barcodes. The number of barcodes corresponds to the number of ordered
	 * cases.
	 */
	private List<String> barcodes;

	public OrderLine(int orderLineNo, Article article, List<String> barcodes) {
		super();
		this.orderLineNo = orderLineNo;
		this.article = article;
		this.barcodes = barcodes;
	}

	public int getOrderLineNo() {
		return orderLineNo;
	}

	public void setOrderLineNo(int orderLineNo) {
		this.orderLineNo = orderLineNo;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public List<String> getBarcodes() {
		return barcodes;
	}

	public void setBarcodes(List<String> barcodes) {
		this.barcodes = barcodes;
	}

}
