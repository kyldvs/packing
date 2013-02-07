package algorithm;

import geom.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import algorithm.parts.Box;

public class Greedy implements Algorithm {

	private static final Random RAND = new Random();
	private static final int SAMPLES = 10000;

	private Map<Integer, Set<Box>> partitionHeights(Order order) {
		Map<Integer, Set<Box>> map = new HashMap<>();
		for (OrderLine line : order.getOrderLines()) {
			Article actArticle = line.getArticle();
			Box article = new Box(Point.origin(), new Point(actArticle.getLength() + 1, actArticle.getWidth() + 1, actArticle.getHeight()));
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

	private List<Box> fillLayer(Box layer, List<Box> boxes, boolean rotate) {
		List<Box> rem = new ArrayList<>();
		int x = 0;
		for(Box b : boxes) {
			if (rotate) {
				b.rotate();
			}
			if (x + b.dim.x > layer.dim.x) {
				x = 0;
			}

			b.at.x = x;
			x += b.dim.x;
			int y = 0;
			for (Box t : layer.boxes) {
				int l1 = b.at.x;
				int r1 = b.at.x + b.dim.x;
				int l2 = t.at.x;
				int r2 = t.at.x + t.dim.x;
				if (l1 <= l2 && r1 > l2 || l1 < r2 && r1 >= r2 || l1 >= l2 && r1 <= r2) {
					y = Math.max(y, t.at.y + t.dim.y);
				}
			}

			b.at.y = y;
			if (b.at.y + b.dim.y >= layer.dim.y) {
				b.at.x = 0;
				b.at.y = 0;
				rem.add(b);
			} else {
				layer.add(b);
			}
		}
		
		return rem;
	}

	@Override
	public Output run(Input in) {

		// TODO Support multiple pallets
		Box pallet = Box.fromPallet(in.getPallets().get(0));
		Map<Integer, Set<Box>> map = partitionHeights(in.getOrder());

		int answerHeight = Integer.MAX_VALUE;
		List<Box> answer = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			List<Box> layers = new ArrayList<>();
			for (int height : map.keySet()) {
				List<Box> boxes = new ArrayList<>(map.get(height));
				Collections.sort(boxes, Box.COMP_AREA);
//				Collections.reverse(boxes);

				if (i == 1 || i == 2) {
					for (Box b : boxes) {
						b.rotate();
					}
				}

				while(!boxes.isEmpty()) {
					Box layer = new Box(Point.origin(), new Point(pallet.dim.x, pallet.dim.y, height));

					if (i == 0 || i == 1) {
						boxes = fillLayer(layer, boxes, false);
					} else if (i == 2) {
						boxes = fillLayer(layer, boxes, false);
						boxes = fillLayer(layer, boxes, true);
					}

					finishLayer(layer);
					layers.add(layer);
				}
			}

			int height = 0;
			for (Box b : layers) {
				height += b.dim.z;
			}
			if (height < answerHeight) {
				answer = layers;
			}
		}

		// Build Quick Output

		QuickOutput curr = new QuickOutput(in);
		curr.layers = answer.toArray(new Box[0]);
		Arrays.sort(curr.layers, Box.COMP_INTERNAL_AREA);
		
		// Metropolis Update

		/*
		double fit = fitness(curr);
		int numlayers = curr.getNumberOfLayers();
		for (int i = 0; i < SAMPLES; i++) {
			if (numlayers > 0) {
				int a = RAND.nextInt(numlayers);
				int b = RAND.nextInt(numlayers);
				curr.swap(a,b);
				double knew = fitness(curr);
				if (knew > fit) {
					fit = knew;
				} else {
					curr.swap(a,b);
				}
			}
		}
		*/

		return curr.toOutput();
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
