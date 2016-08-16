package algorithms;

import java.util.LinkedList;

import tools.Solution;
import tools.TPila;
import tools.TuplaBinaria;

public class RecursiveReducer {
	private static final int DERECHA = 2;
	private static final int IZQUIERDA = 1;
	private static final boolean DEBUG = true;

	public long contadorComprobaciones = 0;

	private int numColumnas, numFilas; // -
	int contadorTestores;
																		// Numero
																		// de
	// soluciones.
	int contadorTTs;

	private TuplaBinaria[] rasgo; // - Lista de rasgos de la matriz
	private LinkedList<Solution> soluciones; // - Lista de TT.

	// --------------------------------------------------------------------------
	public RecursiveReducer() {
		super();
		numFilas = 0;
		numColumnas = 0;
		// - Determinar el # de bit que ocupa un int.
		// int a = 1, cont = 0; while (a != 0) { a <<= 1; cont++; }
		// TuplaBinaria.zizeOfUnidad = cont;
	}

	// ---------------------------------------------------------------------------
	public void set(int filas, int columnas) {
		numFilas = filas;
		numColumnas = columnas;
		rasgo = new TuplaBinaria[numColumnas];
		for (int i = 0; i < numColumnas; i++)
			// rasgo[i] = new TuplaBinaria(numFilas, i + 1); // Original
			rasgo[i] = new TuplaBinaria(numFilas, i); // Modified to make ID
														// start from 0.

	}

	// ---------------------------------------------------------------------------
	public void pushValor(int valor, int fila, int columna) {
		rasgo[columna].setValorEn(fila, valor);
	}

	// ---------------------------------------------------------------------------
	public int getValor(int fila, int columna) {
		return rasgo[columna].getValorEn(fila);
	}

	// ---------------------------------------------------------------------------
	public void extraerMB() { // O(n^2)
		int i, j, p, cont = 0;
		// --------------------
		rotar(DERECHA);
		quickSort(1, -1, -1);
		for (i = 0; i < numColumnas - 1; i++) {
			if (rasgo[i] != null)
				for (j = i + 1; j < numColumnas; j++) {
					if (rasgo[j] != null && rasgo[j].esSubfilaDe(rasgo[i])) {
						rasgo[i] = null; // - Marcar como fila no valida
						cont++;
						break;
					}
				}
		}
		p = 0;
		for (i = 0; i < numColumnas; i++) { // - Eliminar filas no validas.
			if (rasgo[p] != null)
				p++;
			else if (rasgo[i] != null) {
				rasgo[p] = rasgo[i];
				rasgo[i] = null;
				p++;
			}
		}
		numColumnas -= cont;
		rotar(IZQUIERDA);
	}

	// ---------------------------------------------------------------------------
	public int ordenInicial() {
		int k;
		// ---------------
		rotar(DERECHA);
		quickSort(2, -1, -1); // - Ordeno las fila de mayor a menor seg�n la
								// cantidad de 1.
		rotar(IZQUIERDA);
		k = ordenarFilas();
		// quickSort(2, 0, k-1);
		// quickSort(2, k, numColumnas-1);
		return k;
	}

	// ---------------------------------------------------------------------------
	// - Metodo para reducir el numero de columnas.
	// ---------------------------------------------------------------------------
	public void reducirColumnas(int[] countInitalRepated,
			TuplaBinaria[][] initalRepated, TuplaBinaria[] initalAccepted) {
		int i, j;
		boolean[] flag = new boolean[numColumnas];
		for (i = 0; i < numColumnas; i++) {
			flag[i] = true; // Not repeated
		}

		// ---------------
		int contColum = 0;
		for (i = 0; i < numColumnas; i++) {
			countInitalRepated[rasgo[i].getId()] = 0;
			if (flag[i] == true) {
				contColum++;
				initalAccepted[contColum - 1] = rasgo[i];
				for (j = i + 1; j < numColumnas; j++) {
					if (rasgo[i].igualA(rasgo[j]) == true) {
						flag[j] = false;
						initalRepated[rasgo[i].getId()][countInitalRepated[rasgo[i]
								.getId()]++] = rasgo[j];
					}
				}
			}
		}
		numColumnas = contColum;
	}

