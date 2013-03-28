package real;

import geom.Point;
import geom.Tuple;

import java.util.Collections;
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

public class BF implements Algorithm {

	private static final int BLOCK = 10;
	private static final int TIME = 5000;
	
	@Override
	public String run(Input in) {
		Order o = in.order;
		Pallet p = in.pallets.get(0);

		// output information
		double bestDensity = 0;
		List<Box> bestSeq = Lists.newArrayList();
		Map<Box, Point> bestPos = Maps.newHashMap();
		Map<Box, Integer> bestOrient = Maps.newHashMap();

		long start = System.currentTimeMillis(), elapsed = 0;
		while(elapsed < TIME) {

			List<Box> seq = Lists.newArrayList();
			Map<Box, Point> pos = Maps.newHashMap();
			Map<Box, Integer> orient = Maps.newHashMap();
			
			double boxVolume = 0;
			Point max = Point.origin();

			List<Box> boxes = Lists.newArrayList(o.boxes);
			Collections.shuffle(boxes);

			int[][] height = new int[ceil(p.dim.x, BLOCK)][ceil(p.dim.z, BLOCK)];
			int[][][] space = new int[ceil(p.dim.x, BLOCK)][ceil(p.dim.z, BLOCK)][4];

			while (!boxes.isEmpty()) {
				update(space, height);
				List<Tuple> spaces = Lists.newArrayList();
				for (int i = 0; i < height.length; i++) {
					for (int j = 0; j < height[0].length; j++) {
						spaces.add(new Tuple(height[i][j], i, j));
					}
				}
				Collections.sort(spaces);

				boolean updated = false;
				for (int t = 0; t < spaces.size() && !updated; t++) {
					Tuple tuple = spaces.get(t);
					Point at = Point.create(tuple.at(1), tuple.at(0), tuple.at(2));
					for (int i = 0; i < boxes.size() && !updated; i++) {
						Box b = boxes.get(i);
						if (ceil(b.dim.x, BLOCK) < space[at.x][at.z][0] 
								&& ceil(b.dim.z, BLOCK) < space[at.x][at.z][1]) {
							
							// There could still be a collision, have to check all squares in here
							boolean collision = false;
							for (int j = at.x; !collision && j < at.x + ceil(b.dim.x, BLOCK) + 1; j++) {
								for (int k = at.z; !collision && k < at.z + ceil(b.dim.z, BLOCK) + 1; k++) {
									if (height[j][k] > at.y) {
										collision = true;
									}
								}
							}

							if (collision) {
								continue;
							}
							
							int h = at.y + b.dim.y;
							for (int j = at.x; j < at.x + ceil(b.dim.x, BLOCK) + 1; j++) {
								for (int k = at.z; k < at.z + ceil(b.dim.z, BLOCK) + 1; k++) {
									height[j][k] = h;
								}
							}
							
							boxes.remove(i);
							seq.add(b);
							pos.put(b, Point.create(at.x * BLOCK, at.y, at.z * BLOCK));
							orient.put(b, 1);
							
							boxVolume += (double) b.dim.x * b.dim.y * b.dim.z;
							max = Point.create(
									Math.max(max.x, at.x * BLOCK + b.dim.x), 
									Math.max(max.y, at.y * BLOCK + b.dim.y), 
									Math.max(max.z, at.z * BLOCK + b.dim.y));

							updated = true;
						}
					}
				}
				
				if(!updated) {
					break;
				}
			}
			
			// Check if we found a better solution
			double density = boxVolume / ((double) max.x * max.y * max.z);
			if (density > bestDensity) {
				bestDensity = density;
				bestSeq = seq;
				bestPos = pos;
				bestOrient = orient;
			}

			elapsed = System.currentTimeMillis() - start;
		}


		return Output.format(o, p, bestSeq, bestPos, bestOrient);
	}

