package fastCText2;

import java.io.IOException;

import tools.TuplaBinaria;

public class gapCText {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		int typicalTestors = 0, testors = 0, candidates = 0;

		// check to see if the String array is empty
		if (args.length != 1) {
			System.out.println("The only one argument is the input file name!");
		}

		String ifilename = args[0];

		System.out.println(ifilename);

		BasicMatrix BM = new BasicMatrix(ifilename,true);

		// Sort Basic Matrix
		// BasicMatrix BM = sortBM(BMstrRows, BMstrCols, rows, atts);
		//CandidateGenerator cg = new CandidateGenerator(BM.firstRowOnes, BM.atts);
		GapCandGen cg = new GapCandGen(BM.firstRowOnes, BM.atts);
		TuplaBinaria[] acceptanceMasks = new TuplaBinaria[BM.atts];
		// Creating an empty acceptanceMasks
		for (int i = 0; i < BM.atts; i++) {
			acceptanceMasks[i] = new TuplaBinaria(BM.rows, -1);
		}

		boolean done = false;
		boolean contributes, testor, typical;

		long startTime = System.currentTimeMillis();
		// Find Testors
		TuplaBinaria AMl;
		// BMcolumn AMlx;
		while (!done) {
			testor = false; typical = false; // We don't know yet
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
			//System.out.print(cg);
			candidates++;

			if (contributes) {
				//System.out.print(" C");
				if (acceptanceMasks[cg.x].esUnitario()) {
					//System.out.print(" SR");
					testor = true;
					testors++;
					// Check for typical testor
					if (BM.typical(cg.Current, acceptanceMasks)) {
						// TODO decompress reducts
						//System.out.print(" R");
						typicalTestors++;
						typical = true;
					}
				}
			}
			done = cg.getCurrentCandidate(testor, contributes, typical);
			//System.out.print("\n");
		}

		long endTime = System.currentTimeMillis();
		System.out
				.println("time: " + Long.toString(endTime - startTime) + "ms");
		System.out.println("Typical testors: "
				+ Integer.toString(typicalTestors));
		System.out.println("Testors: " + Integer.toString(testors));
		System.out.println("Candidates: " + Integer.toString(candidates));
	}
	
}