	// //
	// ---------------------------------------------------------------------------
	// // - Metodo para reducir el numero de columnas.
	// //
	// ---------------------------------------------------------------------------
	// public void reducirColumnasOld() {
	// int i, j;
	// boolean[] band = new boolean[numColumnas];
	// int[][] tempMat = new int[numColumnas][numColumnas];
	// int contColum = 0, maxFila = 0;
	// TuplaBinaria[] rasgoTemp;
	// // ---------------
	// for (i = 0; i < numColumnas; i++) {
	// band[i] = true;
	// }
	// for (i = 0; i < numColumnas; i++) {
	// tempMat[0][i] = 0;
	// if (band[i] == true) {
	// contColum++;
	// tempMat[0][i] = 1;
	// tempMat[1][i] = rasgo[i].getId();
	// for (j = numColumnas - 1; j >= i + 1; j--) {
	// if (rasgo[i].igualA(rasgo[j]) == true) {
	// tempMat[++tempMat[0][i]][i] = rasgo[j].getId();
	// band[j] = false; // - marcado como rasgo usado
	// }
	// }
	// if (maxFila < tempMat[0][i])
	// maxFila = tempMat[0][i];
	// }
	// }
	// rasgoTemp = new TuplaBinaria[contColum];
	// contraidos = new int[maxFila + 1][contColum];
	// contColum = 0;
	// for (i = 0; i < numColumnas; i++) {
	// if (tempMat[0][i] != 0) {
	// rasgoTemp[contColum] = rasgo[i];
	// rasgoTemp[contColum].idTupla = contColum;
	// for (j = 0; j <= tempMat[0][i]; j++) {
	// contraidos[j][contColum] = tempMat[j][i];
	// }
	// contColum++;
	// }
	// }
	// rasgo = rasgoTemp;
	// numColumnas = contColum; // - Nuevo # de columnas.
	// }

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Ordena mediante el m�todo QuickSort de mayor a menor.
	// En dependencia del parametro opc:
	// Si opc = 1 -> Ordena los rasgos por su valor # como notaci�n binaria
	// opc = 2 -> Ordena los rasgos seg�n la cantidad de valores unitarios.
	// - Tambi�n brinda la posibilidad de ordenar un rango de rasgos determinado
	// por los par�metros izq y der.
	// ***************************************************************************
	private boolean quickSort(int opc, int izq, int der) {
		TuplaBinaria elem_div = null, temp; // * *
		int i, j;
		int indiceIzq;
		int indiceDer;
		boolean bandera;

		TPila pilaIzq = new TPila();
		TPila pilaDer = new TPila();
		/*
		 * Nombre Procedimiento: OrdRap Par�metros: lista a ordenar (rasgo)
		 * �ndice inferior (indiceIzq) �ndice superior (indiceDer)
		 */

		// Inicializaci�n de variables
		pilaIzq.setRange(numColumnas / 2 + 2);
		pilaDer.setRange(numColumnas / 2 + 2);
		if (izq < 0) {
			indiceDer = numColumnas - 1;
			indiceIzq = 0;
		} else {
			indiceDer = der;
			indiceIzq = izq;
		}
		// ----------------
		while (true) {
			while (true) {
				i = indiceIzq - 1;
				j = indiceDer;
				if (indiceDer > -1)
					elem_div = rasgo[indiceDer];
				bandera = true;

				// Verificamos que no se crucen los l�mites.
				if (indiceIzq < indiceDer)
					break;
				if (pilaIzq.vacia())
					return true;
				indiceIzq = pilaIzq.pop();
				indiceDer = pilaDer.pop();
			}

			// Clasificamos la sublista
			while (bandera) {
				while (rasgo[++i].mayorQue(elem_div, opc) && i < j)
					;
				while (rasgo[--j].menorQue(elem_div, opc) && i < j)
					;
				if (i < j) {
					temp = rasgo[i];
					rasgo[i] = rasgo[j];
					rasgo[j] = temp;
				} else
					bandera = false;
			}

			// Copiamos el elemento de divisi�n
			// en su posici�n final
			temp = rasgo[i];
			rasgo[i] = rasgo[indiceDer];
			rasgo[indiceDer] = temp;

			// Aplicamos el procedimiento
			// iterativamente a cada sublista
			if (i + 1 < indiceDer) {
				pilaIzq.push(i + 1);
				pilaDer.push(indiceDer);
			}
			// OrdRap (lista, i + 1, indiceDer);
			// OrdRap (lista, indiceIzq, i - 1);
			indiceDer = i - 1;
		}
	}

