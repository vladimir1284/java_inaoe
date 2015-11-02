/****************************************************************************
Archivo    : fast_ct_ext.java
Contenido  : rutinas para el calculo de los testores tipicos de una MB
             utilizando el nuevo algoritmo tt (escala interior)
             se usa la nueva representacion para verificar cuando
             una combinacion contribuye o no a la combinacion en cuestion.

             Version de prueba. Calcula TT y TE, va eliminando los TE
             cuando se encuentra un super cto. de el.

Dependencia: Ninguna
Compilador : djgpp / otro que maneje enteros a 32 bits
 ****************************************************************************/

package edu.tt.alghoritms.fast_ct_ext;

import edu.tt.InterfaceTT;
import edu.tt.Output;

public class FAST_CT_EXT implements InterfaceTT {
	/* constantes generales */
	private static final int TAM_BYTE = 8;
	private static final char BELL = '\r';// '\a'; HHH
	private static final int TAM_NOM = 80;
	private static final int PRIMER_FILA = 0;
	private static final int NT = 0;
	private static final int TE = 1;
	private static final int TT = 2;
	private static final int MASCARA = 0x80000000;
	private static final int SIN_CEROS = 0xFFFFFFFF;
	// ---------------------------------------------------------------------
	/* varibles de la clase a utilizar */
	private String nom_resul; // nombre del archivo de resultados
	NODO_LISTA cabeza; // cabeza de la lista de TT
	NODO_COMBINACION cabeza_c; // cabeza de la combinacion de rasgos
	private byte[][] mb; // MB boolena
	private int[][] mb_bits; // MB en bits
	private int[] diferencia; // arreglo para verificar si hay contrib
	private int[] masc_com; // mascara complementaria para saber si hay filas de
	// ceros en mb
	private int[] and1; // arreglos para verificar la tipicidad
	private int filas, rasgos; // numero de filas y rasgos de la MB
	private int longitud_filas_bits; // tamano en enteros del No. de filas de la
	// MB
	private int tam_testor; // contiene el tamano del entero en bits
	private int longitud_testor; // tamano del TT empaquetado
	private int[] pos_rasgos; // posicion de los rasgos ordenados en MB
	private int band_tt; // bandera que indica si la comb es TT
	private int band_cto; // bandera para determinar si una comb es TE

	private int[] solucion; // - **
	private int numTestores;
	private int  comprobaciones;
	// time_t t_ini, t_fin; // variables para medir el tiempo HHH

	// ---------------------------------------------------------------------
	/* estructuras que manejan el TT en formacion (Ahora clases internas) */
	private class NODO_COMBINACION {
		public int atributo; // contendra el atributo de la combinacion
		public int[] contribucion; // contribucion de la combinacion con este
		public NODO_COMBINACION siguiente;
	};

	/* estructuras que manejan la lista de TT */
	private class NODO_TESTOR {
		public int valor;
		public NODO_TESTOR liga;
	};

	private class NODO_LISTA {
		NODO_TESTOR testor;
		NODO_LISTA siguiente;
	};

	// ---------------------------------------------------------------------
	public FAST_CT_EXT() {
		super();		
	}
	// ---------------------------------------------------------------------
	public void set(int numFilas, int numRasgos) {
		mb = Crea_matriz(numFilas, numRasgos); // creamos la matriz basica
		filas = numFilas;
		rasgos = numRasgos;
		Calcula_longitud_tt(); // calculamos la longitud de los TT en enteros
		Calcula_longitud_filas_bits(); // calc. la long. filas en bits
		pos_rasgos = Crea_arreglo_int(numRasgos);
		solucion = new int[numRasgos];
		numTestores = 0;
	}

	//---------------------------------------------------------------------
	public void pushValor(int valor, int fila, int columna) {
		mb[fila][columna] = (byte) valor;
	}

