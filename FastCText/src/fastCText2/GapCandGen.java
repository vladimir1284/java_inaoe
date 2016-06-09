package fastCText2;

import tools.TPila;

public class GapCandGen {
	int firstRowOnes;
	TPila Current;
	int x, x_1, atts;

	public GapCandGen(int fstRowOnes, int ats) {
		firstRowOnes = fstRowOnes;
		Current = new TPila();
		Current.setRange(ats);
		Current.push(0); // First attribute is the first candidate
		x_1 = -1;
		x = 0;
		atts = ats;
	}

	public boolean getCurrentCandidate(boolean Testor, boolean Contributes,
			boolean Typical) {
		// System.out.println(J);
		// Last attribute reached
		if (Current.pila[Current.tope] == atts - 1) {
			// Typical Testor or Not Testor
			if (Typical || !Testor) {
				int last = Current.pop();
				while (Current.pila[Current.tope] == (last - 1)) {
					last = Current.pop();
					if (Current.tope == 0)
						break;
				}
			} else {
				Current.pop();
			}
			// x = Current.pila[Current.tope]+1;
			x = Current.pop() + 1;
			// Check for an empty list
			if (Current.tope == -1) {
				x_1 = -1;
			} else {
				x_1 = Current.pila[Current.tope];
			}
			// Add the new attribute
			Current.push(x);
		} else {
			// Is testor or doesn't contributes
			if ((Contributes == false) || (Testor == true)) {
				// x = Current.pila[Current.tope]+1;
				x = Current.pop() + 1;
				x_1 = Current.tope == -1 ? -1 : Current.pila[Current.tope];
				Current.push(x);
			}
			// Not testor but contributes
			if ((Contributes == true) && (Testor == false)) {
				x_1 = x;
				// x++;
				Current.push(++x);
			}
		}
		// Check for done
		return (Current.pila[0] == firstRowOnes); // First attribute has a 0 in
													// the first row
	}

	public String toString() {
		String candidate = "";
		for (int i = 0; i <= Current.tope; i++) {
			candidate += Current.pila[i] + 1;
		}
		return candidate.substring(0, candidate.length());

	}
//	public String toString() {
//		String candidate = "$";
//		for (int i = 0; i <= Current.tope; i++) {
//			candidate += "c_" + Current.pila[i] + ",";
//		}
//		return candidate.substring(0, candidate.length() - 1) + "$";
//
//	}

}
