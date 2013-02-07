package struct;

/**
 * Input
 * 
 * The description of a single pallet.
 * 
 * @author kyle
 */
public class Pallet {

	/**
	 * Pallet Number
	 */
	private int palletNumber;

	/**
	 * Name of the pallet, e.g. "Euro Pallet", CHEP Pallet, US Pallet, etc.
	 */
	private String description;

	/**
	 * Length of the pallet in X direction in [mm]
	 */
	private int length;

	/**
	 * Width of the pallet in Y direction in [mm]
	 */
	private int width;

	/**
	 * Maximum load height of the pallet in Z direction in [mm]
	 */
	private int maxLoadHeight;

	/**
	 * Maximum weight of the pallet in [g]
	 */
	private int maxLoadWeight;

	/**
	 * Allowed overhang along the length side in X direction in [mm]
	 */
	private int overhangLength;

	/**
	 * Allowed overhang along the width side in Y direction in [mm]
	 */
	private int overhangWidth;

	/**
	 * Safety distance between the cases along the length side in X direction in
	 * [mm]
	 */
	private int securityMarginLength;

	/**
	 * Safety distance between the cases along the width side in Y direction in
	 * [mm]
	 */
	private int securityMarginWidth;

	public Pallet(int palletNumber, String description, int length, int width,
			int maxLoadHeight, int maxLoadWeight, int overhangLength,
			int overhangWidth, int securityMarginLength, int securityMarginWidth) {
		super();
		this.palletNumber = palletNumber;
		this.description = description;
		this.length = length;
		this.width = width;
		this.maxLoadHeight = maxLoadHeight;
		this.maxLoadWeight = maxLoadWeight;
		this.overhangLength = overhangLength;
		this.overhangWidth = overhangWidth;
		this.securityMarginLength = securityMarginLength;
		this.securityMarginWidth = securityMarginWidth;
	}

	public int getPalletNumber() {
		return palletNumber;
	}

	public void setPalletNumber(int palletNumber) {
		this.palletNumber = palletNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public int getMaxLoadHeight() {
		return maxLoadHeight;
	}

	public void setMaxLoadHeight(int maxLoadHeight) {
		this.maxLoadHeight = maxLoadHeight;
	}

	public int getMaxLoadWeight() {
		return maxLoadWeight;
	}

	public void setMaxLoadWeight(int maxLoadWeight) {
		this.maxLoadWeight = maxLoadWeight;
	}

	public int getOverhangLength() {
		return overhangLength;
	}

	public void setOverhangLength(int overhangLength) {
		this.overhangLength = overhangLength;
	}

	public int getOverhangWidth() {
		return overhangWidth;
	}

	public void setOverhangWidth(int overhangWidth) {
		this.overhangWidth = overhangWidth;
	}

	public int getSecurityMarginLength() {
		return securityMarginLength;
	}

	public void setSecurityMarginLength(int securityMarginLength) {
		this.securityMarginLength = securityMarginLength;
	}

	public int getSecurityMarginWidth() {
		return securityMarginWidth;
	}

	public void setSecurityMarginWidth(int securityMarginWidth) {
		this.securityMarginWidth = securityMarginWidth;
	}
}
