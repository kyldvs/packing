package algorithm;

import geom.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import struct.Article;
import struct.Input;
import struct.Order;
import struct.OrderLine;
import struct.Output;
import struct.PackList;
import struct.PackPallet;
import struct.Pakkage;
import struct.Pallet;

public class Harmonic implements Algorithm {

	private static final int SAMPLES = 10000;

	private static final Random r = new Random();

	@Override
	public Output run(Input in) {

		Order order = in.getOrder();
		List<OrderLine> orderLines = order.getOrderLines();
		// Build Layers

		// TODO Support multiple pallets
		Pallet pallet = in.getPallets().get(0);

		Map<Integer, Set<Thing>> map = new HashMap<>();
		int maxHeight = 1;
		for (OrderLine line : orderLines) {
			Article a = line.getArticle();
			maxHeight = Math.max(maxHeight, a.getHeight());
			for (String barcode : line.getBarcodes()) {
				Thing t = new Thing(a, barcode, line.getOrderLineNo());
				if (map.get(a.getHeight()) == null) {
					map.put(a.getHeight(), new HashSet<Thing>());
				}
				map.get(a.getHeight()).add(t);
			}
		}

		List<Layer> layers = new ArrayList<Layer>();
		for (int height : map.keySet()) {
			Set<Thing> things = new HashSet<>(map.get(height));
			Map<Integer, Set<Thing>> lengths = new HashMap<>();
			for (int i = 1; i < 100; i++) {
				int round = (int) ((1.0d / i) * (double) maxHeight) + 1;
				lengths.put(round, new HashSet<Thing>());
				Iterator<Thing> iter = things.iterator();
				while(iter.hasNext()) {
					Thing next = iter.next();
					if (next.article.getLength() > round) {
						lengths.get(round).add(next);
						iter.remove();
					}
				}
				
				if (lengths.get(round).isEmpty()) {
					lengths.remove(round);
				}
			}
			
			Layer l = new Layer();
			int len = 0;
			for (int length : lengths.keySet()) {
				int wid = 0;
				for (Thing t : lengths.get(length)) {
					if (wid + t.article.getWidth() > pallet.getWidth()) {
						// Move to next shelf
						if (len + length > pallet.getLength()) {
							// Move to next layer
							layers.add(l);
							l = new Layer();
							len = 0;
						} else {
							len += length;
						}
						wid = 0;
					}
					l.map.put(t, new Point(wid, len, 0));
					wid += t.article.getWidth();
					
					System.out.printf("Point (%d, %d)\n", wid, len);
				}
			}
			layers.add(l);
		}
		
		// Build Quick Output

		QuickOutput curr = new QuickOutput(in);
		curr.layers = layers.toArray(new Layer[0]);

		// Metropolis Update

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

		return curr.toOutput();
	}



	private static double fitness(QuickOutput config) {
		double height = config.getHeight();
		double cog = config.getHeightOfCenterOfGravity();
		return height/cog;
	}

	private class Layer {

		private Map<Thing, Point> map;

		public Layer() {
			map = new HashMap<Thing, Point>();
		}

		public int getHeight() {
			int min = Integer.MAX_VALUE;
			int max = Integer.MIN_VALUE;
			for (Thing t : map.keySet()) {
				min = Math.min(min, map.get(t).z);
				max = Math.max(max, map.get(t).z + t.article.getHeight());
			}
			return max - min;
		}

		public double getWeight() {
			double weight = 0;
			for (Thing t : map.keySet()) {
				weight += t.article.getWeight();
			}
			return weight;
		}

	}

	private class Thing {
		private Article article;
		private String barcode;
		private int orderLineNo;

		public Thing(Article article, String barcode, int orderLineNo) {
			this.article = article;
			this.barcode = barcode;
			this.orderLineNo = orderLineNo;
		}
	}

	private class QuickOutput {

		private Input input;

		/**
		 * layers.get(0) is the one closest to the ground
		 */
		private Layer[] layers;

		public QuickOutput(Input input) {
			this.input = input;
		}

		/**
		 * @return the height of the center of gravity
		 */
		public double getHeightOfCenterOfGravity() {
			double adjustedWeight = 0.0, weight = 0.0, height = 0.0;
			for (Layer l : layers) {
				height += l.getHeight();
				double tmp = l.getWeight();
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
			for (Layer l : layers) {
				total += l.getHeight();
			}
			return total;
		}

		public int getNumberOfLayers() {
			return layers == null ? 0 : layers.length;
		}

		public void swap(int a, int b) {
			Layer x = layers[a];
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
				for (Layer l : layers) {
					height += l.getHeight();
					for (Thing t : l.map.keySet()) {
						Point pt = l.map.get(t);
						int x = pt.x;// + t.article.getLength() / 2;
						int y = pt.y;// + t.article.getWidth() / 2;
						Pakkage p = new Pakkage(packSequence++, incomingSequence++, t.orderLineNo, 0, t.article, t.barcode, new Point(x, y, height), 2, Pakkage.getDefaultApproachPoints(), 0);
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
