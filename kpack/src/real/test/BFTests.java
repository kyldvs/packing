package real.test;

import org.junit.Test;

import real.BF;

public class BFTests {

	@Test
	public void testUpdate() throws Exception {
		int[][] height = new int[][]{{1, 1, 1, 1, 1},{1, 2, 2, 2, 1},{1, 2, 3, 2, 1},{1, 2, 3, 2, 1},{1, 2, 3, 2, 1},{1, 2, 2, 2, 1},{1, 1, 1, 1, 1}};
		int[][][] space = new int[7][5][4];
		
		BF.update(space, height);
		
		for (int d = 0; d < 4; d++) {
			for (int i = 4; i >= 0; i--) {
				for (int j = 0; j < 7; j++) {
					System.out.print(space[j][i][d] + "\t");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();
		}
	}
	
}
