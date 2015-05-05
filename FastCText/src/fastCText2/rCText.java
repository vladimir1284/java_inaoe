package fastCText2;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Iterator;

import tools.RotPtr;
import tools.SmartPtr;
import tools.TPila;
import tools.TuplaBinaria;

public class rCText {
	static int typicalTestors = 0, testors = 0, candidates = 0, cm_last = -1,
			curren_att, curr_tpl = 0, dummy = 0;
	static BasicMatrix BM;
	static TPila B = new TPila();
	static TuplaBinaria[] cm_arr_tup;
	static SmartPtr cm_indx;
	static TuplaBinaria cmBx;

	// static Iterator<Integer> iter;

	public static void main(String[] args) throws IOException {
		LinkedList<Integer> C;		
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
		cm_indx = new SmartPtr(BM.atts);
		// cm_arr_q.setRange(cm_indx.current+1);
		int[][] cm_array = new int[cm_indx.size][TuplaBinaria.numUnidades];
		cm_arr_tup = new TuplaBinaria[cm_indx.size];
		for (int i = 0; i < cm_indx.size; i++) {
			cm_arr_tup[i] = new TuplaBinaria(cm_array[i]);
		}
		B.setRange(BM.atts);
		// ////////////////////////////////////////////////////////////////////
		int j, i = 0;
		while (i < BM.firstRowOnes) {
			candidates++;
			if (BM.BM[i].esUnitario()) {
				testors++;
				typicalTestors++;
			} else {
				// init B
				B.clean();
				B.push(i);

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
	private static void eval(TPila B, TuplaBinaria cmB,
			LinkedList<Integer> C) {
		LinkedList<Integer> contrib = new LinkedList<Integer>();
		LinkedList<Integer> cms = new LinkedList<Integer>();
		Iterator<Integer> iter;

		iter = C.iterator();
		while (iter.hasNext()) {
			cmBx = cm_arr_tup[cm_indx.get()];
			curren_att = (int) iter.next();
			candidates++;
			if (!cmBx.mascAcep(cmB, BM.BM[curren_att])) {
				if (cmBx.esUnitario()) {
					testors++;
					if (BM.typical(B,curren_att)) {
						typicalTestors++;
					}
				} else {
					contrib.add(curren_att);
					cms.add(cm_indx.get());
					cm_indx.next();
				}
			}
		}
		iter = contrib.iterator();
		while (iter.hasNext()) {
			curren_att = (int) iter.next();
			iter.remove();
			if (iter.hasNext()) { // There are remaining elements in the tail
				B.push(curren_att);
				eval(B, cm_arr_tup[cms.getFirst()], contrib);
				B.pop();
			}
			cm_indx.release(cms.removeFirst());
		}
	}
}
