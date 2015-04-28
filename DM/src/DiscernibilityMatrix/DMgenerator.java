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

public class DMgenerator {
	static String ls = System.lineSeparator();
	static LinkedList<TuplaBinaria> DM = new LinkedList<TuplaBinaria>();
	static LinkedList<TuplaBinaria> BM = new LinkedList<TuplaBinaria>();
	static int nulo = 0;
	static boolean saveDM = false;
	static boolean saveBM = false;
	static boolean nda = false;
	static long timeDM;

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
						DMgenerator.class.getCanonicalName(), options);
				return;
			}

			// Si el usuario pide la DM la salvaremos
			if (cmdLine.hasOption("mb")) {
				saveDM = true;
			}
			// No se va a tener en cuenta el atributo de decisión
			if (cmdLine.hasOption("nda")) {
				nda = true;
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

			new HelpFormatter().printHelp(DMgenerator.class.getCanonicalName()
					+ " [opciones] <dtabase.arff>", options); // Error,
																// imprimimos la
																// ayuda
		} catch (java.lang.NumberFormatException ex) {
			new HelpFormatter().printHelp(DMgenerator.class.getCanonicalName()
					+ " [opciones] <dtabase.arff>", options); // Error,
																// imprimimos la
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
		// Actually create the Discernibility Matrix
		if (nda) {
			timeDM = createDMnda(data);
		} else {
			timeDM = createDM(data, class_index);
		}

		if (saveDM)
			saveMatrix(filename, "DM", DM);

		System.out.println("Time for DM: " + timeDM + "ms");
		System.out.println("Number of rows in DM: " + DM.size());
		System.out.println("Number of inconsistencies: " + nulo);

		if (saveBM) {
			long timeBM = createBM();
			saveMatrix(filename, "BM", BM);
			System.out.println("Time for BM: " + timeBM + "ms");
			System.out.println("Number of rows in BM: " + BM.size());
		}

	}

	// Creating the Discernibility Matrix from the Information System
	private static long createDM(Instances data, int class_index) {
		long startTime = System.currentTimeMillis();
		int i, j, k, current_class = 0;
		int condition_atts = data.numAttributes() - 1;
		TuplaBinaria current_tupla;

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
				for (k = 0; k < condition_atts - 1; k++) {
					current_tupla.setValorEn(k,
							data.instance(i).value(k) != data.instance(j)
									.value(k));

				}
				// Save row if not equal to zero
				if (!current_tupla.esNulo()) {
					DM.add(current_tupla);
				} else {
					nulo++;
				}

			}
			// Last element in the class reached
			if (i == class_start_index[current_class] - 1)
				current_class++;
		}
		long endTime = System.currentTimeMillis();
		return endTime - startTime;
	}

	// Creating the Discernibility Matrix from the Information System (no
	// decision attribute)
	private static long createDMnda(Instances data) {
		long startTime = System.currentTimeMillis();
		int i, j, k = 0;
		int condition_atts = data.numAttributes() - 1;
		int nInstances = data.numInstances();
		TuplaBinaria current_tupla;

		// Comparing every pair of instances
		// Stopping before the last class elements
		for (i = 0; i < nInstances - 1; i++) {
			for (j = i + 1; j < nInstances; j++) {
				// Dummy tuple for comparisons results
				current_tupla = new TuplaBinaria(condition_atts, -1);
				// Actually compare two records
				for (k = 0; k < condition_atts; k++) {
					current_tupla.setValorEn(k,
							data.instance(i).value(k) != data.instance(j)
									.value(k));

				}
				// Save row if not equal to zero
				if (!current_tupla.esNulo()) {
					DM.add(current_tupla);
				} else {
					nulo++;
				}

			}
		}
		long endTime = System.currentTimeMillis();
		return endTime - startTime;
	}

	// Create Basic Matrix from the Discernibility Matrix
	@SuppressWarnings("unchecked")
	private static long createBM() {
		long startTime = System.currentTimeMillis();
		TuplaBinaria baseRow, current;
		ListIterator<TuplaBinaria> itr;
		int subrow;
		boolean noBasic;

		// Create a local copy from DM to work on
		LinkedList<TuplaBinaria> LocalDM = new LinkedList<TuplaBinaria>();
		LocalDM = (LinkedList<TuplaBinaria>) DM.clone();

		// External Comparison loop
		while (LocalDM.size() > 1) {
			// Pop the first element as base
			baseRow = LocalDM.pop();
			noBasic = false;
			// Compare with the rest of the list
			itr = (ListIterator<TuplaBinaria>) LocalDM.iterator();
			while (itr.hasNext()) {
				current = itr.next();
				subrow = baseRow.sonSubfila(current);
				// En caso de q la comparación no de cero
				switch (subrow) {
				case 1: // baseRow es subfila de current
					itr.remove();
					break;
				case 2: // current es subfila de baseRow
					// baseRow = new TuplaBinaria(current);
					// itr.remove();
					noBasic = true;
					break;
				}
				if (noBasic)
					break;
			}
			// Append the basic row to the Basic Matrix
			if (!noBasic)
				BM.add(baseRow);
		}
		if (LocalDM.size() > 0)
			BM.add(LocalDM.pop());
		long endTime = System.currentTimeMillis();
		return endTime - startTime;
	}

	// Save the Matrix to disk
	private static void saveMatrix(String filename, String head,
			LinkedList<TuplaBinaria> M) {
		Path p = Paths.get(filename);
		String new_file = head + p.getFileName().toString().split("\\.")[0]
				+ ".txt";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new_file));
			// Write header
			out.write(M.size() + ls);
			out.write(M.getFirst().numBits + ls);

			// Write rows
			Iterator<TuplaBinaria> iterator = M.iterator();
			while (iterator.hasNext()) {
				out.write(iterator.next().toString() + ls);
			}
			out.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
