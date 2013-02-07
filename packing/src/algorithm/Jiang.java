package algorithm;

import geom.Point;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import struct.Article;
import struct.Input;
import struct.OrderLine;
import struct.Output;
import struct.PackList;
import struct.PackPallet;
import struct.Pakkage;
import struct.Pallet;
import struct.Surface;

public class Jiang implements Algorithm {

	private static final boolean SHITFACE = false;

	@Override
	public Output run(Input in) {
		if(SHITFACE) System.out.println(in.getPallets().size());
		if(SHITFACE) System.out.println(in.getOrder().getOrderLines().size());
		List<OrderLine> order = in.getOrder().getOrderLines();
		List<Pallet> pallet = in.getPallets();
		List<PackPallet> tmpList = new ArrayList<PackPallet>();
		tmpList.add(workOnSinglePallet(pallet.get(0), order));
		PackList tmpPackList = new PackList(in.getOrder().getId(), tmpList);
		Output ret = new Output(tmpPackList);
		return ret;
	}

	public static void work(List<Pallet> pallet, List<OrderLine> order) {
		int n = order.size(), m = pallet.size();
		for (int i = 0; i < n; i++) {
			Article cur = order.get(i).getArticle();
			if(SHITFACE) System.out.println(cur.getLength() + " " + cur.getWidth() + " "
					+ cur.getHeight());
		}
		for (int i = 0; i < m; i++) {
			Pallet cur = pallet.get(i);
			if(SHITFACE) System.out.println(cur.getLength() + " " + cur.getWidth() + " "
					+ cur.getMaxLoadHeight());
		}

	}

	public static boolean intersect(Point a, Point b, Point c, Point d) {
		Rectangle X = new Rectangle(a.x, a.y, b.x - a.x, b.y - a.y);
		Rectangle Y = new Rectangle(c.x, c.y, d.x - c.x, d.y - c.y);
		return X.intersects(Y);
	}

	static boolean CanPlace(Point pos, int X, int Y, ArrayList<Point> P,
			ArrayList<Point> Q) {
		for (int i = 0; i < P.size(); i++)
			if (intersect(pos, new Point(X, Y, 0), P.get(i), Q.get(i)))
				return false;
		return true;
	}

