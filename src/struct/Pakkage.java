package struct;

import xml.XMLAble;

public class Pakkage implements XMLAble {

	/**
	 * Sequence number in which the package has to placed on to the pallet
	 */
	private int packSequence;

	/**
	 * Sequence number in with the package has to be conveyed into the cell
	 * 
	 * Note: In the offline mixed palletizing environment the PackSequence and
	 * the IncomingSequence have to be identical
	 */
	private int incomingSequence;

	/**
	 * Unique number extracted from the ORDER.XML file
	 */
	private int orderLineNo;

	/**
	 * Layer number of the pallet on which the package will be placed to
	 */
	private int parentLayer;

	/**
	 * The article of this package
	 */
	private Article article;

	/**
	 * The barcodes of this package
	 */
	private String barcode;

	/**
	 * Final place position of the case on the pallet (X,Y,Z) All units are in
	 * [mm]
	 */
	private Point placePosition;

	/**
	 * Orientation of the case on the pallet in reference to the pallet
	 * coordinate system:
	 * 
	 * 1: 0°; the long side of the case is parallel to the X direction of the
	 * coordinate system
	 * 
	 * 2: 90°; the long side of the case is parallel to the Y direction of the
	 * coordinate system
	 */
	private int orientation;

	/**
	 * 3 Approach points are required
	 */
	public static final int REQUIRED_NUMBER_OF_APPROACH_POINTS = 3;

	/**
	 * Approach position above the pallet.
	 * 
	 * The coordinates of the approach points are relative to the place
	 * positions on the pallet
	 */
	private Point[] approachPoints;

	/**
	 * Highest Point on the pallet before placing the current case
	 */
	private int stackHeightBefore;

	public Pakkage(int packSequence, int incomingSequence, int orderLineNo,
			int parentLayer, Article article, String barcode,
			Point placePosition, int orientation, Point[] approachPoints,
			int stackHeightBefore) {
		super();
		this.packSequence = packSequence;
		this.incomingSequence = incomingSequence;
		this.orderLineNo = orderLineNo;
		this.parentLayer = parentLayer;
		this.article = article;
		this.barcode = barcode;
		this.placePosition = placePosition;
		this.orientation = orientation;
		this.approachPoints = approachPoints;
		this.stackHeightBefore = stackHeightBefore;
	}

	@Override
	public String toXml(String pre) {
		String prepre = pre + "\t";
		StringBuilder sb = new StringBuilder();
		sb.append(pre + "<Package>\n");
		sb.append(prepre + "<PackSequence>" + Integer.toString(packSequence) + "</PackSequence>\n");
		sb.append(prepre + "<IncomingSequence>" + Integer.toString(incomingSequence) + "</IncomingSequence>\n");
		sb.append(prepre + "<OrderLineNo>" + Integer.toString(orderLineNo) + "</OrderLineNo>\n");
		sb.append(prepre + "<ParentLayer>" + Integer.toString(parentLayer) + "</ParentLayer>\n");
		String articleXml = article.toXml(prepre);
		sb.append(articleXml + "\n");
		sb.append(prepre + "<Barcode>" + barcode + "</Barcode>\n");
		sb.append(prepre + "<PlacePosition>\n");
		String placePositionXml = placePosition.toXml(prepre + "\t");
		sb.append(placePositionXml + "\n");
		sb.append(prepre + "</PlacePosition>\n");
		sb.append(prepre + "<Orientation>" + Integer.toString(orientation) + "</Orientation>\n");
		for (int i = 0; i < approachPoints.length; i++) {
			sb.append(prepre + "<ApproachPoint" + Integer.toString(i + 1) + ">\n");
			String approachPointXml = approachPoints[i].toXml(prepre + "\t");
			sb.append(approachPointXml + "\n");
			sb.append(prepre + "</ApproachPoint" + Integer.toString(i + 1) + ">\n");
		}
		sb.append(prepre + "<StackHeightBefore>" + Integer.toString(stackHeightBefore) + "</StackHeightBefore>\n");
		sb.append(pre + "</Package>");
		return sb.toString();
	}
	
	public int getPackSequence() {
		return packSequence;
	}

	public void setPackSequence(int packSequence) {
		this.packSequence = packSequence;
	}

	public int getIncomingSequence() {
		return incomingSequence;
	}

	public void setIncomingSequence(int incomingSequence) {
		this.incomingSequence = incomingSequence;
	}

	public int getOrderLineNo() {
		return orderLineNo;
	}

	public void setOrderLineNo(int orderLineNo) {
		this.orderLineNo = orderLineNo;
	}

	public int getParentLayer() {
		return parentLayer;
	}

	public void setParentLayer(int parentLayer) {
		this.parentLayer = parentLayer;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Point getPlacePosition() {
		return placePosition;
	}

	public void setPlacePosition(Point placePosition) {
		this.placePosition = placePosition;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public Point[] getApproachPoints() {
		return approachPoints;
	}

	public void setApproachPoints(Point[] approachPoints) {
		this.approachPoints = approachPoints;
	}

	public int getStackHeightBefore() {
		return stackHeightBefore;
	}

	public void setStackHeightBefore(int stackHeightBefore) {
		this.stackHeightBefore = stackHeightBefore;
	}

	public static Point[] getDefaultApproachPoints() {
		Point[] ret = new Point[REQUIRED_NUMBER_OF_APPROACH_POINTS];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = new Point(0,0,0);
		}
		return ret;
	}

}
