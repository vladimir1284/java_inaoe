import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

public class TestorFinder implements Runnable {
	private final BlockingQueue<BigInteger> queue;
	BigInteger firstRow;
	int atts;
	BasicMatrix bm;

	public TestorFinder(BlockingQueue<BigInteger> q, BigInteger firstRow,
			int atts, BasicMatrix bm) {
		queue = q;
		this.firstRow = firstRow;
		this.atts = atts;
		this.bm = bm;
	}

	public void run() {
		CandidateGenerator cg = new CandidateGenerator(this.firstRow, this.atts);
		CandidateEval ce = new CandidateEval();
		boolean done = false;
		boolean Contributes;

		// Find Testors
		while (!done) {
			this.bm.evaluateCandidate(cg.Current, ce);
			Contributes = (ce.Nsatisfy > cg.Previous[cg.pervIndex]);
			if (ce.Testor) {
				try {
					queue.put(cg.Current);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			cg.Previous[cg.J] = ce.Nsatisfy;
			done = cg.getCurrentCandidate(ce.Testor, Contributes);

		}
		// Indicate termination
		try {
			queue.put(BigInteger.ZERO);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
