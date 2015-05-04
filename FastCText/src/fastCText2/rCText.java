package fastCText2;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Iterator;

import tools.TPila;
import tools.TuplaBinaria;

public class rCText {
	static int typicalTestors = 0, testors = 0, candidates = 0, cm_last = -1;
	static BasicMatrix BM;
	static TPila cm_arr_q = new TPila();
	static TuplaBinaria[] cm_arr_tup;

	public static void main(String[] args) throws IOException {
		LinkedList<Integer> B, C;
		B = new LinkedList<Integer>();
		C = new LinkedList<Integer>();

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
		// cumulative masks queue
		int max_cm_depth = BM.atts * (BM.atts - 1) / 2;
		cm_arr_q.setRange(max_cm_depth);
		int[][] cm_array = new int[max_cm_depth][TuplaBinaria.numUnidades];
		cm_arr_tup = new TuplaBinaria[max_cm_depth];
		for (int i = 0; i < max_cm_depth; i++) {
			cm_arr_tup[i] = new TuplaBinaria(cm_array[i]);
		}
		// ///////////////////////////////////////////////////////////////////
		int j, i = 0;
		while (i < BM.firstRowOnes) {
			candidates++;
			if (BM.BM[i].esUnitario()) {
				testors++;
				typicalTestors++;
			} else {
				// init B
				B.clear();
				B.add(i);

				// init C
				C.clear();
				for (j = i + 1; j < BM.atts; j++) {
					C.add(j);
				}
				// call recursive function
				eval(B, BM.BM[i], C);
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
	private static void eval(LinkedList<Integer> B, TuplaBinaria cmB,
			LinkedList<Integer> C) {
		TuplaBinaria cmBx;
		LinkedList<Integer> contrib = new LinkedList<Integer>();
		LinkedList<TuplaBinaria> cms = new LinkedList<TuplaBinaria>();
		Iterator<Integer> iter;
		int curren_att;

		iter = C.iterator();
		while (iter.hasNext()) {
			// cmBx = new TuplaBinaria(BM.rows, -1); // TODO Inefficient
			cmBx = cm_arr_tup[cm_last+1];
			curren_att = (int) iter.next();
			candidates++;
			System.out.println(curren_att);
			if (!cmBx.mascAcep(cmB, BM.BM[curren_att])) {
				if (cmBx.esUnitario()) {
					testors++;
					// check if typical
					B.add(curren_att);
					if (BM.typical(B)) {
						typicalTestors++;
					}
					B.removeLast();
				} else {
					contrib.addFirst(curren_att);
					// cms.add(cmBx);
					cm_last++;// push cm to the queue
				}
			}
		}
		iter = contrib.descendingIterator();
		while (iter.hasNext()) {
			curren_att = (int) iter.next();
			iter.remove();
			if (iter.hasNext()) { // There are remaining elements in the tail
				B.add(curren_att);
				// eval(B, cms.removeFirst(), contrib);
				eval(B, cm_arr_tup[cm_last], contrib); 
				cm_last--;// pop cm
				B.removeLast();
			}
		}
	}
}
