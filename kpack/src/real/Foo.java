package real;

import geom.Point;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import parts.Box;
import parts.Input;
import parts.Order;
import parts.Output;
import parts.Pallet;
import algorithm.Algorithm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Foo implements Algorithm {

	private static final int MAX = 100_000_000;
	
	@Override
	public String run(Input in) {
		Order o = in.order;
		Pallet p = in.pallets.get(0);
		List<Box> boxes = Lists.newArrayList(o.boxes);

		// gather heights
		Collections.sort(boxes, new Comparator<Box>() {
			@Override
			public int compare(Box ths, Box tht) {
				return tht.dim.y - ths.dim.y;
			}
		});

		List<List<Box>> heights = Lists.newArrayList();
		for (Box b : boxes) {
			heights.add(Lists.newArrayList(b));
		}

		// slightly less than 1/2 inch
		int threshold = 12;
		for (int i = 0; i < heights.size(); i++) {
			List<Box> li = heights.get(i);
			int max = li.get(0).dim.y;
			for (int j = i + 1; j < heights.size(); j++) {
				List<Box> lj = heights.get(j);
				if (Math.abs(max - lj.get(lj.size() - 1).dim.y) > threshold) {
					for (int k = i + 1; k < j; k++) {
						li.addAll(heights.get(i + 1));
						heights.remove(i + 1);
					}
					break;
				}
			}
		}

		// each layer must be able to fill 75% of the pallet's area
		double areaThreshold = 0.75;
		List<Box> extra = Lists.newArrayList();
		for (int i = 0; i < heights.size(); i++) {
			int area = 0;
			for (Box b : heights.get(i)) {
				area += b.dim.x * b.dim.z;
			}

			if (area < areaThreshold * p.dim.x * p.dim.x) {
				extra.addAll(heights.remove(i));
				i--;
			}
		}

		// output information
		List<Box> seq = Lists.newArrayList();
		Map<Box, Point> pos = Maps.newHashMap();
		Map<Box, Integer> orient = Maps.newHashMap();

		// Put the boxes in the correct arrangement
		int y = 0;
		for (List<Box> layer : heights) {
			int maxY = layer.get(0).dim.y;

			Collections.sort(layer, new Comparator<Box>() {
				@Override
				public int compare(Box o1, Box o2) {
					return o2.dim.z - o1.dim.z;
				}
			});

			
			int x = 0;
			int z = 0;
			
			int maxZ = -1;
			boolean found = true;
			
			List<Box> row = Lists.newArrayList();
			List<List<Box>> rows = Lists.newArrayList();
			while(found) {
				found = false;
				for (int j = 0; j < layer.size(); j++) {
					if (x + layer.get(j).dim.x < p.dim.x && z + layer.get(j).dim.z < p.dim.z) {
						found = true;
						Box b = layer.remove(j);
						row.add(b);
						seq.add(b);
						pos.put(b, Point.create(x, y, z));
						orient.put(b, 1);
						x += b.dim.x;
						maxZ = Math.max(maxZ, b.dim.z);
						break;
					}
				}
				
				if (!found) {
					if (maxZ != -1 && z + maxZ < p.dim.z) {

						// Expand x direction
						int totalX = 0;
						for (Box b : row) {
							totalX += b.dim.x;
						}
						
						int space = (p.dim.x - totalX) / (row.size() - 1);
						for (int i = 0; i < row.size(); i++) {
							Box b = row.get(i);
							Point point = pos.get(b);
							pos.put(b, Point.create(point.x + i * space, point.y, point.z));
						}
						
						rows.add(row);
						row = Lists.newArrayList();
						
						x = 0;
						z += maxZ;
						maxZ = -1;
						found = true;
					}
				}
			}
			
			// Expand z direction
			int totalZ = 0;
			for (List<Box> list : rows) {
				// this is max z in the row
				totalZ += list.get(0).dim.z;
			}
			
			int space = (p.dim.z - totalZ) / (rows.size() - 1);
			for (int i = 0; i < rows.size(); i++) {
				for (Box b : rows.get(i)) {
					Point point = pos.get(b);
					pos.put(b, Point.create(point.x, point.y, point.z + i * space));
				}
			}
			
			extra.addAll(layer);
			y += maxY;
		}
		
		// Now deal with leftovers.
		// Build volumes
		// about 1 inch threshold
		int volThreshold = 26;
		List<Vol> volumes = Lists.newArrayList();
		while(!extra.isEmpty()) {
			Box b = extra.remove(0);
			
			Vol v = new Vol();
			v.add(b, Point.origin());
			
			boolean found = true;
			while(found) {
				found = false;
				for (int j = 0; j < extra.size(); j++) {
					Box a = extra.get(j);
					Point[][][] bounds = Point.bounds(Point.origin(), v.dim);
					Point point = null;
					
//					if (v.checkErr(a, bounds[0][0][0]) < volThreshold) { point = bounds[0][0][0]; }
					if (v.checkErr(a, bounds[1][0][0]) < volThreshold) { point = bounds[1][0][0]; }
					if (v.checkErr(a, bounds[0][1][0]) < volThreshold) { point = bounds[0][1][0]; }
//					if (v.checkErr(a, bounds[1][1][0]) < volThreshold) { point = bounds[1][1][0]; }
					if (v.checkErr(a, bounds[0][0][1]) < volThreshold) { point = bounds[0][0][1]; }
//					if (v.checkErr(a, bounds[1][0][1]) < volThreshold) { point = bounds[1][0][1]; }
//					if (v.checkErr(a, bounds[0][1][1]) < volThreshold) { point = bounds[0][1][1]; }
//					if (v.checkErr(a, bounds[1][1][1]) < volThreshold) { point = bounds[1][1][1]; }
					
					if (point != null) {
						extra.remove(j);
						j--;
						
						v.add(a, point);
						found = true;
					}
				}
			}
			
			System.out.println(v.dim);
			volumes.add(v);
		}
		
		Collections.sort(volumes, new Comparator<Vol>() {
			@Override
			public int compare(Vol ths, Vol tht) {
				return (ths.dim.x * ths.dim.y * ths.dim.z) - (tht.dim.x * tht.dim.y * tht.dim.z);
			}
		});
		
		int x = 0;
		int z = 0;
		int maxZ = -1;
		int maxY = -1;
		Map<Vol, Point> volPos = Maps.newHashMap(); 
		for (Vol v : volumes) {
			if (x + v.dim.x > p.dim.x || z + v.dim.z > p.dim.z) {
				if (maxZ == -1) { continue; }
				x = 0;
				z += maxZ;
				maxZ = -1;
				if (z + v.dim.z > p.dim.z) {
					if (maxY == -1) { continue; }
					z = 0;
					y += maxY;
					maxY = -1;
				}
			}
			
			maxZ = Math.max(maxZ, v.dim.z);
			maxY = Math.max(maxY, v.dim.y);

			volPos.put(v, Point.create(0, y, 0));
//			y += v.dim.y;
			
			x += v.dim.x;
		}
		
		System.out.println(volPos);
		
		for (Vol v : volPos.keySet()) {
			Point volP = volPos.get(v);
			for (int i = 0; i < v.boxes.size(); i++) {
				Box box = v.boxes.get(i);
				Point boxP = v.loc.get(i);
				
				seq.add(box);
				pos.put(box, Point.add(volP, boxP));
				orient.put(box, 1);
			}
		}

		return Output.format(o, p, seq, pos, orient);
	}

	private static class Vol {
		
		int err;
		Point dim;
		
		List<Box> boxes;
		List<Point> loc;
		
		public Vol() {
			err = 0;
			dim = Point.origin();
			
			boxes = Lists.newArrayList();
			loc = Lists.newArrayList();
		}
		
		public void add(Box b, Point p) {
			boxes.add(b);
			loc.add(p);

			dim = Point.create(
					Math.max(dim.x, p.x + b.dim.x),
					Math.max(dim.y, p.y + b.dim.y),
					Math.max(dim.z, p.z + b.dim.z));
			
			updateErr();
		}
		
		public int checkErr(Box b, Point p) {
			if (boxes.isEmpty()) {
				return 0;
			}
			
			int oldErr = err;
			Point oldDim = dim;
			
			add(b, p);
			updateErr();
			int ret = err;
			boxes.remove(boxes.size() - 1);
			loc.remove(loc.size() - 1);
			
			err = oldErr;
			dim = oldDim;
			return ret;
		}
		
		public void updateErr() {
			if (boxes.isEmpty()) {
				err = 0;
				return;
			}
			
			int minX = MAX, maxX = 0, minY = MAX, maxY = 0, minZ = MAX, maxZ = 0;
			for (int i = 0; i < boxes.size(); i++) {
				Box b = boxes.get(i);
				Point p = loc.get(i);
				
				minX = Math.min(minX, p.x);
				maxX = Math.max(maxX, p.x + b.dim.x);
				minY = Math.min(minY, p.y);
				maxY = Math.max(maxY, p.y + b.dim.y);
				minZ = Math.min(minZ, p.z);
				maxZ = Math.max(maxZ, p.z + b.dim.z);
			}
			
			// [x][y][z], 0 = min, 1 = max
			Point[][][] close = new Point[2][2][2];
			
			for (int i = 0; i < boxes.size(); i++) {
				Box b = boxes.get(i);
				Point p = loc.get(i), test;
				
				test = Point.create(p.x, p.y, p.z);
				if (Point.create(minX, minY, minZ).dist(test) <
						Point.create(minX, minY, minZ).dist(close[0][0][0])) {
					close[0][0][0] = test;
				}
				
				test = Point.create(p.x + b.dim.x, p.y, p.z);
				if (Point.create(maxX, minY, minZ).dist(test) <
						Point.create(maxX, minY, minZ).dist(close[1][0][0])) {
					close[1][0][0] = test;
				}
				
				test = Point.create(p.x + b.dim.x, p.y, p.z + b.dim.z);
				if (Point.create(maxX, minY, maxZ).dist(test) <
						Point.create(maxX, minY, maxZ).dist(close[1][0][1])) {
					close[1][0][1] = test;
				}
				
				test = Point.create(p.x, p.y, p.z + b.dim.z);
				if (Point.create(minX, minY, maxZ).dist(test) <
						Point.create(minX, minY, maxZ).dist(close[0][0][1])) {
					close[0][0][1] = test;
				}
				
				test = Point.create(p.x, p.y + b.dim.y, p.z);
				if (Point.create(minX, maxY, minZ).dist(test) <
						Point.create(minX, maxY, minZ).dist(close[0][1][0])) {
					close[0][1][0] = test;
				}
				
				
				test = Point.create(p.x + b.dim.x, p.y + b.dim.y, p.z);
				if (Point.create(maxX, maxY, minZ).dist(test) <
						Point.create(maxX, maxY, minZ).dist(close[1][1][0])) {
					close[1][1][0] = test;
				}

				test = Point.create(p.x + b.dim.x, p.y + b.dim.y, p.z + b.dim.z);
				if (Point.create(maxX, maxY, maxZ).dist(test) <
						Point.create(maxX, maxY, maxZ).dist(close[1][1][1])) {
					close[1][1][1] = test;
				}
				
				test = Point.create(p.x, p.y + b.dim.y, p.z + b.dim.z);
				if (Point.create(minX, maxY, maxZ).dist(test) <
						Point.create(minX, maxY, maxZ).dist(close[0][1][1])) {
					close[0][1][1] = test;
				}
			}
			
			err = Math.max(err, Math.abs(minX - close[0][0][0].x));
			err = Math.max(err, Math.abs(minX - close[0][1][0].x));
			err = Math.max(err, Math.abs(minX - close[0][0][1].x));
			err = Math.max(err, Math.abs(minX - close[0][1][1].x));
			
			err = Math.max(err, Math.abs(minY - close[0][0][0].y));
			err = Math.max(err, Math.abs(minY - close[1][0][0].y));
			err = Math.max(err, Math.abs(minY - close[0][0][1].y));
			err = Math.max(err, Math.abs(minY - close[1][0][1].y));
					
			err = Math.max(err, Math.abs(minZ - close[0][0][0].z));
			err = Math.max(err, Math.abs(minZ - close[1][0][0].z));
			err = Math.max(err, Math.abs(minZ - close[0][1][0].z));
			err = Math.max(err, Math.abs(minZ - close[1][1][0].z));
			
			err = Math.max(err, Math.abs(maxX - close[1][0][0].x));
			err = Math.max(err, Math.abs(maxX - close[1][1][0].x));
			err = Math.max(err, Math.abs(maxX - close[1][0][1].x));
			err = Math.max(err, Math.abs(maxX - close[1][1][1].x));
			
			err = Math.max(err, Math.abs(maxY - close[0][1][0].y));
			err = Math.max(err, Math.abs(maxY - close[1][1][0].y));
			err = Math.max(err, Math.abs(maxY - close[0][1][1].y));
			err = Math.max(err, Math.abs(maxY - close[1][1][1].y));
					
			err = Math.max(err, Math.abs(maxZ - close[0][0][1].z));
			err = Math.max(err, Math.abs(maxZ - close[1][0][1].z));
			err = Math.max(err, Math.abs(maxZ - close[0][1][1].z));
			err = Math.max(err, Math.abs(maxZ - close[1][1][1].z));
		}
	}
	
}
