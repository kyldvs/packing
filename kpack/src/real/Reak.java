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

public class Reak implements Algorithm {

	public class B {
		
		Box a, b, c;
		
		public B(Box a, Box b, Box c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}

	@Override
	public String run(Input in) {
		Order o = in.order;
		Pallet p = in.pallets.get(0);

		List<Box> boxes = Lists.newArrayList(o.boxes);

		// sort by height
		Collections.sort(boxes, new Comparator<Box>() {
			@Override
			public int compare(Box ths, Box tht) {
				return tht.dim.y - ths.dim.y;
			}
		});

		List<B> bs = Lists.newArrayList();
		int maxX = 0;
		int maxZ = 0;
		
		boolean found = true;
		while(found) {
			found = false;
			for (int i = 0; i < boxes.size() && !found; i++) {
				Box bi = boxes.get(i);
				for (int j = i + 1; j < boxes.size() && !found; j++) {
					Box bj = boxes.get(j);
					for (int k = j + 1; k < boxes.size() && !found; k++) {
						Box bk = boxes.get(k);
						if (bi.dim.y == bj.dim.y + bk.dim.y) {
							bs.add(new B(bi, bj, bk));
							
							maxX = Math.max(maxX, Math.max(bi.dim.x + bj.dim.x, bi.dim.x + bk.dim.x));
							maxZ = Math.max(maxZ, Math.max(bi.dim.z, Math.max(bj.dim.z, bk.dim.z)));
							
							found = true;
							boxes.remove(i);
							boxes.remove(j - 1);
							boxes.remove(k - 2);
						}
					}
				}
			}
		}
		
		// output information
		List<Box> seq = Lists.newArrayList();
		Map<Box, Point> pos = Maps.newHashMap();
		Map<Box, Integer> orient = Maps.newHashMap();
		
		int[][] y = new int[(p.dim.x / maxX) + 1][(p.dim.z / maxZ) + 1];
		int x = 0, z = 0;
		for (B b : bs) {
			if ((x + 1) * maxX > p.dim.x) {
				x = 0;
				z++;
			}
			
			if ((z + 1) * maxZ > p.dim.z) {
				z = 0;
			}
			
			seq.add(b.a);
			pos.put(b.a, Point.create((x * maxX), y[x][z], (z * maxZ)));
			orient.put(b.a, 2);
			
			seq.add(b.b);
			pos.put(b.b, Point.create((x * maxX) + b.a.dim.x, y[x][z], (z * maxZ)));
			orient.put(b.b, 2);
			
			seq.add(b.c);
			pos.put(b.c, Point.create((x * maxX) + b.a.dim.x, y[x][z] + b.b.dim.y, (z * maxZ)));
			orient.put(b.c, 2);
			
			y[x][z] += b.a.dim.y;
			x++;
		}

		return Output.format(o, p, seq, pos, orient);
	}

}
