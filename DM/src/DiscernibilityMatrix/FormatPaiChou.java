package DiscernibilityMatrix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import alghoritms.fast_br.tools.TuplaBinaria;
import weka.core.Attribute;
import weka.core.AttributeStats;
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

		// Save data
		String new_file = ifilename	+ ".txt";
		BufferedWriter out = new BufferedWriter(new FileWriter(new_file));
		
		for (int k = 0; k < nInstances; k++) {
			for (int i = 0; i < nAtts; i++) {
				out.write(Double.toString(data.instance(k).value(i)));
				System.out.print(data.instance(k).value(i));
				if (i != (nAtts-1)){
					out.write(",");
					System.out.print(",");
				}
			}
			out.write("\n");
			System.out.print("\n");
		}
		out.close();
	}
}
