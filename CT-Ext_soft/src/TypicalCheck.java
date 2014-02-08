import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

public class TypicalCheck implements Runnable {
	private final BlockingQueue<BigInteger> queue;
	BasicMatrix bm;
	int rows;

	public TypicalCheck(BlockingQueue<BigInteger> q, BasicMatrix bm, int rows) {
		queue = q;
		this.bm = bm;
		this.rows = rows;
	}

	public void run() {
		BigInteger testor = null;

		// Test for Typical Testors
		while (true) {
			// read testor
			try {
				testor = queue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (testor.equals(BigInteger.ZERO)) {
				break;
			}
			if (this.bm.checkTypical(testor)) {
				System.out.println(testor);
			}
		}

	}

}
