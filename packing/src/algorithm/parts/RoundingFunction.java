package algorithm.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import functional.Function;

public abstract class RoundingFunction implements Function<Integer, Integer> {

	public RoundingFunction() {
		// Default Constructor
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

	public static RoundingFunction clustering(final int k) {
		return new RoundingFunction() {

			private int[] round;

			@Override
			public void init(int[] values) {
				if (k >= values.length) {
					round = values.clone();
				} else {
					round = new int[k];
					for (int i = 0; i < k; i++) {
						round[i] = values[(int) (Math.random() * values.length)];
					}

					int[] b = new int[values.length];
					boolean change = true;
					while(change) {
						change = false;
						@SuppressWarnings("unchecked")
						List<Integer>[] clusters = new List[k];
						for (int i = 0; i < k; i++) {
							clusters[i] = new ArrayList<>();
						}
						for (int i = 0; i < values.length; i++) {
							for (int j = 1; j < k; j++) {
								if (Math.abs(values[i] - round[j]) < Math.abs(values[i] - round[b[i]])) {
									b[i] = j;
									change = true;
								}
							}
							clusters[b[i]].add(values[i]);
						}
						for (int i = 0; i < k; i++) {
							int sum = 0;
							for (int v : clusters[i]) {
								sum += v;
							}
							int res = Math.round((float) sum / clusters[i].size());
							if (res != round[i]) {
								round[i] = res;
								change = true;
							}
						}
						if (!change) {
							for (int i = 0; i < k; i++) {
								int max = 0;
								for (int v : clusters[i]) {
									max = Math.max(max, v);
								}
								round[i] = max;
							}
							break;
						}
					}
				}
				Arrays.sort(round);
			}

			@Override
			public Integer apply(Integer o) {
				if (round == null) {
					throw new NullPointerException("This rounding function was never intialized using init(int[] values)");
				}
				int x = Arrays.binarySearch(round, o);
				if (x < 0) {
					return round[-(x + 1)];
				} else {
					return round[x];
				}
			}
		};
	}

}
