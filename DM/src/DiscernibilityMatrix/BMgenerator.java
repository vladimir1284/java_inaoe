package DiscernibilityMatrix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import alghoritms.fast_br.tools.TuplaBinaria;

import org.apache.commons.cli.*;

public class BMgenerator {

	public static void main(String[] args) throws ParseException {
		int maxOnes, minOnes, cols, rows, seed;
		String oFolder;
		// /////////////////////////////////////////////////////////////////////
		// Fase 1: Configuramos las opciones de validación de entrada.
		// /////////////////////////////////////////////////////////////////////

		Options options = new Options();
		options.addOption("max", true, "Maximum number of 1's per row");
		options.addOption("min", true, "Minimum number of 1's per row");
		options.addOption("r", true, "Number of rows");
		options.addOption("c", true, "Number of columns");
		options.addOption("o", true, "Output folder");
		options.addOption("s", true, "Seed for the random number generator");
		options.addOption("h", "help", false, "Prints this message");
		try {
			// /////////////////////////////////////////////////////////////////////
			// Fase 2: Parseamos la entrada con la configuración establecida
			// /////////////////////////////////////////////////////////////////////

			BasicParser parser = new BasicParser();
			CommandLine cmdLine = parser.parse(options, args);

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
			int numatts = 0; // Must have 6 cml arguments

			// Check args
			if (cmdLine.hasOption("max")) {
				numatts++;
			}
			if (cmdLine.hasOption("min")) {
				numatts++;
			}
			if (cmdLine.hasOption("r")) {
				numatts++;
			}
			if (cmdLine.hasOption("c")) {
				numatts++;
			}
			if (cmdLine.hasOption("o")) {
				numatts++;
			}
			if (cmdLine.hasOption("s")) {
				numatts++;
			}
			if (numatts != 6) {
				new HelpFormatter().printHelp(
						DMgenerator.class.getCanonicalName(), options);
				return;
			} else {
				// Get values
				maxOnes = Integer.parseInt(cmdLine.getOptionValue("max"));
				minOnes = Integer.parseInt(cmdLine.getOptionValue("min"));
				cols = Integer.parseInt(cmdLine.getOptionValue("c"));
				rows = Integer.parseInt(cmdLine.getOptionValue("r"));
				seed = Integer.parseInt(cmdLine.getOptionValue("s"));
				oFolder = cmdLine.getOptionValue("o");
			}
		} catch (java.lang.NumberFormatException ex) {
			new HelpFormatter().printHelp(DMgenerator.class.getCanonicalName()
					+ " [opciones] <dtabase.arff>", options); // Error,
																// imprimimos la
																// ayuda
			return;
		}
		// Validate Input Data
		if (cols < maxOnes | minOnes > maxOnes | minOnes < 1) {
			System.out
					.println("Wrong parameters!!! (0 < minOnes <= maxOnes <= cols)");
			return;
		}
		if (rows < 1) {
			System.out.println("Wrong parameters!!! (rows >= 1)");
			return;
		}

		// Print input data
		System.out.println("Number of columns:      " + String.valueOf(cols));
		System.out
				.println("Maximum number of ones: " + String.valueOf(maxOnes));
		System.out
				.println("Minimum number of ones: " + String.valueOf(minOnes));
		System.out.println("Number of rows:         " + String.valueOf(rows));
		System.out.println("Seed:                   " + String.valueOf(seed));
		System.out.println("Output folder:          " + oFolder);

		// Setup
		Random rn = new Random(seed);
		TuplaBinaria[] BM = new TuplaBinaria[rows];
		int currentRow = 0;
		int ntrays = 0;

		// Populate the matrix
		TuplaBinaria newRow;
		while (currentRow < rows) {
			newRow = generateRow(rn, maxOnes, minOnes, cols);
			// Test for basic
			boolean basic = true;
			for (int i = 0; i < currentRow; i++) {
				// Current replace the one in the BM (randomly)
				if (newRow.sonSubfila(BM[i]) == 1) {
					if (rn.nextBoolean())
						BM[i] = newRow;
					basic = false;
					break;
				}
				// Current row is ignored
				if (newRow.sonSubfila(BM[i]) == 2) {
					basic = false;
					break;
				}
			}
			// Terminate if too many trays
			if (ntrays > 10e6) {
				System.out
						.println("We couldn't generate a new row after 10 million trays!!!");
				System.out.println("Already found: "
						+ String.valueOf(currentRow) + " basic rows");
				return;
			} else {
				ntrays++;
			}
			// Add a new row and clear ntrays
			if (basic) {
				BM[currentRow] = newRow;
				ntrays = 0;
				currentRow++;
			}
		}

		// // Print the matrix
		// for (int i = 0; i < rows; i++) {
		// System.out.println(BM[i].toString());
		// }

		// Save the basic matrix
		saveMatrix(oFolder, BM, rows, cols, seed, minOnes, maxOnes);

	}

	// Generates a random row with the desired characteristics
	static private TuplaBinaria generateRow(Random rand, int maxOnes,
			int minOnes, int cols) {
		int pos;
		int nOnes = rand.nextInt(maxOnes - minOnes + 1) + minOnes;
		// Create new row
		TuplaBinaria row = new TuplaBinaria(cols, -1);

		// Populate the row
		while (nOnes > 0) {
			pos = rand.nextInt(cols);
			if (row.getValorEn(pos) == 0) {
				row.setValorEn(pos, 1);
				nOnes--;
			}
		}

		return row;
	}

	// Save the Matrix to disk BM2000x30_1-9_s492658088
	private static void saveMatrix(String oFolder, TuplaBinaria[] BM, int rows,
			int cols, int seed, int minOnes, int maxOnes) {
		String new_file = oFolder + "/BM" + String.valueOf(rows) + "x"
				+ String.valueOf(cols) + "_" + String.valueOf(minOnes) + "-"
				+ String.valueOf(maxOnes) + "_s" + String.valueOf(seed)
				+ ".txt";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new_file));
			// Write header
			out.write(rows + "\n");
			out.write(cols + "\n");

			// Write rows
			for (int i = 0; i < rows; i++) {
				out.write(BM[i].toString() + "\n");
			}
			out.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
