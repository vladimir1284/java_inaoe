package Consola;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.sun.org.apache.xml.internal.security.Init;

import edu.tt.InterfaceTT;
import edu.tt.Output;
import edu.tt.alghoritms.br_colums_CH.Fast_BR;
import edu.tt.alghoritms.br_parallel.BR_Parallel;
import edu.tt.alghoritms.fast_ct_ext.FAST_CT_EXT;
import edu.tt.alghoritms.lex.LEX;

public class Main {
	public static void main(String[] args) throws Exception {
		new Ventana();		
	}
}

//-----------------------------------------------------------------
class Consola implements Output{

	public void resgistTT(int[] tt) {
		int i; 
/*		String cad = "[ "; 
		for (i = 1; i < tt[0] + 1; i++) 
			cad  += (tt[i] + " "); cad += "]"; System.out.println(cad);
*/	}
	//-------------------------------------------------------------	
	public void percent(int p) {
			// System.out.println("Porciento: " + p);
	}
	//-------------------------------------------------------------	
	public void getInfo(String cad) {
		System.out.println(cad);
	}
	@Override
	public void initOutput(String cad) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void write(String cad) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void closeOutput(int tot, String time) {
		// TODO Auto-generated method stub
		
	}
}
//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
class Ventana extends JFrame implements Output {
	long timeInit, timeEnd;
	InterfaceTT alg;
		
	String address = new String("D:\\Lias\\Dpto\\JAVA\\WorkSpace\\MB_30x50.MB");
	InterfaceTT []algorithm = new InterfaceTT[3];
	String []name = new String[3];
	String []filename = new String[3];
	public static String absolutePath;
	public static File generalfile;
	int i;
    //-------- Contenedor de la MB temporal del sistema -----	
	public Fast_BR matrix;

	//-------- Interfase ---- redireccion salida -----
	public Output salidaInfo, temp;
	Consola consola = new Consola();
	FileTool fileTool = new FileTool();	// file tool too.
	Ventana estaVentana = this;
	
	//------- Eventos ----------------
    private ActionListener abrirMB;
    private ActionListener guardarMB;
    private ActionListener salir;
    
    private ActionListener ejecutarAlgoritmo;
    
    private ActionListener abrirListaMB;
    private ActionListener guardarListaMB;
    private ActionListener agregarMB;
    private ActionListener limpiarLista;
    private ActionListener ejecutarScript;

    private ActionListener asignarNoProcesadores;
    private ActionListener direccionarSalida;
    
    private ActionListener procEditNewMB;
	//------- Componentes visuales ---
	TextArea areaTexto;
	TextField edit1;
	JFileChooser seleccion; // - Cuadro de dialodo Abrir/Guardar;
	DialogNumProcessor diag;       // - Cuadro de dialodo No. procesadores;
	DialogNewMatrix diagNewMatrix; // - Cuadro de dialodo Editor de matrices;
	DialogEditScript diagScript;   // - Cuadro de dialodo Editor de Scripts;
	 
