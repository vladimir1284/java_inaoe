package fastCText2;

import java.io.IOException;

import java.util.LinkedList;

import java.util.Iterator;

import tools.TuplaBinaria;

public class Recursive {

	static int typicalTestors = 0, testors = 0, candidates = 0;

	static BasicMatrix BM;

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

			cmBx = new TuplaBinaria(BM.rows, -1); // TODO Inefficient

			curren_att = (int) iter.next();

			candidates++;

			if (!cmBx.mascAcep(cmB, BM.BM[curren_att])) {

				if (cmBx.esUnitario()) {

					testors++;

					// check if typical

					if (BM.typical(B,curren_att) ) {

						typicalTestors++;

					}

				} else {

					contrib.add(curren_att);

					cms.add(cmBx);

				}

			}

		}

		iter = contrib.iterator();

		while (iter.hasNext()) {

			curren_att = (int) iter.next();

			iter.remove();

			if (iter.hasNext()) { // There are remaining elements in the tail

				B.add(curren_att);

				eval(B, cms.removeFirst(), contrib);

				B.removeLast();

			}

		}

	}

}
