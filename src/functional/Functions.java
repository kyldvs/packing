package functional;

public class Functions {

	public static <A, B, C> Function<A, C> compose(final Function<A, B> f1,
			final Function<B, C> f2) {
		
		return new Function<A, C>() {
			@Override
			public C apply(A o) {
				return f2.apply(f1.apply(o));
			}
		};
	}

}
