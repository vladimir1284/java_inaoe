package fastCText2;
import java.math.BigInteger;
import java.util.LinkedList;

public class CandidateGenerator {
	int firstRowOnes;
	LinkedList<Integer> Current;
	int x,x_1,atts;

	public CandidateGenerator(int firstRowOnes, int atts) {
		this.firstRowOnes = firstRowOnes;
		this.Current =  new LinkedList<>();  
		this.Current.add(0); // First attribute is the first candidate
		this.x_1 = -1;
		this.x = 0;
		this.atts = atts;
	}

	public boolean getCurrentCandidate(boolean Testor, boolean Contributes) {
		// System.out.println(this.J);
		// Last attribute reached
		if (this.Current.getLast() == this.atts - 1) {
			this.Current.removeLast();
			this.x = this.Current.getLast()+1;
			this.Current.removeLast();
			// Check for an empty list
			if (this.Current.isEmpty()){
				this.x_1 = -1;
			}else{
				this.x_1 = this.Current.getLast();
			}
			// Add the new attribute
			this.Current.add(this.x);
		} else {
			// Is testor or doesn't contributes
			if ((Contributes == false) || (Testor == true)) {
				this.x = this.Current.getLast()+1;
				this.Current.removeLast();
				this.x_1 = this.Current.getLast();
				this.Current.add(this.x);
			}
			// Not testor but contributes
			if ((Contributes == true) && (Testor == false)) {
				this.x_1 = this.x;
				this.x++;
				this.Current.add(this.x);
			}
		}
		// Check for done
		return (this.Current.getFirst() == this.firstRowOnes); // First attribute has a 0 in the first row
	}
}
