package alghoritms.fast_br.tools;

public class TuplaBinaria {
	/**
	 * zizeOfUnidad - Tama�o en bits que ocupara la unidad de operacion binaria.
	 * numUnidades - No. de Unidades que ocupara. restoBits - Resto de bits en
	 * la ultima unidad.
	 */

	public int numBits;
	private int numUnidades;
	private int restoBits;
	public int idTupla;
	private boolean unitario;

	private int mask1;
	private int mask2;

	private int[] unidad;

	public static int zizeOfUnidad;

	// private unsigned int *unidad, mask1, mask2;

	/**
	 * /--------------------------------------------------------------------- /-
	 * Constructor de la clase: TuplaBinaria
	 * /---------------------------------------------------------------------
	 */
	public TuplaBinaria() {
		super();
	}

	public TuplaBinaria(int cardinal, int noFila) {
		super();
		set(cardinal, noFila);
	}

	public TuplaBinaria(TuplaBinaria tupla) {
		super();
		int i;
		numBits = tupla.numBits;
		numUnidades = tupla.numUnidades;
		unidad = new int[numUnidades];
		restoBits = tupla.restoBits;
		idTupla = tupla.idTupla;
		unitario = tupla.unitario;

		mask1 = tupla.mask1;
		mask2 = tupla.mask2;

		unidad = new int[numUnidades];
		for (i = 0; i < numUnidades; i++)
			unidad[i] = tupla.unidad[i];
	}

	// --------------------------------------------------------------------------
	// **************************************************************************
	// - Inicializar esta tupla (tupla_this), con la cantidad de elemntos que
	// contendr� (cardinal), y con un identificador (ID).
	// **************************************************************************
	public void set(int cardinal, int ID) {
		int total, i;

		numBits = cardinal;
		zizeOfUnidad = 32;// sizeof(unsigned int) * 8; // - registro EAX
		numUnidades = numBits / zizeOfUnidad;
		restoBits = numBits % zizeOfUnidad;
		total = numUnidades > 0 ? numUnidades : 0;
		total += numBits > 0 ? 1 : 0;
		numUnidades = total;

		mask1 = mask2 = 0;
		mask1 = ~mask1;
		for (i = 0; i < restoBits; i++)
			mask2 |= 1 << i;

		unidad = new int[total];
		for (i = 0; i < total; i++)
			unidad[i] = 0;

		idTupla = ID;
	}

	// --------------------------------------------------------------------------
	// ***************************************************************************
	// - Devuelve la tupla como un String.
	// ***************************************************************************
	public String toString(){
		String tupla = "";
		int  i;
		for (i = 0; i < numBits-1; i++)
			tupla += this.getValorEn(i) + " ";
		tupla += this.getValorEn(i);
		return tupla;		
	}

	// --------------------------------------------------------------------------
	// ***************************************************************************
	// - Esta tupla se comvierte en una copia de la tupla operando.
	// ***************************************************************************
	public void igualarA(TuplaBinaria operando) {
		short i;
		for (i = 0; i < numUnidades; i++)
			unidad[i] = operando.unidad[i];
		idTupla = operando.idTupla;
	}

	// ---------------------------------------------------------------------------
	// **************************************************************************
	// retorna verdadero si: and(tupla_this, operando) != (0,...,0)
	// **************************************************************************
	public boolean and(TuplaBinaria operando) {
		int i;
		int k;
		for (i = 0; i < numUnidades; i++) {
			k = unidad[i] & operando.unidad[i];
			if (k != 0)
				return false;
		}
		return true;
	}

	// --------------------------------------------------------------------------
	// **************************************************************************
	// Aplica el operador and y retorna cont para verificar si es != 0
	// **************************************************************************
	public int operatorAnd(TuplaBinaria operando) {
		int i;
		int cont = 0;
		for (i = 0; i < numUnidades; i++) {
			unidad[i] = unidad[i] & operando.unidad[i];
			cont += unidad[i];
		}
		return cont;
	}

	// --------------------------------------------------------------------------
	// **************************************************************************
	// ma_l+[X] = or(ma_l , X)
	// **************************************************************************
	public boolean mascAcep(TuplaBinaria operando1, TuplaBinaria operando2) {
		int i;
		boolean band = true;
		boolean band2 = true;
		int temp = mask1;

		// - Falta verificar si oper y result tienen la
		// misma cantidad de bits que esta clase.
		for (i = 0; i < numUnidades; i++) {
			unidad[i] = operando1.unidad[i] | operando2.unidad[i]; // - Aplicar
			// OR
			// - Verificar si Ma(f+x) == Ma(f)
			if (band && (unidad[i] != operando1.unidad[i]))
				band = false;
			// - Verificar si Ma(f+x) == 1...1
			if (restoBits > 0 && i == numUnidades - 1)
				temp = mask2;
			if (band2 && (unidad[i] != temp))
				band2 = false;
		}
		unitario = band2;
		return band;
	}

	// --------------------------------------------------------------------------
	// **************************************************************************
	// - Esta funcion devuelve verdadero si todos los elementos de esta tupla
	// son unitarios (1,...,1).
	// **************************************************************************
	public boolean esUnitario() {
		return unitario;
	}