	/**
	 * Diagram:
	 * 
	 *		z    [1]
	 * 		+-----------+
	 * 		|           |
	 * 		|           |
	 * 	 [2]|           |[0]
	 * 		|           |
	 * 		|           |
	 * 		+-----------+x 
	 * 		     [3]
	 * 
	 * Updates the space array with correct information given the height description of the packing
	 */
	public static void update(int[][][] space, int[][] height) {
		int x = height.length, z = height[0].length;

		int dir;
		int[] at;
		int[][] hi;

		// space[.][.][0]: positive x
		dir = 0;
		at = new int[z];
		hi = new int[z][x + 1];

		for (int i = 0; i < z; i++) {
			space[x - 1][i][dir] = 1;
			hi[i][at[i]++] = x;
		}

		for (int i = x - 2; i >= 0; i--) {
			for (int j = 0; j < z; j++) {
				if (height[i][j] < height[i + 1][j]) {
					space[i][j][dir] = 1;
					hi[j][at[j]++] = i + 1;
				} else if (height[i][j] == height[i + 1][j]) {
					space[i][j][dir] = space[i + 1][j][dir] + 1;
				} else {
					while(hi[j][at[j] - 1] < x && height[i][j] > height[hi[j][at[j] - 1]][j]) {
						at[j]--;
					}
					space[i][j][dir] = hi[j][at[j] - 1] - i;
				}
			}
		}

		// space[.][.][1]: positive z
		dir = 1;
		at = new int[x];
		hi = new int[x][z + 1];

		for (int i = 0; i < x; i++) {
			space[i][z - 1][dir] = 1;
			hi[i][at[i]++] = z;
		}

		for (int j = z - 2; j >= 0; j--) {
			for (int i = 0; i < x; i++) {
				if (height[i][j] < height[i][j + 1]) {
					space[i][j][dir] = 1;
					hi[i][at[i]++] = j + 1;
				} else if (height[i][j] == height[i][j + 1]) {
					space[i][j][dir] = space[i][j + 1][dir] + 1;
				} else {
					while(hi[i][at[i] - 1] < z && height[i][j] > height[i][hi[i][at[i] - 1]]) {
						at[i]--;
					}
					space[i][j][dir] = hi[i][at[i] - 1] - j;
				}
			}
		}

		// space[.][.][2]: negative x
		dir = 2;
		at = new int[z];
		hi = new int[z][x + 1];

		for (int i = 0; i < z; i++) {
			space[0][i][dir] = 1;
			hi[i][at[i]++] = -1;
		}

		for (int i = 1; i < x; i++) {
			for (int j = 0; j < z; j++) {
				if (height[i][j] < height[i - 1][j]) {
					space[i][j][dir] = 1;
					hi[j][at[j]++] = i - 1;
				} else if (height[i][j] == height[i - 1][j]) {
					space[i][j][dir] = space[i - 1][j][dir] + 1;
				} else {
					while(hi[j][at[j] - 1] >= 0 && height[i][j] > height[hi[j][at[j] - 1]][j]) {
						at[j]--;
					}
					space[i][j][dir] = i - hi[j][at[j] - 1];
				}
			}
		}

		// space[.][.][3]: negative z
		dir = 3;
		at = new int[x];
		hi = new int[x][z + 1];

		for (int i = 0; i < x; i++) {
			space[i][0][dir] = 1;
			hi[i][at[i]++] = -1;
		}

		for (int j = 1; j < z; j++) {
			for (int i = 0; i < x; i++) {
				if (height[i][j] < height[i][j - 1]) {
					space[i][j][dir] = 1;
					hi[i][at[i]++] = j - 1;
				} else if (height[i][j] == height[i][j - 1]) {
					space[i][j][dir] = space[i][j - 1][dir] + 1;
				} else {
					while(hi[i][at[i] - 1] >= 0 && height[i][j] > height[i][hi[i][at[i] - 1]]) {
						at[i]--;
					}
					space[i][j][dir] = j - hi[i][at[i] - 1];
				}
			}
		}
	}

	/**
	 * Does integer division, but takes the ceiling rather than floor
	 */
	private int ceil(int p, int q) {
		return p % q == 0 ? p / q : (p / q) + 1;
	}
}