	/*
	 * int main() {
	 * 
	 * if (//Carga_archivo()) { //time(&t_ini); Ordena_mb(); // ordenamos la mb
	 * Crea_mb_bits(); // generamos la mb en bits, para calcular la contribucion
	 * Algoritmo_tt(); // aplicamos el algoritmo //time(&t_fin);
	 * Guarda_resultados(); //Libera_memoria(); }
	 */
	// ---------------------------------------------------------------------
	/*
	 * // lee la MB booleana de archivo HHH (incluir el codigo.....)
	 */
	// ---------------------------------------------------------------------
	/*
	 * Rutinas para la creacion y manejo de la memoria dinamica
	 */
	// ---------------------------------------------------------------------
	/* crea un arreglo de longitud n de enteros dinamicamente */
	private int[] Crea_arreglo_int(int n) {
		return new int[n];
	}

	// ---------------------------------------------------------------------
	/* crea un arreglo de longitud n de enteros sin signo dinamicamente */
	private int[] Crea_arreglo_unsigned(int n) {
		return new int[n];
	}

	// ---------------------------------------------------------------------
	/* crea una matriz de tamano nxm de unsigned int dinamicamente */
	private int[][] Crea_matriz_unsigned(int n, int m) {
		int i;
		int[][] nueva = new int[n][];
		for (i = 0; i < n; i++)
			nueva[i] = Crea_arreglo_unsigned(m);
		return nueva;
	}

	// ---------------------------------------------------------------------
	/* crea un arreglo de longitud n de chars dinamicamente */
	private byte[] Crea_arreglo(int n) {
		return new byte[n];
	}

	// ---------------------------------------------------------------------
	/* crea una matriz de tamano mxn de chars dinamicamente */
	private byte[][] Crea_matriz(int n, int m) {
		int i;
		byte[][] nueva = new byte[n][];
		for (i = 0; i < n; i++)
			nueva[i] = new byte[m];// Crea_arreglo(n);
		return nueva;
	}

	// ---------------------------------------------------------------------
	/* crea una nueva lista testor */
	private NODO_LISTA Crea_testor(int[] lista_testor) {
		NODO_LISTA t;
		NODO_TESTOR tt1, tt2;
		int i;

		/* creamos el nuevo testor y su primer elemento */
		t = new NODO_LISTA();
		t.siguiente = null;

		tt1 = new NODO_TESTOR();

		t.testor = tt1;
		tt1.valor = lista_testor[0];
		for (i = 1; i < longitud_testor; i++) {
			tt2 = new NODO_TESTOR();
			tt1.liga = tt2;
			tt2.valor = lista_testor[i];
			tt1 = tt2;
		}
		tt1.liga = null; /* para que ya no apunte a otro */

		return t;
	}

	// ---------------------------------------------------------------------
	/* crea un nuevo nodo para el atributo generado */
	private NODO_COMBINACION Crea_atributo(int atrib) {
		NODO_COMBINACION nuevo;
		int i;
		nuevo = new NODO_COMBINACION();
		nuevo.atributo = atrib;
		nuevo.siguiente = null;
		nuevo.contribucion = Crea_arreglo_unsigned(longitud_filas_bits);

		if (atrib != -1) // si no es la cabeza
			// copiamos el atributo de la mb en bits
			for (i = 0; i < longitud_filas_bits; i++)
				nuevo.contribucion[i] = mb_bits[i][pos_rasgos[atrib]];

		return nuevo;
	}

	// ---------------------------------------------------------------------
	/*
	 * elimina la lista de TT actual, menos la cabeza // void Elimina_lista_tt()
	 * HHH
	 * 
	 * libera la memoria ocupada por una matriz void Libera_matriz(charimg, int
	 * tam) / libera la memoria ocupada por una matriz void
	 * Libera_matriz_unsigned(unsigned intimg, int tam) void Libera_memoria()
	 */
	// ---------------------------------------------------------------------
	/* Genera la matriz de bits, para calcular las contribuciones */
	private void Crea_mb_bits() {
		int i, j;
		int masc_aux;

		mb_bits = Crea_matriz_unsigned(longitud_filas_bits, rasgos);

		for (i = 0; i < filas; i++)
			for (j = 0; j < rasgos; j++)
				if (mb[i][j] == 1) // prendemos el bit en la mb_bits
					Prende_bit(i, j);

		diferencia = Crea_arreglo_unsigned(longitud_filas_bits);

		// generamos la mascara de bits complementaria
		masc_com = Crea_arreglo_unsigned(longitud_filas_bits);
		for(i=0; i<longitud_filas_bits; i++) masc_com[i] = SIN_CEROS;
		
		and1 = Crea_arreglo_unsigned(longitud_filas_bits);

		masc_aux = SIN_CEROS;
		i = longitud_filas_bits - 1; // posicion del ultimo byte en el arreglo
		masc_aux = masc_aux << (tam_testor - (filas % tam_testor)); /* a partir de que bit */
		masc_com[i] = masc_aux;
//		masc_com[i] = masc_com[i] | masc_aux;
	}

