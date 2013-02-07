package primitives;

public class Primitives {

	public static int[] toIntArr(Integer[] arr) {
		int[] ret = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			ret[i] = arr[i];
		}
		return ret;
	}
	
}
