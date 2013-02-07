package struct;

import java.util.List;

import xml.XMLAble;

/**
 * Output
 * 
 * @author kyle
 */
public class PackList implements XMLAble {

	/**
	 * Unique order number extracted from the ORDER.XML file
	 */
	private int OrderID;

	/**
	 * List of the pallets to be packed
	 */
	private List<PackPallet> palletData;

	public PackList(int orderID, List<PackPallet> palletData) {
		super();
		OrderID = orderID;
		this.palletData = palletData;
	}

	@Override
	public String toXml(String pre) {
		String prepre = pre + "\t";
		String preprepre = prepre + "\t";
		StringBuilder sb = new StringBuilder();
		sb.append(pre + "<PackList>\n");
		sb.append(prepre + "<OrderID>" + Integer.toString(OrderID) + "</OrderID>\n");
		sb.append(prepre + "<PackPallets>\n");
		for (PackPallet packPallet : palletData) {
			String packPalletXml = packPallet.toXml(preprepre);
			sb.append(packPalletXml + "\n");
		}
		sb.append(prepre + "</PackPallets>\n");
		sb.append(pre + "</PackList>");
		return sb.toString();
	}
	
	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public List<PackPallet> getPalletData() {
		return palletData;
	}

	public void setPalletData(List<PackPallet> palletData) {
		this.palletData = palletData;
	}

}