	// ---------------------------------------------------------------------
	/* pone en 1 el bit de la columna en la posicion mandada */
	private void Prende_bit(int posicion, int columna) {
		int masc;
		int pos_byte;

		masc = MASCARA;
		pos_byte = posicion / tam_testor; /* en que byte */
		masc = (int)1 << ((tam_testor-1) - (posicion % tam_testor)); /* en que bit */
		mb_bits [pos_byte][columna] = mb_bits[pos_byte][columna] | masc;
	}

	// ---------------------------------------------------------------------
	/*
	 * Rutinas del algoritmo para generar testores tipicos
	 */
	// ---------------------------------------------------------------------
	public void extraerTT(Output registerData) {
		int rasgo_unitario, ultimo_rasgo, rasgo_siguiente;
		int contrib;
		NODO_COMBINACION atrib;

		Ordena_mb(); // ordenamos la mb
		Crea_mb_bits(); // generamos la mb en bits, para calcular la
						// contribucion
		// creamos la cabeza de la lista de testores
		  registerData.getInfo("\n *************************************** ");
		  registerData.getInfo("\n   ALGORITMO --  FAST CT_EXT ");
		  registerData.getInfo("\n ***************************************\n");


		cabeza = Crea_testor(Crea_arreglo_unsigned(rasgos));
		cabeza_c = Crea_atributo(-1); // creamos la cabeza de la lista de
		// atributos

		rasgo_unitario = 0; // primer rasgo a analizar
		comprobaciones = 0; // Contador de comprobaciones
		
		while (mb[PRIMER_FILA][pos_rasgos[rasgo_unitario]] != 0) // Para cada
		// columna
		// imprescindible
		{
			atrib = Crea_atributo(rasgo_unitario);
			ultimo_rasgo = rasgo_siguiente = atrib.atributo;
			Anexa_atributo(atrib);
			Calcula_contribucion(atrib); // estos atributos siempre contribuyen

			if (band_tt == TE) // verificamos si es TE el rasgo solo
			{
				Guarda_tt(registerData);
				ultimo_rasgo = Elimina_ultimo_atributo();
			} else // generamos las combinaciones con los restantes atributos
			{
				do {
					// verificamos si puede anexarse otro rasgo a la combinacion
					if ((rasgo_siguiente = Busca_siguiente(rasgo_siguiente)) > 0) {
						atrib = Crea_atributo(rasgo_siguiente);
						Anexa_atributo(atrib);
						contrib = Calcula_contribucion(atrib);

						if (contrib != 0) // hay contribucion
						{
							if (band_tt == NT) // se continua anexando rasgos
								ultimo_rasgo = atrib.atributo;
							else if (band_tt == TE) // es TE
							{
								Guarda_tt(registerData);
								ultimo_rasgo = Elimina_ultimo_atributo();
							}
						} else
							// eliminamos el atributo de la lista
							ultimo_rasgo = Elimina_ultimo_atributo();
					} else // eliminamos el ultimo atributo de la lista
					{
						rasgo_siguiente = ultimo_rasgo;
						ultimo_rasgo = Elimina_ultimo_atributo();
					}
				} while (cabeza_c.siguiente != null);
			}

			rasgo_unitario++;
		}
	}

