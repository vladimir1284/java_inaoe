package tools;

public class Solution {
	public int length;
	private TuplaBinaria[] attsArray;
	public Solution(int length) {
		this.length = length;
		attsArray = new TuplaBinaria[length];
	}
	public String toString() {
		String tt = "";
		for(int i = 0; i< length; i++)
			tt += "x"+Integer.toString(attsArray[i].getId()+1);
		return tt;
	}
	public void setAttribute(int pos, TuplaBinaria att){
		attsArray[pos] =  att;
	}
	public TuplaBinaria getAttribute(int pos){
		return attsArray[pos];
	}
}
