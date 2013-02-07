package parts;

import geom.Point;

public class Pallet {

	public final Desc desc;
	public final Point dim;
	public final int capacity;
	public final Point overhang;
	public final Point margins;
	
	private Pallet(Desc desc, Point dim, int capacity, Point overhang, Point margins) {
		this.desc = desc;
		this.dim = dim;
		this.capacity = capacity;
		this.overhang = overhang;
		this.margins = margins;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + capacity;
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + ((dim == null) ? 0 : dim.hashCode());
		result = prime * result + ((margins == null) ? 0 : margins.hashCode());
		result = prime * result
				+ ((overhang == null) ? 0 : overhang.hashCode());
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
		Pallet other = (Pallet) obj;
		if (capacity != other.capacity)
			return false;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (dim == null) {
			if (other.dim != null)
				return false;
		} else if (!dim.equals(other.dim))
			return false;
		if (margins == null) {
			if (other.margins != null)
				return false;
		} else if (!margins.equals(other.margins))
			return false;
		if (overhang == null) {
			if (other.overhang != null)
				return false;
		} else if (!overhang.equals(other.overhang))
			return false;
		return true;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private Desc desc;
		private Point dim;
		private int capacity;
		private Point overhang;
		private Point margins;
		
		private Builder() {}
		
		public Pallet build() {
			return new Pallet(desc, dim, capacity, overhang, margins);
		}
		
		public Builder desc(int number, String description) {
			this.desc = Desc.create(number, description);
			return this;
		}
		
		public Builder dimensions(int x, int y, int z) {
			this.dim = Point.create(x, y, z);
			return this;
		}
		
		public Builder capacity(int capacity) {
			this.capacity = capacity;
			return this;
		}
		
		public Builder overhang(int x, int z) {
			this.overhang = Point.create(x, 0, z);
			return this;
		}
		
		public Builder margins(int x, int z) {
			this.margins = Point.create(x, 0, z);
			return this;
		}
	}
}
