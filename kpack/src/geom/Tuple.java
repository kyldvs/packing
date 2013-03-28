package geom;

public class Tuple implements Comparable<Tuple> {

	int[] values;
	
	public Tuple(int...values) {
		this.values = values == null ? new int[0] : values;
		
	}
	
	public int at(int i) {
		return values[i];
	}

	@Override
	public int compareTo(Tuple tht) {
		for (int i = 0; i < values.length && i < tht.values.length; i++) {
			if (values[i] != tht.values[i]) {
				return Integer.compare(values[i], tht.values[i]);
			}
		}
		return Integer.compare(values.length, tht.values.length);
	}
}
