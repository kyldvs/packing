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

import struct.Article;
import struct.Input;
import struct.Order;
import struct.OrderLine;
import struct.Pallet;

public class Xml {

//	public static void main(String[] args) throws Exception {
//		input(new File("test.xml"));
//	}

	public static final Input input(File xml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = db.parse(xml);
		Element message = dom.getDocumentElement();
		Element palletInit = childElementByTag(message, "PalletInit");
		Element pallets = childElementByTag(palletInit, "Pallets");
		List<Pallet> input_pallets = new ArrayList<Pallet>();
		for (Element pallet : childrenElementsByTag(pallets, "Pallet")) {
			int palletNumber = intValue(pallet, "PalletNumber");
			String description = textValue(pallet, "Description");

			Element dimensions = childElementByTag(pallet, "Dimensions");
			int length = intValue(dimensions, "Length");
			int width = intValue(dimensions, "Width");
			int maxLoadHeight = intValue(dimensions, "MaxLoadHeight");
			int maxLoadWeight = intValue(dimensions, "MaxLoadWeight");

			Element overhang = childElementByTag(pallet, "Overhang");
			int overhangLength = intValue(overhang, "Length");
			int overhangWidth = intValue(overhang, "Width");

			Element securityMargins = childElementByTag(pallet, "SecurityMargins");
			int securityMarginLength = intValue(securityMargins, "Length");
			int securityMarginWidth = intValue(securityMargins, "Width");

			input_pallets.add(new Pallet(palletNumber, description, length, width, maxLoadHeight, maxLoadWeight, overhangLength, overhangWidth, securityMarginLength, securityMarginWidth));
		}

		Element order = childElementByTag(message, "Order");
		int id = intValue(order, "ID");
		String description = textValue(order, "Description");
		Element restrictions = childElementByTag(order, "Restrictions");
		boolean familyGrouping = booleanValue(restrictions, "FamilyGrouping");
		boolean ranking = booleanValue(restrictions, "Ranking");
		Element orderLines = childElementByTag(order, "OrderLines");
		List<OrderLine> input_orderLines = new ArrayList<OrderLine>();
		for (Element orderLine : childrenElementsByTag(orderLines, "OrderLine")) {
			int orderLineNo = intValue(orderLine, "OrderLineNo");
			Element article = childElementByTag(orderLine, "Article");
			int art_id = intValue(article, "ID");
			String art_description = textValue(article, "Description");
			int type = intValue(article, "Type");
			int length = intValue(article, "Length");
			int width = intValue(article, "Width");
			int height = intValue(article, "Height");
			int weight = intValue(article, "Weight");
			int family = intValue(article, "Family");
			Article input_article = new Article(art_id, art_description, type, length, width, height, weight, family, Article.DEFAULT_MAXRANKONTOP, Article.DEFAULT_HANDLINGANGLES);

			Element barcodes = childElementByTag(orderLine, "Barcodes");
			List<String> input_barcodes = new ArrayList<String>();
			for (Element barcode : childrenElementsByTag(barcodes, "Barcode")) {
				input_barcodes.add(textValue(barcode));
			}

			input_orderLines.add(new OrderLine(orderLineNo, input_article, input_barcodes));
		}

		Order input_order = new Order(id, description, familyGrouping, ranking, input_orderLines);
		Input input = new Input(input_pallets, input_order);
		return input;
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
		else return Integer.parseInt(text);
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
