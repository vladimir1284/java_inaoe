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
	private static final int DERECHA = 2;
	private static final int IZQUIERDA = 1;
	TuplaBinaria[] BM;
	int firstRowOnes, rows, atts;
	int[] repeatedCount;
	int[] repeatedAtts;
	int[][] contraidos; // - Lista de rasgos agrupados.
	private int porcReduccion;

	public class RepeatedRow {
		public int index;
		public int count;

		public RepeatedRow(int ind, int cnt) {
			index = ind;
			count = cnt;
		}
	}

	public BasicMatrix(String ifilename, boolean reduce) throws IOException {// TuplaBinaria[]
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
			// Size of BM (rows taken as columns!!!!!)
			String line = br.readLine();
			atts = Integer.parseInt(line);
			line = br.readLine();
			rows = Integer.parseInt(line);

			BM = new TuplaBinaria[atts];
			for (int i = 0; i < atts; i++)
				BM[i] = new TuplaBinaria(rows, i);

			repeatedCount = new int[rows];
			repeatedAtts = new int[rows];

			// Dual representation for faster processing
			// BMstrRows = new char[rows][atts]; // Representation by rows
			// BMstrCols = new char[atts][rows]; // Representation by columns

			// The rest of the matrix
			line = br.readLine();
			int i = 0;
			String[] temp;
			while (line != null) {
				// Parse Row
				temp = line.trim().replace("\t", " ").split(" ");
				for (int j = 0; j < rows; j++) {
					// BMstrRows[i][j] = temp[j].charAt(0);
					// BMstrCols[j][i] = temp[j].charAt(0);
					if (temp[j].charAt(0) == '1') {
						BM[i].setValorEn(j, 1);
					}
				}
				i++;
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		// Rotate to make columns and rows correct
		rotar(DERECHA);

		if (reduce)
			// Reduce columns
			reduceColumns();
		// reducirColumnas();

		// Sort BM
		ordenInicial();

		// Debug printing the BM
		// for (int i = 0; i < rows; i++) {
		// for (int j = 0; j < atts; j++) {
		// System.out.print(BM[j].getValorEn(i) + " ");
		// }
		// System.out.print("\n");
		// }
	}

	// ---------------------------------------------------------------------------
	// - Metodo para reducir el numero de columnas.
	// ---------------------------------------------------------------------------
	public void reducirColumnas() {
		int i, j;
		boolean[] band = new boolean[atts];
		int[][] tempMat = new int[atts][atts];
		int contColum = 0, maxFila = 0;
		TuplaBinaria[] BMTemp;
		// ---------------
		for (i = 0; i < atts; i++) {
			band[i] = true;
		}
		for (i = 0; i < atts; i++) {
			tempMat[0][i] = 0;
			if (band[i] == true) {
				contColum++;
				tempMat[0][i] = 1;
				tempMat[1][i] = BM[i].getId();
				for (j = atts - 1; j >= i + 1; j--) {
					if (BM[i].igualA(BM[j]) == true) {
						tempMat[++tempMat[0][i]][i] = BM[j].getId();
						band[j] = false; // - marcado como BM usado
					}
				}
				if (maxFila < tempMat[0][i])
					maxFila = tempMat[0][i];
			}
		}
		BMTemp = new TuplaBinaria[contColum];
		contraidos = new int[maxFila + 1][contColum];
		contColum = 0;
		for (i = 0; i < atts; i++) {
			if (tempMat[0][i] != 0) {
				BMTemp[contColum] = BM[i];
				BMTemp[contColum].idTupla = contColum;
				for (j = 0; j <= tempMat[0][i]; j++) {
					contraidos[j][contColum] = tempMat[j][i];
				}
				contColum++;
			}
		}
		BM = BMTemp;
		porcReduccion = (contColum * 100) / atts;
		atts = contColum; // - Nuevo # de columnas.
	}

	private void reduceColumns() {
		// Eliminate zero row and compress repeated
		int remainig = atts;
		int emptyCols = 0, repeatedCols = 0;
		boolean[] toEliminate = new boolean[atts];

		for (int i = 0; i < atts - 1; i++) {
			if (!toEliminate[i]) {
				// Check for zero column
				if (BM[i].isEmpty()) {
					emptyCols++;
					toEliminate[i] = true;
					remainig--;
				} else {
					int locRepeated = 1;
					for (int j = i + 1; j < atts; j++) {
						// Check for repetitions
						if (BM[i].igualA(BM[j])) {
							repeatedCols++;
							locRepeated++;
							toEliminate[j] = true;
							remainig--;
						}
					}
					if (locRepeated > 1) {
						repeatedCount[BM[i].getId()] = locRepeated;
					}
				}
			}
		}
//		System.out.println("Zero columns: " + Integer.toString(emptyCols));
//		System.out.println("Repeated columns: "
//				+ Integer.toString(repeatedCols));

		// New basic matrix
		TuplaBinaria[] temBM = new TuplaBinaria[remainig];
		int j = 0, k = 0;
		for (int i = 0; i < atts; i++) {
			if (!toEliminate[i]) {
				temBM[j++] = BM[i];
			}
			if (repeatedCount[i] != 0) {
				repeatedAtts[k++] = i;
			}
		}
		BM = temBM;
		atts = remainig;
		// for (int i = 0; i < atts; i++) {
		// System.out.println(BM[i].getId());
		// }
	}

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
//		// Print sorted BM for testing
//		for (int r = 0; r < rows; r++) {
//			for (int i = 0; i < atts; i++) {
//				System.out.print(BMsort[i][r]);
//			}
//			System.out.print('\n');
//		}
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

	// ---------------------------------------------------------------------------
	public void ordenInicial() {
		TuplaBinaria temprow;

		int[] ids = new int[atts];

		for (int i = 0; i < atts; i++) {
			ids[i] = BM[i].getId();
		}

		rotar(DERECHA);

		// Poner la fila con menor cantidad de unos al inicio
		int nOnes = rows;
		int id = 0, count;
		for (int i = 0; i < atts; i++) {
			count = BM[i].cantValoresUnitarios();
			if (count < nOnes) {
				nOnes = count;
				id = i;
			}
		}
		temprow = BM[atts - 1];
		BM[atts - 1] = BM[id];
		BM[id] = temprow;

		rotar(IZQUIERDA);
		
		for (int i = 0; i < atts; i++) {
			BM[i].idTupla = ids[i];
		}
		
		firstRowOnes = nOnes;

		// Poner columnas con unos en 1ra fila a la izquierda
		TuplaBinaria[] temBM = new TuplaBinaria[atts];
		int init = 0, end = atts - 1;
		for (int i = 0; i < atts; i++) {
			if (BM[i].getValorEn(0) == 1) {
				temBM[init++] = BM[i];
			} else {
				temBM[end--] = BM[i];
			}
		}
		BM = temBM;

	}

	// ---------------------------------------------------------------------------
	private void rotar(int sentido) {
		int i, j, antNumFilas, antatts;
		TuplaBinaria[] tuplaTemp; // / - Se esta trabajando con get valor
									// - Hay que cambiar por setValorEnPos()
		// ----------------

		antNumFilas = rows;
		antatts = atts;
		tuplaTemp = new TuplaBinaria[rows];
		for (i = 0; i < rows; i++) {
			tuplaTemp[i] = new TuplaBinaria(atts, i);
			tuplaTemp[i].set(atts, i);
			for (j = 0; j < atts; j++) {
				if (sentido == DERECHA)
					tuplaTemp[i].setValorEn(j, BM[j].getValorEn(rows - 1 - i));
				else if (sentido == IZQUIERDA)
					tuplaTemp[i].setValorEn(j, BM[atts - 1 - j].getValorEn(i));
			}
		}
		BM = tuplaTemp;
		rows = antatts; // - Inversion del rango. Cambio.
		atts = antNumFilas;
	}
}