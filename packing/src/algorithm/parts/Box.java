package algorithm.parts;

import geom.Point;
import geom.Points;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import struct.Pallet;

public class Box {

	public static final Comparator<Box> COMP_AREA = new Comparator<Box>() {
		@Override
		public int compare(Box ths, Box tht) {
			return tht.area() - ths.area();
		}
	};
	
	public static final Comparator<Box> COMP_INTERNAL_AREA = new Comparator<Box>() {
		@Override
		public int compare(Box ths, Box tht) {
			return tht.internalArea() - ths.internalArea();
		}
	};
	
	public static Box fromPallet(Pallet p) {
		return new Box(Point.origin(), new Point(p.getLength(), p.getWidth(), p.getMaxLoadHeight()));
	}
	
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
	
	public int area() {
		return dim.x * dim.y;
	}
	
	public int internalArea() {
		int area = 0;
		for (Box b : boxes) {
			area += b.area();
		}
		return area;
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
	
	public void rotate() {
		p("orientation", i("orientation") == 1 ? 2 : 1);
		int tmp = dim.x;
		dim.x = dim.y;
		dim.y = tmp;
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
	
	public boolean has(String key) {
		return map.containsKey(key);
	}

	public int realX() {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (Box b : boxes) {
			min = Math.min(b.at.x, min);
			max = Math.max(b.at.x + b.dim.x, max);
		}
		return max - min;
	}
	
	public int realY() {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (Box b : boxes) {
			min = Math.min(b.at.y, min);
			max = Math.max(b.at.y + b.dim.y, max);
		}
		return max - min;
	}
}
