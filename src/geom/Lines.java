package geom;

public class Lines {
	
	public static boolean parallel(Line l1, Line l2) {
		Point n1 = l1.normalize();
		Point n2 = l2.normalize();
		Point cross = Points.crossProduct(n1, n2);
		return Point.origin().equals(cross);
	}

}
