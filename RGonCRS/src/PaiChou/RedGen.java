package PaiChou;

import tools.TPila;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class RedGen {
	static int xSize, nRows, currClass, numClasses;
	static TPila currIN = new TPila();// Current number of inconsistencies
	static TPila cList = new TPila();// Current candidate base
	static Instances data;
	static TPila inconstI;
	static TPila attrClass, posCand1, posCand;
	static int typicalTestors = 0, testors = 0, candidates = 0;
	static int[] class_start_index; // Starting index of class
	static ArrayList<TPila> attrIN;

	// // Comparator to sort by CRS
	// class MyComparator implements Comparator<Integer> {
	// @Override
	// public int compare(Integer i1, Integer i2) {
	// return new Integer(attrClass.pila[i1]).compareTo(new Integer(
	// attrClass.pila[i2]));
	// }
	// }

	public static void main(String[] args) throws Exception {
		int i;
		Integer pos;

		// Read dataset
		String filename = args[0];
		// Parsing ARFF file
		DataSource source = new DataSource(filename);
		data = source.getDataSet();
		// setting class attribute if the data format does not provide this
		// information. For example, the XRFF format saves the class attribute
		// information as well
		int class_index = data.numAttributes() - 1;
		if (data.classIndex() == -1) {
			data.setClassIndex(class_index);
		}
		// Filter objects with missing data
		deleteMissingData();
		// Sort by class
		data.sort(class_index);
		AttributeStats stats = data.attributeStats(class_index);
		numClasses = stats.distinctCount; // Number of classes
		class_start_index = new int[numClasses];
		for (i = 0; i < numClasses - 1; i++) {
			class_start_index[i + 1] = stats.nominalCounts[i]
					+ class_start_index[i];
		}
		// Intial setup
		nRows = data.numInstances();
		xSize = data.numAttributes() - 1;

		attrClass = new TPila();
		posCand1 = new TPila();
		posCand = new TPila();
		attrClass.setRange(xSize);
		posCand1.setRange(xSize);
		posCand.setRange(xSize);

		attrIN = new ArrayList<TPila>();
		for (i = 0; i < xSize; i++) {
			TPila inconstI = new TPila();
			inconstI.setRange(nRows);
			attrIN.add(i, inconstI);
		}
		currIN.setRange(nRows);
		for (i = 0; i < nRows; i++)
			currIN.push(i);
		for (i = 0; i < xSize; i++) {
			posCand1.push(i);
			posCand.push(i);
		}

		// Compute indiscrenibles for each attribute
		cList.setRange(xSize);
		for (i = 0; i <= posCand1.tope; i++) {
			pos = posCand1.pila[i];
			cList.push(pos);
			attrClass.push(genInconst(cList, pos));
			cList.pop();
		}
		System.out.println(posCand);
		System.out.println(attrClass);
		// Sort attributes by their CRS (descending)
		quickSort(posCand, attrClass, 0, posCand.tope);
		System.out.println(posCand);
	}

	// Delete objects with missing data
	private static void deleteMissingData() {
		int nInstances = data.numInstances();
		for (int i = 0; i < nInstances - 1; i++) {
			if (data.instance(i).hasMissingValue()) {
				data.delete(i--);
				nInstances--;
			}
		}
	}

	// return the inconst region 1 x k row vector
	private static int genInconst(TPila checkList, int posCand) {
		int inClass = 0, current_class = 0;
		int i = 0, j = 0, k;
		boolean equal;
		boolean[] added = new boolean[currIN.tope + 1];

		inconstI = attrIN.get(posCand);
		inconstI.clean();

		// Detect class in the first inconsistent object
		while (currIN.pila[0] >= i) {
			current_class = i++;
		}
		i = 0;
		// Comparing instances in different classes
		// Stopping before the last class elements
		while (currIN.pila[i] < class_start_index[numClasses - 1]) {
			j = i + 1;
			while (currIN.pila[j] < data.numInstances()) {
				//OJO hay q meterlo al saco sea o no de la misma clase
				if ((j >= class_start_index[current_class + 1]) & (!added[j])) {
					// Compare
					equal = true;
					// Use every attribute in checkList
					for (k = 0; k <= checkList.tope; k++) {
						if (data.instance(currIN.pila[i]).value(
								checkList.pila[k]) != data.instance(
								currIN.pila[j]).value(checkList.pila[k])) {
							equal = false;
							break;
						}
					}
					if (equal) {
						// Add to inconsistent list if not yet
						if (!added[i]) {
							inconstI.push(currIN.pila[i]);
							added[i] = true;
							inClass++;
						}
						if (!added[j]) {
							inconstI.push(currIN.pila[j]);
							added[j] = true;
						}
					}
				}
				j++;
				if (j > currIN.tope)
					break;
			}
			i++;
			// Last element in the class reached (passed)
			if (currIN.pila[i] >= (class_start_index[current_class + 1]))
				current_class++;
		}
		Arrays.sort(inconstI.pila);// Sort inconsistent indices
		return nRows - inconstI.tope + 1 + inClass;
	}

	// Reverse QuickSort by CRS
	public static void quickSort(TPila atts, TPila crs, int low, int high) {
		// pick the pivot
		int middle = low + (high - low) / 2;
		int pivot = crs.pila[middle];

		// make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (crs.pila[i] > pivot) {
				i++;
			}

			while (crs.pila[j] < pivot) {
				j--;
			}

			if (i <= j) {
				int temp = crs.pila[i];
				int temp1 = atts.pila[i];
				crs.pila[i] = crs.pila[j];
				atts.pila[i] = atts.pila[j];
				crs.pila[j] = temp;
				atts.pila[j] = temp1;
				i++;
				j--;
			}
		}

		// recursively sort two sub parts
		if (low < j)
			quickSort(atts, crs, low, j);

		if (high > i)
			quickSort(atts, crs, i, high);
	}
}
