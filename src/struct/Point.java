package struct;

import xml.XMLAble;

public class Point implements XMLAble {

	public int x;
	public int y;
	public int z;

	public Point() {
		this(0, 0, 0);
	}

	public Point(Point p) {
		this(p.x, p.y, p.z);
	}

	public Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toXml(String pre) {
		StringBuilder sb = new StringBuilder();
		sb.append(pre + "<X>" + Integer.toString(x) + "</X>\n");
		sb.append(pre + "<Y>" + Integer.toString(y) + "</Y>\n");
		sb.append(pre + "<Z>" + Integer.toString(z) + "</Z>");
		return sb.toString();
	}
	
	
}
