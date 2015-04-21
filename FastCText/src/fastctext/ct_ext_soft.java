
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public class ct_ext_soft {

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

        int rows, atts;

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

        BigInteger[] acceptanceMasks = new BigInteger[atts];

        long startTime = System.currentTimeMillis();

        // Sort Basic Matrix		
        BasicMatrix BM = sortBM(BMstrRows, BMstrCols, rows, atts, acceptanceMasks);
        int veces;
        for (veces = 0; veces < 10000; veces++) {
            CandidateGenerator cg = new CandidateGenerator(BM.firstRowOnes, atts);

            boolean done = false;
            boolean contributes, testor;

            // Find Testors
            BigInteger AMlx, AMl;
            while (!done) {
                testor = false; // We don't know yet
                if (cg.x_1 == -1) {
                    AMl = BigInteger.ZERO;
                } else {
                    AMl = acceptanceMasks[cg.x_1];
                }
                AMlx = BM.getAMlx(AMl, cg.x);
                acceptanceMasks[cg.x] = AMlx;
                if (AMlx.equals(AMl)) {
                    contributes = false;
                } else {
                    contributes = true;
                    if (AMlx.and(AMlx.add(BigInteger.ONE)).equals(BigInteger.ZERO)) {
                        testor = true;
                        Testors++;
                        // Check for typical testor
                        if (BM.typical(cg.Current)) {
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
        }
        // BaseCandidate bc = new BaseCandidate();
        // cg.removeMSB(new BigInteger("101001",2), bc);
        // System.out.println(bc.Candidate);
        // System.out.println(bc.Jel);
        // System.out.println(bc.Jprev);

        long endTime = System.currentTimeMillis();
        System.out.println("time: " + Long.toString(endTime - startTime) + "ms");
        System.out.println("Typical testors: " + Integer.toString(typicalTestors));
        System.out.println("Testors: " + Integer.toString(Testors));
    }

    private static BasicMatrix sortBM(char[][] BMstrRows, char[][] BMstrCols,
            int rows, int atts, BigInteger[] acceptanceMasks) {
        int maxOnes = atts;
        int j, nOnes = 0;
        int indx, minRow = 0;
        BigInteger[] BM = new BigInteger[atts];

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
        // Save in a BigInteger array
        char Temp;
        for (int i = 0; i < atts; i++) {
            Temp = BMsort[i][minRow];
            BMsort[i][minRow] = BMsort[i][0];
            BMsort[i][0] = Temp;
            BM[i] = new BigInteger(new String(BMsort[i]), 2);
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
