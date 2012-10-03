package geom;

/**
 * Represents a plane in 3D space
 * 
 * @author kyle
 */
public class Plane {

	public Line l1, l2;
	
	/**
	 * Represents the x,y plane
	 */
	public Plane() {
		l1 = Line.x();
		l2 = Line.y(); 
	}
	
	public Plane(Line l1, Line l2) {
		if (l1 == null || l2 == null || Lines.parallel(l1, l2)) {
			throw new RuntimeException("Degenerate Plane");
		}
		this.l1 = l1;
		this.l2 = l2;
	}
	
}
