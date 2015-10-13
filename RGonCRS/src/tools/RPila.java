package tools;


public class RPila {
	public int[] atts;
	public TuplaBinaria[] CMs;
	public int current; // Index behind the last element in the stack
	public int head; // Index of the first element in the stack
	int max; // Max number of elements in the stack
	
	public RPila(int n_atts, int n_rows){
		atts = new int [n_atts];
		CMs = new TuplaBinaria [n_atts];
		max = n_atts;
		current = 0;
		head = 0;
		for (int i=0;i<n_atts;i++){
			CMs[i] = new TuplaBinaria(n_rows,i);
		}
	}
	
	public void push(){
		current++;
	}
	
	public int popFirst(){
		return atts[head++];
	}
	
	public void clear(){
		current = 0;
		head = 0;
	}
}
