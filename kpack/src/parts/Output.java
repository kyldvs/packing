package parts;

import geom.Point;

import java.util.List;
import java.util.Map;

public class Output {

	public static String format(Order o, Pallet p, List<Box> seq, Map<Box, Point> pos,	Map<Box, Integer> orient) {
		StringBuilder sb = new StringBuilder();
		sb.append("<Response>\n");
		sb.append("\t<PackList>\n");
		sb.append("\t\t<OrderID>" + o.desc.num + "</OrderID>\n");
		sb.append("\t\t<PackPallets>\n");
		sb.append("\t\t\t<PackPallet>\n");
		sb.append("\t\t\t\t<PalletNumber>" + p.desc.num + "</PalletNumber>\n");
		sb.append("\t\t\t\t<Description>" + p.desc.str + "</Description>\n");
		sb.append("\t\t\t\t<Dimensions>\n");
		sb.append("\t\t\t\t\t<Length>" + p.dim.x + "</Length>\n");
		sb.append("\t\t\t\t\t<Width>" + p.dim.z + "</Width>\n");
		sb.append("\t\t\t\t\t<MaxLoadHeight>" + p.dim.y + "</MaxLoadHeight>\n");
		sb.append("\t\t\t\t\t<MaxLoadWeight>" + p.capacity + "</MaxLoadWeight>\n");
		sb.append("\t\t\t\t</Dimensions>\n");
		sb.append("\t\t\t\t<Packages>\n");
		for(int i = 0; i < seq.size(); i++) {
			Box b = seq.get(i);
			sb.append("\t\t\t\t\t<Package>\n");
			sb.append("\t\t\t\t\t\t<PackSequence>" + i + "</PackSequence>\n");
			sb.append("\t\t\t\t\t\t<IncomingSequence>" + i + "</IncomingSequence>\n");
			sb.append("\t\t\t\t\t\t<OrderLineNo>" + b.orderLine + "</OrderLineNo>\n");
			sb.append("\t\t\t\t\t\t<ParentLayer>" + 0 + "</ParentLayer>\n"); // XXX
			sb.append("\t\t\t\t\t\t<Article>\n");
			sb.append("\t\t\t\t\t\t\t<ID>" + b.article.num + "</ID>\n");
			sb.append("\t\t\t\t\t\t\t<Description>" + b.article.str + "</Description>\n");
			sb.append("\t\t\t\t\t\t\t<Type>" + 0 + "</Type>\n"); // XXX
			sb.append("\t\t\t\t\t\t\t<Length>" + b.dim.x + "</Length>\n");
			sb.append("\t\t\t\t\t\t\t<Width>" + b.dim.z + "</Width>\n");
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

			int or = orient.get(b);
			Point b_pos = pos.get(b);
			sb.append("\t\t\t\t\t\t\t<X>" + (b_pos.x + (or == 1 ? b.dim.x / 2 : b.dim.z / 2)) + "</X>\n");
			sb.append("\t\t\t\t\t\t\t<Y>" + (b_pos.z + (or == 1 ? b.dim.z / 2 : b.dim.x / 2)) + "</Y>\n");
			sb.append("\t\t\t\t\t\t\t<Z>" + (b_pos.y + b.dim.y) + "</Z>\n");

			sb.append("\t\t\t\t\t\t</PlacePosition>\n");
			sb.append("\t\t\t\t\t\t<Orientation>" + or + "</Orientation>\n"); // XXX
			for (int j = 1; j <= 3; j++) {
				sb.append("\t\t\t\t\t\t<ApproachPoint" + j + ">\n"); // XXX
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
