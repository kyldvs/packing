package real;

import geom.Point;

import java.util.List;
import java.util.Map;
import java.util.Random;

import parts.Box;
import parts.Input;
import parts.Order;
import parts.Pallet;
import algorithm.Algorithm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Reak implements Algorithm {

	@Override
	public String run(Input in) {
		Order o = in.order;
		Pallet p = in.pallets.get(0);
		
		// Output information
		List<Box> order = Lists.newArrayList();
		Map<Box, Point> pos = Maps.newHashMap();
		Map<Box, Integer> orientation = Maps.newHashMap();
		
		int y = 0;
		for (Box b : o.boxes) {
			order.add(b);
			pos.put(b, Point.create(0, y, 0));
			orientation.put(b, new Random().nextInt(2) + 1); // 2 is 90, or do I put 0-90 in here
			y += b.dim.y;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("<Response>\n");
		sb.append("\t<PackList>\n");
		sb.append("\t\t<OrderID>" + o.desc.num + "</OrderID>\n");
		sb.append("\t\t<PackPallets>\n");
		sb.append("\t\t\t<PackPallet>\n");
		sb.append("\t\t\t\t<PalletNumber>" + p.desc.num + "</PalletNumber>\n");
		sb.append("\t\t\t\t<Description>" + p.desc.str + "</Description>\n");
		sb.append("\t\t\t\t<Dimensions>\n");
		sb.append("\t\t\t\t\t<Length>" + p.dim.z + "</Length>\n");
		sb.append("\t\t\t\t\t<Width>" + p.dim.x + "</Width>\n");
		sb.append("\t\t\t\t\t<MaxLoadHeight>" + p.dim.y + "</MaxLoadHeight>\n");
		sb.append("\t\t\t\t\t<MaxLoadWeight>" + p.capacity + "</MaxLoadWeight>\n");
		sb.append("\t\t\t\t</Dimensions>\n");
		sb.append("\t\t\t\t<Packages>\n");
		for(int i = 0; i < order.size(); i++) {
			Box b = order.get(i);
			sb.append("\t\t\t\t\t<Package>\n");
			sb.append("\t\t\t\t\t\t<PackSequence>" + i + "</PackSequence>\n");
			sb.append("\t\t\t\t\t\t<IncomingSequence>" + i + "</IncomingSequence>\n");
			sb.append("\t\t\t\t\t\t<OrderLineNo>" + b.orderLine + "</OrderLineNo>\n");
			sb.append("\t\t\t\t\t\t<ParentLayer>" + 0 + "</ParentLayer>\n"); // XXX
			sb.append("\t\t\t\t\t\t<Article>\n");
			sb.append("\t\t\t\t\t\t\t<ID>" + b.article.num + "</ID>\n");
			sb.append("\t\t\t\t\t\t\t<Description>" + b.article.str + "</Description>\n");
			sb.append("\t\t\t\t\t\t\t<Type>" + 0 + "</Type>\n"); // XXX
			sb.append("\t\t\t\t\t\t\t<Length>" + b.dim.z + "</Length>\n");
			sb.append("\t\t\t\t\t\t\t<Width>" + b.dim.x + "</Width>\n");
			sb.append("\t\t\t\t\t\t\t<Height>" + b.dim.y + "</Height>\n");
			sb.append("\t\t\t\t\t\t\t<Weight>" + b.weight + "</Weight>\n");
			sb.append("\t\t\t\t\t\t\t<Family>" + 0 + "</Family>\n"); // XXX
			sb.append("\t\t\t\t\t\t\t<Rank>" + 1 + "</Rank>\n"); // XXX
			sb.append("\t\t\t\t\t\t\t<MaxRankOnTop>" + 1000 + "</MaxRankOnTop>\n"); // XXX
			sb.append("\t\t\t\t\t\t\t<HandlingAngles>" + "All" + "</HandlingAngles>\n"); // XXX
			sb.append("\t\t\t\t\t\t\t<MinTorque>" + 1 + "</MinTorque>\n"); // XXX
			sb.append("\t\t\t\t\t\t\t<MaxTorque>" + 100 + "</MaxTorque>\n"); // XXX
			sb.append("\t\t\t\t\t\t</Article>");
			sb.append("\t\t\t\t\t\t<Barcode>" + b.barcode + "</Barcode>\n");
			sb.append("\t\t\t\t\t\t<PlacePosition>\n");
			
			int or = orientation.get(b);
			Point b_pos = pos.get(b);
			sb.append("\t\t\t\t\t\t\t<X>" + (b_pos.x + (or == 1 ? b.dim.z / 2 : b.dim.x / 2)) + "</X>\n");
			sb.append("\t\t\t\t\t\t\t<Y>" + (b_pos.z + (or == 1 ? b.dim.x / 2 : b.dim.z / 2)) + "</Y>\n");
			sb.append("\t\t\t\t\t\t\t<Z>" + (b_pos.y + b.dim.y) + "</Z>\n");

			sb.append("\t\t\t\t\t\t</PlacePosition>\n");
			sb.append("\t\t\t\t\t\t<Orientation>" + or + "</Orientation>\n"); // XXX
			for (int j = 1; j <= 3; j++) {
				sb.append("\t\t\t\t\t\t<ApproachPoint" + j + ">\n");
				sb.append("\t\t\t\t\t\t\t<X>" + 0 + "</X>\n");
				sb.append("\t\t\t\t\t\t\t<Y>" + 0 + "</Y>\n");
				sb.append("\t\t\t\t\t\t\t<Z>" + 0 + "</Z>\n");
				sb.append("\t\t\t\t\t\t</ApproachPoint" + j + ">\n");
			}
			sb.append("\t\t\t\t\t\t<StackHeightBefore>" + 0 + "</StackHeightBefore>\n"); // XXX
			sb.append("\t\t\t\t\t</Package>\n");
		}
		sb.append("\t\t\t\t</Packages>\n");
		sb.append("\t\t\t</PackPallet>\n");
		sb.append("\t\t</PackPallets>\n");
		sb.append("\t</PackList>\n");
		sb.append("</Response>");
		return sb.toString();
	}

}
