package fastCText2;

import java.util.LinkedList;
import java.util.ListIterator;

public class BasicMatrix {
	BMcolumn[] BM;
	int firstRowOnes, rows;
	int [][] acceptanceMasks;

	public BasicMatrix(BMcolumn[] BM, int firstRowOnes, int rows,
			int [][] AcceptanceMasks) {
		this.BM = BM;
		this.firstRowOnes = firstRowOnes;
		this.rows = rows;
		this.acceptanceMasks = AcceptanceMasks;
	}

	public int[] getAMlx(int[] AMl, int x) {
		return this.BM[x].or(AMl);
	}

	public int[] getCMlx(int[] AMl, int[] CMl, int x) {
		return this.BM[x].getCMlx(CMl, AMl);
	}

	public boolean typical(LinkedList<Integer> testor) {
		// Get testor's compatibility mask
		ListIterator<Integer> iterator = testor.listIterator();
		int[] AMl = new int [this.BM[0].nwords];
		int[] CMl = new int [this.BM[0].nwords];
		int x;
		while (iterator.hasNext()) {
			x = iterator.next();
			CMl = getCMlx(AMl, CMl, x);
			AMl = acceptanceMasks[x];
			//AMl = getAMlx(AMl, x);
		}
		// Check that every attribute in testor has a typical row
		iterator = testor.listIterator();
		while (iterator.hasNext()) {
			if (this.BM[iterator.next()].andEqZ(CMl)) {
				return false;
			}
		}
		return true;
	}
}