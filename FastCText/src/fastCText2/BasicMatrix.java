package fastCText2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import tools.TPila;
import tools.TuplaBinaria;

public class BasicMatrix {
	TuplaBinaria[] BM;
	int firstRowOnes, rows, atts;

	public BasicMatrix(String ifilename) throws IOException {// TuplaBinaria[]
																// BM, int
																// firstRowOnes,
																// int rows) {
		// this.BM = BM;
		// this.firstRowOnes = firstRowOnes;
		// this.rows = rows;

		char[][] BMstrRows, BMstrCols;
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
		// Sort BM
		BM = new TuplaBinaria[atts];
		sortBM(BMstrRows, BMstrCols);
	}

	// public int[] getAMlx(int[] AMl, int x) {
	// return this.BM[x].or(AMl);
	// }
	//
	// public int[] getCMlx(int[] AMl, int[] CMl, int x) {
	// return this.BM[x].getCMlx(CMl, AMl);
	// }

	public boolean typical(LinkedList<Integer> testor,
			TuplaBinaria[] acceptanceMasks) {
		// Get testor's compatibility mask
		ListIterator<Integer> iterator = testor.listIterator();
		TuplaBinaria AMl = new TuplaBinaria(rows, -1);
		TuplaBinaria CMl = new TuplaBinaria(rows, -1);
		int x;
		while (iterator.hasNext()) {
			x = iterator.next();
			CMl.mascComp(CMl, BM[x], AMl);
			AMl = acceptanceMasks[x];
		}
		// Check that every attribute in testor has a typical row
		iterator = testor.listIterator();
		while (iterator.hasNext()) {
			if (BM[iterator.next()].andNEqZ(CMl)) {
				return false;
			}
		}
		return true;
	}

	public boolean typical(TPila testor, TuplaBinaria[] acceptanceMasks) {
		// Get testor's compatibility mask
		TuplaBinaria AMl = new TuplaBinaria(rows, -1);
		TuplaBinaria CMl = new TuplaBinaria(rows, -1);
		int x, i;
		for (i = 0; i <= testor.tope; i++) {
			x = testor.pila[i];
			CMl.mascComp(CMl, BM[x], AMl);
			AMl = acceptanceMasks[x];
		}
		// Check that every attribute in testor has a typical row
		for (i = 0; i <= testor.tope; i++) {
			if (BM[testor.pila[i]].andNEqZ(CMl)) {
				return false;
			}
		}
		return true;
	}

	private void sortBM(char[][] BMstrRows, char[][] BMstrCols) {
		int maxOnes = atts;
		int j, nOnes = 0;
		int indx, minRow = 0;
		// TuplaBinaria[] BM = new TuplaBinaria[atts];

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
		firstRowOnes = maxOnes;
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
			BM[i] = new TuplaBinaria(new String(BMsort[i]));
		}
		// // Print sorted BM for testing
		// for (int r = 0; r < rows; r++) {
		// for (int i = 0; i < atts; i++) {
		// System.out.print(BMsort[i][r]);
		// }
		// System.out.print('\n');
		// }
	}

	// public boolean typical(TPila testor) {
	// // ListIterator<Integer> iterator = base.listIterator();
	// TuplaBinaria AMl = new TuplaBinaria(rows, -1);
	// TuplaBinaria CMl = new TuplaBinaria(rows, -1);
	// int i, x;
	// for (i = 0; i <= testor.tope; i++) {
	// x = testor.pila[i];
	// CMl.mascComp(CMl, BM[x], AMl);
	// AMl.mascAcep(AMl, BM[x]);
	// }
	// // Check that every attribute in testor has a typical row
	// for (i = 0; i <= testor.tope; i++) {
	// if (BM[testor.pila[i]].andNEqZ(CMl)) {

	public boolean typical(LinkedList<Integer> testor, Integer curr_att) {
		Iterator<Integer> iter = testor.iterator();
		TuplaBinaria AMl = new TuplaBinaria(rows, -1);
		TuplaBinaria CMl = new TuplaBinaria(rows, -1);
		int x;

		while (iter.hasNext()) {
			x = iter.next();
			TuplaBinaria.masks(AMl, CMl, BM[x]);
		}// Check that every attribute in testor has a typical row
		TuplaBinaria.masks(AMl, CMl, BM[curr_att]);
		iter = testor.listIterator();
		while (iter.hasNext()) {
			if (BM[iter.next()].andNEqZ(CMl)) {
				return false;
			}
		}
		if (BM[curr_att].andNEqZ(CMl)) {
			return false;
		}
		return true;
	}

	public boolean typical(TPila testor, Integer curr_att) {
		TuplaBinaria AMl = new TuplaBinaria(rows, -1);
		TuplaBinaria CMl = new TuplaBinaria(rows, -1);

		for (int i = 0; i <= testor.tope; i++) {
			TuplaBinaria.masks(AMl, CMl, BM[testor.pila[i]]);
		}
		// Check that every attribute in testor has a typical row
		TuplaBinaria.masks(AMl, CMl, BM[curr_att]);
		for (int i = 0; i <= testor.tope; i++) {
			if (BM[testor.pila[i]].andNEqZ(CMl)) {
				return false;
			}
		}
		if (BM[curr_att].andNEqZ(CMl)) {
			return false;
		}
		return true;
	}
}