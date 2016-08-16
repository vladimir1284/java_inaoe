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

	private int numColumnas, numFilas, contadorTestores, contadorTTs; // -
																		// Numero
																		// de
	// soluciones.

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
		boolean condicion2, seAcepta = true, esTestor = true;
		int tempSol[], top = 0; // - arreglo temporal para extraer los TT
								// compactados.

		TuplaBinaria rasgo_X, enCurso_x;
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
		int countRepated[][];

		TPila pilaPrimero = new TPila();
		TPila pilaUltimo = new TPila();

		// ***************** Oredenar la matriz *********************
		posUltColumUnitaria = ordenInicial();
		// ******************* Inicialización ***********************
		tempSol = new int[numColumnas];
		// - Inicializar pilas de mascaras.
		accepted = new TuplaBinaria[numColumnas + 1][];
		acceptedMasks = new TuplaBinaria[numColumnas + 1][];
		for (i = 0; i <= numColumnas; i++) {
			acceptedMasks[i] = new TuplaBinaria[numColumnas + 1];
			accepted[i] = new TuplaBinaria[numColumnas + 1];
			for (j = 0; j <= numColumnas; j++) {
				acceptedMasks[i][j] = new TuplaBinaria(numFilas, 0);
			}
		}
		// ************** Reducir el numero de columnas **************
		reducirColumnas(countRepated[0], repeated[0], accepted[0]);
		// - Inicializar las máscaras de nivel 0 con los rasgos iniciales
		for (j = 0; j <= numColumnas; j++) {
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

				esTestor = false;
				seAcepta = false;
				if (acceptedMasks[listaIndex + 1][enCurso_x.getId()].mascAcep(
						acceptedMasks[listaIndex][accepted[listaIndex][j]
								.getId()], enCurso_x) == false) {
					seAcepta = true; // Contribuye
					// Si es super-reducto
					if (acceptedMasks[listaIndex + 1][enCurso_x.getId()]
							.esUnitario()) {

						filaSel[++listaIndex] = enCurso_x;
						top = 0;
						tempSol[top] = countRepated[listaIndex][top];

						Solution solucion = new Solution(listaIndex + 1); // No.
																			// de
																			// rasgos
																			// del
						// TT. (cabecera)
						while (top >= 0) { // - Extarer los TT de los
											// pseudoTT.
							if (tempSol[top] == 0)
								top--;
							else {
								solucion[top + 1] = mycontraidos[tempSol[top]][filaSel[top].idTupla];
								tempSol[top]--;
								if (top < listaIndex) {
									top++;
									tempSol[top] = mycontraidos[0][filaSel[top].idTupla];

									// if (tempSol[top] > 1)
									// pseudoTT = true;

								} else {
									// registerData.resgistTT(solucion);
									TuplaBinaria[] solution = new TuplaBinaria[solucion[0]];
									for (int i1 = 0; i1 < solucion[0]; i1++) {
										solution[i1] = SortedByID[solucion[i1 + 1]];
									}
									if (typical(solution, solucion[0])) {
										contadorTestores++;

										if (DEBUG) {
											// Print the solution
											for (int i1 = 0; i1 < solucion[0]; i1++) {
												System.out
														.print("x"
																+ Integer
																		.toString(solucion[i1 + 1] + 1));
											}
											// if (pseudoTT) {
											if (true) {
												// pseudoTT = false;

												if (!typical(solution,
														solucion[0])) {
													System.out.print('*');
													contadorTestores--;
												}
											}
											System.out.println();
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
						esTestor = true;
					}
					// }
				}
				// *********************************************************
				// - Si enCurso_x es no excluyente con l y no forma un TT con el
				// mismo.
				if (seAcepta) {
					// - Agrego enCurso_x a proxima lista (l_1);
					pMatriz[++proxUltimo][listaIndex + 1] = enCurso_x;

					// ======================= RR =========================
					// Recordar las máscaras locales para la reducción dinámica
					local_mask[proxUltimo] = new TuplaBinaria(
							mascaraAcep[listaIndex + 1]);
					// ====================================================

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

				// ================= Main Modifications for RR =================
				// Aquí se tienen todos los candidatos del siguiente nivel
				int min_num_atts = 3; // Para menos attributos no vale la pena
				if (ultimo >= min_num_atts) {
					// for (int i1 = 0; i1 < ultimo; i1++) {
					// // System.out.print(pMatriz[i1][listaIndex].getId()+",");
					// System.out.print(local_mask[i1] + ";");
					// }
					// System.out.println();
					boolean repeat = false;
					boolean[] repeated = new boolean[ultimo + 1];
					int[][] tempMat = new int[ultimo + 1][ultimo + 1];
					nrepeated = 0;
					for (int u = 0; u < ultimo - 1; u++) {
						if (!repeated[u]) {
							for (int k = u + 1; k <= ultimo; k++) {
								if (local_mask[u].igualA(local_mask[k])) {
									tempMat[++tempMat[0][u]][u] = pMatriz[k][listaIndex]
											.getId();
									nrepeated++;
									repeat = true;
									repeated[k] = true; // Marcar como usado
								}
							}
						}
					}

					int[][] contraidos = new int[numColumnas][Lcontraidos[listaIndex - 1][0].length];
					// Copy the previous array
					for (int z = 0; z < Lcontraidos[listaIndex - 1].length; z++)
						contraidos[z] = Lcontraidos[listaIndex - 1][z].clone();

					if (repeat) {
						// if (false) {
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
						// ==============================================================

						int idx = 0;
						// Actualizar la lista de contraidos
						for (int i1 = 0; i1 <= ultimo; i1++) {
							if (DEBUG) {
								System.out
										.println("rasgo: "
												+ Integer
														.toString(pMatriz[i1][listaIndex]
																.getId() + 1)
												+ ", mask: " + local_mask[i1]);
							}
							if (!repeated[i1]) {
								pMatriz[idx++][listaIndex] = pMatriz[i1][listaIndex];
								int curr_id = pMatriz[i1][listaIndex].getId();

								for (int r = 0; r < tempMat[0][i1]; r++) {
									// For each local repeated
									int repeated_id = tempMat[r + 1][i1];
									for (int lr = 0; lr < contraidos[0][repeated_id]; lr++) {
										// For each original repeated
										contraidos[++contraidos[0][curr_id]][curr_id] = contraidos[lr + 1][repeated_id];
									}
								}
							}
						}
						// ======= Imprimir Lista de contraidos ===========
						if (DEBUG) {
							System.out.println();
							for (int f1 = 0; f1 < Lcontraidos[listaIndex - 1][0].length; f1++) {
								System.out
										.print("Att x"
												+ Integer
														.toString(contraidos[1][f1] + 1)
												+ ": ");
								for (int f2 = 2; f2 <= contraidos[0][f1]; f2++) {
									System.out
											.print("x"
													+ Integer
															.toString(contraidos[f2][f1] + 1));
								}
								System.out.println();
							}
						}
						// ================================================

						// Lcontraidos[listaIndex] = contraidos;
						ultimo -= nrepeated;
						repeat = false;
					}
					// Conservar una referancia a la lista anterior de
					// contraidos
					// if (Lcontraidos[listaIndex]==null)
					// Lcontraidos[listaIndex] = Lcontraidos[listaIndex -
					// 1];
					// Actualizar hasta el final la listas de contraidos
					Lcontraidos[listaIndex] = contraidos;
					// for (int indice = listaIndex+1; indice <= numColumnas;
					// indice++) {
					// Lcontraidos[indice] = null;
					//
					// }
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
	private boolean typical(TuplaBinaria[] superreduct, int natts) {
		// Get testor's compatibility mask
		TuplaBinaria AMl = new TuplaBinaria(superreduct[0]);
		TuplaBinaria CMl = new TuplaBinaria(superreduct[0]);
		int i;
		for (i = 1; i < natts; i++) {
			CMl.mascComp(CMl, superreduct[i], AMl);
			AMl.mascAcep(AMl, superreduct[i]);
		}
		// Check that every attribute in testor has a typical row
		for (i = 0; i < natts; i++) {
			if (superreduct[i].andNEqZ(CMl)) {
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
	public void operatorIgual(RR objeto) {
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

	// ---------------------------------------------------------------------------
	public double[] extraerImportancia(double alpha, double beta) {
		int i;
		// -----------------
		// return frecAparicion;
		// return frecAmplitud;
		if (numColumnas > 0) {
			if (importancia == null)
				importancia = new double[numColumnas];
			for (i = 0; i < numColumnas; i++) {
				importancia[i] = alpha * frecAparicion[i] + beta
						* frecAmplitud[i];
			}
			return importancia;
		}
		return null;
	}
}
