package struct;

import java.util.List;

import xml.XMLAble;

/**
 * Output
 * 
 * @author kyle
 */
public class PackPallet implements XMLAble {

	/**
	 * The Pallet to be packed
	 */
	private Pallet pallet;

	/**
	 * Tha packages to be packed on the pallet
	 */
	private List<Pakkage> pakkages;

	public PackPallet(Pallet pallet, List<Pakkage> pakkages) {
		super();
		this.pallet = pallet;
		this.pakkages = pakkages;
	}

	@Override
	public String toXml(String pre) {
		String prepre = pre + "\t";
		String preprepre = prepre + "\t";
		StringBuilder sb = new StringBuilder();
		sb.append(pre + "<PackPallet>\n");
		sb.append(prepre + "<PalletNumber>" + Integer.toString(pallet.getPalletNumber()) + "</PalletNumber>\n");
		sb.append(prepre + "<Description>" + pallet.getDescription() + "</Description>\n");
		sb.append(prepre + "<Dimensions>\n");
		sb.append(preprepre + "<Length>" + Integer.toString(pallet.getLength()) + "</Length>\n");
		sb.append(preprepre + "<Width>" + Integer.toString(pallet.getWidth()) + "</Width>\n");
		sb.append(preprepre + "<MaxLoadHeight>" + Integer.toString(pallet.getMaxLoadHeight()) + "</MaxLoadHeight>\n");
		sb.append(preprepre + "<MaxLoadWeight>" + Integer.toString(pallet.getMaxLoadWeight()) + "</MaxLoadWeight>\n");
		sb.append(prepre + "</Dimensions>\n");
		sb.append(prepre + "<Packages>\n");
		for (Pakkage pakkage : pakkages) {
			String pakkageXml = pakkage.toXml(preprepre);
			sb.append(pakkageXml + "\n");
		}
		sb.append(prepre + "</Packages>\n");
		sb.append(pre + "</PackPallet>");
		return sb.toString();
	}
	
	public Pallet getPallet() {
		return pallet;
	}

	public void setPallet(Pallet pallet) {
		this.pallet = pallet;
	}

	public List<Pakkage> getPakkages() {
		return pakkages;
	}

	public void setPakkages(List<Pakkage> pakkages) {
		this.pakkages = pakkages;
	}

}
