package algorithm.parts;

import geom.Point;
import geom.Points;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Box {

	public Point at, dim;
	public List<Box> boxes;
	public Map<String, Object> map;

	public Box(Point at, Point dim) {
		this.at = at;
		this.dim = dim;
		this.boxes = new ArrayList<>();
		this.map = new HashMap<>();
	}

	public Box(Box copy) {
		this.at = new Point(copy.at);
		this.dim = new Point(copy.dim);
		this.boxes = new ArrayList<>();
		for (Box b : copy.boxes) {
			this.boxes.add(new Box(b));
		}
		this.map = new HashMap<>(copy.map);
	}
	
	public Point center() {
		return new Point(at.x + dim.x / 2, at.y + dim.y / 2, at.z + dim.z / 2);
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
