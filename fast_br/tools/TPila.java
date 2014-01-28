package alghoritms.fast_br.tools;

public class TPila {
	public int [] pila;
	public int tope;  /** # de elementos en la pila; */
	private int max;   /** Maximo # de elementos en la pila */

	/** 
	/---------------------------------------------------------------------
	/- Constructor de la clase: TPila
	/---------------------------------------------------------------------
	*/
	public TPila() {
		super();
		tope = -1;
		max = 0;
	}
	//--------------------------------------------------------------------
	public void setRange (int rang) {
		this.max = rang;
		this.pila = new int[rang];
	}
	//--------------------------------------------------------------------
	public void push (int val) {		
		if (this.tope < this.max-1) {
			this.tope++;
			this.pila[tope] = val;			
		}
	}
	//--------------------------------------------------------------------
	public int pop () {
		  if (this.tope >=0) return this.pila[tope--];
		  else return -1; //this.pila[0];
	}
	//--------------------------------------------------------------------
	public int get (int n) {
		  if (n>=0 && n<=tope)
		    return pila[n];
		  else return pila[0];
	}
	//--------------------------------------------------------------------
	public void cut (int n) {
		  if (n>=0 && n<=tope)
		    tope=n;
	}
	//--------------------------------------------------------------------
	public boolean vacia () {
		  if (tope == -1) return true;
		  return false;
	}
	//--------------------------------------------------------------------
}