	// --------------------------------------------------------------------------
	// **************************************************************************
	// - Esta funcion devuelve verdadero si todos los elementos de esta tupla
	// son cero (0,...,0).
	// **************************************************************************
	public boolean esNulo() {
		for (int i = 0; i < numUnidades; i++) {
			if (this.unidad[i] != 0)
				return false;
		}
		return true;
	}

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// mc_l+[X] = tupla_this = or(and(xor(mc_l , X), mc_l ), and(not(ma_l), X))
	// ***************************************************************************
	public void mascComp(TuplaBinaria mascara, TuplaBinaria x,
			TuplaBinaria mascaraAcep) {
		int i;
		// unsigned int temp1, temp2, temp3;

		for (i = 0; i < numUnidades; i++) {

			// - Propuesta inicial Lias.

			/*
			 * unidad[i] = (((mascara.unidad[i] ^ x->unidad[i]) &
			 * mascara.unidad[i]) | (~mascaraAcep.unidad[i] & x->unidad[i]));
			 */

			// - Cambio Propuesto Yosvany (autor principal del LEX 2003). OK.

			unidad[i] = ((mascara.unidad[i] & ~x.unidad[i]) | (~mascaraAcep.unidad[i] & x.unidad[i]));

		}
	}

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Esta tupla se comvierte en una copia de la tupla operando.
	// ***************************************************************************
	public void operatorIgual(TuplaBinaria operando) {
		short i;
		for (i = 0; i < numUnidades; i++)
			unidad[i] = operando.unidad[i];
		idTupla = operando.idTupla;
	}

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Retorna verdadero si: tupla_this es subfila de la tupla operando.
	// - Esto es si se mira las tuplas como filas en una MD.
	// ***************************************************************************
	public boolean esSubfilaDe(TuplaBinaria operando) {
		short i;
		for (i = 0; i < numUnidades; i++)
			if (operando.unidad[i] != (unidad[i] | operando.unidad[i]))
				return false;
		return true;
	}

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Evalúa si tupla_this es subfila de la tupla operando o viceversa.
	// - Esto es si se mira las tuplas como filas en una MD.
	// Restorna:
	// 0 - No son subfila (se quedan las 2)
	// 1 - tupla_this es subfila de operando (se queda tupla_this)
	// 2 - operando es subfila detupla_this (se queda operando)
	// ***************************************************************************
	public int sonSubfila(TuplaBinaria operando) {
		short i;
		int or_result;
		for (i = 0; i < numUnidades; i++) {
			or_result = unidad[i] | operando.unidad[i];
			if (operando.unidad[i] == or_result)
				return 1;
			if (this.unidad[i] == or_result)
				return 2;
		}
		return 0;
	}

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Retorna verdadero si: tupla_this es mayor que la tupla operando.
	// - En este caso, temos dos opciones para este criterio de comparacion.
	// 1. Mirar las tuplas como los n�meros que representan en notaci�n binaria.
	// 2. Mirar las tuplas seg�n la cantidad de elementos unitarios que
	// contengan.
	// ***************************************************************************
	public boolean mayorQue(TuplaBinaria operando, int opc) {
		int i;
		if (opc == 2)
			return cantValoresUnitarios() > operando.cantValoresUnitarios();
		for (i = numUnidades - 1; i >= 0; i--)
			if (unidad[i] != operando.unidad[i])
				if (unidad[i] > operando.unidad[i])
					return true;
				else
					return false;
		return false;
	}

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Retorna verdadero si: tupla_this es menor que la tupla operando.
	// - Igual al anterior.
	// ***************************************************************************
	public boolean menorQue(TuplaBinaria operando, int opc) {
		int i;
		if (opc == 2)
			return cantValoresUnitarios() < operando.cantValoresUnitarios();
		for (i = numUnidades - 1; i >= 0; i--)
			if (unidad[i] != operando.unidad[i])
				if (unidad[i] < operando.unidad[i])
					return true;
				else
					return false;
		return false;
	}

	// ***************************************************************************
	// - Retorna verdadero si: tupla_this es Igual la tupla operando.
	// ***************************************************************************
	public boolean igualA(TuplaBinaria operando) {
		int i;
		for (i = 0; i < numUnidades; i++)
			if (unidad[i] != operando.unidad[i])
				return false;
		return true;
	}

	// ---------------------------------------------------------------------------
	// Muy ineficiente
	public int cantValoresUnitarios() {
		int i;
		int contador = 0;
		for (i = 0; i < numBits; i++)
			if (getValorEn(i) == 1)
				contador++;
		return contador;
	}

	// ---------------------------------------------------------------------------
	// Muy ineficiente
	public int cantValoresUnitarios(int pos) {
		int i;
		int contador = 0;
		for (i = pos; i < numBits; i++)
			if (getValorEn(i) == 1)
				contador++;
		return contador;
	}

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Retorna retorna el valor del identificador de esta tupla.
	// ***************************************************************************
	// ---------------------------------------------------------------------------
	public int getId() {
		return idTupla;
	}

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Extraer el valor del elemento que ese encuentra en una posicion dada.
	// ***************************************************************************
	public int getValorEn(int posicion) {
		int k, rk, t;
		k = posicion / zizeOfUnidad;
		rk = posicion % zizeOfUnidad;
		t = unidad[k] & 1 << rk;
		t = t != 0 ? 1 : 0;
		return t;
	}

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Colocar el valor (unitario o no unitario), en una posicion dentro de la
	// tupla.
	// ***************************************************************************
	public void setValorEn(int pos, int valor) {
		int k, rk, var = 1;
		k = pos / zizeOfUnidad;
		rk = pos % zizeOfUnidad;
		if (valor != 0) {
			unidad[k] = unidad[k] | (var << rk);
		} else
			unidad[k] = unidad[k] & (~(var << rk));
	}

	// ---------------------------------------------------------------------------
	// ***************************************************************************
	// - Colocar el valor (unitario o no unitario), en una posicion dentro de la
	// tupla.
	// ***************************************************************************
	public void setValorEn(int pos, boolean valor) {
		int k, rk, var = 1;
		k = pos / zizeOfUnidad;
		rk = pos % zizeOfUnidad;
		if (valor) {
			unidad[k] = unidad[k] | (var << rk);
		} else
			unidad[k] = unidad[k] & (~(var << rk));
	}
}
