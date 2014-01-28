//----------------------------------------------------------------------------
// - modofocacion del BR: 
//   Reduccion del n�mero de columnas, generar los pseudo TT,
//   reconstruccion de los TT originales.
//----------------------------------------------------------------------------
package alghoritms.fast_br;

import alghoritms.fast_br.tools.TPila;
import alghoritms.fast_br.tools.TuplaBinaria;

public class FastBR {
	private static final int DERECHA = 2;
	private static final int IZQUIERDA = 1;
	
	private int numColumnas, numFilas, contadorTestores; // - Numero de
							// soluciones.
	private double [] frecAparicion; // -
	private double [] frecAmplitud; // -
	private double [] importancia;

	private TuplaBinaria [] rasgo; // - Lista de rasgos de la matriz
	private int [] solucion; // - Lista de TT.
	private int [][] contraidos; // - Lista de rasgos agrupados.
	private int porcReduccion; // - guardar el prciento de reduccion.
	//--------------------------------------------------------------------------
	public FastBR() {
		super();
		  numFilas = 0;
		  numColumnas = 0;
		  // - Determinar el # de bit que ocupa un int.
		  int a = 1, cont = 0; while (a != 0) { a <<= 1; cont++; }
		  TuplaBinaria.zizeOfUnidad = cont;
	}
	//---------------------------------------------------------------------------
	public void set(int filas, int columnas) {
	  numFilas = filas;
	  numColumnas = columnas;
	  rasgo = new TuplaBinaria [numColumnas];
	  for(int i=0; i<numColumnas; i++)
	    rasgo[i] = new TuplaBinaria(numFilas, i+1);

	}
	//---------------------------------------------------------------------------
	public void pushValor(int valor, int fila, int columna) {
	  rasgo[columna].setValorEn(fila, valor);
	}
	//---------------------------------------------------------------------------
	public int getValor(int fila, int columna) {
	  return rasgo[columna].getValorEn(fila);
	}
	//---------------------------------------------------------------------------
	public void extraerMB() {     // O(n^2)
	  int i,j,p, cont = 0;
	  //--------------------
	  rotar(DERECHA);
	  quickSort(1, -1, -1);
	  for (i=0; i<numColumnas-1; i++) {
	    if (rasgo[i] != null)
	      for (j=i+1; j<numColumnas; j++){
	         if (rasgo[j] != null && rasgo[j].esSubfilaDe(rasgo[i])) {
	           rasgo[i] = null;  // - Marcar como fila no valida
	           cont++;
	           break;
	         }
	    }
	  }
	  p = 0;
	  for (i=0; i<numColumnas; i++) {   // -  Eliminar filas no validas.
	    if (rasgo[p] != null)
	      p++;
	    else
	      if (rasgo[i] != null) {
	        rasgo[p] = rasgo[i];
	        rasgo[i] = null;
	        p++;
	      }
	  }
	  numColumnas -= cont;
	  rotar(IZQUIERDA);
	}
	//---------------------------------------------------------------------------
	public int ordenInicial() {
	  int k;
	  //---------------
	  rotar(DERECHA);
	  quickSort(2, -1, -1);    // - Ordeno las fila de mayor a menor seg�n la cantidad de 1.
	  rotar(IZQUIERDA);
	  k = ordenarFilas();
	  //quickSort(2, 0, k-1);
	  //quickSort(2, k, numColumnas-1);
	  return k;
	}
	//---------------------------------------------------------------------------
	// - Metodo para reducir el numero de columnas.
	//---------------------------------------------------------------------------
	public void reducirColumnas() {
	  int i, j;
	  boolean []band = new boolean[numColumnas];
	  int [][]tempMat = new int[numColumnas][numColumnas];
	  int contColum = 0, maxFila = 0;
	  TuplaBinaria []rasgoTemp;
	  //---------------
	  for (i=0; i<numColumnas; i++) {
		  band[i] = true;
	  }
	  for (i=0; i<numColumnas; i++) {
		  tempMat[0][i] = 0;
		  if (band[i] == true) {
			  contColum++;
			  tempMat[0][i] = 1;  tempMat[1][i] = rasgo[i].getId();
			  for (j=numColumnas-1; j >= i+1; j--) {		  
				 if (rasgo[i].igualA(rasgo[j])== true) {
					 tempMat[++tempMat[0][i]][i] = rasgo[j].getId();
					 band[j] = false; // - marcado como rasgo usado
				 } 				 
			  }
			 if (maxFila < tempMat[0][i]) 
				 maxFila = tempMat[0][i];
		  }		  
	  }
	  rasgoTemp = new TuplaBinaria[contColum];
	  contraidos = new int[maxFila+1][contColum];	
	  contColum = 0;
	  for (i=0; i<numColumnas; i++) {
		  if (tempMat[0][i] != 0) {
			  rasgoTemp[contColum] = rasgo[i];
			  rasgoTemp[contColum].idTupla = contColum;
			  for (j=0; j <= tempMat[0][i]; j++) {
				  contraidos[j][contColum] = tempMat[j][i]; 
			  }
			  contColum++;
		  }
	  }
	  rasgo = rasgoTemp;
	  porcReduccion = (contColum*100)/numColumnas;
	  numColumnas = contColum; // - Nuevo # de columnas.
	}
	//---------------------------------------------------------------------------
	//***************************************************************************
	// - Ordena mediante el m�todo QuickSort de mayor a menor.
	//  En dependencia del parametro opc:
	//  Si opc = 1 -> Ordena los rasgos por su valor # como notaci�n binaria
	//	   opc = 2 -> Ordena los rasgos seg�n la cantidad de valores unitarios.
	// - Tambi�n brinda la posibilidad de ordenar un rango de rasgos determinado
	//   por los par�metros izq y der.
	//***************************************************************************
	private boolean quickSort(int opc, int izq , int der) {
	  TuplaBinaria elem_div = null, temp; // * *
	  int i, j;
	  int indiceIzq;
	  int indiceDer;
	  boolean bandera;

	  TPila pilaIzq = new TPila();
	  TPila pilaDer = new TPila();
	/*  Nombre Procedimiento: OrdRap
	 Par�metros:
	    lista a ordenar (rasgo)
	    �ndice inferior (indiceIzq)
	    �ndice superior (indiceDer)*/

	  // Inicializaci�n de variables
	  pilaIzq.setRange(numColumnas/2+2);
	  pilaDer.setRange(numColumnas/2+2);
	  if (izq < 0) {
	    indiceDer = numColumnas-1;
	    indiceIzq = 0;
	  }
	  else {
	    indiceDer = der;
	    indiceIzq = izq;
	  }
	  //----------------
	  while (true) {
	    while (true) {
	      i = indiceIzq- 1 ;
	      j = indiceDer;
	      if (indiceDer > -1) elem_div = rasgo[indiceDer];
	      bandera = true;

	      // Verificamos que no se crucen los l�mites.
	      if (indiceIzq < indiceDer)
	        break;
	      if (pilaIzq.vacia())
	        return true;
	      indiceIzq = pilaIzq.pop();
	      indiceDer = pilaDer.pop();
	    }

	    //  Clasificamos la sublista
	    while (bandera) {
	      while (rasgo[++i].mayorQue(elem_div, opc) && i < j);
	      while (rasgo[--j].menorQue(elem_div, opc) && i < j);
	        if (i < j) {
	          temp = rasgo[i];
	          rasgo[i] = rasgo[j];
	          rasgo[j] = temp;
	        }
	        else
	          bandera = false;
	    }

	    // Copiamos el elemento de divisi�n
	    // en su posici�n final
	    temp = rasgo[i];
	    rasgo[i] = rasgo[indiceDer];
	    rasgo[indiceDer] = temp;

	    // Aplicamos el procedimiento
	    // iterativamente a cada sublista
	    if (i+1 < indiceDer) {
	      pilaIzq.push(i+1);
	      pilaDer.push(indiceDer);
	    }
	    // OrdRap (lista, i + 1, indiceDer);
	    // OrdRap (lista, indiceIzq, i - 1);
	    indiceDer = i - 1;
	  }
	}
	//---------------------------------------------------------------------------
	private int ordenarFilas() {
	  int i,p;
	  TuplaBinaria temp;
	  //-----------------------------
	  p = 0;
	  for (i=0; i<numColumnas; i++) {
	    if (rasgo[p].getValorEn(0) == 1)   // <- hice Cambio a get firstColumnValue
	      p++;
	    else
	      if (rasgo[i].getValorEn(0) == 1) {
	        temp = rasgo[p];
	        rasgo[p] = rasgo[i];
	        rasgo[i] = temp;
	        p++;
	      }
	  }
	  return p;
	}
	//---------------------------------------------------------------------------
	private void rotar(int sentido) {
	  int i, j, antNumFilas, antNumColumnas;
	  TuplaBinaria [] tuplaTemp;         /// - Se esta trabajando con get valor
	                                    //  - Hay que cambiar por setValorEnPos()
	  //----------------
	  //getRango(antNumFilas, antNumColumnas);
	  antNumFilas = getNumFilas();
	  antNumColumnas = getNumColumnas();
	  tuplaTemp = new TuplaBinaria [numFilas];
	  for (i=0; i<numFilas; i++) {
	   tuplaTemp[i] = new TuplaBinaria(numColumnas, i);
	   for(j=0; j<numColumnas; j++) {
	     if (sentido == DERECHA) 
	    	 tuplaTemp[i].setValorEn(j, rasgo[j].getValorEn(numFilas-1-i));
	     else if (sentido == IZQUIERDA)
	        tuplaTemp[i].setValorEn(j, rasgo[numColumnas-1-j].getValorEn(i));
	   }    
	  }
	  rasgo = tuplaTemp;
	  numFilas = antNumColumnas;        // - Inversion del rango. Cambio.
	  numColumnas = antNumFilas;
	}
	//---------------------------------------------------------------------------
	public int getNumFilas() {return numFilas;}
	public int getNumColumnas() {return numColumnas;}
	//---------------------------------------------------------------------------
	//***************************************************************************
	// - Las listas de tl estar�n representadas por las filas de la matriz pMatriz,
	//  y la �ltima lista de tl estar� siempre indicada por el �ndice listaIndex.
	// - Los �ndices (primero y ultimo) indexan los rasgos iniciales y finales
	//   de cada lista en tl. Cada lista (l') en tl tiene su valor (primero y
	//   ultimo), los cuales se guardan en pila cuando se analizan otras listas.
	//***************************************************************************
	public void extraerTT(RegisterData registerData) {
	  int listaIndex, primero, ultimo, proxUltimo, i, j, temp, hueco, p, f, k=0;
	  int posUltColumUnitaria;
	  boolean condicion2, seAcepta=true, esTestor=true;
	  int tempSol [], top=0; // - arreglo temporal para extraer los TT compactados.

	  TuplaBinaria rasgo_X, enCurso_x;
	  TuplaBinaria [] mascaraComp; // - Pila de mascara de compatibilidad.
	  TuplaBinaria [] mascaraAcep; // - Pila de mascara de aceptacion.
	   // - Lista de rasgos seleccionadas de MB para formar un TT (l en el algoritmo).
	  TuplaBinaria [] filaSel;
	   // - Matriz triangular superior de punteros a TuplaBinarias, se utiliza para
	   //  simular la lista de listas (tl en el algoritmo).
	  TuplaBinaria [][]pMatriz;

	  TPila  pilaPrimero = new TPila();
	  TPila  pilaUltimo = new TPila();
	  //boolean rasgoUnitario;

	  // registerData.printInfo("\n ******************************************** ");
	  // registerData.printInfo("\n   ALGORITMO --  BR REDUCCION DE COLUMNAS ");
	  // registerData.printInfo("\n ********************************************\n");


	  //*****************************************    Reducir el numero de columnas.
	  reducirColumnas();
	  registerData.getInfo(Integer.toString(100-porcReduccion)+"% de reduccion de columnas\n");
	 //******************************************    Oredenar la matriz
	  posUltColumUnitaria = ordenInicial();
	 //******************************************    Inicializacion
	  tempSol = new int[numColumnas];
	    // - Inicializar pilas de mascaras.
	  mascaraComp = new TuplaBinaria[numColumnas+1];
	  mascaraAcep = new TuplaBinaria[numColumnas+1];
	  for (i=0; i<=numColumnas; i++) {
		mascaraComp[i] = new  TuplaBinaria(numFilas,0); 
	    mascaraAcep[i] = new  TuplaBinaria(numFilas,0);
	  }
	    // - Inicializar lista l, registro de los rasgos en curso.
	  filaSel = new TuplaBinaria[numColumnas+1];
	    // - Inicializar tl, lista de listas.
	  pMatriz = new TuplaBinaria [numColumnas+1][];   // - Crear matriz.
	  for (i=0; i<numColumnas; i++) pMatriz[i] = new TuplaBinaria[numColumnas-i];
	    // - Colocar en pMatriz (tl), la lista de todos los rasgos de MB
	    //  despu�s del paso 1.
	  listaIndex = 0;
	  for (i=0; i<numColumnas; i++) pMatriz[i][0] = rasgo[i];
	  primero = 0;            // -  El primer elem. tiene �ndice 0
	  ultimo = numColumnas-1;
	    // - Darle un rango maximo a las pilas que contienen los indices
	    //  que definen cada lista de tl.
	  pilaPrimero.setRange(numColumnas);
	  pilaUltimo.setRange(numColumnas);
	    // - N�mero de testores calculados.
	  contadorTestores = 0;
	    // - Almacen de cada resultado(TT)
	  solucion = new int[numColumnas+1]; 
	    // - Inicializar registro de frecuencias. (Importancia Informacional).
	  frecAparicion = new double[numColumnas];
	  frecAmplitud  = new double[numColumnas];
	  for (i=0; i<numColumnas; i++) {
	    frecAparicion[i] = 0;
	    frecAmplitud[i]  = 0;
	  }
	  
	  long contadorComprobaciones = 0;

	  paso_3:  //******************************************   Proceso
      while (true) {		  
		       //	-> Hacer  X = primero(lu)
		    rasgo_X = pMatriz[primero][listaIndex];
		       // -> Si  | tl | = 1 ...
		    if (listaIndex == 0)  {
		      if (primero >= posUltColumUnitaria) {
		        break; // SALIR 		    	
		      }
		      //if (porciento != null) <- registrar el avance. 
		      registerData.percent((primero+1)*100/posUltColumUnitaria);
		      
		      mascaraComp[0] = rasgo_X;  // - PROPOSICION 2.1
		      mascaraAcep[0] = rasgo_X;
		      if (rasgo_X.cantValoresUnitarios() == numFilas ) {
		    	  // - Registrar un solo rasgo.
		         solucion[0] = 1;
		         solucion[1] = primero+1;
		         registerData.resgistTT(solucion);
		         contadorTestores++;
		         primero ++;
		         continue paso_3;  
		      }
		    }
		    else {  // - PROPOSICIONES  2.2 , 2.3
		      mascaraAcep[listaIndex].mascAcep(mascaraAcep[listaIndex-1], rasgo_X);
		      mascaraComp[listaIndex].mascComp(mascaraComp[listaIndex-1], rasgo_X,
		                                       mascaraAcep[listaIndex-1]);
		    }
		     // -> l = sust(l, X, | tl |)
		  filaSel[listaIndex] = rasgo_X;
	
		     // ->  Hacer l_1 = incl(l, prox(lu))  y  ctt = ctt U TT(l, prox(lu))
		  proxUltimo = -1;
		  for (j=primero+1; j<=ultimo; j++) {  //- para cada elem. enCurso_x de prox(lu)
		    enCurso_x = pMatriz[j][listaIndex];
		    
		    contadorComprobaciones++;
		    
		    esTestor = false;
		    // - Si cada elem j se agrega para formar un TT con el conj.
		    // ************* - PROPOCICI�N  2.4 - **********************
		      seAcepta = false;    // - Si no se cumple la condici�n #1
		      if (mascaraAcep[listaIndex+1].mascAcep(mascaraAcep[listaIndex], enCurso_x) == false) {
		        mascaraComp[listaIndex+1].mascComp(mascaraComp[listaIndex], enCurso_x,
		                                           mascaraAcep[listaIndex]);
		        condicion2 = false;
		        for (i=0; i<=listaIndex; i++) {   // -  Condicion #2
		          if (filaSel[i].and(mascaraComp[listaIndex+1]) == true) {
		            condicion2 = true;
		            break;
		          }
		        }
		        if(condicion2 == false) {
		          seAcepta = true;          // - PROPOSICI�N 2.5 - Condicion #3.
		          if (mascaraAcep[listaIndex+1].esUnitario()){ // - l+[enCurso_x] �es un TT?
		            // vvvvvvvvvvvvvvvvvvvvvvv - REGISTRAR TT - vvvvvvvvvvvvvvvvvvvvvvv
		                  //if (contadorTestores < 1000000) {
		                    filaSel[++listaIndex] = enCurso_x;
		                    top = 0; tempSol[top] = contraidos[0][filaSel[top].idTupla];
		                    solucion[0] = listaIndex+1; // No. de rasgos del TT. (cabecera)
		                    while (top >= 0) {  // - Extarer los TT de los pseudoTT.
		                    	if (tempSol[top] == 0) top--;
		                    	else {
		                    		solucion[top+1] = contraidos[tempSol[top]][filaSel[top].idTupla];
		                    		tempSol[top]--;
		                    		if(top < listaIndex) {
		                    			top++;
		                    			tempSol[top] = contraidos[0][filaSel[top].idTupla];
		                    		}
		                    		else {
		                    			registerData.resgistTT(solucion);
		                    			contadorTestores++;
		                    		}
		                    	}		                    	
		                    }
		                    listaIndex--;
		                 // }
		            // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		             // - como l + [enCurso_x] forman un TT entonces
		            seAcepta = false;   // - enCurso_x no se agrega a la prox lista.
		            esTestor = true;
		          }
		        }       //-Art�culo 176-177 - Reglamento docente..
		      }
		    // *********************************************************
		    // - Si enCurso_x es no excluyente con l y no forma un TT con el mismo.
		    if (seAcepta) {
		      // - Agrego enCurso_x a proxima lista (l_1);
	
		      pMatriz[++proxUltimo][listaIndex+1] = enCurso_x;
		    }
		  }//for
	
		  if (primero+1 < ultimo) { // -> Si  | l'| > 1
		        // ->  - Se selecciona proxima lista.
		    primero++;  ///////  - f. Hacer l' = elim(l', primero(l'))
		        // -> Si | l_1 | <= 1, entonces ir al paso 3  -Si no existe nueva lista.
	
		    if (proxUltimo <= 0)  continue; //break paso_3; 
		        // -> Hacer tl = tl + l1
		    pilaPrimero.push(primero); // - Guardo el rango de la presente lista.
		    pilaUltimo.push(ultimo);
		    listaIndex++;                   // - Se indexa la pr�xima lista.
		    primero = 0;               // - Se actualiza los rangos de la proxima lista.
		    ultimo = proxUltimo;
		        // -> Ir al paso 3
		    continue; //break paso_3; 
	
		  }
		  else if (seAcepta == true || esTestor){   // - Eliminar el hueco.  tl = elimK(tl, hueco(l', tl))
		      k++;
		      hueco = listaIndex-1;
		      while (true) {
		        if (hueco <= 0) break;
		        p = pilaPrimero.get(hueco-1);
		        f = pilaUltimo.get(hueco-1);
		        if (listaIndex-(hueco-1) != f-p) break;
		        hueco-- ;
		      }
		      pilaPrimero.cut(hueco);
		      pilaUltimo.cut(hueco);
		      listaIndex = hueco+1;  
		  }   // */
		   // - Indexo y extraigo intervalo, de la lista.
		  primero = pilaPrimero.pop();   //- No ser�a de la anterior sin�...
		  ultimo  = pilaUltimo.pop();    //  la que toque segun listaIndex ::::
		  listaIndex --;
		   continue; ////break paso_3;  
      } // while(true)  
		  //**************************************************************
	    registerData.getInfo("\nBR -- No. de Comprobaciones: " + 
	             Long.toString(contadorComprobaciones)); 
	  //fin:
/*	    for (i=0; i<numColumnas; i++) {
	      frecAmplitud[i]  /=  frecAparicion[i];
	      frecAparicion[i] /=  contadorTestores;
	    }
*/	}
	//---------------------------------------------------------------------------
	public int getNumSoluciones() {
		return contadorTestores;
	}
	//---------------------------------------------------------------------------
	public void operatorIgual(FastBR objeto) {
	  int i, j;
	  int nFilas, nColumnas;
	  //----------
	  nFilas    =  objeto.numFilas;
	  nColumnas =  objeto.numColumnas;
	  set(nFilas, nColumnas);
	  for(i=0; i<numFilas; i++) {
	    for(j=0; j<numColumnas; j++) {
	      pushValor(objeto.getValor(i,j),i,j);
	    }
	  }
	}
	//---------------------------------------------------------------------------
	public double [] extraerImportancia(double alpha, double beta) {
	  int i;
	  //-----------------
	  //return frecAparicion;
	  //return frecAmplitud;
	  if (numColumnas > 0) {
	    if (importancia == null) importancia = new double[numColumnas];
	    for (i=0; i<numColumnas; i++) {
	      importancia[i] = alpha*frecAparicion[i] + beta*frecAmplitud[i];
	    }
	    return importancia;
	  }
	  return null;
	}
}

