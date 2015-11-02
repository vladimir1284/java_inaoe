package Consola;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.GridLayout;
import java.awt.ItemSelectable;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class DialogEditScript extends JFrame {
	
	static public Ventana ventana;
	public int numAlg;
	static public JFileChooser diagFile; // - Cuadro de dialodo Abrir/Guardar;
	FileTool fileTool = new FileTool();
	TextArea areaTexto;
		
	public int ordenEjecucion[];
	public int contMB;
	public String ordenMB[];
	Label []labelNum, nameAlg;
	Checkbox []checkList;
	// ----- Eventos	
	ItemListener actionSelect;
	
	private ActionListener abrirListaMB, guardarListaMB, agregarMB, limpiarLista;

	//--------------------------------------------------------------------
	DialogEditScript () { // CONSTRUCTOR
		setEvents();
		initInterfase();
	}

	private void initInterfase() {
		
		//--------------------
		int i,j;
		
		diagFile = new JFileChooser(new File("alias").getAbsolutePath());
		
		/*******************************************************************
		        Inicializamos el modo grafico   	  
		*******************************************************************/
		// definimos el titulo de la ventana 
		setTitle("Editor de Scripts");
		// definimos el tam de la ventana como la pantalla completa
		setSize(700, 350);
		setLocation(100, 100);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setLayout (new BorderLayout ());
		
		setVisible(false); // indica que no se muestre la ventana
		//------------------------------------------------------------------
		numAlg = ventana.algorithm.length;
		labelNum = new Label[numAlg];
		nameAlg = new Label[numAlg];
		checkList = new Checkbox[numAlg];
		ordenEjecucion = new int[numAlg];
		ordenMB = new String[50];
		contMB = 0;
		
		for (i=0; i<numAlg; i++) {
			labelNum[i] = new Label("?");
			nameAlg[i] = new Label(ventana.name[i]);
			checkList[i] = new Checkbox(String.valueOf(i)); 
			checkList[i].addItemListener(actionSelect);
			ordenEjecucion[i] = 0;
		}
		Panel panel = new Panel ();
		panel.setLayout (new GridLayout (numAlg, 3));		
		for (i=0; i<numAlg; i++) {
			panel.add(checkList[i]);
			panel.add(labelNum[i]);
			panel.add(nameAlg[i]);
		}
		add(panel, BorderLayout.NORTH);
		  areaTexto = new TextArea ();	
		  Panel panareas = new Panel ();
	      panareas.setLayout (new GridLayout (1, 1));
	      panareas.add(areaTexto);
	      add(panareas, BorderLayout.CENTER);
	      areaTexto.setEditable(false);		
		
	      Menu m_exp = new Menu("Script Tools");
	      // Submenu
	      MenuItem mOpenList = new MenuItem("Abrir lista");
	      m_exp.add(mOpenList);  mOpenList.addActionListener(abrirListaMB);
	      // Submenu
	      MenuItem mSaveList = new MenuItem("Guardar lista");
	      m_exp.add(mSaveList); mSaveList.addActionListener(guardarListaMB);
	      // Submenu
	      MenuItem mAgMB = new MenuItem("Agregar MB a la lista");
	      m_exp.add(mAgMB); mAgMB.addActionListener(agregarMB);
	      // Submenu
	      MenuItem mClearList = new MenuItem("Limpiar lista");
	      m_exp.add(mClearList); mClearList.addActionListener(limpiarLista);
	      // Submenu
	
	      MenuBar mb = new MenuBar();
	      setMenuBar(mb);
	      mb.add(m_exp);
	}
	//------------------------------------------------------------
	private void setEvents() {
		//------------------------------------------------------
		//-- Evento de seleccionar cualquier Check Box.
		//------------------------------------------------------
		actionSelect = new ItemListener() {		
			public void itemStateChanged(ItemEvent event) {
				int idx, i, max = 0, val;
				
				// - Obtener el indice del Item seleccionado.
				idx = Integer.parseInt(event.getItem().toString());
				
				if (event.getStateChange() == event.SELECTED) {
					for (i=0; i<numAlg; i++)
						if (ordenEjecucion[i] > max) max = ordenEjecucion[i];
					ordenEjecucion[idx] = max + 1;
				}
				else { // - event.DESELECTED;
					val = ordenEjecucion[idx];
					ordenEjecucion[idx] = 0;
					for (i=0; i<numAlg; i++)
						if (ordenEjecucion[i] > val)
							ordenEjecucion[i] = ordenEjecucion[i]-1;
				}	
				
				for (i=0; i<numAlg; i++)
					if (ordenEjecucion[i] == 0)
						labelNum[i].setText(" ");
					else labelNum[i].setText(String.valueOf(ordenEjecucion[i]));					
				
/*				areaTexto.append(String.valueOf(a));
				areaTexto.append("\n");
*/			}
		};		
		//------------------------------------------------------
		// - Abrir configuracion de Sacript
		//------------------------------------------------------
		abrirListaMB =  new ActionListener() {  
	         public void actionPerformed( ActionEvent evento ) {	        	 
	        	 int []nAlg, cMB;
	        	 nAlg = new int[1]; 
	        	 cMB = new int[1];
	        	 int i;
	        	 try {
	        		 if(diagFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	        			 File file = diagFile.getSelectedFile();
	        			 fileTool.readConfScript(file, ordenEjecucion, nAlg, ordenMB, cMB);
	        			 numAlg = nAlg[0];
	        			 contMB = cMB[0];
	     				 for (i=0; i<numAlg; i++)
	    					if (ordenEjecucion[i] == 0)
	    						labelNum[i].setText(" ");
	    					else labelNum[i].setText(String.valueOf(ordenEjecucion[i]));					
	     				 for (i=0; i<contMB; i++) {
		        			 areaTexto.append(ordenMB[i]);	        			 
		        			 areaTexto.append("\n");	        			 
	     				 }	     					 
	        		 }
	        	 }
	        	 catch(Exception e) {
	        	 } // fin de try y catch	 
			 }
		};
		//------------------------------------------------------
		// - Guardar configuracion de Script
		//------------------------------------------------------
		guardarListaMB =  new ActionListener() {  
	         public void actionPerformed( ActionEvent evento ) {
	        	 try {
	        		 if(diagFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	        			 File file = diagFile.getSelectedFile();
	        			 fileTool.writeConfScript(file, ordenEjecucion, numAlg, ordenMB, contMB);
	        		 }
	        	 }
	        	 catch(Exception e) {
	        	 } // fin de try y catch	 
	         }
		};
		//------------------------------------------------------
		// - Agregar MB a la selccion actual
		//------------------------------------------------------
		agregarMB =  new ActionListener() {  
	         public void actionPerformed( ActionEvent evento ) {
	        	 try {
	        		 if(diagFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	        			 File file = diagFile.getSelectedFile();
	        			 ordenMB[contMB] = new String(file.getPath());
	        			 contMB++;
	        			 areaTexto.append(file.getPath());	        			 
	        			 areaTexto.append("\n");	        			 
	        		 }
	        	 }
	        	 catch(Exception e) {
	        	 } // fin de try y catch 	        	 
	         }
		};
		//------------------------------------------------------
		// - setear a 0 toda la configuracion.
		//------------------------------------------------------
		limpiarLista =  new ActionListener() {  
	         public void actionPerformed( ActionEvent evento ) {
	        	 int i;
	        	 for (i=0; i<numAlg; i++) {
	        		 ordenEjecucion[i] = 0;
	        		 checkList[i].setState(false);
	        	 }
				for (i=0; i<numAlg; i++)
						labelNum[i].setText("?");

	        	 contMB = 0;
	        	 areaTexto.replaceRange("", 0, 200000000);
	         }
		};


		
		//, guardarListaMB, agregarMB, ;
	}
}
