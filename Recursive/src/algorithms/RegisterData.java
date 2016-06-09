package algorithms;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RegisterData {
	private String TTs = "";
	private String Infos = "";
	String eol = System.getProperty("line.separator");  
	public void RegisterData() {
	}

	public void save2File(String filename, int ntt, long time) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			// Write data to file
			out.write(String.format("BR -- No. de Soluciones: %d%n", ntt));
			String[] temp;
			temp = Infos.split(eol);
			out.write(temp[temp.length-1]+eol);
			out.write(String.format("Tiempo de ejecucion del algoritmo en segundos: %d%n",time)); 
			out.write(TTs);
			out.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void getInfo(String info) {
		Infos += info;
	}

	public void percent(int percent) {
		System.out.println(percent + "%");
	}

	public void resgistTT(int[] solucion) {
		// Save Obtained Typical Testor
		int nTT = solucion[0];
		int[] myIntArray = new int[nTT];

		for (int i = 1; i < nTT + 1; i++) {
			myIntArray[i - 1] = solucion[i];
		}

		// SORTS ARRAY FROM SMALLEST TO LARGEST INT
		Arrays.sort(myIntArray);

		TTs += "{ ";
		for (int i = 0; i < nTT; i++) {
			TTs += String.format("X%d ", myIntArray[i]);
		}
		TTs += "}" + eol;
	}

}