    //--------------------------------------------------------------------------------
    // - 							CONSTRUCTOR 
	//--------------------------------------------------------------------------------
	Ventana() {
		    //thisWnd = this; 
			salidaInfo = temp = this;

			algorithm[0] = new Fast_BR();    name[0] = new String("Fast BR");
			//algorithm[4] = new BR_Parallel(2);    name[4] = new String("BR_Parallel     ");
			algorithm[1] = new LEX(); 		      name[1] = new String("LEX             ");
			algorithm[2] = new FAST_CT_EXT();     name[2] = new String("FAST_CT_EXT     ");
			
			filename[0] = new String("FastBR");
			//filename[4] = new String("BR_Parallel_");
			filename[1] = new String("LEX");
			filename[2] = new String("FastCT-EXT");
			
			DialogEditScript.ventana = this;
			
			//DialogEditScript.diagFile = 
			diagScript = new DialogEditScript();
			
			registrarEventos(); // - Obtener las implementaciones para los eventos.
			
			seleccion = new JFileChooser(new File("").getAbsolutePath());
			matrix = new Fast_BR();
			diag = new DialogNumProcessor();
			diagNewMatrix = new DialogNewMatrix();
	   	/*******************************************************************
	                  Inicializamos el modo grafico   	  
	   	*******************************************************************/
	      // definimos el titulo de la ventana
	      setTitle("Algoritmos para el calculo de los TT");
	      //Obtenemos las dimensiones de la pantalla
	      int ancho_pantalla = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	      int alto_pantalla = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;             
	      // definimos el tam de la ventana como la pantalla completa
	      setSize(ancho_pantalla/2, (int)(alto_pantalla/1.2));
	 	  // permitimos que se cierre la ventana
		  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	      setVisible(true); // indica que se muestre la ventana

	      //*************************************************************
	      // - MEMO 1
	      //*************************************************************
	      // creamos las areas de texto para mostrar los datos y objetos
	      areaTexto = new TextArea ();	      
	      Panel panareas = new Panel ();
	      panareas.setLayout (new GridLayout (1, 1));
	      panareas.add(areaTexto);
	      add(panareas, BorderLayout.CENTER);
	      
	   	/*******************************************************************
	                  Habilitamos el menu de opciones
	   	*******************************************************************/
	      // **************************************************************
	      // MENU -- ARCHIVO
	      // **************************************************************
	      Menu m_arch = new Menu("Archivo");
	      
	      MenuItem m_c_arch = new MenuItem("Abrir MB");// Submenu 
	      m_arch.add(m_c_arch); // lo agregamos como opcion de menu	      
	      m_c_arch.addActionListener(abrirMB); // le asignamos la accion al menu cargar-archivo
	      // Submenu cargar-objeto
	      MenuItem storeMB = new MenuItem("Guardar MB");// Submenu 
	      m_arch.add(storeMB); // lo agregamos como opcion de menu	      
	      storeMB.addActionListener(guardarMB); // le asignamos la accion al menu cargar-archivo
	      // Submenu cargar-objeto
	      MenuItem m_c_obj = new MenuItem("Salir");
	      m_arch.add(m_c_obj); // lo agregamos como opcion de menu
	      // le asignamos la accion al menu cargar-archivo
	      m_c_obj.addActionListener(salir);     
	      
	      // **************************************************************
	      // MENU -- ALGORITMOS PARA EL CLACULO DE TT
	      // **************************************************************
	      Menu m_proc = new Menu("Algoritmos TT");
      
	      // LISTA DE SUBMENUS.
	      for (i=0; i<algorithm.length; i++) {
		      MenuItem m_knn_proc = new MenuItem(name[i]);
		      m_proc.add(m_knn_proc); // lo agregamos como opcion de menu
		      // le asignamos la accion al menu la accion.
		      m_knn_proc.addActionListener(ejecutarAlgoritmo);
	      }
	      // **************************************************************
	      // MENU -- EXPERIMENTACION
	      // **************************************************************
	      Menu m_exp = new Menu("Experimentacion");
	      // Submenu
	      MenuItem mOpenList = new MenuItem("Abrir editor de Script");
	      m_exp.add(mOpenList);  mOpenList.addActionListener(abrirListaMB);
	      // Submenu
	      MenuItem mExecute = new MenuItem("Ejecutar Script");
	      m_exp.add(mExecute); mExecute.addActionListener(ejecutarScript);
	      // **************************************************************
	      // MENU -- CONFIGURACION
	      // **************************************************************
	      Menu m_conf = new Menu("Configuracion");
	      // Submenu 
	      MenuItem mNproc = new MenuItem("Asignar No de Procesadores");// Submenu 
	      m_conf.add(mNproc); // lo agregamos como opcion de menu	      
	      mNproc.addActionListener(asignarNoProcesadores); 	     
	      // Submenu 
	      Menu mDirecc = new Menu("Direccionar salida");// Submenu 
	      m_conf.add(mDirecc); // lo agregamos como opcion de menu	      
	      //mDirecc.addActionListener(asignarNoProcesadores); // TODO	     
		      // Sub - Submenu 
		      MenuItem m1 = new MenuItem("Consola");// Submenu 
		      mDirecc.add(m1); // lo agregamos como opcion de menu	      
		      m1.addActionListener(direccionarSalida); 
		      // Sub - Submenu 
		      MenuItem m2 = new MenuItem("Archivo");// Submenu 
		      mDirecc.add(m2); // lo agregamos como opcion de menu	      
		      m2.addActionListener(direccionarSalida); 
		      // Sub - Submenu 
		      MenuItem m3 = new MenuItem("Ventana");// Submenu 
		      mDirecc.add(m3); // lo agregamos como opcion de menu	      
		      m3.addActionListener(direccionarSalida); 
		      // **************************************************************
		      // MENU -- EDITOR
		      // **************************************************************
		      Menu edit_conf = new Menu("Editor MB");
		      // Submenu 
		      MenuItem editMB = new MenuItem("Editar nueva MB");// Submenu 
		      edit_conf.add(editMB); // lo agregamos como opcion de menu	      
		      editMB.addActionListener(procEditNewMB); 	     

	      // **************************************************************
	      // Habilitamos la barra de menus principal
	      MenuBar mb = new MenuBar();
	      setMenuBar(mb);
	      mb.add(m_arch);
	      mb.add(m_proc);
	      mb.add(m_exp);
	      mb.add(m_conf);
	      mb.add(edit_conf);
	   }

	
	   //------------------------------------------------------------------ 
	   // -           IMPLEMENTACION  DE   LOS   EVENTOS
	   //------------------------------------------------------------------ 
	   private void registrarEventos() {	    	  
	    	  //--------------------------------------------------------
	    	  // - Metodo que ejecuta el algoritmo seleccionado.
	    	  //--------------------------------------------------------
		      ejecutarAlgoritmo = new ActionListener() {
		          public void actionPerformed(ActionEvent evento) {
			        	BR_Parallel.numProcessors = diag.numprocessor;
		        	    for(i=0; i<name.length; i++)
		        		    if (name[i] == evento.getActionCommand())
		        		   	    break;
		        	    try {
					    	int fi, j, fil, col;
							alg = algorithm[i];
							//FileTool.readMatrix(address, alg);
							//Ventana.llenarMB(alg, matrix);
							//-----------------------------------------
					    	fil = matrix.getNumFilas();
					    	col = matrix.getNumColumnas();
					    	alg.set(fil, col);
					    	for (fi=0; fi<fil; fi++) {
					    		for (j=0; j<col; j++) { // - llenar matrix.
					    			alg.pushValor(matrix.getValor(fi, j), fi, j);
					    		}
					    	}	    	
							//--------------------
					    	
					    	if (salidaInfo == fileTool) {
					    		fileTool.openFile(Ventana.generalfile);
					    	}
					    	
					    	String nameRun = Ventana.this.getAlgFileName(i);
					    	salidaInfo.initOutput(nameRun);
					    	
							timeInit = System.currentTimeMillis();
							alg.extraerTT(salidaInfo);
							timeEnd = System.currentTimeMillis();
							
							salidaInfo.getInfo("\n"+ alg.getNumSoluciones() + " TT -> " + name[i] 
							        + " : " + fileTool.conv(timeEnd - timeInit)+"\n");
							
							
							if (salidaInfo == fileTool) 
								salidaInfo.getInfo("TT almacenados en : " + nameRun + "\n");		
							
								salidaInfo.closeOutput(alg.getNumSoluciones(), fileTool.conv(timeEnd - timeInit));
								
								fileTool.closeFile();
			        	    }		        	    
		        	    
		        	    finally {
		        	    	
		        	    }
		          }
		      };     
	    	  //--------------------------------------------------------
	    	  // - Metodo que abre una MB.
	    	  //--------------------------------------------------------
		      abrirMB = new ActionListener() {  
			         public void actionPerformed( ActionEvent evento ) {			        	 		
			        	 try {
			        		 if(seleccion.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			        			 File file = seleccion.getSelectedFile();
			        			 fileTool.readMatrix(file, matrix);	
			        			 iniFile();
			        			 salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo ");
			        			 salidaInfo.getInfo("\n  Matriz cargada"); 
					        	 salidaInfo.getInfo("\n  Dimension:"+matrix.getNumFilas() 
			        					 + "x" + matrix.getNumColumnas());
			        			 salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo \n");
			        			 endFile();
			        		 }
			        	 }
			        	 catch(Exception e) {
			        		 iniFile();
			        		    salidaInfo.getInfo("\n------ ERROR -------");
			        			salidaInfo.getInfo("\n" + e.getMessage());
			        		    salidaInfo.getInfo("\n--------------------\n");
			        		    endFile();
			        	 } // fin de try y catch	 
			         }
			        	 //address = edit1.getText();
			             //areadatos.setEditable(false); ya esta borrado...
			         
			      }; 
		    	  //--------------------------------------------------------
		    	  // - Metodo que guarda la MB activa.
		    	  //--------------------------------------------------------
			      guardarMB = new ActionListener() {  
				         public void actionPerformed( ActionEvent evento ) {			        	 		
				        	 try {
				        		 if(seleccion.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				        			 File file = seleccion.getSelectedFile();
				        			 fileTool.saveMatrix(file, matrix);	
				        			 iniFile();
				        			 salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo ");
				        			 salidaInfo.getInfo("\n  Matriz Guardada"); 
						        	 salidaInfo.getInfo("\n  Dimension:"+matrix.getNumFilas() 
				        					 + "x" + matrix.getNumColumnas());
				        			 salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo \n");
				        			 endFile();
				        		 }
				        	 }
				        	 catch(Exception e) {
				        		 iniFile();
				        		    salidaInfo.getInfo("\n------ ERROR -------");
				        			salidaInfo.getInfo("\n" + e.getMessage());
				        		    salidaInfo.getInfo("\n--------------------\n");
				        		    endFile();
				        	 } // fin de try y catch	 
				         }
				        	 //address = edit1.getText();
				             //areadatos.setEditable(false); ya esta borrado...
				         
				      }; 
	    	  //--------------------------------------------------------
	    	  // - Metodo para salir del programa.
	    	  //--------------------------------------------------------
			  salir =  new ActionListener() {  
			         public void actionPerformed( ActionEvent evento ) {
			        	 System.exit(0);
			         }
			  };
	    	  //--------------------------------------------------------
	    	  // - Metodo asignar el num proc para el metodo paralelo.
	    	  //--------------------------------------------------------
			  asignarNoProcesadores =  new ActionListener() {  
				         public void actionPerformed( ActionEvent evento ) {
				        	 diag.salida = salidaInfo;
				        	 diag.setVisible(true);				        	 
/*				        	 BR_Parallel.numProcessors = 
				        		 Integer.parseInt(edit1.getText());
				        	 BR_Parallel_1.numProcessors = 
				        		 Integer.parseInt(edit1.getText());
			        		 salidaInfo.getInfo("\n"+edit1.getText() + " Procesadores\n");
*/				        	 
				         }
				};
	    	  //--------------------------------------------------------
	    	  // - Metodo Direccionar la salida de los resultados.
	    	  //--------------------------------------------------------
			  direccionarSalida =  new ActionListener() {  
				         public void actionPerformed(ActionEvent evento ) {
				        	 String opcion = evento.getActionCommand();
				        	 iniFile();
		        			 salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo ");
		        			 salidaInfo.getInfo("\n Redirecionando la salida a: " + opcion);
		        			 salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo ");
		        			 endFile();
				        	 if (opcion == "Consola") {
				        		// if (salidaInfo == fileTool) fileTool.closeFile();
				        		 salidaInfo = consola;
				        	 }				        	 
				        	 if (opcion == "Ventana") {
				        		// if (salidaInfo == fileTool) fileTool.closeFile();
				        		 salidaInfo = temp;
				        	 }		
				        	 if (opcion == "Archivo") {
				        		// if (salidaInfo == fileTool) fileTool.closeFile();
				        		 JFileChooser select = new JFileChooser(new File("").getAbsolutePath());
				        		 if(seleccion.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				        			 //if (salidaInfo == fileTool) fileTool.closeFile();
				        			 Ventana.generalfile = seleccion.getSelectedFile();				        			 
				        			 
				        			 Path path = seleccion.getSelectedFile().toPath().getParent();
				        			 Ventana.absolutePath = path.toString();				        			 
				        			 if (path.getParent() != null)
										Ventana.absolutePath += "\\";
										
				        			 salidaInfo = fileTool;				        			 
				        		 }
				        	 }
				        	 
		        			 //salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo ");
		        			 //salidaInfo.getInfo("\n Iniciada nueva salida");
		        			 // salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo\n");				        
		        			}
				};
	    	  //--------------------------------------------------------
	    	  // - Metodo Editar nueva MB.
	    	  //--------------------------------------------------------
				procEditNewMB =  new ActionListener() {  
			         public void actionPerformed(ActionEvent evento ) {
			        	 diagNewMatrix.ventanaMadre = estaVentana ;
			        	 diagNewMatrix.salidaInfo = salidaInfo;
			        	 diagNewMatrix.fileTool = fileTool;
			        	 diagNewMatrix.setVisible(true); 
				     }
				};
				
	    	  //--------------------------------------------------------
	    	  // - Metodo Abrir editor de scipts.
	    	  //--------------------------------------------------------
			  abrirListaMB =  new ActionListener() {  
			         public void actionPerformed(ActionEvent evento ) {
			        	 diagScript.setVisible(true); 
			         }
			  };

	    	  //--------------------------------------------------------
	    	  // - Metodo Ejecutar script.
	    	  //--------------------------------------------------------
			  ejecutarScript =  new ActionListener() {  
			         public void actionPerformed(ActionEvent evento ) {
			        	 int i, j, k, q, contMB;
					     int fi, fil, col;
			        	 String dirMB = new String();
			        	 contMB = diagScript.contMB;
			        	 for (i=0; i<contMB; i++) {				// - para cada Matriz
			        		 dirMB = diagScript.ordenMB[i];
			        		 File file = new File(dirMB);
			        		 fileTool.readMatrix(file, matrix);	
			        		 
			        		 
			        		 iniFile();
		        			 salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo ");
		        			 salidaInfo.getInfo("\n  Matriz cargada"); 
				        	 salidaInfo.getInfo("\n  Dimension:"+matrix.getNumFilas() 
		        					 + "x" + matrix.getNumColumnas());
		        			 salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo \n");
		        			 endFile();
// - asegurarme de no dejar getInfoLibre....
		        			 
		        			 for (j = 1; j <= name.length; j++) { // - para cada algoritmo.
				        	    for(k=0; k<name.length; k++)
				        		    if (diagScript.ordenEjecucion[k] == j)
				        		   	    break;	
				        	    if (k == name.length) continue;
					        	BR_Parallel.numProcessors = diag.numprocessor;
				        	    try {
									alg = algorithm[k];
									//FileTool.readMatrix(address, alg);
									//Ventana.llenarMB(alg, matrix);
									//-----------------------------------------
							    	fil = matrix.getNumFilas();
							    	col = matrix.getNumColumnas();
							    	alg.set(fil, col);
							    	for (fi=0; fi<fil; fi++) {
							    		for (q=0; q<col; q++) { // - llenar matrix.
							    			alg.pushValor(matrix.getValor(fi, q), fi, q);
							    		}
							    	}	    	
									//--------------------
							    	
							    	if (salidaInfo == fileTool) {
							    		fileTool.openFile(Ventana.generalfile);
							    	}

							    	
							    	String nameRun = Ventana.this.getAlgFileName(k);
							    	salidaInfo.initOutput(nameRun);
							    	
									timeInit = System.currentTimeMillis();
									alg.extraerTT(salidaInfo);
									timeEnd = System.currentTimeMillis();
									
									
									salidaInfo.closeOutput(alg.getNumSoluciones(), fileTool.conv(timeEnd - timeInit));
									
									salidaInfo.getInfo("\n"+ alg.getNumSoluciones() + " TT -> " + name[i] 
									        + " : " + fileTool.conv(timeEnd - timeInit)+"\n");
									
									if (salidaInfo == fileTool) {
										salidaInfo.getInfo("TT almacenados en : " + nameRun + "\n");
										salidaInfo.closeOutput(alg.getNumSoluciones(), fileTool.conv(timeEnd - timeInit));										
										fileTool.closeFile();
									}
					        	}
				        	    finally {
				        	    	
				        	    }
		        			 }		        		 
			        	 }
				     }
				};	
	    }	
	    // -------------------------------------------------------------
	   public void iniFile() {
		   if (salidaInfo == fileTool) {
			   fileTool.openFile(Ventana.generalfile);
		   }
		   
	   }
	    // -------------------------------------------------------------
	   public void endFile() {
		   if (salidaInfo == fileTool) {
			   fileTool.closeFile();
		   }
	   }
			    // -------------------------------------------------------------
			   private String getAlgFileName(int idx) {
			DateFormat dateFormat = new SimpleDateFormat("--dd_mm_yyyy--HH_mm_ss_SSS");
			Date date = new Date();
			String yourDate = dateFormat.format(date);

	    	return absolutePath+filename[idx] + yourDate + ".TT";
	   }
	    // -------------------------------------------------------------
	    //  ASIGANCION DE LA MB A LOS ALGORITMOS
	    // -------------------------------------------------------------
	    private static void llenarMB(InterfaceTT alg, Fast_BR mat) {
	    	int i, j, fil, col;
	    	fil = mat.getNumFilas();
	    	col = mat.getNumColumnas();
	    	alg.set(fil, col);
	    	for (i=0; i<fil; i++) {
	    		for (j=0; i<col; j++) { // - llenar matrix.
	    			alg.pushValor(mat.getValor(i, j), i, j);
	    		}
	    	}	    	
	    }
	    // -------------------------------------------------------------
	    //        IMPLEMENTACION    DE    LA   ENTRADA
	    // -------------------------------------------------------------
	    // Metodos para la interfase de salida....
	    // Estos metodos manipulan la informacion extraida de los algs 
	    // para ser mostrada en el cuadro de texto....
	    // **************************************************************
		//-------------------------------------------------------------
		public void resgistTT(int[] tt) {
/*			int i; 
			String cad = "[ "; 
			for (i = 1; i < tt[0] + 1; i++) 
				cad  += (tt[i] + " "); cad += "]\n"; 
			areaTexto.append(cad);*/
		}
		//-------------------------------------------------------------	
		public void percent(int p) {
				// areaTexto.append("Porciento: " + p);
		}
		//-------------------------------------------------------------	
		public void getInfo(String cad) {
			areaTexto.append(cad);
		}