	// ---------------------------------------------------------------------
	/*
	 * calcula la contribucion del nuevo atributo con la lista; modifica el
	 * estado de la bandera global tt
	 */
	private int Calcula_contribucion(NODO_COMBINACION atrib) {
		NODO_COMBINACION a1;
		int i, band_cont;

		a1 = cabeza_c;
		while (a1.siguiente != atrib)
			// nos posicionamos en el ultimo rasgo de la combinacion
			a1 = a1.siguiente;

		// se verifica si hay filas de ceros en la mb, y se modifica
		for (i = 0; i < longitud_filas_bits; i++)
			atrib.contribucion[i] = a1.contribucion[i] | atrib.contribucion[i];

		band_cont = 0; // asumimos que no hay contribucion

		// verificamos si es NT o TE la combinacion con el nuevo atributo
		if (Filas_ceros(atrib.contribucion, longitud_filas_bits) != 0) // hay
		// filas de ceros
		{
			band_tt = NT; // fue no testor

			if (Diferencia_mayor_cero(atrib.contribucion, a1.contribucion) != 0)
				band_cont = 1; // hay contribucion
		} else {
			band_cont = 1; // hay contribucion
			band_tt = TE; // fue testor, pero no se sabe si es tipico
		}

		return band_cont;
	}

	// ---------------------------------------------------------------------
	/* retorna 1 si existen filas de ceros en el vector; 0 E.O.C. */
	private int Filas_ceros(int[] vector, int tam) {
		int band, i;
		band = 0; // suponemos que ninguna fila es de ceros
		i = 0;
		while ((i < tam) && (band == 0)) {
			if (vector[i] != masc_com[i])
				band = 1; // hay filas de ceros
			//if ((vector[i] | masc_com[i]) != SIN_CEROS)				
			i++;
		}
		return band;
	}

	// ---------------------------------------------------------------------
	/* retorna 1 si la diferencia de v1 con v2 es mayor que 0; 0 e.o.c. */
	private int Diferencia_mayor_cero(int[] v1, int[] v2) {
		int band, i;
		band = 0; // suponemos que la diferencia no es mayor que cero

		for (i = 0; i < longitud_filas_bits; i++)
			diferencia[i] = v1[i] - v2[i];
		if (Mayor_cero(diferencia, longitud_filas_bits) != 0)
			band = 1;
		return band;
	}

	// ---------------------------------------------------------------------
	/* retorna 1 si el vector mandado es diferente a 0; 0 e.o.c. */
	private int Mayor_cero(int[] vector, int longitud) {
		int band, i;

		band = 0; // suponemos no es mayor que cero
		i = 0;
		while ((i < longitud) && (band == 0)) {
			if (vector[i] != 0)
				band = 1;
			i++;
		}
		return band;
	}
	// ---------------------------------------------------------------------
	/* busca el siguiente rasgo para ver si puede anexarse a la lista */
	private int Busca_siguiente(int rasgo_siguiente) {
		int sig_atributo;

		if (rasgo_siguiente < rasgos - 1)
			sig_atributo = rasgo_siguiente + 1;
		else
			sig_atributo = -1;

		return (sig_atributo);
	}

	// ---------------------------------------------------------------------
	/* anexa el nuevo atributo en la lista de atributos generada */
	private void Anexa_atributo(NODO_COMBINACION atrib) {
		NODO_COMBINACION a1;

		// ponemos el apuntador en la ultima posicion no nula
		a1 = cabeza_c;

		while (a1.siguiente != null)
			a1 = a1.siguiente;
		a1.siguiente = atrib; // lo adicionamos a la combinacion de rasgos
	}

	// ---------------------------------------------------------------------
	/* elimina de la lista el atributo mandado */
	private int Elimina_ultimo_atributo() {
		NODO_COMBINACION a1, a2;
		int ultimo;

		a1 = cabeza_c;
		a2 = a1.siguiente;

		while (a2.siguiente != null) // nos posicionamos en el ultimo atributo
		{
			a2 = a2.siguiente;
			a1 = a1.siguiente;
		}
		a1.siguiente = a2.siguiente; // actualizamos los apuntadores
		ultimo = a1.atributo;

		band_tt = NT; // actualizamos la bandera
		return (ultimo);
	}

