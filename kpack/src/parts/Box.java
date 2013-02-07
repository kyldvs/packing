package parts;

import geom.Point;

public class Box {
	
	public final int orderLine;
	public final Desc article;
	public final Point dim;
	public final int weight;
	public final String barcode;
	
	private Box(int orderLine, Desc article, Point dim, int weight, String barcode) {
		this.orderLine = orderLine;
		this.article = article;
		this.dim = dim;
		this.weight = weight;
		this.barcode = barcode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((article == null) ? 0 : article.hashCode());
		result = prime * result + ((barcode == null) ? 0 : barcode.hashCode());
		result = prime * result + ((dim == null) ? 0 : dim.hashCode());
		result = prime * result + orderLine;
		result = prime * result + weight;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Box other = (Box) obj;
		if (article == null) {
			if (other.article != null)
				return false;
		} else if (!article.equals(other.article))
			return false;
		if (barcode == null) {
			if (other.barcode != null)
				return false;
		} else if (!barcode.equals(other.barcode))
			return false;
		if (dim == null) {
			if (other.dim != null)
				return false;
		} else if (!dim.equals(other.dim))
			return false;
		if (orderLine != other.orderLine)
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private int orderLine;
		private Desc article;
		private Point dim;
		private int weight;
		private String barcode;
		
		private Builder() {}
		
		public Box build() {
			return new Box(orderLine, article, dim, weight, barcode);
		}
		
		public Builder orderLine(int orderLine) {
			this.orderLine = orderLine;
			return this;
		}
		
		public Builder article(int id, String description) {
			this.article = Desc.create(id, description);
			return this;
		}
		
		public Builder dim(int x, int y, int z) {
			this.dim = Point.create(x, y, z);
			return this;
		}
		
		public Builder weight(int weight) {
			this.weight = weight;
			return this;
		}
		
		public Builder barcode(String barcode) {
			this.barcode = barcode;
			return this;
		}
	}
	
}
