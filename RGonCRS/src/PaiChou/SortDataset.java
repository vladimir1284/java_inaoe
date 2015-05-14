package PaiChou;

import tools.TPila;
import weka.core.Instance;
import weka.core.InstanceComparator;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;

public class SortDataset {
	public static void main(String[] args) throws Exception {
		// Read dataset
		String filename = args[0];
		// Parsing ARFF file
		DataSource source = new DataSource(filename);
		Instances data = source.getDataSet();
		System.out.println("\nDataset:\n");
		System.out.println(data);
		// Sort by attributes
		for(int i=0;i<data.numAttributes();i++){
			data.sort(i);
		}
		System.out.println("\nSorted Dataset:\n");
		System.out.println(data);
	}

	// Comapre instances using a list of attributes
	// returns -1 if inst1 is smaller than inst2, 0 if equal and +1 if greater
	private int compare(Instance inst1, Instance inst2, TPila atts) {
		int result = 0;
		for (int i = 0; i <= atts.tope; i++) {
			if (Utils.eq(inst1.value(atts.pila[i]), inst2.value(atts.pila[i]))) {
				continue;
			} else {
				if (inst1.value(atts.pila[i]) < inst2.value(atts.pila[i]))
					result = -1;
				else
					result = 1;
				break;
			}
		}

		return result;
	}
	  /**
	   * Partitions the instances around a pivot. Used by quicksort and
	   * kthSmallestValue.
	   *
	   * @param attIndex the attribute's index (index starts with 0)
	   * @param l the first index of the subset (index starts with 0)
	   * @param r the last index of the subset (index starts with 0)
	   *
	   * @return the index of the middle element
	   */
	  //@ requires 0 <= attIndex && attIndex < numAttributes();
	  //@ requires 0 <= left && left <= right && right < numInstances();
	  protected int partition(Instances data, TPila atts, int l, int r) {
	    
	    Instance pivot = data((l + r) / 2);

	    while (l < r) {
	      while ((instance(l).value(attIndex) < pivot) && (l < r)) {
	        l++;
	      }
	      while ((instance(r).value(attIndex) > pivot) && (l < r)) {
	        r--;
	      }
	      if (l < r) {
	        swap(l, r);
	        l++;
	        r--;
	      }
	    }
	    if ((l == r) && (instance(r).value(attIndex) > pivot)) {
	      r--;
	    } 

	    return r;
	  }
	  
	  /**
	   * Implements quicksort according to Manber's "Introduction to
	   * Algorithms".
	   *
	   * @param attIndex the attribute's index (index starts with 0)
	   * @param left the first index of the subset to be sorted (index starts with 0)
	   * @param right the last index of the subset to be sorted (index starts with 0)
	   */
	  //@ requires 0 <= attIndex && attIndex < numAttributes();
	  //@ requires 0 <= first && first <= right && right < numInstances();
	  protected void quickSort(TPila atts, int left, int right) {

	    if (left < right) {
	      int middle = partition(atts, left, right);
	      quickSort(atts, left, middle);
	      quickSort(atts, middle + 1, right);
	    }
	  }
}
