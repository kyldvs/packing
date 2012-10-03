package algorithm.parts;

import geom.Point;
import geom.Points;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Box {

	public Point at, dim;
	public Set<Box> boxes;
	public Map<String, Object> map;

	public Box(Point at, Point dim) {
		this.at = at;
		this.dim = dim;
		this.boxes = new HashSet<>();
		this.map = new HashMap<>();
	}

	public Box(Box copy) {
		this.at = new Point(copy.at);
		this.dim = new Point(copy.dim);
		this.boxes = new HashSet<>();
		for (Box b : copy.boxes) {
			this.boxes.add(new Box(b));
		}
		this.map = new HashMap<>(copy.map);
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
	
	public void p(String key, Object value) {
		map.put(key, value);
	}

	public Object o(String key) {
		return map.get(key);
	}
	
	public String s(String key) {
		return (String) map.get(key);
	}
	
	public int i(String key) {
		return (Integer) map.get(key);
	}
}
