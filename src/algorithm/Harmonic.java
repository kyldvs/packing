package algorithm;

import geom.Point;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	private Map<Integer, Set<Box>> partitionHeights(Order order) {
		Map<Integer, Set<Box>> map = new HashMap<>();
		for (OrderLine line : order.getOrderLines()) {
			Article actArticle = line.getArticle();
			Box article = new Box(Point.origin(), new Point(actArticle.getLength(), actArticle.getWidth(), actArticle.getHeight()));
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
			int rx = rf.apply(b.dim.x);
			int ry = rf.apply(b.dim.y);
			
			if (Math.random() < 0.5 || (double) b.dim.x / rx < (double) b.dim.y / ry) {
				b.rotate();
			}
			
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

		// TODO Support multiple pallets
		Box pallet = Box.fromPallet(in.getPallets().get(0));
		Map<Integer, Set<Box>> map = partitionHeights(in.getOrder());

		List<Box> layers = new ArrayList<>();
		for (int height : map.keySet()) {
			Map<Integer, Set<Box>> rounded = round(map.get(height));

			Box layer = new Box(Point.origin(), new Point(pallet.dim.x, pallet.dim.y, height));

			// Shelve "height"
			int shelfStart = 0, shelfEnd = 0;
			int y = 0;
			int x = 0;
			for (int dim : rounded.keySet()) {
				for (Box b : rounded.get(dim)) {
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
			layers.add(layer);
		}

		// Build Quick Output

		QuickOutput curr = new QuickOutput(in);
		curr.layers = layers.toArray(new Box[0]);

		Arrays.sort(curr.layers, new Comparator<Box>() {
			static final int BUMP = 100_000_000;
			@Override
			public int compare(Box o1, Box o2) {
//				Box l1 = o1.boxes.get(o1.boxes.size() - 1);
//				Box l2 = o2.boxes.get(o2.boxes.size() - 1);
				int d1 = area(o1);//((l1.at.x + l1.dim.x) * (l1.at.y + l1.dim.y));
				int d2 = area(o2);//((l2.at.x + l2.dim.x) * (l2.at.y + l2.dim.y));
				
				int ret = 0;
				if (Math.abs(d1 - d2) < 100) {
					ret = o2.i("weight") - o1.i("weight"); 
				} else {
					ret = d1 - d1;
				}
				
				if (o1.has("full") && o2.has("full")) {
					return ret;
				} else if (o1.has("full")) {
					return ret - BUMP;
				} else if (o2.has("full")) {
					return ret + BUMP;
				} else {
					return ret;
				}
			}
		});
		
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

		return curr.toOutput();
	}

	private int area(Box layer) {
		int area = 0;
		for (int i = 0; i < layer.boxes.size(); i++) {
			Box b = layer.boxes.get(i);
			area += b.dim.x * b.dim.y;
		}
		return area;
	}
	
	private void expandX(Box layer) {
		List<Integer> shelves = shelves(layer);
		int maxX = 0;
		for (int i = shelves.get(shelves.size() - 2); i < shelves.get(shelves.size() - 1); i++) {
			Box check = layer.boxes.get(i);
			maxX = Math.max(maxX, check.at.x + check.dim.x);
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
			int add = (layer.dim.y - (last.at.y + last.dim.y)) / (z - a - 1);
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

	private static double fitness(QuickOutput config) {
		double height = config.getHeight();
		double cog = config.getHeightOfCenterOfGravity();
		return height/cog;
	}

	private class QuickOutput {

		private Input input;

		/**
		 * layers.get(0) is the one closest to the ground
		 */
		private Box[] layers;

		public QuickOutput(Input input) {
			this.input = input;
		}

		/**
		 * @return the height of the center of gravity
		 */
		public double getHeightOfCenterOfGravity() {
			double adjustedWeight = 0.0, weight = 0.0, height = 0.0;
			for (Box l : layers) {
				height += l.dim.z;
				int tmp = l.i("weight");
				adjustedWeight += (height * tmp);
				weight += tmp;
			}
			return adjustedWeight / weight;
		}

		/**
		 * @return the total height of this output
		 */
		public int getHeight() {
			int total = 0;
			for (Box l : layers) {
				total += l.dim.z;
			}
			return total;
		}

		public int getNumberOfLayers() {
			return layers == null ? 0 : layers.length;
		}

		public void swap(int a, int b) {
			Box x = layers[a];
			layers[a] = layers[b];
			layers[b] = x;
		}

		public Output toOutput() {
			int packSequence = 0;
			int incomingSequence = 0;

			List<PackPallet> palletData = new ArrayList<PackPallet>();
			for (Pallet pallet : input.getPallets()) {
				List<Pakkage> pakkages = new ArrayList<Pakkage>();
				int height = 0;
				for (Box l : layers) {
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
				PackPallet packPallet = new PackPallet(pallet, pakkages);
				palletData.add(packPallet);
			}

			PackList packList = new PackList(input.getOrder().getId(), palletData);
			Output out = new Output(packList);
			return out;
		}
	}

}
