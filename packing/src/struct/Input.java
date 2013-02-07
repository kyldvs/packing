package struct;

import java.util.List;

public class Input {

	/**
	 * The pallets in the input file
	 */
	private List<Pallet> pallets;

	/**
	 * The order in the input file
	 */
	private Order order;
	
	public Input(List<Pallet> pallets, Order order) {
		super();
		this.pallets = pallets;
		this.order = order;
	}

	public List<Pallet> getPallets() {
		return pallets;
	}

	public void setPallets(List<Pallet> pallets) {
		this.pallets = pallets;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
}
