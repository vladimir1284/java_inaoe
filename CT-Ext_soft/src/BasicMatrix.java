import java.math.BigInteger;


public class BasicMatrix {
	BigInteger[] BM;
	int rows;
	
	public BasicMatrix(BigInteger[] BM, int rows){
		this.BM = BM;
		this.rows = rows;
	}
	
	public void evaluateCandidate(BigInteger candidate, CandidateEval evaluation){
		int count = 0;
		BigInteger temp;
		for (int i = 0; i < this.rows; i++){
			temp = this.BM[i].and(candidate);
			if (!(temp.equals(BigInteger.ZERO))){
				count++;
			}
		}
		evaluation.Nsatisfy = count;
		evaluation.Testor = (count == this.rows);
	}
}
