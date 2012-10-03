package geom;

public class Points {

	public static Point crossProduct(Point a, Point b) {
		return new Point(
				(a.y * b.z) - (a.z * b.y),
				(a.z * b.x) - (b.z * a.x),
				(a.z * b.y) - (a.y * b.x));
	}
	
	public static Point add(Point a, Point b) {
		return new Point(a.x + b.x, a.y + b.y, a.z + b.z);
	}
	
	/**
	 * @return true if a is contained in the rectangular prism defined by p1 and p2
	 */
	public static boolean between(Point a, Point p1, Point p2) {
		if (a.x >= p1.x && a.x <= p2.x) {
			return a.y >= p1.y && a.y <= p2.y && a.z >= p1.z && a.z <= p2.z;
		} else if (a.x >= p2.x && a.x <= p1.x){
			return a.y >= p2.y && a.y <= p1.y && a.z >= p2.z && a.z <= p1.z;
		} else {
			return false;
		}
	}
}
