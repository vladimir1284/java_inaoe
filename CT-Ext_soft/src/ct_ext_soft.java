import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public class ct_ext_soft {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		char [][] BMstr;
		
		//check to see if the String array is empty
        if (args.length != 1) {
            System.out.println("The only one argument is the input file name!");
        }
        
        String ifilename = args[0];
        
        System.out.println(ifilename);
        
		int rows, atts;
		
		BufferedReader br = new BufferedReader(new FileReader(ifilename));
		try {
			// Size of BM
			String line = br.readLine();
			rows = Integer.parseInt(line);
			line = br.readLine();
			atts = Integer.parseInt(line);
			
			BMstr = new char[rows][atts];
			
			// The rest of the matrix
			line = br.readLine();
			int i = 0;
			String[] temp;
			while (line != null) {
				// Parse Row
				temp = line.trim().replace("\t"," ").split(" ");
				for(int j = 0; j < atts; j++){
					BMstr[i][j] = temp[j].charAt(0);
				}
				i++;
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		
		// Sort Basic Matrix
		BigInteger[] BMsorted = sortBM(BMstr, rows, atts);
		BasicMatrix bm = new BasicMatrix(BMsorted, rows);
		CandidateGenerator cg = new CandidateGenerator(BMsorted[0], atts);
		CandidateEval ce = new CandidateEval();
		
		boolean done = false;
		boolean Contributes;
		
		System.out.println("Testors:");
		// Find Testors
		while(!done){
			bm.evaluateCandidate(cg.Current, ce);
			Contributes = (ce.Nsatisfy > cg.Previous[cg.pervIndex]);
			cg.Previous[cg.J] = ce.Nsatisfy;
			//if (ce.Testor){
			System.out.println(cg.Current);
			System.out.println(ce.Testor);
			System.out.println(Contributes);
			//}
			cg.getCurrentCandidate(ce.Testor, Contributes);
		}
		
//		BaseCandidate bc = new BaseCandidate();
//		cg.removeMSB(new BigInteger("101001",2), bc);
//		System.out.println(bc.Candidate);
//		System.out.println(bc.Jel);
//		System.out.println(bc.Jprev);
		
		
		long endTime = System.currentTimeMillis();

	}
	
	private static BigInteger[] sortBM(char [][] BMstr, int rows, int atts){
		int maxOnes = atts;
		int j, nOnes = 0;
		int minRow = 0;
		
		// Find row with minimum ones
		for (int i = 0; i < rows; i++){
			j = 0;
			nOnes = 0;
			while ((nOnes < maxOnes) && (j < atts)){
				if (BMstr[i][j] == '1'){
					nOnes++;
				}
				j++;
			}
			if (nOnes < maxOnes){
				minRow = i;
				maxOnes = nOnes;
			}
		}
		
		// Taking index of ones in first row
		int[] indOnes = new int[nOnes];
		int[] indZeros = new int[atts-nOnes];
		
		int o = 0 ,k = 0;
		
		for (int i = 0; i < atts; i++){
			if (BMstr[minRow][i] == '1'){
				indOnes[o] = i;
				o++;
			}
			else{
				indZeros[k] = i;
				k++;
			}
		}
		
		BigInteger[] BMsort = new BigInteger[rows];
		
		// First row
		char[] Row = new char[atts];
		for (int i = 0; i < atts; i++){
			if (i < nOnes){
				Row[atts-1-i] = BMstr[minRow][indOnes[i]];
			}
			else{
				Row[atts-1-i] = BMstr[minRow][indZeros[i-nOnes]];
			}
		}
		BMsort[0] = new BigInteger(new String(Row),2);
		//System.out.println(Row);
		// Others rows
		int currow = 0, ind = 1;
		while (currow < rows){
			if (currow != minRow){
				for (int i = 0; i < atts; i++){
					if (i < nOnes){
						Row[atts-1-i] = BMstr[currow][indOnes[i]];
					}
					else{
						Row[atts-1-i] = BMstr[currow][indZeros[i-nOnes]];
					}
				}
				BMsort[ind] = new BigInteger(new String(Row),2);
				//System.out.println(Row);
				ind++;
			}
			currow++;
		}
				
		return BMsort;
		
	}
}