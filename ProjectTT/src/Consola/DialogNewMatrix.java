package Consola;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import edu.tt.Output;
import edu.tt.alghoritms.br_colums_CH.Fast_BR;

public class DialogNewMatrix extends JFrame {	
	public int numprocessor = 2;
	public boolean aceptar;
	Label text1;
	TextField textNumFilas,textNumColumnas;
	Button button1, button2, button3;
	Scrollbar dispIniVal, dispFinVal, distVal, contVal, size, columnRedunt;
	Label LdispIniVal, LdispFinVal, LdistVal, LcontVal, LcolRedund;
	Panel imgPanel = new Panel ();
	public FileTool fileTool;
	// ----- Eventos
	AdjustmentListener adjustScrolldispIniVal, adjustScrolldispFinVal, 
					   adjustScrolldistVal, adjustScrollcontVal, adjustScrollsize,
					   adjustScrollcolRedund;
	ActionListener clickAceptar, clickCancelar, clickGuardar;
	int sizePaint = 3;

	JFileChooser seleccion; // - Cuadro de dialodo Abrir/Guardar;
	DialogNewMatrix este;
	Output salida;
	//-------------------------
	int fil, col;
	int [][]matrix;
	Fast_BR matrixBR = new Fast_BR();
	public Output salidaInfo, temp;
	public Ventana ventanaMadre;
	
	//--------------------------------------------------------------------
	DialogNewMatrix () { // CONSTRUCTOR
		setEvents();
		initInterfase();
		generteMatrix();
		paintMatrix();
	}
	//--------------------------------------------------------------------
	private void initInterfase() {
		//--------------------
		aceptar = false;
		este = this;
		/*******************************************************************
		        Inicializamos el modo grafico   	  
		*******************************************************************/
		// definimos el titulo de la ventana
		setTitle("Crear nueva matriz básica");
		// definimos el tam de la ventana como la pantalla completa
		setSize(700, 350);
		setLocation(100, 100);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setVisible(true); // indica que se muestre la ventana
		this.setLayout (new BorderLayout ());
		
		setVisible(false); // indica que no se muestre la ventana
		
		seleccion = new JFileChooser(new File("alias").getAbsolutePath());
		
		//*************************************************************
		// - SCROLLS BARS 
		//*************************************************************	      
		//text10 = new Label("Muestra en pixeles de la MB");		
		textNumFilas = new TextField(); textNumFilas.setText("100");
		textNumColumnas = new TextField(); textNumColumnas.setText("100");
		dispIniVal  = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, 10);
		dispIniVal.addAdjustmentListener(adjustScrolldispIniVal);
		dispIniVal.setValue(4);
		dispFinVal  = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, 10);
		dispFinVal.addAdjustmentListener(adjustScrolldispFinVal);
		dispFinVal.setValue(4);
		distVal     = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, 10);
		distVal.addAdjustmentListener(adjustScrolldistVal);
		distVal.setValue(4);
		contVal    = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, 10); 
		contVal.addAdjustmentListener(adjustScrollcontVal);
		contVal.setValue(4);
		size    = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 3, 10); 
		size.addAdjustmentListener(adjustScrollsize);

		columnRedunt  = new Scrollbar(Scrollbar.HORIZONTAL, 0, 3, 0, 50); 
		columnRedunt.addAdjustmentListener(adjustScrollcolRedund);
		
		LdispIniVal = new Label("5"); LdispIniVal.setSize(3,1);
		LdispFinVal = new Label("5"); LdispFinVal.setSize(3,1);
		LdistVal    = new Label("5"); LdistVal.setSize(3,1);
		LcontVal    = new Label("5"); LcontVal.setSize(3,1);
		LcolRedund    = new Label("0"); LcolRedund.setSize(3,1);
		text1 = new Label("");  text1.setSize(3,1);
		
		//text9 = new Label("lllllll");
		
		button1 = new Button ("Aceptar");  button1.addActionListener(clickAceptar);	       
		button2 = new Button ("Cancelar"); button2.addActionListener(clickCancelar);
		button3 = new Button ("Guardar");  button3.addActionListener(clickGuardar);
		// contenedor para el area parametros
		Panel panel = new Panel ();
		panel.setLayout (new GridLayout (8, 3));
		panel.add(new Label("Filas"));            panel.add(textNumFilas);    panel.add(text1);			
		panel.add(new Label("Columnas"));         panel.add(textNumColumnas); panel.add(size);			
		panel.add(new Label("Dispersion inicial")); panel.add(dispIniVal);    panel.add(LdispIniVal);			
		panel.add(new Label("Dispersion final")); panel.add(dispFinVal);      panel.add(LdispFinVal);			
		panel.add(new Label("Distribucion"));     panel.add(distVal);         panel.add(LdistVal);			
		panel.add(new Label("Contraste"));        panel.add(contVal);	      panel.add(LcontVal);			
		panel.add(new Label("% de colum redundantes"));  panel.add(columnRedunt);	panel.add(LcolRedund);			
		panel.add(button1);                       panel.add(button2);         panel.add(button3);	
		imgPanel.setLayout (new GridLayout (1, 1));
		add(imgPanel, BorderLayout.CENTER);
		//imgPanel.add(text9);
		add(panel, BorderLayout.WEST);
		//add(text9, BorderLayout.NORTH);		
		// contenedor para el area de la matriz; 
		
		button1.addActionListener(new ActionListener() {  
	         public void actionPerformed(ActionEvent evento) {
	        	// DialogNewMatrix.this.test();
	         }			
		});		    
			// lo agregamos al oeste del contenedor
