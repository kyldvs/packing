package algorithm;

import geom.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import primitives.Primitives;
import struct.Article;
import struct.Input;
import struct.Order;
import struct.OrderLine;
import struct.Output;
import struct.PackList;
import struct.PackPallet;
import struct.Pakkage;
import struct.Pallet;
import algorithm.parts.Box;
import algorithm.parts.RoundingFunction;

public class Harmonic implements Algorithm {

	private RoundingFunction rf; 

	public Harmonic(RoundingFunction rf) {
		this.rf = rf;
	}

	private Map<Integer, Set<Box>> partitionHeights(Order order, boolean rotate, int ohl, int ohw) {
		ohl++;
		ohw++;
		System.out.printf("OHL: %d, OHW: %d\n", ohl, ohw);
		Map<Integer, Set<Box>> map = new HashMap<>();
		for (OrderLine line : order.getOrderLines()) {
			Article actArticle = line.getArticle();
			Box article = new Box(Point.origin(), new Point(actArticle.getLength() + ohl, actArticle.getWidth() + ohw, actArticle.getHeight()));
			article.p("weight", actArticle.getWeight());
			article.p("article", actArticle);

			for (String barcode : line.getBarcodes()) {
				Box item = new Box(article);
				item.p("barcode", barcode);
				item.p("orderLineNumber", line.getOrderLineNo());
				item.p("orientation", 1);
				if (map.get(article.dim.z) == null) {
					map.put(article.dim.z, new HashSet<Box>());
				}
				map.get(article.dim.z).add(item);
				if (rotate) {
					item.rotate();
				}
			}
		}
		return map;
	}

	private Map<Integer, Set<Box>> round(Set<Box> items) {
		List<Integer> dims = new LinkedList<>();
		for (Box b : items) {
			dims.add(b.dim.x);
			dims.add(b.dim.y);
		}

		rf.init(Primitives.toIntArr(dims.toArray(new Integer[0])));
		Map<Integer, Set<Box>> rounded = new HashMap<>();
		for (Box b : items) {
			//			int rx = rf.apply(b.dim.x);
			//			int ry = rf.apply(b.dim.y);
			//			
			//			if ((double) b.dim.x / rx < (double) b.dim.y / ry) {
			//				b.rotate();
			//			}

			int r = rf.apply(b.dim.x);
			if (!rounded.containsKey(r)) {
				rounded.put(r, new HashSet<Box>());
			}
			rounded.get(r).add(b);
		}
		return rounded;
	}

	@Override
	public Output run(Input in) {
		QuickOutput one = quickOutput(in, true);
		QuickOutput two = quickOutput(in, true);
		return one.getHeight() < two.getHeight() ? one.toOutput() : two.toOutput();
	}

	public QuickOutput quickOutput(Input in, boolean rotate) {
		// TODO Support multiple pallets
		Pallet realPallet = in.getPallets().get(0);
		Box pallet = Box.fromPallet(realPallet);
		Map<Integer, Set<Box>> map = partitionHeights(in.getOrder(), rotate, realPallet.getOverhangLength(), realPallet.getOverhangWidth());

		List<Box> layers = new ArrayList<>();
		List<Box> unfinished = new ArrayList<>();
		for (int height : map.keySet()) {
			Map<Integer, Set<Box>> rounded = round(map.get(height));

			Box layer = new Box(Point.origin(), new Point(pallet.dim.x, pallet.dim.y, height));

			// Shelve "height"
			int shelfStart = 0, shelfEnd = 0;
			int y = 0;
			int x = 0;
			for (int dim : rounded.keySet()) {
				List<Box> boxes = new ArrayList<>(rounded.get(dim));
				Collections.sort(boxes, Box.COMP_AREA);
				Collections.reverse(boxes);
				for (Box b : boxes) {
					//System.out.println("Width: " + b.dim.x + " Rounded to: " + dim);
					if (b.at.y + b.dim.y + y > layer.dim.y) {
						y = 0;
						x += dim;
						shelfStart = shelfEnd + 1;
						shelfEnd = shelfStart;
					}

					if (b.at.x + b.dim.x + x > layer.dim.x) {
						//System.out.println("Pallet Width: " + layer.dim.x);
						//System.out.println("Width: " + (b.at.x + b.dim.x + x));
						x = 0;
						y = 0;
						fullLayer(layer);
						layers.add(layer);
						layer = new Box(Point.origin(), new Point(pallet.dim.x, pallet.dim.y, height));
					}

					b.at.x += x;
					b.at.y += y;

					y += b.dim.y;
					layer.add(b);
					shelfEnd++;
				}
			}

			finishLayer(layer);
			unfinished.add(layer);
		}

		Collections.sort(layers, Box.COMP_INTERNAL_AREA);
		Collections.sort(unfinished, Box.COMP_INTERNAL_AREA);

		for (Box un : unfinished) {
			int dx = (pallet.dim.x - un.realX()) / 2;
			int dy = (pallet.dim.y - un.realY()) / 2;
			for (Box u : un.boxes) {
				u.at.x += dx; 
				u.at.y += dy;
			}
		}

		layers.addAll(unfinished);

		/*
		int height = 0;
		for (Box b : layers) height += b.dim.z;
		for (Box b : unfinished) height += b.dim.z;
		int min = height / pallet.dim.z + 1;
		int unCount = unfinished.size() / min;
		 */

		List<List<Box>> pallets = new ArrayList<>();
		while(!layers.isEmpty()) {
			int rem = pallet.dim.z;
			List<Box> p = new ArrayList<>();
			while(!layers.isEmpty() && rem - layers.get(0).dim.z >= 0) {
				rem -= layers.get(0).dim.z;
				p.add(layers.remove(0));
			}
			pallets.add(p);
		}

		// Build Quick Output

		System.out.println("Number of pallets: " + pallets.size());
		System.out.println(pallets.get(0));

		QuickOutput curr = new QuickOutput(in);
		curr.pallets = pallets;

		// Metropolis Update
		/*
		double fit = fitness(curr);
		int numlayers = curr.getNumberOfLayers();
		for (int i = 0; i < SAMPLES; i++) {
			int a = r.nextInt(numlayers);
			int b = r.nextInt(numlayers);
			curr.swap(a,b);
			double knew = fitness(curr);
			if (knew > fit) {
				fit = knew;
			} else {
				curr.swap(a,b);
			}
		}
		 */

		return curr;
	}

