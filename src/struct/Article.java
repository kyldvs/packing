package struct;

import xml.XMLAble;

/**
 * Input/Output
 * 
 * A type of case, the number of barcodes represents the number of this type of
 * case that is ordered
 * 
 * @author kyle
 */
public class Article implements XMLAble {

	/**
	 * Unique identifier of the article
	 */
	private int ID;

	/**
	 * Name of the article
	 */
	private String description;

	/**
	 * Packaging Type. [1 = Square box]. Currently only value 1 is allowed
	 */
	private int type;

	/**
	 * Length of the case in [mm]
	 * 
	 * Length by Width are specifying the bottom of the case. The length value
	 * should be larger than the width value
	 */
	private int length;

	/**
	 * Width of the case in [mm]
	 */
	private int width;

	/**
	 * Height of the case in [mm]
	 */
	private int height;

	/**
	 * Weight of the case in [g]
	 */
	private int weight;

	/**
	 * Number of the family group to which this case belongs to. Only relevant
	 * if Parameter Order.familyGrouping was set to True.
	 * 
	 * @see Order.familyGrouping
	 */
	private int family;

	/**
	 * Specifies the maximum rank class of cases, which can be placed on top of
	 * this case
	 */
	private int maxRankOnTop = DEFAULT_MAXRANKONTOP;
	public static final int DEFAULT_MAXRANKONTOP = 1000;

	/**
	 * Allowed gripper orientation during the pick up on the conveyor.
	 * 
	 * Default: All
	 */
	private String handlingAngles = DEFAULT_HANDLINGANGLES;
	public static final String DEFAULT_HANDLINGANGLES = "All";

	/**
	 * The rank of this article
	 */
	private int rank = DEFAULT_RANK;
	public static final int DEFAULT_RANK = 1;
	
	/**
	 * The minimum torque usable on this article
	 */
	private int minTorque = DEFAULT_MINTORQUE;
	public static final int DEFAULT_MINTORQUE = 1;
	
	/**
	 * The maximum torque usable on this article
	 */
	private int maxTorque = DEFAULT_MAXTORQUE;
	public static final int DEFAULT_MAXTORQUE = 100;
	
	public Article(int iD, String description, int type, int length, int width,
			int height, int weight, int family, int maxRankOnTop,
			String handlingAngles) {
		super();
		ID = iD;
		this.description = description;
		this.type = type;
		this.length = length;
		this.width = width;
		this.height = height;
		this.weight = weight;
		this.family = family;
		this.maxRankOnTop = maxRankOnTop;
		this.handlingAngles = handlingAngles;
	}

	@Override
	public String toXml(String pre) {
		String prepre = pre + "\t";
		StringBuilder sb = new StringBuilder();
		sb.append(pre + "<Article>\n");
		sb.append(prepre + "<ID>" + Integer.toString(ID) + "</ID>\n");
		sb.append(prepre + "<Description>" + description + "</Description>\n");
		sb.append(prepre + "<Type>" + Integer.toString(type) + "</Type>\n");
		sb.append(prepre + "<Length>" + Integer.toString(length) + "</Length>\n");
		sb.append(prepre + "<Width>" + Integer.toString(width) + "</Width>\n");
		sb.append(prepre + "<Height>" + Integer.toString(height) + "</Height>\n");
		sb.append(prepre + "<Weight>" + Integer.toString(weight) + "</Weight>\n");
		sb.append(prepre + "<Family>" + Integer.toString(family) + "</Family>\n");
		sb.append(prepre + "<Rank>" + Integer.toString(rank) + "</Rank>\n");
		sb.append(prepre + "<MaxRankOnTop>" + Integer.toString(maxRankOnTop) + "</MaxRankOnTop>\n");
		sb.append(prepre + "<HandlingAngles>" + handlingAngles + "</HandlingAngles>\n");
		sb.append(prepre + "<MinTorque>" + Integer.toString(minTorque) + "</MinTorque>\n");
		sb.append(prepre + "<MaxTorque>" + Integer.toString(maxTorque) + "</MaxTorque>\n");
		sb.append(pre + "</Article>");
		return sb.toString();
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getFamily() {
		return family;
	}

	public void setFamily(int family) {
		this.family = family;
	}

	public int getMaxRankOnTop() {
		return maxRankOnTop;
	}

	public void setMaxRankOnTop(int maxRankOnTop) {
		this.maxRankOnTop = maxRankOnTop;
	}

	public String getHandlingAngles() {
		return handlingAngles;
	}

	public void setHandlingAngles(String handlingAngles) {
		this.handlingAngles = handlingAngles;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getMinTorque() {
		return minTorque;
	}

	public void setMinTorque(int minTorque) {
		this.minTorque = minTorque;
	}

	public int getMaxTorque() {
		return maxTorque;
	}

	public void setMaxTorque(int maxTorque) {
		this.maxTorque = maxTorque;
	}

}
