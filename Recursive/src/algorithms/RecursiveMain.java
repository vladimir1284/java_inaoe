package algorithms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.*;

public class RecursiveMain {
	static String ls = System.lineSeparator();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String ifilename = null;
		long startTime = System.currentTimeMillis();
		int stop = 2; // Default number of attributes to stop the vereification
						// of repeated

		CommandLineParser parser = null;
		CommandLine cmdLine = null;
		// /////////////////////////////////////////////////////////////////////
		// Fase 1: Configuramos las opciones de validación de entrada.
		// /////////////////////////////////////////////////////////////////////

		Options options = new Options();
		options.addOption("s", true,
				"Stop the search for repeated attribute (counting from the last column)");
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
						RecursiveMain.class.getCanonicalName(), options);
				return;
			}
			// Si el usuario define el numero de bins
			if (cmdLine.hasOption("s")) {
				stop = Integer.parseInt(cmdLine.getOptionValue("s"));

			}
			if (cmdLine.getArgList().size() != 1) {
				throw new ParseException(
						"El último argumento es el nombre del fichero con la matriz básica!");
			} else {
				ifilename = (String) cmdLine.getArgList().get(0);
			}

		} catch (org.apache.commons.cli.ParseException ex) {
			System.out.println(ex.getMessage() + ls);

			new HelpFormatter().printHelp(
					RecursiveMain.class.getCanonicalName()
							+ " [opciones] <basic_matrix.txt>", options); // Error,
																		// imprimimos
																		// la
																		// ayuda
		} catch (java.lang.NumberFormatException ex) {
			new HelpFormatter().printHelp(
					RecursiveMain.class.getCanonicalName()
							+ " [opciones] <basic_matrix.txt>", options); // Error,
																		// imprimimos
																		// la
																		// ayuda
		}

		System.out.println(ifilename);

		Integer rows, atts;
		RecursiveReducer rrAlgorithm = new RecursiveReducer(stop);

		BufferedReader br = new BufferedReader(new FileReader(ifilename));
		try {
			// Size of BM
			String line = br.readLine();
			rows = Integer.parseInt(line);
			line = br.readLine();
			atts = Integer.parseInt(line);

			rrAlgorithm.set(rows, atts);

			// The rest of the matrix
			line = br.readLine();
			int i = 0;
			String[] temp;
			while (line != null) {
				// Parse Row
				temp = line.trim().replace("\t", " ").split(" ");
				for (int j = 0; j < atts; j++) {
					// System.out.print(temp[j]);
					rrAlgorithm.pushValor(Integer.parseInt(temp[j]), i, j);
				}

				i++;
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		// Where result are going to
		RegisterData dataOut = new RegisterData();

		// Do the job
		rrAlgorithm.extraerTT(dataOut);

		long endTime = System.currentTimeMillis();

		// // Save data
		// String[] temp;
		// temp = ifilename.split("MB");
		// String ofilename = "BR" + temp[temp.length-1];
		// dataOut.save2File(ofilename, brAlgorithm.getNumSoluciones(),
		// (endTime-startTime)/1000);
		System.out
				.println("time: " + Long.toString(endTime - startTime) + "ms");
		System.out.println("Typical testors: "
				+ Integer.toString(rrAlgorithm.contadorTTs));
		System.out.println("Testors: "
				+ Integer.toString(rrAlgorithm.contadorTestores));
		System.out.println("Candidates: "
				+ Long.toString(rrAlgorithm.contadorComprobaciones));
	}

}