	// ---------------------------------------------------------------------
	/* guarda el tt generado en la lista de testores */
	public void Guarda_tt(Output registerData) {
		//int[] lista_testor;
		//NODO_LISTA t1, t2;
		NODO_COMBINACION a1;
		
		if (Es_tt() != 0) // verificamos si es TT la combinacion
		{
			solucion[0] = 0;
			a1 = cabeza_c.siguiente;
			while (a1 != null) {	
				solucion[0]++;
				solucion[solucion[0]] = pos_rasgos[a1.atributo]+1;//a1.atributo+1;
				a1 = a1.siguiente;				
			}
			numTestores++;
			registerData.resgistTT(solucion);
			/*lista_testor = new int[longitud_testor + 1];
			// guardamos la combinacion en la lista de TT
			Empaqueta_lista(lista_testor);
			t1 = Crea_testor(lista_testor);
			t2 = cabeza.siguiente;
			t1.siguiente = t2;
			cabeza.siguiente = t1; // lo anexamos al principio de la lista
			*/
		}
	}

	// ---------------------------------------------------------------------
	/* empaqueta al individuo y lo deja en lista_testor */
	private void Empaqueta_lista(int[] lista_testor) {
		NODO_COMBINACION a1;
		int masc;
		int pos_byte;

		a1 = cabeza_c.siguiente;

		/* ponemos en uno los elementos de la lista */
		while (a1 != null) {
			masc = MASCARA;
			/* en que byte */
			pos_byte = pos_rasgos[a1.atributo] / tam_testor;
			/* en que bit */
			masc = masc >> (pos_rasgos[a1.atributo] % tam_testor);
			lista_testor[pos_byte] = (int) lista_testor[pos_byte] | masc;
			a1 = a1.siguiente;
		}
	}

	// ---------------------------------------------------------------------
	/* calcula el tamano del testor para su almacenamiento */
	private void Calcula_longitud_tt() {

		// tam_testor = sizeof(int)*TAM_BYTE; // - HHH
		// - Determinar el # de bit que ocupa un int.
		int a = 1, cont = 0; while (a != 0) { a <<= 1; cont++; }
		tam_testor = cont; // 

		if ((rasgos % tam_testor) != 0)
			longitud_testor = rasgos / tam_testor + 1;
		else
			/* division entera exacta */
			longitud_testor = rasgos / tam_testor;
	}

	// ---------------------------------------------------------------------
	/* calcula el tamano de las filas en bit para su almacenamiento */
	private void Calcula_longitud_filas_bits() {

		if ((filas % tam_testor) != 0)
			longitud_filas_bits = filas / tam_testor + 1;
		else
			/* division entera exacta */
			longitud_filas_bits = filas / tam_testor;
	}

	// ---------------------------------------------------------------------
	/* verifica si la combinacion mandada es TT */
	private int Es_tt() {
		NODO_COMBINACION x_solo, x_or;
		int band, i;
		int[] or1; // arreglo para verificar la tipicidad

		x_solo = cabeza_c.siguiente;
		band = 1;

		// verificamos la condicion de tipicidad
		while ((x_solo != null) && (band != 0)) {
			x_or = cabeza_c.siguiente;

			or1 = Crea_arreglo_unsigned(longitud_filas_bits);

			// hacemos el OR con todos los rasgos restantes
			while (x_or != null) {
				if (x_or != x_solo)
					for (i = 0; i < longitud_filas_bits; i++)
						or1[i] = (or1[i])
								| (mb_bits[i][pos_rasgos[x_or.atributo]]);
				x_or = x_or.siguiente;
			}

			// realizamos la operacion para distinguir filas con un solo uno
			for (i = 0; i < longitud_filas_bits; i++)
				and1[i] = (mb_bits[i][pos_rasgos[x_solo.atributo]]) & (~or1[i]);

			if (Mayor_cero(and1, longitud_filas_bits) == 0)
				band = 0;

			x_solo = x_solo.siguiente;
		}
		return (band);
	}

