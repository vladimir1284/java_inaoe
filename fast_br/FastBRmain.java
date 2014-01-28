package alghoritms.fast_br;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.print.attribute.IntegerSyntax;

public class FastBRmain {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		
		//check to see if the String array is empty
        if (args.length != 1) {
            System.out.println("The only one argument is the input file name!");
        }
        
        String ifilename = args[0];
        
		Integer rows, atts;
		FastBR brAlgorithm = new FastBR();
		
		BufferedReader br = new BufferedReader(new FileReader(ifilename));
		try {
			// Size of BM
			String line = br.readLine();
			rows = Integer.parseInt(line);
			line = br.readLine();
			atts = Integer.parseInt(line);
			
			brAlgorithm.set(rows, atts);
			
			// The rest of the matrix
			line = br.readLine();
			int i = 0;
			String[] temp;
			while (line != null) {
				// Parse Row
				temp = line.trim().replace("\t"," ").split(" ");
				for(int j = 0; j < atts; j++){
					//System.out.print(temp[j]);
					brAlgorithm.pushValor(Integer.parseInt(temp[j]), i, j);
				}
				
				i++;
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		// Where result are going to
		RegisterData dataOut = new RegisterData();
		
		// Do the job
		brAlgorithm.extraerTT(dataOut);
		
		long endTime = System.currentTimeMillis();
		
		// Save data
		String[] temp;
		temp = ifilename.split("MB");
		String ofilename = "BR" + temp[temp.length-1];
		dataOut.save2File(ofilename, brAlgorithm.getNumSoluciones(), 
									(endTime-startTime)/1000);		
	}

}
