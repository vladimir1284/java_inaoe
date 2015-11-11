package DiscernibilityMatrix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import alghoritms.fast_br.tools.TuplaBinaria;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import org.apache.commons.cli.*;

public class fastBMcalculator {
	static String ls = System.lineSeparator();
	static int condition_atts;
	static DMrow[] DM;
	static int DMsize = 0;
	static int index_min_hw;
	static int min_hw;
	static TuplaBinaria[] BM;
	static int BMsize = 0;
	static int nulo = 0;
	static boolean saveDM = false;
	static boolean saveBM = false;
	static boolean nda = false;
	static boolean miss = false;
	static long timeDM;

	static private class DMrow {
		TuplaBinaria tupla;
		int hw;

		public DMrow(TuplaBinaria dm, int dm_hw) {
			tupla = dm;
			hw = dm_hw;
		}
	}

	public static void main(String[] args) throws Exception {
		CommandLineParser parser = null;
		CommandLine cmdLine = null;
		String filename = null;
		// /////////////////////////////////////////////////////////////////////
		// Fase 1: Configuramos las opciones de validación de entrada.
		// /////////////////////////////////////////////////////////////////////

		Options options = new Options();
		options.addOption("md", false,
				"Salvar Matriz de Discernibilidad (si no se pide bm, ésta es por defecto)");
		options.addOption("mb", false, "Salvar Matriz Básica");
		options.addOption("nda", false, "No usar atributo de desición");
		options.addOption("miss", false,
				"Eliminar objetos con falta de información");
		options.addOption("h", "help", false, "Imprime el mensaje de ayuda");

		try {

			// /////////////////////////////////////////////////////////////////////
			// Fase 2: Parseamos la entrada con la configuración establecida
			// /////////////////////////////////////////////////////////////////////

			parser = new BasicParser();
			cmdLine = parser.parse(options, args);

			// /////////////////////////////////////////////////////////////////////
			// Fase 3: Analizamos los resultados y realizamos las tareas
			// pertinentes
			// /////////////////////////////////////////////////////////////////////

			// Si está la opcion de ayuda, la imprimimos y salimos.
			if (cmdLine.hasOption("h")) { // No hace falta preguntar por el
											// parámetro "help". Ambos son
											// sinónimos
				new HelpFormatter().printHelp(
						fastBMcalculator.class.getCanonicalName(), options);
				return;
			}

			// Si el usuario pide la DM la salvaremos
			if (cmdLine.hasOption("md")) {
				saveDM = true;
			}
			// No se va a tener en cuenta el atributo de decisión
			if (cmdLine.hasOption("nda")) {
				nda = true;
			}
			// Borrar instancias con missing data
			if (cmdLine.hasOption("miss")) {
				miss = true;
			}
			// Si el usuario pide la BM la salvaremos
			// Si no, se salvará la DM
			if (cmdLine.hasOption("mb")) {
				saveBM = true;
			} else {
				saveDM = true;
			}

			if (cmdLine.getArgList().size() != 1) {
				throw new ParseException(
						"El último argumento es el nombre del fichero arff!");
			} else {
				filename = (String) cmdLine.getArgList().get(0);
			}

		} catch (org.apache.commons.cli.ParseException ex) {
			System.out.println(ex.getMessage() + ls);

			new HelpFormatter().printHelp(
					fastBMcalculator.class.getCanonicalName()
							+ " [opciones] <dtabase.arff>", options); // Error,
																		// imprimimos
																		// la
																		// ayuda
		} catch (java.lang.NumberFormatException ex) {
			new HelpFormatter().printHelp(
					fastBMcalculator.class.getCanonicalName()
							+ " [opciones] <dtabase.arff>", options); // Error,
																		// imprimimos
																		// la
																		// ayuda
		}

		// Parsing ARFF file
		DataSource source = new DataSource(filename);
		Instances data = source.getDataSet();
		// setting class attribute if the data format does not provide this
		// information
		// For example, the XRFF format saves the class attribute information as
		// well
		int class_index = data.numAttributes() - 1;
		if (data.classIndex() == -1) {
			data.setClassIndex(class_index);
		}
		// Filter missing data if required
		if (miss)
			deleteMissingData(data);
		// Actually create the Discernibility Matrix
		if (nda) {
			// timeDM = createDMnda(data);
		} else {
			timeDM = createDM(data, class_index);
		}

		if (saveDM){
			// saveMatrix(filename, "DM", DM, DMsize);
		}
		System.out.println("Time for DM: " + timeDM + "ms");
		System.out.println("Number of rows in DM: " + DMsize);
		System.out.println("Number of inconsistencies: " + nulo);

		if (saveBM) {
			long timeBM = createBM();
			saveMatrix(filename, "BM", BM, BMsize);
			System.out.println("Time for BM: " + timeBM + "ms");
			System.out.println("Number of rows in BM: " + BMsize);
		}

	}

