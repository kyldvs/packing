package geom;

public class Points {

	public static Point crossProduct(Point a, Point b) {
		return new Point(
				(a.y * b.z) - (a.z * b.y),
				(a.z * b.x) - (b.z * a.x),
				(a.z * b.y) - (a.y * b.x));
	}
	
}
