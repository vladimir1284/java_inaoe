package edu.tt.alghoritms.lex;

import edu.tt.InterfaceTT;
import edu.tt.Output;

public class LEX implements InterfaceTT {
    private int numFilas, numColumnas, posUltColumUnitaria;
    private int numTestores;
    private int [] tt;
    private short [][] matriz; // **
    private short [][] crl;// **
    private int [] Fxl;
    private int [] AFxl;
    private int [] proxFxl;
    private int [] ordenFilas;
    private short [] ordenColumnas;
    private int [] solucion;
    //---------------------------------------------------------------------------
    public LEX() {
		super();		
	}
    //---------------------------------------------------------------------------
    public void set(int filas, int columnas) {
      int i;
      numFilas =  filas;
      numColumnas =  columnas;
      matriz = new short [numFilas][];
      crl = new short [numFilas][];
      Fxl = new int [numColumnas];
      AFxl = new int [numColumnas];
      proxFxl = new int [numColumnas];
      tt = new int [numColumnas];
      ordenFilas = new int[numFilas];
      ordenColumnas = new short[numColumnas];
      for (i=0; i<numFilas; i++) {
        matriz[i] = new short[numColumnas];
        crl[i] = new short[numColumnas];
        ordenFilas[i] = i;
      }
      for (i=0; i<numColumnas; i++) {
        ordenColumnas[i] = (short)i;
        Fxl[i] = 0;
      }
      solucion = new int[columnas];
    }
    //---------------------------------------------------------------------------
    public void pushValor(int valor, int fila, int columna) {
      matriz[fila][columna] = (short)valor;
    }
    //---------------------------------------------------------------------------
    public void extraerTT(Output registerData) {
      int progreso;
      int indexL, razgo_x;;
      int []temp1;
      boolean band;
      ReturnStruct ret;
      numTestores = 0;
	  registerData.getInfo("\n *************************************** ");
	  registerData.getInfo("\n   ALGORITMO --  LEX ");
	  registerData.getInfo("\n ***************************************\n");
      
      //****************************************************************** Paso #1;
      moverFilas();                                 //******* a)
      moverColumnas();                              //******* b)
      progreso = 0;
      //****************************************************************** Paso #2;
      indexL = -1; razgo_x = 0;
      //****************************************************************** Paso #3;
      paso_3:
      while (true) {
    	  while (true) {
		      band = true;
		      if (indexL == -1)
		        if (matriz[0][razgo_x]==0)   //******* a)
		          break paso_3; // goto fin;
		        else {
		          progreso++;
		          //- porciento de avance del algoritmo.
		          registerData.percent((progreso+1)*100/posUltColumUnitaria);		          
		        }
		      if (esExcluyente( indexL,  razgo_x) == true) { //******* b)
		        band = false;
		        break; //goto paso_4;
		      }
		      if (condicion_3c( indexL,  razgo_x) == true) { //******* c)
		        registrarTT(registerData,  indexL,  razgo_x);
		        //numTestores++;
		        band = true;
		        break; //goto paso_4;
		      }
		      if (razgo_x == numColumnas-1)                //******* d)
		    	break; //goto paso_4;
		      indexL++;                                    //******* e)
		      temp1 = Fxl;
		      Fxl = proxFxl;
		      proxFxl = temp1;
		      break;
    	  }
		  //****************************************************************** Paso #4;
	      //paso_4:
	      while (true) {  
		      if (razgo_x < numColumnas-1) {   //******* a)
		        razgo_x++;
		        break; //goto paso_3;
		      }
		      if (indexL == -1)                //******* b)
		    	  break paso_3; // goto fin;
		      if (band == true) {              //******* c)
		    	ret = proxRasgo( indexL,  razgo_x); indexL = ret.v1; razgo_x = ret.v2;  
		        if (ret.band == false)
		        	break paso_3; // goto fin;
		        break; //goto paso_3;
		      }
		      else {
		    	if (numTestores == 729) {
		        	  int kkk;
		        	  kkk = 45;
		    	}  
		    	ret = condicion_4d(indexL,  razgo_x); 
		    	indexL = ret.v1; 
		    	razgo_x = ret.v2;
		        continue; //goto paso_4;
		      }		      
	      }
      }
	  //fin:
    }
    //---------------------------------------------------------------------------
    private void moverFilas() {  //******* 1 .a)
      int i, j, filaSel=0, cont, minFila = numColumnas;
	  short temp;
      //------------------------------
      for (i=0; i<numFilas; i++) {
        cont =0;
        for (j=0; j<numColumnas; j++) {
           if (matriz[i][j] == 1) cont++;
        }
        if (cont < minFila) {
          minFila = cont; filaSel = i;
        }
      }
      ordenFilas[0] = filaSel; // - Intercambiamos a la primera fila.
      ordenFilas[filaSel] = 0;
      for (i=0; i<numColumnas; i++) {
        temp = matriz[0][i];
        matriz[0][i] = matriz[filaSel][i];
        matriz[filaSel][i] = temp;
      }
    }
    //---------------------------------------------------------------------------
    private void moverColumnas() {  //******* 1 .- b)
      int i, j, p;
	  short temp;
      //------------------------
      p = 0;
      for (i=0; i<numColumnas; i++) {
        if (matriz[0][p] == 1)
          p++;
        else
          if (matriz[0][i] == 1) {
            temp = ordenColumnas[p];
            ordenColumnas[p] = ordenColumnas[i];
            ordenColumnas[i] = temp;
            for (j=0; j<numFilas; j++) {
              temp = matriz[j][p];
              matriz[j][p] = matriz[j][i];
              matriz[j][i] = temp;
            }
            p++;
          }
      }
      posUltColumUnitaria = p;
    }
    //---------------------------------------------------------------------------
    private boolean esExcluyente(int indexL, int razgo_x) {
      int i,j;
      boolean prop2 = true;
      //----------
      for (i=0; i<=indexL; i++) AFxl[i] = 0;

      if (indexL == -1)  return false;
      for (i=0; i<numFilas; i++) {
        if (matriz[i][razgo_x] == 1) {   // (1) // <----***** ver
          if (crl[i][indexL] == 1)       // (2)
            for (j=0; j<=indexL; j++) {
              if (matriz[i][tt[j]] == 1) {
                 AFxl[j]++;
                  if (AFxl[j] >= Fxl[j]) return true;   // (3) - Nueva proposi_
                 break;                   //cion: ver (Explicación sobre el LEX.txt)
              }
            }
          else
            if (crl[i][indexL] == 0)
              prop2 = false;  // - Proposicion 2 no se cumple.
        }
      }
      return prop2;
    }
    //---------------------------------------------------------------------------
    private boolean condicion_3c(int indexL, int razgo_x) {
      int i,j; int k;
      boolean band = true;
      //----------
      tt[indexL+1] = razgo_x;
      proxFxl[indexL+1] = 0;
      if (indexL == -1) {
        for (i=0; i<numFilas; i++) {
          k = matriz[i][razgo_x];
          crl[i][0] = (short)k;
          if(k == 1) proxFxl[0]++;
        }
        if (proxFxl[0] == numFilas)
          return true;
        return false;
      }
      for (i=0; i<=indexL; i++) proxFxl[i] = Fxl[i];
      for(i=0; i<numFilas; i++) {
        k =  crl[i][indexL] + matriz[i][razgo_x];
        crl[i][indexL+1] = (short)k;
        if (k == 0) band = false;
        if (k==1 && matriz[i][razgo_x]==1) proxFxl[indexL+1]++;
        if (k==2 && matriz[i][razgo_x]==1) {
          for (j=0; j<=indexL; j++)
            if (matriz[i][tt[j]] == 1) {
              proxFxl[j]--;
            }
        }
      }
      return band;
    }
    //---------------------------------------------------------------------------
    private void registrarTT(Output registerData, int indexL, int razgo_x) {
      int i;
      if (numTestores < 1000000) {     // <*****
        tt[++indexL] = razgo_x;  // - push(m, S)
        solucion[0] =  (indexL+1);
        for (i=1; i<=indexL+1; i++) {       
          solucion[i]  = (ordenColumnas[tt[i-1]]+1);
        }
        registerData.resgistTT(solucion);
      }   // <<***
      numTestores++;
    }
    //---------------------------------------------------------------------------
    private ReturnStruct proxRasgo(int indexL, int razgo_x) {
      int i, j;
      ReturnStruct ret = new ReturnStruct();
      //----------                        // 123456
      for (i=indexL; i>=0; i--) {    // - Buscar hueco.
        if (tt[i]+1 < tt[i+1]) {
          razgo_x = (tt[i]+1);
          indexL = i-1;
          break;
        }
      }
      ret.v1 = indexL;
      ret.v2 = razgo_x;
      ret.band = true; 
      if (i == -1) {
        ret.band = false;  return ret;  // - No existe Xp (hueco).
      }
      if (indexL == -1) return ret;  // -> l = [], ir al paso 3.
        
      for (j=0; j<=indexL; j++)       // - actualizar Fxl, crl.
        Fxl[j] = 0;
      for(i=0; i<numFilas; i++) {
        if (crl[i][indexL] == 1)
          for (j=0; j<=indexL; j++)
              if (matriz[i][tt[j]] == 1) {
                Fxl[j]++;
                break;
              }
      }
      return ret;           
    }
    //---------------------------------------------------------------------------
    private ReturnStruct condicion_4d(int indexL, int razgo_x) {
      int i,j;
      ReturnStruct ret = new ReturnStruct();
      //---------
      razgo_x = tt[indexL];
      indexL--;
      for (j=0; j<=indexL; j++)
        Fxl[j] = 0;
      if (indexL >= 0) // - si l no vacia, actualizar listas.
	      for(i=0; i<numFilas; i++) {
	        if (crl[i][indexL] == 1)
	          for (j=0; j<=indexL; j++)
	              if (matriz[i][tt[j]] == 1) {
	                Fxl[j]++;
	                break;
	              }
	      }
      ret.v1 = indexL;
      ret.v2 = razgo_x;
      ret.band = false;
      return ret;
    }
    //---------------------------------------------------------------------------
    public int getNumSoluciones() {
     return numTestores;
    }
}
class ReturnStruct {
	public int v1;
	public int v2;
	public boolean band;
}