		@Override
		public void initOutput(String cad) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void write(String cad) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void closeOutput(int tot, String time) {
			// TODO Auto-generated method stub
			
		}
	  
	} // fin de la clase Ventana
//--------------------------------------------------------------------
//--------------------------------------------------------------------
//--------------------------------------------------------------------
//--------------------------------------------------------------------
//--------------------------------------------------------------------
//--------------------------------------------------------------------
//--------------------------------------------------------------------
class DialogNumProcessor extends JFrame {
	public int numprocessor = 2;
	public boolean aceptar;
	TextField edit1;
	Button button1;
	DialogNumProcessor este;
	Output salida;
	//--------------------
	DialogNumProcessor() {
		aceptar = false;
		este = this;
		/*******************************************************************
		        Inicializamos el modo grafico   	  
		*******************************************************************/
		// definimos el titulo de la ventana
		setTitle("Introduzca el número de procesadores.");
		// definimos el tam de la ventana como la pantalla completa
		setSize(320, 50);
		setLocation(100, 100);
		setVisible(false); // indica que se muestre la ventana
		
		//*************************************************************
		// - EDIT 1
		//*************************************************************	      
		edit1 = new TextField ();
		button1 = new Button ("Aceptar"); 	      
		edit1.setText("2");
		// contenedor para el area parametros
		Panel pan_param = new Panel ();
		pan_param.setLayout (new GridLayout (1, 2));
		pan_param.add(edit1);
		pan_param.add(button1);
		button1.addActionListener(new ActionListener() {  
	         public void actionPerformed(ActionEvent evento) {
	        	 numprocessor = Integer.parseInt(edit1.getText());	
	        	 aceptar =  true;
	        	 este.setVisible(false);
	        	 
        		 salida.getInfo("\nooooooooooooooooooooooooooooooooooo");
        		 salida.getInfo("\n"+ numprocessor+ " Procesadores");
        		 salida.getInfo("\nooooooooooooooooooooooooooooooooooo");

	         }			
		});		    
		// lo agregamos al oeste del contenedor
		add(pan_param, BorderLayout.NORTH); 
	}

}
