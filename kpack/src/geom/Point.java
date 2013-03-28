package geom;


public class Point {

	public static Point origin() {
		return new Point(0,0,0);
	}
	
	public static Point create(int x, int y, int z) {
		return new Point(x, y, z);
	}
	
	public static Point create(Point p) {
		return create(p.x, p.y, p.z);
	}
	
	public static Point[][][] bounds(Point p, Point dim) {
		Point[][][] ret = new Point[2][2][2];
		ret[0][0][0] = (create(p.x, p.y, p.z));
		ret[1][0][0] = (create(p.x + dim.x, p.y, p.z));
		ret[0][1][0] = (create(p.x, p.y + dim.y, p.z));
		ret[0][0][1] = (create(p.x, p.y, p.z + dim.z));
		ret[1][1][0] = (create(p.x + dim.z, p.y + dim.y, p.z));
		ret[0][1][1] = (create(p.x, p.y + dim.y, p.z + dim.z));
		ret[1][0][1] = (create(p.x + dim.x, p.y, p.z + dim.z));
		ret[1][1][1] = (create(p.x + dim.x, p.y + dim.y, p.z + dim.z));
		return ret;
	}
	
	public static Point add(Point a, Point b) {
		return create(a.x + b.x, a.y + b.y, a.z + b.z);
	}
	
	public final int x;
	public final int y;
	public final int z;

	private Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double dist(Point b) {
		return b == null ? 1e30 : Math.sqrt(Math.pow(x - b.x, 2) + Math.pow(y - b.y, 2) + Math.pow(z - b.z, 2));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
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
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d, %d)", x, y, z);
	}
}
