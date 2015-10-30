package Consola;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.tt.InterfaceTT;
import edu.tt.Output;
import edu.tt.alghoritms.br_colums_CH.Fast_BR;

//---------------------------------------------------------------
//- Herramienta auxiliar. 
//SERVICIOS: - LECTURA DE LA MB DESDE ARCHIVO.
//				SALIDA DE LOS ALGORIMOS HACIA ARCHIVO (no Implementado)
//---------------------------------------------------------------
public class FileTool implements Output{
	BufferedWriter filew = null;
	
	BufferedWriter resultTT = null;

	private String outputFileName;
	
	public FileTool() {
		super();
		// TODO Auto-generated constructor stub
	}
	//-----------------------------------------------------------
	public void readMatrix(File file, InterfaceTT obj) {
		try {
			
			BufferedReader entrada = new BufferedReader(new FileReader(file));
			 
			String[] values;
			int fil, col, i, j;
			// ----------------
			fil = Integer.parseInt(entrada.readLine());
			col = Integer.parseInt(entrada.readLine());

			obj.set(fil, col);

			for (i = 0; i < fil; i++) {
				values = entrada.readLine().split(" ");
				for (j = 0; j < col; j++)
					obj.pushValor(Integer.parseInt(values[j]), i, j);
			}
			entrada.close();
		} catch (java.io.FileNotFoundException fnfex) {
			System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}
	}
	//-----------------------------------------------------------
	public void saveMatrix(File file, Fast_BR obj) {
		try {
			
			BufferedWriter salida = new BufferedWriter(new FileWriter(file));
			 
			String values;
			int fil, col, i, j;
			// ----------------
			fil = obj.getNumFilas();
			col = obj.getNumColumnas();
			salida.write(Integer.toString(fil));
			salida.newLine();
			salida.write(Integer.toString(col));
			salida.newLine();

			for (i = 0; i < fil; i++) {
				values = "";
				for (j = 0; j < col; j++) {
					values += String.valueOf(obj.getValor(i, j)) + " ";
				}
				salida.write(values);
				salida.newLine();
			}
			salida.close();
		} catch (java.io.FileNotFoundException fnfex) {
			System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}
	}
	//-----------------------------------------------------------------
	public String conv(long val) {
		String[] unit = new String[4];
		unit[3] = new String(" ms");
		unit[2] = new String(" sec");
		unit[1] = new String(" min");
		unit[0] = new String(" hou");
		String cad;
		int cont = 3;
		long resto = 0;
		boolean band  = true;
		int numDiv = 1000;
		while (true) {
			if (val / numDiv < 1) {
				cad = Long.toString(val) + unit[cont];
				if (cont < 3) {
					cad += ", ";
					cad += Long.toString(resto) + unit[cont + 1];
				}
				return cad;
			}
			resto = val % numDiv;
			val = val / numDiv;
			cont--;
			if (band == true) {
				numDiv = 60; band = false;
			}
		}
		// System.currentTimeMillis();
	}
	public void closeFile() {
		if (filew != null) {
			try {
				filew.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		filew = null;
	}
	public void openFile(File selectedFile) {
		// TODO Auto-generated method stub
		closeFile();		
		try {
			filew = new BufferedWriter(new FileWriter(selectedFile, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	//-------------------------------------------------------------
	public void resgistTT(int[] tt) {
		int i; 
		String cad = "{ "; 
		for (i = 1; i < tt[0] + 1; i++) 
			cad  += ("X" + tt[i] + " "); 
		cad += "}"; 
		
		try {
			
			resultTT.write(cad);
			resultTT.newLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	//-------------------------------------------------------------	
	public void percent(int p) {
			// System.out.println("Porciento: " + p);
	}
	//-------------------------------------------------------------	
	public void getInfo(String cad) {
		try {
			filew.write(cad);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//-------------------------------------------------------------	
	// - Metodo especializado en cargar un script para la experiemntacion
	//  usado en la clase DialogEditScript.
	//-------------------------------------------------------------		
	public void readConfScript(File file2, int[] ordenEjecucion, int []numAlg, String[] ordenMB, int []contMB) {
		try {
			
			BufferedReader entrada = new BufferedReader(new FileReader(file2));
			 
			int fil, col, i, j;
			// ----------------
			numAlg[0] = Integer.parseInt(entrada.readLine());
			for (i = 0; i < numAlg[0]; i++) {
				ordenEjecucion[i] = Integer.parseInt(entrada.readLine());
			}
			contMB[0] = Integer.parseInt(entrada.readLine());
			for (i = 0; i < contMB[0]; i++) {
				ordenMB[i] = entrada.readLine();
			}
			entrada.close();
		} catch (java.io.FileNotFoundException fnfex) {
			System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}
	}
	//-------------------------------------------------------------	
	// - Metodo especializado en guardar un script de experiemntacion
	//  - usado en la clase DialogEditScript.
	//-------------------------------------------------------------		
	public void writeConfScript(File file2, int[] ordenEjecucion, int numAlg, String[] ordenMB, int contMB) {
		try {			
			BufferedWriter salida = new BufferedWriter(new FileWriter(file2));
			 
			String values;
			int fil, col, i, j;
			// ----------------
			salida.write(Integer.toString(numAlg));
			salida.newLine();
			for (i=0; i<numAlg; i++) {
			  salida.write(Integer.toString(ordenEjecucion[i]));
			  salida.newLine();
			}
			salida.write(Integer.toString(contMB));
			salida.newLine();
			for (i=0; i<contMB; i++) {
			  salida.write(ordenMB[i]);
			  salida.newLine();
			}
			salida.close();
		} catch (java.io.FileNotFoundException fnfex) {
			System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}
		
	}
	@Override
	public void initOutput(String cad) {
		outputFileName = cad;
		
		File file = new File(Ventana.absolutePath+"TempData.xxx");
		try {
			file.createNewFile();
			resultTT = new BufferedWriter(new FileWriter(file));			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		 

	}
	@Override
	public void write(String cad) {
		
	}
	// ------------------------------------------------------------------------
	@Override
	public void closeOutput(int numTT, String time) {
		
		try {
			
			resultTT.close();
			
			
			File file = new File(Ventana.absolutePath+"TempData.xxx");
			file.createNewFile();
			BufferedReader fileInput = new BufferedReader(new FileReader(file));
			
			file = new File(outputFileName);
			file.createNewFile();
			BufferedWriter fileOutput = new BufferedWriter(new FileWriter(file));
			
			
			fileOutput.write("El numero de testores tipicos hallado fue : " + numTT + "\n");
			fileOutput.write("Tiempo de ejecucion del algoritmo en segundos : " + time + "\n");
			
			String aLine;
			while ((aLine = fileInput.readLine()) != null) {
				fileOutput.write(aLine);
				fileOutput.newLine();
			}
			
			fileInput.close();
			fileOutput.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