	// Creating the Discernibility Matrix from the Information System
	private static long createDM(Instances data, int class_index) {
		long startTime = System.currentTimeMillis();
		int i, j, k, current_class = 0;
		condition_atts = data.numAttributes() - 1;
		min_hw = condition_atts; // Minimum Hamming Weigh
		TuplaBinaria current_tupla;
		// Create arrays
		int DMmaxsize = data.numInstances()*(data.numInstances()-1)/2;
		DM = new DMrow[DMmaxsize];

		// Sort by class
		data.sort(class_index);
		AttributeStats stats = data.attributeStats(class_index);

		int numClasses = stats.distinctCount; // Number of classes
		// Starting index for instances in data belonging to class i
		int[] class_start_index = new int[numClasses];
		for (i = 0; i < numClasses - 1; i++) {
			class_start_index[i + 1] = stats.nominalCounts[i]
					+ class_start_index[i];
		}

		// Comparing instances in different classes
		// Stopping before the last class elements
		for (i = 0; i < class_start_index[numClasses - 1]; i++) {
			for (j = class_start_index[current_class + 1]; j < data
					.numInstances(); j++) {
				// Dummy tuple for comparisons results
				current_tupla = new TuplaBinaria(condition_atts, -1);
				// Actually compare two records
				int hw = 0;
				for (k = 0; k < condition_atts; k++) {
					if (data.instance(i).value(k) != data.instance(j).value(k)) {
						current_tupla.setValorEn(k, 1);
						hw++;
					}

				}
				// Save row if not equal to zero
				if (!current_tupla.esNulo()) {
					if (hw < min_hw) {
						min_hw = hw; // Update the row with minimum number of
										// 1's
						index_min_hw = DMsize;
					}
					DM[DMsize++] = new DMrow(current_tupla, hw); // Tracking the
																	// number of
																	// ones in a
					// row

				} else {
					nulo++;
				}

			}
			// Last element in the class reached
			if (i == (class_start_index[current_class + 1] - 1))
				current_class++;
		}
		long endTime = System.currentTimeMillis();
		return endTime - startTime;
	}

	// // Creating the Discernibility Matrix from the Information System (no
	// // decision attribute)
	// private static long createDMnda(Instances data) {
	// long startTime = System.currentTimeMillis();
	// int i, j, k = 0;
	// int condition_atts = data.numAttributes() - 1;
	// int nInstances = data.numInstances();
	// TuplaBinaria current_tupla;
	//
	// // Comparing every pair of instances
	// // Stopping before the last class elements
	// for (i = 0; i < nInstances - 1; i++) {
	// for (j = i + 1; j < nInstances; j++) {
	// // Dummy tuple for comparisons results
	// current_tupla = new TuplaBinaria(condition_atts, -1);
	// // Actually compare two records
	// for (k = 0; k < condition_atts; k++) {
	// current_tupla.setValorEn(k,
	// data.instance(i).value(k) != data.instance(j)
	// .value(k));
	//
	// }
	// // Save row if not equal to zero
	// if (!current_tupla.esNulo()) {
	// DM.add(current_tupla);
	// } else {
	// nulo++;
	// }
	//
	// }
	// }
	// long endTime = System.currentTimeMillis();
	// return endTime - startTime;
	// }

	// Create Basic Matrix from the Discernibility Matrix
	@SuppressWarnings("unchecked")
	private static long createBM() {
		BM = new TuplaBinaria[DMsize]; // Larger BM possible
		long startTime = System.currentTimeMillis();
		DMrow baseRow, current;

		// Create the two arrays for DM storage
		int[] DMindex = new int[DMsize - 1];
		int CurrSize = DMsize - 1;
		int[] TempIndex = new int[DMsize - 1];
		int TempSize;
		int j = 0;
		for (int i = 0; i < DMsize; i++) {
			if (i != index_min_hw) {
				DMindex[j++] = i;
			}
		}

		while (CurrSize > 1) {
			// Pop the row with minimum Hamming Weigh as base
			baseRow = DM[index_min_hw];
			BM[BMsize++] = baseRow.tupla;

			// Compare with the rest of the list
			TempSize = 0;
			min_hw = condition_atts;
			for (int i = 0; i < CurrSize; i++) {
				current = DM[DMindex[i]];
				if (!baseRow.tupla.esSubfilaDe(current.tupla)) {
					// This row must be preserved
					if (current.hw < min_hw) {
						min_hw = current.hw;
						index_min_hw = DMindex[i];
					}
					TempIndex[TempSize++] = DMindex[i];
				}
			}
			// Update the list of remaining rows
			DMindex = TempIndex;
			CurrSize = TempSize;
		}
		// Add the last remaining row
		BM[BMsize++] = DM[DMindex[0]].tupla;
		long endTime = System.currentTimeMillis();
		return endTime - startTime;
	}

	// Save the Matrix to disk
	private static void saveMatrix(String filename, String head,
			TuplaBinaria[] M, int sizeM) {
		Path p = Paths.get(filename);
		String new_file = head + p.getFileName().toString().split("\\.")[0]
				+ ".txt";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new_file));
			// Write header
			out.write(sizeM + ls);
			out.write(M[0].numBits + ls);

			// Write rows
			for (int i = 0; i < sizeM; i++) {
				out.write(M[i].toString() + ls);
			}
			out.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private static void deleteMissingData(Instances data) {
		int nInstances = data.numInstances();
		// System.out.println(nInstances);
		for (int i = 0; i < nInstances; i++) {
			if (data.instance(i).hasMissingValue()) {
				data.delete(i--);
				nInstances--;
			}
		}
		// System.out.println(data.numInstances());
	}
}
