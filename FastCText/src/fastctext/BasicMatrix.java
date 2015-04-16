import java.math.BigInteger;
import java.util.LinkedList;
import java.util.ListIterator;

public class BasicMatrix {
	BigInteger[] BM;
	int firstRowOnes, rows;
	BigInteger[] acceptanceMasks;

	public BasicMatrix(BigInteger[] BM, int firstRowOnes, int rows,
			BigInteger[] AcceptanceMasks) {
		this.BM = BM;
		this.firstRowOnes = firstRowOnes;
		this.rows = rows;
		this.acceptanceMasks = AcceptanceMasks;
	}

	public BigInteger getAMlx(BigInteger AMl, int x) {
		return AMl.or(this.BM[x]);
	}

	public BigInteger getCMlx(BigInteger AMl, BigInteger CMl, int x) {
		return CMl.xor(this.BM[x]).and(CMl)
				.or(AMl.xor(this.BM[x]).and(this.BM[x]));
	}

	public boolean typical(LinkedList<Integer> testor) {
		// Get testor's compatibility mask
		ListIterator<Integer> iterator = testor.listIterator();
		BigInteger AMl = BigInteger.ZERO;
		BigInteger CMl = BigInteger.ZERO;
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
			if (CMl.and(this.BM[iterator.next()]).equals(BigInteger.ZERO)) {
				return false;
			}
		}
		return true;
	}
}