	// ---------------------------------------------------------------------------
	private int ordenarFilas() {
		int i, p;
		TuplaBinaria temp;
		// -----------------------------
		p = 0;
		for (i = 0; i < numColumnas; i++) {
			if (rasgo[p].getValorEn(0) == 1) // <- hice Cambio a get
												// firstColumnValue
				p++;
			else if (rasgo[i].getValorEn(0) == 1) {
				temp = rasgo[p];
				rasgo[p] = rasgo[i];
				rasgo[i] = temp;
				p++;
			}
		}
		return p;
	}

	// ---------------------------------------------------------------------------
	private void rotar(int sentido) {
		int i, j, antNumFilas, antNumColumnas;
		TuplaBinaria[] tuplaTemp; // / - Se esta trabajando con get valor
									// - Hay que cambiar por setValorEnPos()
		// ----------------
		// getRango(antNumFilas, antNumColumnas);
		antNumFilas = getNumFilas();
		antNumColumnas = getNumColumnas();
		tuplaTemp = new TuplaBinaria[numFilas];
		for (i = 0; i < numFilas; i++) {
			tuplaTemp[i] = new TuplaBinaria(numColumnas, i);
			for (j = 0; j < numColumnas; j++) {
				if (sentido == DERECHA)
					tuplaTemp[i].setValorEn(j,
							rasgo[j].getValorEn(numFilas - 1 - i));
				else if (sentido == IZQUIERDA)
					tuplaTemp[i].setValorEn(j,
							rasgo[numColumnas - 1 - j].getValorEn(i));
			}
		}
		rasgo = tuplaTemp;
		numFilas = antNumColumnas; // - Inversion del rango. Cambio.
		numColumnas = antNumFilas;
	}

	// ---------------------------------------------------------------------------
	public int getNumFilas() {
		return numFilas;
	}

