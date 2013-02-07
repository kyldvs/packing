package parts;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class Order {
	
	public final Desc desc;
	public final ImmutableList<Box> boxes; 
	
	private Order(Desc desc, ImmutableList<Box> boxes) {
		this.desc = desc;
		this.boxes = boxes;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private Desc desc;
		private List<Box> boxes; 
		
		private Builder() {
			boxes = Lists.newArrayList();
		}
		
		public Order build() {
			return new Order(desc, ImmutableList.copyOf(boxes));
		}
		
		public Builder desc(int number, String description) {
			this.desc = Desc.create(number, description);
			return this;
		}
		
		public Builder box(Box box) {
			boxes.add(box);
			return this;
		}
	}
	
}
