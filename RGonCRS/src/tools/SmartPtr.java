package tools;

public class SmartPtr {
	int next_ptr;
	public int size;
	int[] ptr_arr;

	public SmartPtr(int atts) {
		size = (atts - 1) * atts / 2+1; // Max depth in the array
		ptr_arr = new int[size];
		// Init pos array in natural order
		for (int i = 0; i < size; i++) {
			ptr_arr[i] = i;
		}
		next_ptr = 0; // Point to the first position
	}

	public void next() {
		next_ptr++; // shift to the next and return the head 
	}

	public void release(int pos) {
		ptr_arr[--next_ptr] = pos; // shift back the pointer and save the
									// released position in the head

	}
	public int get(){
		return ptr_arr[next_ptr];
	}

}
