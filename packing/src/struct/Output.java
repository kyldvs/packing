package struct;

import xml.XMLAble;

public class Output implements XMLAble {
	
	/**
	 * The packList to output
	 */
	private PackList packList;

	public Output(PackList packList) {
		super();
		this.packList = packList;
	}

	@Override
	public String toXml(String pre) {
		StringBuilder sb = new StringBuilder();
		sb.append(pre + "<Response>\n");
		String packListXml = packList.toXml(pre + "\t");
		sb.append(packListXml + "\n");
		sb.append(pre + "</Response>");
		return sb.toString();
	}

	public PackList getPackList() {
		return packList;
	}

	public void setPackList(PackList packList) {
		this.packList = packList;
	}
}
