package struct;

import java.util.List;

/**
 * Input
 * 
 * An order consisting of a list of Articles (cases), and othe fields.
 * 
 * @author kyle
 */
public class Order {

	/**
	 * Unique Order ID
	 */
	private int id;
	
	/**
	 * Decsription of the order
	 */
	private String description;

	/**
	 * Family grouping enabled (True) or disabled (False).
	 * 
	 * Default: False
	 */
	private boolean familyGrouping = false;

	/**
	 * Ranking enabled (True) or disabled (False).
	 * 
	 * Default: False
	 */
	private boolean ranking = false;

	/**
	 * Order Lines
	 */
	private List<OrderLine> orderLines;

	public Order(int id, String description, boolean familyGrouping,
			boolean ranking, List<OrderLine> orderLines) {
		super();
		this.id = id;
		this.description = description;
		this.familyGrouping = familyGrouping;
		this.ranking = ranking;
		this.orderLines = orderLines;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isFamilyGrouping() {
		return familyGrouping;
	}

	public void setFamilyGrouping(boolean familyGrouping) {
		this.familyGrouping = familyGrouping;
	}

	public boolean isRanking() {
		return ranking;
	}

	public void setRanking(boolean ranking) {
		this.ranking = ranking;
	}

	public List<OrderLine> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(List<OrderLine> orderLines) {
		this.orderLines = orderLines;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
