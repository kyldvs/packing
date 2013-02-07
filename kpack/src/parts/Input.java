package parts;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class Input {

	public final ImmutableList<Pallet> pallets;
	public final Order order;
	
	private Input(ImmutableList<Pallet> pallets, Order order) {
		this.pallets = pallets;
		this.order = order;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private List<Pallet> pallets;
		private Order order;
		
		private Builder() {
			this.pallets = Lists.newArrayList();
		}
		
		public Input build() {
			return new Input(ImmutableList.copyOf(pallets), order);
		}
		
		public Builder order(Order order) {
			this.order = order;
			return this;
		}
		
		public Builder palle(Pallet pallet) {
			this.pallets.add(pallet);
			return this;
		}
	}
	
}
