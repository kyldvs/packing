package xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import parts.Box;
import parts.Input;
import parts.Order;
import parts.Pallet;

public class Xml {

	public static final Input input(File xml) throws ParserConfigurationException, SAXException, IOException {
		Input.Builder ib = Input.builder();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = db.parse(xml);
		Element message = dom.getDocumentElement();
		Element palletInit = childElementByTag(message, "PalletInit");
		Element pallets = childElementByTag(palletInit, "Pallets");
		
		for (Element pallet : childrenElementsByTag(pallets, "Pallet")) {
			Pallet.Builder pb = Pallet.builder();
			pb.desc(intValue(pallet, "PalletNumber"), textValue(pallet, "Description"));
			Element dimensions = childElementByTag(pallet, "Dimensions");
			pb.dimensions(intValue(dimensions, "Length"), intValue(dimensions, "Width"), intValue(dimensions, "MaxLoadHeight"));
			pb.capacity(intValue(dimensions, "MaxLoadWeight"));
			Element overhang = childElementByTag(pallet, "Overhang");
			pb.overhang(intValue(overhang, "Length"), intValue(overhang, "Width"));
			Element securityMargins = childElementByTag(pallet, "SecurityMargins");
			pb.margins(intValue(securityMargins, "Length"), intValue(securityMargins, "Width"));
			ib.palle(pb.build());
		}

		Element order = childElementByTag(message, "Order");
		Order.Builder ob = Order.builder();
		ob.desc(intValue(order, "ID"), textValue(order, "Description"));
		
		// <ignore>
		Element restrictions = childElementByTag(order, "Restrictions");
		@SuppressWarnings("unused")
		boolean familyGrouping = booleanValue(restrictions, "FamilyGrouping");
		@SuppressWarnings("unused")
		boolean ranking = booleanValue(restrictions, "Ranking");
		// </ignore>
		
		Element orderLines = childElementByTag(order, "OrderLines");
		for (Element orderLine : childrenElementsByTag(orderLines, "OrderLine")) {
			int orderLineNo = intValue(orderLine, "OrderLineNo");
			Element article = childElementByTag(orderLine, "Article");
			int art_id = intValue(article, "ID");
			String art_description = textValue(article, "Description");
			int length = intValue(article, "Length");
			int width = intValue(article, "Width");
			int height = intValue(article, "Height");
			int weight = intValue(article, "Weight");

			// <ignore>
			@SuppressWarnings("unused")
			int type = intValue(article, "Type");
			@SuppressWarnings("unused")
			int family = intValue(article, "Family");
			// </ignore>
			
			Element barcodes = childElementByTag(orderLine, "Barcodes");
			for (Element barcode : childrenElementsByTag(barcodes, "Barcode")) {
				Box.Builder bb  = Box.builder();
				bb.article(art_id, art_description);
				bb.dim(width, height, length);
				bb.barcode(textValue(barcode));
				bb.weight(weight);
				bb.orderLine(orderLineNo);
				ob.box(bb.build());
			}
		}

		ib.order(ob.build());
		return ib.build();
	}

	public static final Element childElementByTag(Element element, String tag) {
		if (tag == null || element == null) return null;
		NodeList l = element.getChildNodes();
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if (tag.equals(e.getTagName())) {
					return e;
				}
			}
		}
		return null;
	}

	public static final List<Element> childrenElementsByTag(Element element, String tag) {
		List<Element> ret = new ArrayList<Element>();
		if (tag == null || element == null) return ret;
		NodeList l = element.getChildNodes();
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if (e.getTagName().equals(tag)) {
					ret.add(e);
				}
			}
		}
		return ret;
	}

	public static final boolean booleanValue(Element n, String tag) {
		String text = textValue(n, tag);
		if (text == null || text.isEmpty()) return false;
		else return Boolean.parseBoolean(text);
	}

	public static final int intValue(Element n, String tag) {
		String text = textValue(n, tag);
		if (text == null || text.isEmpty()) return 0;
		else return Integer.parseInt(text.trim());
	}

	public static final String textValue(Element e) {
		String text = "";
		Node shit = e.getFirstChild();
		if (shit != null) {
			text = shit.getNodeValue();
		}
		return text;
	}

	public static final String textValue(Element e, String tag) {
		String text = "";
		NodeList nl = e.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element ele = (Element) n;
				if (tag.equals(ele.getTagName())) {
					Node shit = n.getFirstChild();
					if (shit == null) {
						continue;
					} else {
						text = shit.getNodeValue();
						break;
					}
				}
			}
		}

		return text;
	}
}
