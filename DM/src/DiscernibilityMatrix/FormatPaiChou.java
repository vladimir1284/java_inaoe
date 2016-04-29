package DiscernibilityMatrix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.security.acl.LastOwnerException;

import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.InstanceComparator;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class FormatPaiChou {
	public static void main(String[] args) throws Exception {

		// check to see if the String array is empty
		if (args.length != 1) {
			System.out.println("The only one argument is the input file name!");
		}

		String ifilename = args[0];

		// Parsing ARFF file
		DataSource source = new DataSource(ifilename);
		Instances data = source.getDataSet();

		// Delete missing data
		int nInstances = data.numInstances();
		int nAtts = data.numAttributes();

		for (int i = 0; i < nInstances; i++) {
			if (data.instance(i).hasMissingValue()) {
				data.delete(i--);
				nInstances--;
			}
		}

		// Remove duplicates
		int class_index = data.numAttributes() - 1;
		if (data.classIndex() == -1) {
			data.setClassIndex(class_index);
		}
		removeDuplicates(data, class_index);

		// Handle inconsistencies
		removeInconsistencies(data, class_index);
		nInstances = data.numInstances();

		System.out.println("pass 2");
		InstanceComparator comparator = new InstanceComparator();
		comparator.setIncludeClass(false);
		for (int i = 0; i < data.numInstances() - 1; i++) {
			for (int j = i + 1; j < data.numInstances(); j++) {
				if (comparator.compare(data.instance(i), data.instance(j)) == 0) {
					// Actually compare two records
					boolean equal = true;
					for (int k = 0; k < nAtts - 1; k++) {
						if (data.instance(i).value(k) != data.instance(j)
								.value(k)) {
							equal = false;
							break;
						}
					}
					if (equal) {
						System.out.println("Inconsistent");
					}
				}
			}
		}
		

		// Save data
		String new_file = ifilename + ".txt";
		BufferedWriter out = new BufferedWriter(new FileWriter(new_file));

		for (int k = 0; k < nInstances; k++) {
			for (int i = 0; i < nAtts; i++) {
				out.write(Double.toString(data.instance(k).value(i)));
				// System.out.print(data.instance(k).value(i));
				if (i != (nAtts - 1)) {
					out.write(",");
					// System.out.print(",");
				}
			}
			out.write("\n");
			// System.out.print("\n");
		}
		out.close();
	}

	// Remove duplicated objects
	private static void removeDuplicates(Instances data, int class_index) {
		InstanceComparator comparator = new InstanceComparator();
		comparator.setIncludeClass(true);
		for (int i = 0; i < data.numInstances() - 1; i++) {
			for (int j = i + 1; j < data.numInstances(); j++) {
				if (compare(data.instance(i), data.instance(j),true)) {
					System.out.print("Duplicated: ");
					System.out.println(data.instance(j));
					data.delete(j--);
				}
			}
		}
	}
	
	private static boolean compare(Instance obj1, Instance obj2, boolean use_class){
		int discount = 1;
		if (use_class) discount = 0;
		boolean equal = true;
		for (int k = 0; k < obj1.numAttributes() - discount; k++) {
			if (obj1.value(k) != obj2.value(k)) {
				equal = false;
				break;
			}
		}
		return equal;
		
	}
	// Remove inconsistencies
	private static void removeInconsistencies(Instances data, int class_index) {
		InstanceComparator comparator = new InstanceComparator();
		comparator.setIncludeClass(false);

		// Get the value for the next class
		AttributeStats stats = data.attributeStats(class_index);
		int new_class = stats.distinctCount; // Number of classes;

		for (int i = 0; i < data.numInstances() - 1; i++) {
			for (int j = i + 1; j < data.numInstances(); j++) {
				if (compare(data.instance(i), data.instance(j),false)) {
					System.out.print(String.format("Inconsistent (%s,%s): ",
							new_class, data.instance(j).value(class_index)));
					System.out.println(data.instance(j));
					data.delete(j--);
					// move the other object for a different class
					data.instance(i).setClassValue(new_class++);
					// System.out.println(data.instance(i).value(class_index));
				}
			}
		}
	}

	// // Looking for inconsistencies in the Information System
	// private static void removeInconst(Instances data, int class_index) {
	// int i, j, k, current_class = 0;
	// int condition_atts = data.numAttributes() - 1;
	//
	// // Sort by class
	// data.sort(class_index);
	// AttributeStats stats = data.attributeStats(class_index);
	//
	// int numClasses = stats.distinctCount; // Number of classes
	// // Starting index for instances in data belonging to class i
	// int[] class_start_index = new int[numClasses];
	// for (i = 0; i < numClasses - 1; i++) {
	// class_start_index[i + 1] = stats.nominalCounts[i]
	// + class_start_index[i];
	// }
	//
	// numClasses = stats.distinctCount; // Number of classes
	// int new_class = numClasses;
	// // Comparing instances in different classes
	// // Stopping before the last class elements
	// for (i = 0; i < class_start_index[numClasses - 1]; i++) {
	// for (j = class_start_index[current_class + 1]; j < data
	// .numInstances(); j++) {
	// // Actually compare two records
	// boolean equal = true;
	// for (k = 0; k < condition_atts; k++) {
	// if (data.instance(i).value(k) != data.instance(j).value(k)) {
	// equal = false;
	// break;
	// }
	//
	// }
	// // Put inconsistent object in a new class
	// if (equal) {
	// // delete one of the equal objects
	// System.out.print(String.format("Inconsisten (%s,%s): ",
	// new_class,data.instance(j).value(class_index)));
	// System.out.println(data.instance(j));
	// data.delete(j--);
	// // move the other object for a different class
	// data.instance(i).setClassValue(new_class++);
	// }
	//
	// }
	// // Last element in the class reached
	// if (i == (class_start_index[current_class + 1] - 1))
	// current_class++;
	// }
	// }
}
