package geom;

import java.util.ArrayList;
import java.util.List;

public class Volume {

	Volume parent;
	List<Volume> children;

	/*
	 * If this volume has children, then these represent
	 * a bounding box positioned relative to the parent
	 * volume.
	 * 
	 * Otherwise they represent a solid object with the
	 * given relative position, and dimensions.
	 */
	Point at;
	Point dim;
	
	/*
	 * Other variables to keep track of
	 */
	int actualVolume;
	
	public Volume() {
		parent = null;
		children = new ArrayList<Volume>();
	}

	public void rotate() {
		at = Point.create(at.z, at.y, at.x);
		dim = Point.create(dim.z, dim.y, dim.x);
		for (Volume c : children) {
			c.rotate();
		}
	}

	public void shiftx(int amount) {
		at = Point.create(at.x + amount, at.y, at.z);
		for (Volume c : children) {
			c.shiftx(amount);
		}
	}
	
	public void shifty(int amount) {
		at = Point.create(at.x, at.y + amount, at.z);
		for (Volume c : children) {
			c.shifty(amount);
		}
	}
	
	public void shiftz(int amount) {
		at = Point.create(at.x, at.y, at.z + amount);
		for (Volume c : children) {
			c.shiftz(amount);
		}
	}
	
	/*
	 * Static Methods
	 */
	
	public static Volume merge(Volume a, Volume b) {
		Volume ret = new Volume();

		// Compute best sides to intersect
		int yzyz = Math.min(a.dim.y, b.dim.y) * Math.min(a.dim.z, b.dim.z);	// rotate nothing
		int yzyx = Math.min(a.dim.y, b.dim.y) * Math.min(a.dim.z, b.dim.x);	// rotate b
		int yxyz = Math.min(a.dim.y, b.dim.y) * Math.min(a.dim.x, b.dim.z); // rotate a
		int yxyx = Math.min(a.dim.y, b.dim.y) * Math.min(a.dim.x, b.dim.x); // rotate a, b
		
		if (yzyz >= yzyx && yzyz >= yxyz && yzyz >= yxyx) {
		} else if (yzyx >= yxyz && yzyx >= yxyx) {
			b.rotate();
		} else if (yxyz >= yxyx) {
			a.rotate();
		} else {
			a.rotate();
			b.rotate();
		}
		
		b.shiftx(a.dim.x);
		ret.actualVolume = a.actualVolume + b.actualVolume;
		ret.children.add(a);
		ret.children.add(b);
		
		return ret;
	}
	
}
