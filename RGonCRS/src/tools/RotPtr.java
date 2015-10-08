package tools;

public class RotPtr {
	public int current;
	private int mask;
	private int proct;

	public RotPtr(int atts){
		proct = 0;
		int n = 1, size;
		size = (atts-1)*atts/2; // Max depth in the array
		while (n < size){
			n = n<<1;
		}
		mask = n-1; // min mask of ones covering size
		current = mask;
	}

	public int next() {
		// rotates the pointer
		current = (current + 1) & mask;
//		if (current == 0){
//			current += proct+1;
//		}
		return current;
	}

//	public void protect() {
//		// protect from the start to this position
//		proct = current;
//	}
//
//	public void release(int release) {
//		proct = current;		
//	}

}
