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
import algorithm.parts.Box;

public class Harmonic implements Algorithm {

	private static final int SAMPLES = 10000;

	private static final Random r = new Random();

	@Override
	public Output run(Input in) {

		Order order = in.getOrder();
		List<OrderLine> orderLines = order.getOrderLines();

		// TODO Support multiple pallets
		
		Pallet actPallet = in.getPallets().get(0);
		
		Box pallet = new Box(Point.origin(), new Point(actPallet.getLength(), actPallet.getWidth(), actPallet.getMaxLoadHeight()));

		Map<Integer, Set<Box>> map = new HashMap<>();
		
		for (OrderLine line : orderLines) {
			Article actArticle = line.getArticle();
			Box article = new Box(Point.origin(), new Point(actArticle.getLength(), actArticle.getWidth(), actArticle.getHeight()));
			article.p("weight", actArticle.getWeight());
			article.p("article", actArticle);
			
			for (String barcode : line.getBarcodes()) {
				Box item = new Box(article);
				item.p("barcode", barcode);
				item.p("orderLineNumber", line.getOrderLineNo());
				if (map.get(article.dim.z) == null) {
					map.put(article.dim.z, new HashSet<Box>());
				}
				map.get(article.dim.z).add(item);
			}
		}

		List<Box> layers = new ArrayList<>();
		for (int height : map.keySet()) {
			int maxX = 1;
			Set<Box> items = new HashSet<>(map.get(height));
			for (Box b : items) {
				maxX = Math.max(maxX, b.dim.x);
			}
			
			Map<Integer, Set<Box>> xDim = new HashMap<>();
			
			int last = (int) (0.01 * maxX + 1);
			int lastI = (int) (1.0 / (Math.max(last - 1.0, 1) / (double) maxX));
			
			for (int i = lastI; i > 0; i--) {
				int round = (int) ((1.0d / i) * (double) maxX) + 1;
				xDim.put(round, new HashSet<Box>());
				Set<Box> roundedBoxes = xDim.get(round);
				
				Iterator<Box> iter = items.iterator();
				while(iter.hasNext()) {
					Box next = iter.next();
					if (next.dim.x < round) {
						roundedBoxes.add(next);
						iter.remove();
					}
				}
			}
			
			if (!items.isEmpty()) {
				System.out.println("Had some items left... weird.");
				xDim.put(maxX, new HashSet<Box>());
				Set<Box> roundedBoxes = xDim.get(maxX);
				for (Box box : items) {
					roundedBoxes.add(box);
				}
			}
			
			Box layer = new Box(Point.origin(), new Point(pallet.dim.x, pallet.dim.y, height));

			// Shelve "height"
			int y = 0;
			int x = 0;
			for (int dim : xDim.keySet()) {
				for (Box b : xDim.get(dim)) {
					if (b.at.y + b.dim.y + y > layer.dim.y) {
						y = 0;
						x += dim;
					}
					if (b.at.x + b.dim.x + x > layer.dim.x) {
						x = 0;
						y = 0;
						finishLayer(layer);
						layers.add(layer);
						layer = new Box(Point.origin(), new Point(pallet.dim.x, pallet.dim.y, height));
					}
					
					b.at.x += x;
					b.at.y += y;
					
					y += b.dim.y;
					
					
					
					layer.add(b);
				}
			}
			
			finishLayer(layer);
			layers.add(layer);
			
			/*
			
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
			
			*/
		}
		
		// Build Quick Output

		QuickOutput curr = new QuickOutput(in);
		curr.layers = layers.toArray(new Box[0]);

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
								1, 
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