	private void expandX(Box layer) {
		List<Integer> shelves = shelves(layer);
		int maxX = 0;
		for (int i = shelves.get(shelves.size() - 2); i < shelves.get(shelves.size() - 1); i++) {
			Box check = layer.boxes.get(i);
			maxX = Math.max(maxX, check.at.x + check.dim.x);
		}

		if (shelves.size() <= 2) {
			return;
		}

		int space = layer.dim.x - maxX;
		int add = space / (shelves.size() - 2);
		for (int i = 0; i < shelves.size() - 1; i++) {
			int a = shelves.get(i);
			int z = shelves.get(i + 1);
			for (int j = a; j < z; j++) {
				layer.boxes.get(j).at.x += (add * i);
			}
		}
	}

	private void expandY(Box layer) {
		List<Integer> shelves = shelves(layer);
		for (int i = 0; i < shelves.size() - 1; i++) {
			int a = shelves.get(i);
			int z = shelves.get(i + 1);
			Box last = layer.boxes.get(z - 1);
			int add = z - a - 1 == 0 ? 0 : (layer.dim.y - (last.at.y + last.dim.y)) / (z - a - 1);
			for (int j = a; j < z; j++) {
				layer.boxes.get(j).at.y += (add * (j - a));
			}
		}

	}

	private List<Integer> shelves(Box layer) {
		int start = 0, lastX = -1;
		List<Integer> shelves = new ArrayList<>();
		for (Box b : layer.boxes) {
			if (b.at.x != lastX) {
				lastX = b.at.x;
				shelves.add(start);
			}
			start++;
		}
		shelves.add(start);
		return shelves;
	}

	private void fullLayer(Box layer) {
		layer.p("full", null);
		expandX(layer);
		expandY(layer);
		finishLayer(layer);
	}

	private void finishLayer(Box layer) {
		int weight = 0;
		for (Box b : layer.boxes) {
			weight += b.i("weight");
		}
		layer.p("weight", weight);
	}

	private class QuickOutput {

		private Input input;

		/**
		 * layers.get(0) is the one closest to the ground
		 */
		private List<List<Box>> pallets;

		public QuickOutput(Input input) {
			this.input = input;
		}

		/**
		 * @return the height of the center of gravity
		 */
		public double getHeightOfCenterOfGravity(int pallet) {
			double adjustedWeight = 0.0, weight = 0.0, height = 0.0;
			for (Box l : pallets.get(pallet)) {
				height += l.dim.z;
				int tmp = l.i("weight");
				adjustedWeight += (height * tmp);
				weight += tmp;
			}
			return adjustedWeight / weight;
		}

		public int getHeight() {
			int total = 0;
			for (List<Box> p : pallets) {
				for (Box l : p) {
					total += l.dim.z;
				}
			}
			return total;
		}

		public Output toOutput() {
			int packSequence = 0;
			int incomingSequence = 0;

			List<PackPallet> palletData = new ArrayList<PackPallet>();
			int on = 0;
			for (Pallet pallet : input.getPallets()) {
				List<Pakkage> pakkages = new ArrayList<Pakkage>();
				int height = 0;
				
				while (on < pallets.size()) {
					for (Box l : pallets.get(on)) {
						height += l.dim.z;
						for (Box b : l.boxes) {
							Point center = b.center();
							Pakkage p = new Pakkage(
									packSequence++, 
									incomingSequence++, 
									b.i("orderLineNumber"), 
									0, 
									(Article) b.o("article"), 
									b.s("barcode"),
									new Point(center.x, center.y, height), 
									b.i("orientation"), 
									Pakkage.getDefaultApproachPoints(),
									0);
							pakkages.add(p);
						}
					}
					on++;
				}

				PackPallet packPallet = new PackPallet(pallet, pakkages);
				palletData.add(packPallet);
				
			}

			PackList packList = new PackList(input.getOrder().getId(), palletData);
			Output out = new Output(packList);
			return out;
		}
	}

}
