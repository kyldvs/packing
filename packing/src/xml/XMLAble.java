package xml;

public interface XMLAble {

	/**
	 * @param pre
	 *            a prefix for each line for spacing purposes, should only
	 *            contain whitespace
	 * @return a valid XML formatted string of the object, the last line should
	 *         contain text, do not leave a blank line at the end
	 */
	public String toXml(String pre);

}
