package algorithm.parts;

import geom.Point;
import geom.Points;

import java.util.HashSet;
import java.util.Set;

public class Box {

	public Point at, dim;
	public Set<Box> boxes;

	public Box(Point at, Point dim) {
		this.at = at;
		this.dim = dim;
		this.boxes = new HashSet<>();
	}

	public void add(Box b) {
		boxes.add(b);
	}

	public void addIfIn(Box b) {
		if (Points.between(b.at, at, Points.add(at, dim))
				&& Points.between(Points.add(b.at, b.dim), at,
						Points.add(at, dim))) {
			add(b);
		}
	}
}
