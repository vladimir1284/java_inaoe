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
		for (int i = this.atts - 1; i >= 0; i--) {
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
	
	public void getCurrentCandidate(boolean Testor, boolean Contributes){
		//System.out.println(this.J);
		// Last attribute reached
		if (this.J == this.atts-1){
			this.remove2MSB(this.Current, this.BC);
			this.pervIndex = this.BC.Jprev;
			this.J = this.BC.Jel + 1;			
			this.Current = this.BC.Candidate.setBit(this.J);
		} else{
		// Is testor or doesn't contributes
		if ((Contributes == false) || (Testor == true)){
			this.removeMSB(this.Current, this.BC);
			this.pervIndex = this.BC.Jprev;
			this.J = this.BC.Jel + 1;
			this.Current = this.BC.Candidate.setBit(this.J);
		}
		// Not testor but contributes
		if ((Contributes == true) && (Testor == false)){
			this.pervIndex = this.J;
			this.J++;
			this.Current = this.Current.setBit(this.J);
		}
		}
		
	}
}
