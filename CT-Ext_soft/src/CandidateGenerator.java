import java.math.BigInteger;


public class CandidateGenerator {
	BigInteger FirstRow, Current;
	int[] Previous; // Last count of satisfying rows
	int J, atts, pervIndex;
	BaseCandidate BC;
	
	public CandidateGenerator(BigInteger firstRow, int atts){
		this.FirstRow = firstRow;
		this.Current = BigInteger.ONE;
		this.Previous = new int[atts];
		this.J = 0;
		this.pervIndex = 0;
		this.atts = atts;
		this.BC = new BaseCandidate();
	}

	public void removeMSB(BigInteger value, BaseCandidate bc) {
		int flag = 0;
		BigInteger mask = BigInteger.ONE;

		mask = mask.shiftLeft(this.atts - 1);
		for (int i = this.atts - 1; i > 0; i--) {
			if (!((value.and(mask).equals(BigInteger.ZERO)))) {
				if (flag == 1) {
					bc.Jprev = i; // Last remaining bit set
					break;
				} else {
					bc.Candidate = value.clearBit(i);
					bc.Jel = i;
					flag = 1;
				}
			}
			mask = mask.shiftRight(1);
		}

	}

	public void remove2MSB(BigInteger value, BaseCandidate bc) {
		int flag = 0;
		BigInteger mask = BigInteger.ONE;

		mask = mask.shiftLeft(this.atts - 1);
		for (int i = this.atts - 1; i > 0; i--) {
			if (!((value.and(mask).equals(BigInteger.ZERO)))) {
				if (flag == 2) {
					bc.Jprev = i; // Last remaining bit set
					break;
				} else {
					value = value.clearBit(i);
					if (flag == 1) {
						bc.Candidate = value;
						flag = 2;
						bc.Jel = i;
					} else {
						flag = 1;
					}
				}
			}
			mask = mask.shiftRight(1);
		}
	}
	
	public BigInteger getCurrentCandidate(boolean Testor, boolean Contributes){
		// Last attribute reached
		if (this.J == this.atts-1){
			
		}
			
		return Current;
		
	}
}
