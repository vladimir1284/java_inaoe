package fastCText2;

public class BMcolumn {
	static final int [] ONES= {1,3,7,15,31,63,127,255,511,1023,2047,4095,
	                           8191,16383,32767,65535,131071,262143,524287,
	                           1048575,2097151,4194303,8388607,16777215,
	                           33554431,67108863,134217727,268435455,
	                           536870911,1073741823,2147483647,-1};
	
	public int length; // The number of words in the int array representation
	public int rest; // The number of bits in the last word minus 1
	public int nwords;
	public int[] data;

	public BMcolumn(int[] data, int length) {
		this.length = length;
		this.nwords = (this.length + 31) / 32; // ceil division
		this.rest 	= length - (this.nwords-1)*32 -1;
		this.data 	= data;
	}

	public BMcolumn(String column) {
		this.length = column.length();
		this.nwords = (this.length + 31) / 32; // ceil division
		this.rest 	= length - (this.nwords-1)*32 -1;
		this.data = new int[this.nwords];

		// filing the data array
		if (this.nwords == 1) {
			this.data[0] = parseInt(column);
		} else {
			int i;
			for (i = 0; i < this.nwords - 1; i++) {
				// From binary string to integer
				this.data[i] = parseInt(
						column.substring(i * 32, (i + 1) * 32));
			}
			// Remaining bits
			this.data[this.nwords - 1] = parseInt(column.substring(i * 32));
		}
	}

	public int parseInt(String s) {
		/*
		 * Parse binary strings up to 32 chars
		 */

		if (s == null) {
			throw new NumberFormatException("null");
		}

		int result = 0, digit;
		int negresult = -2147483648;
		int i = 0, len = s.length();
		boolean negative = false;
		
		if ((s.charAt(0) == '1')&(len==32)) {
            negative = true;
            i = 1;
        }

		while (i < len) {
			digit = Character.digit(s.charAt(i++), 2);
			result *= 2;
			result -= digit;
		}
		return negative ? negresult-result : -result;

	}

	public boolean eqz() {
		// Check if the data is equal to zero
		for (int i = 0; i < this.nwords; i++) {
			if (this.data[i] != 0) {
				return false;
			}
		}
		return true;
	}

	public boolean eqOnes() {
		// Check if the data has a 1 in every position
		int i;
		for (i = 0; i < this.nwords - 1; i++) {
			if (this.data[i] != -1) {// -1 is the twos complement full of 1's
				return false;
			}
		}
		// Remaining word
		if (this.data[i] != ONES[this.rest]) {
			return false;
		}
		return true;
	}

	 public boolean equals(int[] operand){
	 // Check if the data is equal to operand
	 for (int i = 0; i < this.nwords; i++) {
	 if (this.data[i] != operand[i]){
	 return false;
	 }
	 }
	 return true;
	 }
	
	public int[] and(int[] operand) {
		// Returns binary AND between data and operand
		int[] result = new int[this.nwords];
		for (int i = 0; i < this.nwords; i++) {
			result[i] = this.data[i] & operand[i];
		}
		return result;
	}

	public int[] or(int[] operand) {
		// Returns binary OR between data and operand
		int[] result = new int[this.nwords];
		for (int i = 0; i < this.nwords; i++) {
			result[i] = this.data[i] | operand[i];
		}
		return result;
	}

	public int[] not() {
		// Returns binary complement of data
		int[] result = new int[this.nwords];
		for (int i = 0; i < this.nwords; i++) {
			result[i] = ~this.data[i];
		}
		return result;
	}

	public int[] getCMlx(int[] CMl, int[] AMl) {
		// Returns the Compatibility Mask of L + data
		// Lias-Rodríguez, A., & Pons-Porrata, A. (2009).
		// BR: A new method for computing all typical testors.
		// Lecture Notes in Computer Science, 433–440.
		int[] result = new int[this.nwords];
		for (int i = 0; i < this.nwords; i++) {
			result[i] = ((~this.data[i]) & CMl[i]) | ((~AMl[i]) & this.data[i]);
		}
		return result;
	}

	public boolean andEqZ(int[] operand) {
		// Check if the binary AND between data and operand is equal to zero
		for (int i = 0; i < this.nwords; i++) {
			if ((this.data[i] & operand[i]) != 0) {
				return false;
			}
		}
		return true;
	}
}