//			add(pan_param, BorderLayout.NORTH); 
		}
    //------------------------------------------------------------------------------		
	protected void generteMatrix() {
		int i, j, valRnd, val=0, k=0;
		float pi=0, dI=0, dF=0, dif=0, kkk=0;
		Random rnd = new Random();
		int dipInic = Integer.parseInt(LdispIniVal.getText());
		int dipFin = Integer.parseInt(LdispFinVal.getText());
		int distribucion = Integer.parseInt(LdistVal.getText());
		int constraste = Integer.parseInt(LcontVal.getText());
		int porcCol = Integer.parseInt(LcolRedund.getText());
		fil = Integer.parseInt(textNumFilas.getText());
		col = Integer.parseInt(textNumColumnas.getText());
		
/*		dI = dipInic<=dipFin ? (float)dipInic/10 : (float)dipFin/10;
		dF = dipInic>=dipFin ? (float)dipInic/10 : (float)dipFin/10;
*/		dI = (float)dipInic/10 ;
		dF = (float)dipFin/10;
		dif = dF-dI;
				
		matrixBR.set(fil, col);
		for (i=0; i<fil; i++) {
			for (j=0; j<col; j++) {
				pi = i*100/fil;
				valRnd = (int) (pi*10/100);
				k = 100;
				kkk = dI+((dF-dI)*j/col);
				val = rnd.nextInt(k) < k*kkk ? 0 : 1;
				matrixBR.pushValor(val, i, j);
			}
		}
		// - Repetir algunas de las columnas
		if (porcCol > 0) {
			int porc, p1, p2; 			
			porc = (porcCol*col)/100;
			boolean[] band = new boolean[col];
			for (i=0; i<col; i++) band[i] = false;
			for (i=0; i<porc; i++) {
				p1 = rnd.nextInt(col);
				do {
					p2 = rnd.nextInt(col);
				} while(band[p2] == true || p1 == p2);
				for (j=0; j<fil; j++) {
					matrixBR.pushValor(matrixBR.getValor(j, p1), j, p2);					
				}
				band[p2] = true;
			}			
		}
		
		matrixBR.extraerMB();
		
		// TODO Auto-generated method stub		
	}
	//--------------------------------------------------------------------
	@Override
	public void repaint() {
		paintMatrix();		
	};
	//--------------------------------------------------------------------
	private void paintMatrix() {
		// TODO Auto-generated method stub
		int fact = sizePaint;
		int i, j;
		Graphics gr = imgPanel.getGraphics();
		int fila = matrixBR.getNumFilas();
		int colu = matrixBR.getNumColumnas();
		
		text1.setText("Dim MB ("+fila+"x"+colu+")");
		gr.clearRect(0, 0, 1000, 1000);
		
		for (i=0; i<fila; i++) {
			for (j=0; j<colu; j++) {
				if (matrixBR.getValor(i, j) == 0) 
					gr.setColor(Color.black);
				else 
					gr.setColor(Color.white);
				gr.fillRect(j*fact,i*fact,fact-1,fact-1);
				
			}
		}	
	}
	//------------------------------------------------------------
	private void setEvents() {
			// TODO Auto-generated method stub
			adjustScrolldispIniVal = new AdjustmentListener() {  
				public void adjustmentValueChanged(AdjustmentEvent event) {
					int a = event.getValue();
					DialogNewMatrix.this.LdispIniVal.setText(String.valueOf(a+1));
					DialogNewMatrix.this.generteMatrix();
					DialogNewMatrix.this.paintMatrix();					
				}
		    };
		    adjustScrolldispFinVal = new AdjustmentListener() {  
				public void adjustmentValueChanged(AdjustmentEvent event) {
					int a = event.getValue();
					DialogNewMatrix.this.LdispFinVal.setText(String.valueOf(a+1));				
					DialogNewMatrix.this.generteMatrix();
					DialogNewMatrix.this.paintMatrix();					
				}
		    };
		    adjustScrolldistVal = new AdjustmentListener() {  
				public void adjustmentValueChanged(AdjustmentEvent event) {
					int a = event.getValue();
					DialogNewMatrix.this.LdistVal.setText(String.valueOf(a+1));				
					DialogNewMatrix.this.generteMatrix();
					DialogNewMatrix.this.paintMatrix();					
				}
		    };
		    adjustScrollcontVal = new AdjustmentListener() {  
				public void adjustmentValueChanged(AdjustmentEvent event) {
					int a = event.getValue();
					DialogNewMatrix.this.LcontVal.setText(String.valueOf(a+1));				
					DialogNewMatrix.this.generteMatrix();
					DialogNewMatrix.this.paintMatrix();					
				}
		    };
		    adjustScrollcolRedund = new AdjustmentListener() {  
				public void adjustmentValueChanged(AdjustmentEvent event) {
					int a = event.getValue();
					DialogNewMatrix.this.LcolRedund.setText(String.valueOf(a+1));				
					DialogNewMatrix.this.generteMatrix();
					DialogNewMatrix.this.paintMatrix();					
				}
		    };
		    adjustScrollsize = new AdjustmentListener() {  
				public void adjustmentValueChanged(AdjustmentEvent event) {
					sizePaint = event.getValue();
					DialogNewMatrix.this.paintMatrix();					
				}
		    };
			clickAceptar = new ActionListener() {  
		         public void actionPerformed(ActionEvent evento) {
		        	 iniFile();
        			 salida.getInfo("\n oooooooooooooooooooooooooooooooooooooooo ");
        			 salida.getInfo("\n  Matriz Generada"); 
		        	 salida.getInfo("\n  Dimension:"+matrixBR.getNumFilas() 
        					 + "x" + matrixBR.getNumColumnas());
        			 salida.getInfo("\n oooooooooooooooooooooooooooooooooooooooo \n");
        			 endFile();
        			 ventanaMadre.matrix.operatorIgual(matrixBR);
        			 setVisible(false);
		        	 
		        	// DialogNewMatrix.this.test();
		         }			
			};		    
			clickCancelar = new ActionListener() {  
		         public void actionPerformed(ActionEvent evento) {
		        	setVisible(false);
		        	// DialogNewMatrix.this.test();
		         }			
			};		    
			clickGuardar = new ActionListener() {  
		         public void actionPerformed(ActionEvent evento) {
		        	 try {
		        		 if(seleccion.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		        			 File file = seleccion.getSelectedFile();
		        			 fileTool.saveMatrix(file, DialogNewMatrix.this.matrixBR);	
		        			 iniFile();
		        			 salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo ");
		        			 salidaInfo.getInfo("\n  Matriz Guardada"); 
				        	 salidaInfo.getInfo("\n  Dimension:" +matrixBR.getNumFilas() 
		        					 + "x" + DialogNewMatrix.this.matrixBR.getNumColumnas());
		        			 salidaInfo.getInfo("\n oooooooooooooooooooooooooooooooooooooooo \n");
		        			 endFile();
		        		 }
		        	 }
		        	 catch(Exception e) {		        		    
		        		    salidaInfo.getInfo("\n------ ERROR -------");
		        			salidaInfo.getInfo("\n" + e.getMessage());
		        		    salidaInfo.getInfo("\n--------------------\n");
		        	 } // fin de try y catch	 
		        	// DialogNewMatrix.this.test();
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

}
