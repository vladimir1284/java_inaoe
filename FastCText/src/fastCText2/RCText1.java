package fastCText2;

import java.io.IOException;

import tools.RPila;
import tools.TPila;
import tools.TuplaBinaria;

public class RCText1 {
	static int typicalTestors = 0, testors = 0, candidates = 0, cm_last = -1,
			curren_att, curr_tpl = 0, dummy = 0, level, indx;
	static BasicMatrix BM;
	static TPila B = new TPila();
	static TuplaBinaria[] cm_arr_tup;
	// static SmartPtr cm_indx;
	static TuplaBinaria cmBx, curren_CM;
	static RPila[] MemArr;

	// static Iterator<Integer> iter;

	public static void main(String[] args) throws IOException {

		// check to see if the String array is empty
		if (args.length != 1) {
			System.out.println("The only one argument is the input file name!");
		}

		String ifilename = args[0];

		System.out.println(ifilename);

		BM = new BasicMatrix(ifilename);
		long startTime = System.currentTimeMillis();
		// ---------------------------------------------------------------------------
		// ***********************_Main_CT-EXT_execution_*****************************
		MemArr = new RPila[BM.atts];
		for (int i = BM.atts; i > 0; i--) {
			MemArr[BM.atts-i] = new RPila(i, BM.rows);
		}
		B.setRange(BM.atts);
		// ////////////////////////////////////////////////////////////////////
		int j, i = 0;
		while (i < BM.firstRowOnes) {
			candidates++;
			//System.out.println("x_"+i);
			
			if (BM.BM[i].esUnitario()) {
				testors++;
				typicalTestors++;
			} else {
				// init B
				B.clean();
				B.push(i);

				// init C
				level = 0;
				MemArr[level].clear();
				for (j = i + 1; j < BM.atts; j++) {
					MemArr[level].atts[MemArr[level].current] = j;
					MemArr[level].push();
				}
				// call recursive function
				eval(B, BM.BM[i]);
			}
			i++;
		}

		// ***************************************************************************
		// ---------------------------------------------------------------------------
		long endTime = System.currentTimeMillis();
		System.out
				.println("time: " + Long.toString(endTime - startTime) + "ms");
		System.out.println("Typical testors: "
				+ Integer.toString(typicalTestors));
		System.out.println("Testors: " + Integer.toString(testors));
		System.out.println("Candidates: " + Integer.toString(candidates));
	}

	// ----------------------------------------------------------------------------
	// ***************************_Recursive_CT-EXT_******************************
	// ---------------------------------------------------------------------------
	private static void eval(TPila B, TuplaBinaria cmB) {
		level++; // Next recursion level
		for (indx = MemArr[level - 1].head; indx < MemArr[level - 1].current; indx++) {
			MemArr[level].atts[MemArr[level].current] = MemArr[level - 1].atts[indx]; // Evaluated
																						// att
																						// from
																						// C
			cmBx = MemArr[level].CMs[MemArr[level].current]; // CM(B+x)
			candidates++;
			//System.out.println(B.toString()+"x_"+MemArr[level].atts[MemArr[level].current]);

			if (!cmBx.mascAcep(cmB,
					BM.BM[MemArr[level].atts[MemArr[level].current]])) {
				if (cmBx.esUnitario()) {
					testors++;
					if (BM.typical(B, MemArr[level].atts[MemArr[level].current])) {
						typicalTestors++;
					}
				} else {
					MemArr[level].push();
				}
			}
		}

		while(MemArr[level].head != MemArr[level].current){
			curren_CM = MemArr[level].CMs[MemArr[level].head];
			curren_att = MemArr[level].popFirst();
			if (MemArr[level].head != MemArr[level].current) { // There are
																// remaining
																// elements in
																// the tail
				B.push(curren_att);
				eval(B, curren_CM);
				B.pop();
			}
		}
		MemArr[level].clear();
		level--;// Previous recursion level
	}
}