	public int getNumColumnas() {
		return numColumnas;
	}

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Las listas de tl estar�n representadas por las filas de la matriz
	// pMatriz,
	// y la �ltima lista de tl estar� siempre indicada por el �ndice listaIndex.
	// - Los �ndices (primero y ultimo) indexan los rasgos iniciales y finales
	// de cada lista en tl. Cada lista (l') en tl tiene su valor (primero y
	// ultimo), los cuales se guardan en pila cuando se analizan otras listas.
	// ***************************************************************************
	public void extraerTT(RegisterData registerData) {
		int listaIndex, primero, ultimo, proxUltimo, i, j, hueco, p, f;
		int posUltColumUnitaria;
		boolean seAcepta = true, isTypical = true;
		int tempSol[], top = 0; // - arreglo temporal para extraer los TT
								// compactados.

		TuplaBinaria rasgo_X, enCurso_x;
		// Último TT encontrado
		Solution lastTT;
		// - Lista de rasgos seleccionadas de MB para formar un TT (l en el
		// algoritmo).
		TuplaBinaria[] filaSel;
		// - Matriz que guarda los rasgos acceptados en cada nivel de la
		// recursividad.
		TuplaBinaria[][] accepted;
		// - Matriz que guarda la máscara de aceptación acumulativas de
		// los rasgos acceptados en cada nivel de la recursividad.
		TuplaBinaria[][] acceptedMasks;
		// - Matriz que guarda los rasgos repetidos para cada
		// rasgo acceptado en cada nivel de la recursividad.
		TuplaBinaria[][][] repeated;
		// - Matriz que guarda la cantidad de rasgos repetidos para cada
		// rasgo acceptado en cada nivel de la recursividad.
		int countRepeated[][];

		TPila pilaPrimero = new TPila();
		TPila pilaUltimo = new TPila();

		// ***************** Oredenar la matriz *********************
		posUltColumUnitaria = ordenInicial();
		// ******************* Inicialización ***********************
		tempSol = new int[numColumnas];
		// - Inicializar pilas de mascaras.
		accepted = new TuplaBinaria[numColumnas + 1][numColumnas + 1];
		acceptedMasks = new TuplaBinaria[numColumnas + 1][];
		repeated = new TuplaBinaria[numColumnas + 1][numColumnas + 1][numColumnas + 1];
		for (i = 0; i <= numColumnas; i++) {
			acceptedMasks[i] = new TuplaBinaria[numColumnas + 1];
			for (j = 0; j <= numColumnas; j++) {
				acceptedMasks[i][j] = new TuplaBinaria(numFilas, 0);
			}
		}
		countRepeated = new int[numColumnas][numColumnas];
		
		// ************** Reducir el numero de columnas **************
		reducirColumnas(countRepeated[0], repeated[0], accepted[0]);
		// - Inicializar las máscaras de nivel 0 con los rasgos iniciales
		for (j = 0; j < numColumnas; j++) {
			acceptedMasks[0][accepted[0][j].getId()] = accepted[0][j];
		}
		filaSel = new TuplaBinaria[numColumnas + 1];
		listaIndex = 0;
		primero = 0; // - El primer elem. tiene índice 0
		ultimo = numColumnas - 1;
		// - Darle un rango maximo a las pilas que contienen los indices
		// que definen cada lista de tl.
		pilaPrimero.setRange(numColumnas);
		pilaUltimo.setRange(numColumnas);
		// - Número de testores calculados.
		contadorTestores = 0;
		contadorTTs = 0;

		paso_3: // ****************************************** Proceso
		while (true) {
			// Tomar el primer rasgo acceptado
			rasgo_X = accepted[listaIndex][primero];
			// Si hay un solo attributo en el candidato
			if (listaIndex == 0) {
				if (primero >= posUltColumUnitaria) {
					break; // SALIR
				}
				contadorComprobaciones++;
				if (rasgo_X.cantValoresUnitarios() == numFilas) {
					// - Registrar un solo rasgo.
					Solution solucion = new Solution(1);
					solucion.setAttribute(0, rasgo_X);
					// registerData.resgistTT(solucion);
					contadorTestores++;
					contadorTTs++;
					primero++;
					continue paso_3;
				}
			}
			filaSel[listaIndex] = rasgo_X;

			// Verificar la contribución
			proxUltimo = -1;
			for (j = primero + 1; j <= ultimo; j++) { // Para cada rasgo
														// aceptado
				enCurso_x = accepted[listaIndex][j]; // Rasgo actual
				contadorComprobaciones++;

				isTypical = false;
				seAcepta = false;
				if (acceptedMasks[listaIndex + 1][enCurso_x.getId()].mascAcep(
						acceptedMasks[listaIndex][filaSel[listaIndex]
								.getId()], enCurso_x) == false) {
					seAcepta = true; // Contribuye
					// Si es super-reducto
					if (acceptedMasks[listaIndex + 1][enCurso_x.getId()]
							.esUnitario()) {
						seAcepta = false;

						filaSel[++listaIndex] = enCurso_x;
						top = 0;
						tempSol[top] = countRepeated[listaIndex][top] + 1;

						while (top >= 0) { // - Extarer los TT de los
											// pseudoTT.
							Solution solucion = new Solution(listaIndex + 1);
							
							if (tempSol[top] == 0) {
								top--;
							} else {
								if (tempSol[top] == 1) {
									solucion.setAttribute(top, filaSel[top]);
								} else {
									solucion.setAttribute(top,
											repeated[listaIndex][filaSel[top]
													.getId()][tempSol[top] - 2]);
								}
								tempSol[top]--;
								if (top < listaIndex) {
									top++;
									tempSol[top] = countRepeated[listaIndex][top] + 1;

								} else {
									contadorTestores++;
									if (typical(solucion)) {
										isTypical = true;
										lastTT = solucion;
										soluciones.add(solucion);
										contadorTTs++;

										if (DEBUG) 
											// Print the solution
											System.out.println(solucion);
											

										}
									}

								}
							}
						}
						listaIndex--;
						// }
						// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
						// - como l + [enCurso_x] forman un TT entonces
						seAcepta = false; // - enCurso_x no se agrega a la
											// prox lista.
						isTypical = true;
					}

				// *********************************************************
				// - Si enCurso_x es no excluyente con l y no forma un TT con el
				// mismo.
				if (seAcepta) {
					// - Agrego enCurso_x a los aceptados del proximo nivel de recursividad
					//   si no está repetida su máscara de aceptación.
					boolean isRepated = false;
					for(i=0;i<=proxUltimo;i++){
						if(acceptedMasks[listaIndex + 1][accepted[listaIndex + 1][i].getId()].igualA(enCurso_x)){
							// Está repetido en los proximos niveles de recursividad
							repeated[listaIndex + 1][accepted[listaIndex + 1][i].getId()][countRepeated[listaIndex + 1][accepted[listaIndex + 1][i].getId()]++]=enCurso_x;
							isRepated = true;
							break;
						}
					}
					if (!isRepated)
						accepted[listaIndex + 1][++proxUltimo] = enCurso_x;


				}
			}// for

			if (primero + 1 < ultimo) { // -> Si | l'| > 1
				// -> - Se selecciona proxima lista.
				primero++; // ///// - f. Hacer l' = elim(l', primero(l'))
				// -> Si | l_1 | <= 1, entonces ir al paso 3 -Si no existe nueva
				// lista.

				if (proxUltimo <= 0)
					continue; // break paso_3;
				// -> Hacer tl = tl + l1
				pilaPrimero.push(primero); // - Guardo el rango de la presente
											// lista.
				pilaUltimo.push(ultimo);
				listaIndex++; // - Se indexa la pr�xima lista.
				primero = 0; // - Se actualiza los rangos de la proxima lista.
				ultimo = proxUltimo;

				if (DEBUG) {
					// === Imprimir la base del candidato ===
					System.out.print("Base: ");
					for (int d = 0; d < listaIndex; d++) {
						System.out
								.print("x"
										+ Integer.toString(filaSel[d]
												.getId() + 1));
					}
					System.out.println();
				}
						// ======= Imprimir Lista de contraidos ===========
						if (DEBUG) {
							System.out.println();
							for (i = primero; i <= ultimo; i++) {
								System.out
										.print("Att x"
												+ Integer
														.toString(accepted[listaIndex][i].getId() + 1)
												+ ": ");
								for (j = 0; j < countRepeated[listaIndex][accepted[listaIndex][i].getId()]; j++) {
									System.out
											.print("x"
													+ Integer
															.toString(repeated[listaIndex][accepted[listaIndex][i].getId()][j].getId() + 1));
								}
								System.out.println();
							}
						}
					
				

				// ====================================================================

				// -> Ir al paso 3
				continue; // break paso_3;
				// TODO revisar lo q pasa con el gap
				// } else if (seAcepta == true || esTestor) { // - Eliminar el
				// hueco.
			} else if (false) { // - Eliminar el hueco.
								// tl = elimK(tl,
								// hueco(l', tl))
				hueco = listaIndex - 1;
				while (true) {
					if (hueco <= 0)
						break;
					p = pilaPrimero.get(hueco - 1);
					f = pilaUltimo.get(hueco - 1);
					if (listaIndex - (hueco - 1) != f - p)
						break;
					hueco--;
				}
				pilaPrimero.cut(hueco);
				pilaUltimo.cut(hueco);
				listaIndex = hueco + 1;
			} // */
				// - Indexo y extraigo intervalo, de la lista.
			primero = pilaPrimero.pop(); // - No ser�a de la anterior sin�...
			ultimo = pilaUltimo.pop(); // la que toque segun listaIndex ::::
			listaIndex--;
			continue; // //break paso_3;
		} // while(true)
			// **************************************************************
			// registerData.getInfo("\nBR -- No. de Comprobaciones: " +
		// Long.toString(contadorComprobaciones));
		// fin:
		/*
		 * for (i=0; i<numColumnas; i++) { frecAmplitud[i] /= frecAparicion[i];
		 * frecAparicion[i] /= contadorTestores; }
		 */
	}