	// ---------------------------------------------------------------------
	/* guarda los TT encontrados en disco */
	// void Guarda_resultados()
	// ---------------------------------------------------------------------
	/*
	 * Rutinas para realizar el ordenamiento de la matriz
	 */
	/*
	 * ordena la MB. filas: en orden ascendente; columnas: las columnas
	 * indispensables van al inicio
	 */
	void Ordena_mb() {
		int[] unos_filas;
		int[] pos_filas;
		int[] unos_rasgos;

		unos_filas = Crea_arreglo_int(filas);
		pos_filas = Crea_arreglo_int(filas);
		unos_rasgos = Crea_arreglo_int(rasgos);

		Calcula_unos_filas(unos_filas, pos_filas);
		Ordena_ascendente(unos_filas, pos_filas, filas); // ordenamos las filas
		// de MB
		Cambia_filas_mb(pos_filas); // cambiamos las filas en MB ordenadas

		Marca_unos_rasgos(unos_rasgos, pos_rasgos);
		Ordena_descendente(unos_rasgos, pos_rasgos, 0, rasgos); // ordenamos las
		// columnas de
		// MB
		Ordena_descendente_ordenados(unos_rasgos, pos_rasgos);
	}

	// ---------------------------------------------------------------------
	private void Ordena_descendente_ordenados(int[] vector, int[] posicion) {
		int i, j;
		int[] unos;

		unos = Crea_arreglo_int(rasgos);

		for (j = 0; j < rasgos; j++)
			for (i = 0; i < filas; i++)
				unos[j] += mb[i][j];

		j = 0; // buscamos la posicion del ultimo uno en la primera fila
		while (vector[posicion[j]] != 0)
			j++;
		// ordenamos las columnas imprescindibles
		Ordena_descendente(unos, posicion, 0, j);
		// ordenamos las columnas restantes
		Ordena_descendente(unos, posicion, j, rasgos);
	}

	// ---------------------------------------------------------------------
	/* ordena en forma descendente el vector mandado */
	private void Ordena_descendente(int[] vector, int[] posicion, int ini,
			int tam) {
		int i, j, aux;
		for (i = ini; i <= tam - 2; i++)
			for (j = tam - 1; j > i; j--)
				if (vector[posicion[j]] > vector[posicion[j - 1]]) {
					aux = posicion[j];
					posicion[j] = posicion[j - 1];
					posicion[j - 1] = aux;
				}
	}

	// ---------------------------------------------------------------------
	/* ordena en forma ascendente el vector mandado */
	private void Ordena_ascendente(int[] vector, int[] posicion, int tam) {
		int i, j, aux;

		for (i = 0; i <= tam - 2; i++)
			for (j = tam - 1; j > i; j--)
				if (vector[posicion[j]] < vector[posicion[j - 1]]) {
					aux = posicion[j];
					posicion[j] = posicion[j - 1];
					posicion[j - 1] = aux;
				}
	}

	// ---------------------------------------------------------------------
	/* Cambia las filas de MB para que queden en el orden mandado */
	void Cambia_filas_mb(int[] pos_filas) {
		byte[][] mb_aux;
		int i, j;

		mb_aux = Crea_matriz(filas, rasgos);

		for (i = 0; i < filas; i++)
			// creamos la matriz con el orden mandado
			for (j = 0; j < rasgos; j++)
				mb_aux[i][j] = mb[pos_filas[i]][j];

		for (i = 0; i < filas; i++)
			// Copiamos la matriz ordenada en la mb
			for (j = 0; j < rasgos; j++)
				mb[i][j] = mb_aux[i][j];
	}

	// ---------------------------------------------------------------------
	/* Marca los 1's en la primera fila de MB */
	private void Marca_unos_rasgos(int[] unos_rasgos, int[] pos_rasgos) {
		int j;
		for (j = 0; j < rasgos; j++) {
			pos_rasgos[j] = j;
			if (mb[PRIMER_FILA][j] != 0) // tiene un uno
				unos_rasgos[j] = 1;
		}
	}

	// ---------------------------------------------------------------------
	/*
	 * calcula el numero de unos de cada fila de MB, y almacena la posicion
	 * inicial de las filas en la matriz
	 */
	private void Calcula_unos_filas(int[] unos_filas, int[] pos_filas) {
		int i, j;

		for (i = 0; i < filas; i++) {
			pos_filas[i] = i;
			for (j = 0; j < rasgos; j++)
				if (mb[i][j] != 0) // tiene un uno
					unos_filas[i]++;
		}
	}
	// ---------------------------------------------------------------------
	//----------------------------------------------------------------
    public int getNumSoluciones() {
        return numTestores;
    }

}
