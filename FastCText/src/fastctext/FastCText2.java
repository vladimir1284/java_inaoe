package fastCText2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import fastCText2.BMcolumn;

public class FastCText2 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		int typicalTestors = 0;
		int Testors = 0;
		char[][] BMstrRows, BMstrCols;
		
		// check to see if the String array is empty
		if (args.length != 1) {
			System.out.println("The only one argument is the input file name!");
		}

		String ifilename = args[0];

		System.out.println(ifilename);

		int rows, atts, nwords;

		// Read Input file
		BufferedReader br = new BufferedReader(new FileReader(ifilename));
		try {
			// Size of BM
			String line = br.readLine();
			rows = Integer.parseInt(line);
			line = br.readLine();
			atts = Integer.parseInt(line);

			// Dual representation for faster processing
			BMstrRows = new char[rows][atts]; // Representation by rows
			BMstrCols = new char[atts][rows]; // Representation by columns

			// The rest of the matrix
			line = br.readLine();
			int i = 0;
			String[] temp;
			while (line != null) {
				// Parse Row
				temp = line.trim().replace("\t", " ").split(" ");
				for (int j = 0; j < atts; j++) {
					BMstrRows[i][j] = temp[j].charAt(0);
					BMstrCols[j][i] = temp[j].charAt(0);
				}
				i++;
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		
		nwords = (rows + 31) / 32;
		int [][] acceptanceMasks = new int [atts][nwords];		
		
		long startTime = System.currentTimeMillis();
		
		// Sort Basic Matrix		
		BasicMatrix BM = sortBM(BMstrRows, BMstrCols, rows, atts, acceptanceMasks);
		CandidateGenerator cg = new CandidateGenerator(BM.firstRowOnes, atts);

		boolean done = false;
		boolean contributes, testor;

		// Find Testors
		int[] AMl;
		BMcolumn AMlx;
		while (!done) {
			testor = false; // We don't know yet
			if (cg.x_1 == -1) {
				AMl = new int [nwords];
			} else {
				AMl = acceptanceMasks[cg.x_1];
			}
			AMlx = new BMcolumn(BM.getAMlx(AMl, cg.x),rows);
			acceptanceMasks[cg.x] = AMlx.data;
			if (AMlx.equals(AMl)) {
				contributes = false;
			} else {
				contributes = true;
				if (AMlx.eqOnes()) {
					testor = true;
					Testors++;
					// Check for typical testor
					if (BM.typical(cg.Current)){
						typicalTestors++;
					}
				}
			}

			// if (ce.Testor){
//			System.out.println(cg.Current);
//			System.out.println(testor);
//			System.out.println(contributes);
			// }
			done = cg.getCurrentCandidate(testor, contributes);
		}

		// BaseCandidate bc = new BaseCandidate();
		// cg.removeMSB(new BMcolumn("101001",2), bc);
		// System.out.println(bc.Candidate);
		// System.out.println(bc.Jel);
		// System.out.println(bc.Jprev);

		long endTime = System.currentTimeMillis();
		System.out.println("time: "+Long.toString(endTime-startTime)+"ms");
		System.out.println("Typical testors: "+ Integer.toString(typicalTestors));
		System.out.println("Testors: " + Integer.toString(Testors));
	}

	private static BasicMatrix sortBM(char[][] BMstrRows, char[][] BMstrCols,
			int rows, int atts, int[][] acceptanceMasks) {
		int maxOnes = atts;
		int j, nOnes = 0;
		int indx, minRow = 0;
		BMcolumn[] BM = new BMcolumn[atts];

		// Find row with minimum ones
		for (int i = 0; i < rows; i++) {
			j = 0;
			nOnes = 0;
			while ((nOnes < maxOnes) && (j < atts)) {
				if (BMstrRows[i][j] == '1') {
					nOnes++;
				}
				j++;
			}
			if (nOnes < maxOnes) {
				minRow = i;
				maxOnes = nOnes;
			}
		}

		// Taking index of ones in first row
		int[] indOnes = new int[nOnes];
		int[] indZeros = new int[atts - nOnes];

		int o = 0, k = 0;

		for (int i = 0; i < atts; i++) {
			if (BMstrRows[minRow][i] == '1') {
				indOnes[o] = i;
				o++;
			} else {
				indZeros[k] = i;
				k++;
			}
		}

		char[][] BMsort = new char[atts][rows]; // By columns

		// Sort Columns
		for (int i = 0; i < atts; i++) {
			if (i < nOnes) {
				indx = indOnes[i];
			} else {
				indx = indZeros[i - nOnes];
			}
			BMsort[i] = BMstrCols[indx].clone();
		}
		// Swap the first rows and the row with minimum number of ones
		// Save in a BMcolumn array
		char Temp;
		for (int i = 0; i < atts; i++) {
			Temp = BMsort[i][minRow];
			BMsort[i][minRow] = BMsort[i][0];
			BMsort[i][0] = Temp;
			BM[i] = new BMcolumn(new String(BMsort[i]));
		}
//		// Print sorted BM for testing
//		for (int r = 0; r < rows; r++) {
//			for (int i = 0; i < atts; i++) {
//				System.out.print(BMsort[i][r]);
//			}
//			System.out.print('\n');
//		}
		
		return new BasicMatrix(BM, nOnes, rows, acceptanceMasks);
	}
}