	public static PackPallet workOnSinglePallet(Pallet pallet,
			List<OrderLine> order) {

		int bestValue = 0;
		ArrayList<ArrayList<Point>> ans = null;
		ArrayList<ArrayList<String>> barcode = null;
		//		for (int i = 0; i < order.size(); i++) {
		//			Article cur = order.get(i).getArticle();
		//			// if(SHITFACE) System.out.println(cur.getLength()+" "+cur.getWidth()+" "+cur.getHeight()+" "+order.get(i).getBarcodes());
		//		}
		Collections.sort(order, new Comparator<OrderLine>() {
			public int compare(OrderLine o1, OrderLine o2) {
				return o2.getArticle().getHeight()
						- o1.getArticle().getHeight();
			}
		});
		int n = order.size();
		for (int rt = 0; rt < 10; rt++) {
			Collections.shuffle(order);
			PriorityQueue<Surface> PQ = new PriorityQueue<Surface>(100,
					new Comparator<Surface>() {
				public int compare(Surface o1, Surface o2) {
					return o1.height*(o2.x2 - o2.x1) * (o2.y2 - o2.y1)
								- o2.height*(o1.x2 - o1.x1) * (o1.y2 - o1.y1);
				}
			});
			PQ.add(new Surface(0, 0, pallet.getLength(), pallet.getWidth(), 0));
			ArrayList<ArrayList<Point>> ret = new ArrayList<ArrayList<Point>>();
			ArrayList<ArrayList<String>> codes = new ArrayList<ArrayList<String>>();
			for (int i = 0; i < n; i++)
				ret.add(new ArrayList<Point>());
			for (int i = 0; i < n; i++)
				codes.add(new ArrayList<String>());
			while (!PQ.isEmpty()) {
				Surface cur = PQ.remove();
				HashSet<Point> Points = new HashSet<Point>();// points to be
				// considered
				ArrayList<Point> P = new ArrayList<Point>();
				ArrayList<Point> Q = new ArrayList<Point>();
				Points.add(new Point(cur.x1, cur.y1, 0));
				Points.add(new Point(cur.x1, cur.y2, 0));
				Points.add(new Point(cur.x2, cur.y1, 0));
				Points.add(new Point(cur.x2, cur.y2, 0));
				int curH = -1;
				boolean done = false;
				for (int i = 0; i < n; i++) {
					if (done)
						break;
					Article curarArticle = order.get(i).getArticle();
					if (ret.get(i).size() < order.get(i).getBarcodes().size()
							&& (curH == -1 || curH == order.get(i).getArticle()
							.getHeight())) {
						for (Point X : Points) {
							if (X.x + curarArticle.getLength() <= cur.x2
									&& X.y + curarArticle.getWidth() <= cur.y2
									&& curarArticle.getHeight() + cur.height <= pallet
									.getMaxLoadHeight()
									&& CanPlace(X, curarArticle.getLength(),
											curarArticle.getWidth(), P, Q)) {
								curH = order.get(i).getArticle().getHeight();
								Points.add(new Point(X.x
										+ curarArticle.getLength(), X.y, 0));
								Points.add(new Point(X.x, X.y
										+ curarArticle.getWidth(), 0));
								Points.add(new Point(X.x
										+ curarArticle.getLength(), X.y
										+ curarArticle.getWidth(), 0));
								ret.get(i).add(new Point(X.x, X.y, cur.height));
								codes.get(i).add(
										order.get(i).getBarcodes()
										.get(ret.get(i).size() - 1));
								P.add(X);
								Q.add(new Point(X.x + curarArticle.getLength(),
										X.y + curarArticle.getWidth(), 0));
								// if(SHITFACE) System.out.println("_______________________");
								// if(SHITFACE) System.out.println(X.x+" "+
								// X.y+" "+cur.height);
								// if(SHITFACE) System.out.println(cur.x1+" "+cur.y1+" "+cur.x2+" "+cur.y2+" "+cur.height);
								// if(SHITFACE) System.out.println(order.get(i).getArticle().getLength()+" "+order.get(i).getArticle().getWidth()+" "+order.get(i).getArticle().getHeight());
								// if(SHITFACE) System.out.println(X.x+" "+X.y+" "+
								// (X.x+curarArticle.getLength())+" "+(X.y+curarArticle.getWidth())+" "+
								// (curH+cur.height));
								// if(SHITFACE) System.out.println((X.x+curarArticle.getLength())+" "+
								// X.y+" "+ cur.x2+" "+
								// (X.y+curarArticle.getWidth())+" "+
								// cur.height);
								// if(SHITFACE) System.out.println(X.x+" "+(
								// X.y+curarArticle.getWidth())+" "+ cur.x2+" "+
								// cur.y2+" "+ cur.height);
								//
								PQ.add(new Surface(X.x, X.y, X.x
										+ curarArticle.getLength(), X.y
										+ curarArticle.getWidth(), curH
										+ cur.height));
								if (X.x + curarArticle.getLength() != cur.x2)
									PQ.add(new Surface(X.x
											+ curarArticle.getLength(), X.y,
											cur.x2, X.y
											+ curarArticle.getWidth(),
											cur.height));
								if (X.y + curarArticle.getWidth() != cur.y2)
									PQ.add(new Surface(X.x, X.y
											+ curarArticle.getWidth(), cur.x2,
											cur.y2, cur.height));
								done = true;
								break;
							}
						}
					}
				}
			}
			// for (int i=0;i<n;i++) if(SHITFACE) System.out.println(used[i]);

			int tot = 0;
			for (int i = 0; i < n; i++)
				tot += ret.get(i).size();
			if (tot > bestValue) {
				bestValue = tot;
				ans = ret;
				barcode = codes;
			}
		}
		// for (int i=0;i<n;i++)
		// if(SHITFACE) System.out.println(ans.get(i).size()+" : "+
		// order.get(i).getBarcodes().size());
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < ans.get(i).size(); j++) {
				if(SHITFACE) System.out.println("________");
				if(SHITFACE) System.out.println("Barcode : " + barcode.get(i).get(j));
				if(SHITFACE) System.out.println("Position: " + ans.get(i).get(j).x + " " + ans.get(i).get(j).y + " " + ans.get(i).get(j).z);
			}
		}
		List<Pakkage> retpakkages = new ArrayList<Pakkage>();
		int tot = 0;
		for (int i = 0; i < n; i++)
			for (int j = 0; j < ans.get(i).size(); j++) {
				for (int k = 0; k < n; k++)
					for (int p = 0; p < order.get(k).getBarcodes().size(); p++) {
						if (order.get(k).getBarcodes().get(p).equals(barcode.get(i).get(j))) {
							Article art = order.get(k).getArticle();
							Point pt = ans.get(i).get(j);
							
							pt.z += art.getHeight();
							pt.x += art.getLength() / 2;
							pt.y += art.getWidth() / 2;
							
							retpakkages.add(new Pakkage(tot, tot, order.get(k).getOrderLineNo(), 0, order.get(k).getArticle(), order.get(k).getBarcodes().get(p), pt, 1, new Point[]{new Point(), new Point(), new Point()}, 0));
							tot++;
						}
					}
			}
		PackPallet ret = new PackPallet(pallet, retpakkages);
		return ret;
	}

}
