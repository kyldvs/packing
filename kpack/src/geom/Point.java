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
	
	public final int x;
	public final int y;
	public final int z;

	private Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
}
