package fastCText2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import tools.TuplaBinaria;

public class FastCText3 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		long typicalTestors = 0, testors = 0, candidates = 0;

		// check to see if the String array is empty
		if (args.length != 1) {
			System.out.println("The only one argument is the input file name!");
		}

		String ifilename = args[0];

		System.out.println(ifilename);

		BasicMatrix BM = new BasicMatrix(ifilename,false);

		// Sort Basic Matrix
		// BasicMatrix BM = sortBM(BMstrRows, BMstrCols, rows, atts);
		//CandidateGenerator cg = new CandidateGenerator(BM.firstRowOnes, BM.atts);
		FastCandGen cg = new FastCandGen(BM.firstRowOnes, BM.atts);
		TuplaBinaria[] acceptanceMasks = new TuplaBinaria[BM.atts];
		// Creating an empty acceptanceMasks
		for (int i = 0; i < BM.atts; i++) {
			acceptanceMasks[i] = new TuplaBinaria(BM.rows, -1);
		}

		boolean done = false;
		boolean contributes, testor;

		long startTime = System.currentTimeMillis();
		// Find Testors
		TuplaBinaria AMl;
		// BMcolumn AMlx;
		while (!done) {
			testor = false; // We don't know yet
			if (cg.x_1 == -1) {
				// System.out.println(cg.Current);
				AMl = new TuplaBinaria(BM.rows, -1); // Current candidate has
														// only
														// 1 attribute
			} else {
				AMl = acceptanceMasks[cg.x_1];
			}
			// AMlx = new BMcolumn(BM.getAMlx(AMl, cg.x), rows);
			// acceptanceMasks[cg.x] = AMlx.data;
			contributes = !acceptanceMasks[cg.x].mascAcep(AMl, BM.BM[cg.x]);
			//System.out.println(cg);
			candidates++;

			if (contributes) {
				if (acceptanceMasks[cg.x].esUnitario()) {
					// System.out.println("testor");
					testor = true;
					testors++;
					// Check for typical testor
					if (BM.typical(cg.Current, acceptanceMasks)) {
						typicalTestors++;
					}
				}
			}
			done = cg.getCurrentCandidate(testor, contributes);
		}

		long endTime = System.currentTimeMillis();
		System.out
				.println("time: " + Long.toString(endTime - startTime) + "ms");
		System.out.println("Typical testors: "
				+ Long.toString(typicalTestors));
		System.out.println("Testors: " + Long.toString(testors));
		System.out.println("Candidates: " + Long.toString(candidates));
	}

}
