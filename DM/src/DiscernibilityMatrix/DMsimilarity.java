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
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import org.apache.commons.cli.*;

public class DMsimilarity {
	static String ls = System.lineSeparator();
	static LinkedList<double[]> DM = new LinkedList<double[]>();
	static LinkedList<double[]> BM = new LinkedList<double[]>();
	static int nulo = 0;
	static boolean saveDM = false;
	static boolean saveBM = false;
	static boolean nda = false;
	static boolean miss = false;
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
		// Filter missing data if required
		if (miss)
			deleteMissingData(data);
		// Actually create the Discernibility Matrix
		if (nda) {
			//timeDM = createDMnda(data);
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
		double[] current_tupla;

		// Sort by class
		data.sort(class_index);
		AttributeStats stats = data.attributeStats(class_index);
		
		// Finding range of numerical values
		double [] range = new double[condition_atts];
		for (k = 0; k < condition_atts; k++) {
			if (data.attribute(k).type() == Attribute.NUMERIC){
				AttributeStats attStats = data.attributeStats(k);
				range[k] = attStats.numericStats.max - attStats.numericStats.min;
			}
				
		}

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
				current_tupla = new double[condition_atts];
				// Actually compare two records
				for (k = 0; k < condition_atts; k++) {
					// Missing Value implies 1 in the comparison result
					if (data.instance(i).isMissing(k)
							|| data.instance(j).isMissing(k)) {
						current_tupla[k] = 1;
					} else {
						switch (data.attribute(k).type()) {
						case Attribute.NUMERIC: {
							current_tupla[k] = Math.abs(data.instance(i).value(k) - data.instance(j)
									.value(k))/range[k];

							break;
						}
						default: {
							if (data.instance(i).value(k) != data.instance(j)
									.value(k)) {
								current_tupla[k] = 1;
							} else {
								current_tupla[k] = 0;
							}
						}

						}

					}
				}
				// Save row if not equal to zero
				boolean emptyRow = true;
				for (k = 0; k < condition_atts; k++) {
					if (current_tupla[k] != 0){
						emptyRow = false;
						break;
					}
				}
				if (!emptyRow) {
					DM.add(current_tupla);
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

	// Create Basic Matrix from the Discernibility Matrix
	@SuppressWarnings("unchecked")
	private static long createBM() {
		long startTime = System.currentTimeMillis();
		double[] baseRow;
		double[] current;
		ListIterator<double[]> itr;
		int subrow;
		boolean noBasic;

		// Create a local copy from DM to work on
		LinkedList<double[]> LocalDM = new LinkedList<double[]>();
		LocalDM = (LinkedList<double[]>) DM.clone();

		// External Comparison loop
		while (LocalDM.size() > 1) {
			// Pop the first element as base
			baseRow = LocalDM.pop();
			noBasic = false;
			// Compare with the rest of the list
			itr = (ListIterator<double[]>) LocalDM.iterator();
			while (itr.hasNext()) {
				current = itr.next();
				subrow = sonSubfila(baseRow,current);
				// En caso de q la comparación no de cero
				switch (subrow) {
				case 1: // baseRow es subfila de current
					itr.remove();
					break;
				case 2: // current es subfila de baseRow
					// baseRow = new TuplaBinaria(current);
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
	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Evalúa si baseRow es subfila de la tupla current o viceversa.
	// - Esto es si se mira las tuplas como filas en una MD.
	// Restorna:
	// 0 - No son subfila (se quedan las 2)
	// 1 - baseRow es subfila de current (se queda baseRow)
	// 2 - operando es subfila de baseRow (se queda current)
	// ***************************************************************************
	private static int sonSubfila(double[] baseRow, double[] current) {
		short i;
		boolean baseRowSUBcurrent = true;
		boolean currentSUBbaseRow = true;
		
		for (i = 0; i < current.length; i++) {			
			if (baseRow[i] > current[i]) {
				baseRowSUBcurrent = false; // Ya baseRow no puede ser subfila de current
				if (!currentSUBbaseRow) {
					return 0; // Si current no puede ser subfila de baseRow
								// terminamos
				}
			}
			if (current[i] > baseRow[i]) {
				currentSUBbaseRow = false; // Ya current no puede ser subfila de
										// baseRow
				if (!baseRowSUBcurrent) {
					return 0; // Si baseRow no puede ser subfila de current
								// terminamos
				}
			}
		}
		return baseRowSUBcurrent ? 1 : 2;
	}
	// Save the Matrix to disk
	private static void saveMatrix(String filename, String head,
			LinkedList<double[]> M) {
		Path p = Paths.get(filename);
		String new_file = head + p.getFileName().toString().split("\\.")[0]
				+ ".txt";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new_file));
			// Write header
			out.write(M.size() + ls);
			out.write(M.getFirst().length + ls);

			// Write rows
			Iterator<double[]> iterator = M.iterator();
			while (iterator.hasNext()) {
				double[] row =iterator.next();
				for (int k = 0; k < row.length; k++) {
					out.write(Double.toString(row[k])+' ');
				}
				out.write(ls);
			}
			out.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private static void deleteMissingData(Instances data) {
		int nInstances = data.numInstances();
		// System.out.println(nInstances);
		for (int i = 0; i < nInstances - 1; i++) {
			if (data.instance(i).hasMissingValue()) {
				data.delete(i--);
				nInstances--;
			}
		}
		// System.out.println(data.numInstances());
	}
}
