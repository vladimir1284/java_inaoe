package fastCText2;

import java.util.LinkedList;
import java.util.ListIterator;

import tools.TuplaBinaria;

public class BasicMatrix {
	TuplaBinaria[] BM;
	int firstRowOnes, rows;

	public BasicMatrix(TuplaBinaria[] BM, int firstRowOnes, int rows) {
		this.BM = BM;
		this.firstRowOnes = firstRowOnes;
		this.rows = rows;
	}

//	public int[] getAMlx(int[] AMl, int x) {
//		return this.BM[x].or(AMl);
//	}
//
//	public int[] getCMlx(int[] AMl, int[] CMl, int x) {
//		return this.BM[x].getCMlx(CMl, AMl);
//	}

	public boolean typical(LinkedList<Integer> testor, TuplaBinaria[] acceptanceMasks) {
		// Get testor's compatibility mask
		ListIterator<Integer> iterator = testor.listIterator();
		TuplaBinaria AMl = new TuplaBinaria(rows,-1);
		TuplaBinaria CMl = new TuplaBinaria(rows,-1);
		int x;
		while (iterator.hasNext()) {
			x = iterator.next();
			CMl.mascComp(CMl, BM[x], AMl);
			AMl = acceptanceMasks[x];
		}
		// Check that every attribute in testor has a typical row
		iterator = testor.listIterator();
		while (iterator.hasNext()) {
			if (BM[iterator.next()].andNEqZ(CMl)) {
				return false;
			}
		}
		return true;
	}
}