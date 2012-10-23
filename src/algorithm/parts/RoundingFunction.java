package algorithm.parts;

import java.util.Arrays;
import java.util.LinkedList;

import functional.Function;

public abstract class RoundingFunction implements Function<Integer, Integer> {

	public RoundingFunction() {
		// Default Constructor
	}
	
	public RoundingFunction(int[] values) {
		init(values);
	}
	
	public abstract void init(int[] values);
	
	@Override
	public abstract Integer apply(Integer o);
	
	public static RoundingFunction harmonic() {
		return new RoundingFunction() {
			protected int[] rounded;
			
			@Override
			public void init(int[] values) {
				int min = values[0];
				int max = values[0];
				for (int i = 1; i < values.length; i++) {
					min = Math.min(min, values[i]);
					max = Math.max(max, values[i]);
				}
				
				LinkedList<Integer> r = new LinkedList<>();
				int i = 1;
				int diff = max - min;
				while(1.0 / i * diff > 1.0) {
					r.addFirst((int) (1.0 / i * diff + min));
					i++;
				}
				r.addFirst(min - 1);
				r.addLast(max + 1);
				rounded = new int[r.size()];
				int at = 0;
				for (int x : r) {
					rounded[at++] = x;
				}
			}
			
			@Override
			public Integer apply(Integer o) {
				if (rounded == null) {
					throw new NullPointerException("This rounding function was never intialized using init(int[] values)");
				}
				int x = Arrays.binarySearch(rounded, o);
				if (x < 0) {
					return rounded[-(x + 1)];
				} else {
					return rounded[x];
				}
			}
		};
	}
	
}
