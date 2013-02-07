package geom;

/**
 * Represents a line
 * 
 * @author kyle
 */
public class Line {

	public static Line x() {
		return new Line(Point.origin(), new Point(1,0,0));
	}
	
	public static Line y() {
		return new Line(Point.origin(), new Point(0,1,0));
	}
	
	public static Line z() {
		return new Line(Point.origin(), new Point(0,0,1));
	}
	
	public Point p1, p2;
	
	/**
	 * Unit vector along the x axis
	 */
	public Line() {
		p1 = new Point(0,0,0);
		p2 = new Point(1,0,0);
	}
	
	public Line(Point p1, Point p2) {
		if (p1 == null || p2 == null || p1.equals(p2)) {
			throw new RuntimeException("Degenerate Line");
		}
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public Point normalize() {
		return new Point(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
	}
}