	// ******************************************************************
	// Verify a super reduct as reduct by using compatibility
	// This is intended for super reducts araising from repeated
	// attibutes in RR.
	// ******************************************************************
	private boolean typical(Solution superreduct) {
		// Get testor's compatibility mask
		TuplaBinaria AMl = new TuplaBinaria(superreduct.getAttribute(0));
		TuplaBinaria CMl = new TuplaBinaria(superreduct.getAttribute(0));
		int i;
		for (i = 1; i < superreduct.length; i++) {
			CMl.mascComp(CMl, superreduct.getAttribute(i), AMl);
			AMl.mascAcep(AMl, superreduct.getAttribute(i));
		}
		// Check that every attribute in testor has a typical row
		for (i = 0; i < superreduct.length; i++) {
			if (superreduct.getAttribute(i).andNEqZ(CMl)) {
				return false;
			}
		}
		return true;
	}

	// ---------------------------------------------------------------------------
	public int getNumSoluciones() {
		return contadorTestores;
	}

	// ---------------------------------------------------------------------------
	public void operatorIgual(RecursiveReducer objeto) {
		int i, j;
		int nFilas, nColumnas;
		// ----------
		nFilas = objeto.numFilas;
		nColumnas = objeto.numColumnas;
		set(nFilas, nColumnas);
		for (i = 0; i < numFilas; i++) {
			for (j = 0; j < numColumnas; j++) {
				pushValor(objeto.getValor(i, j), i, j);
			}
		}
	}

}
