import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Node Class: implement Runnable to enable multithreading. 
 * Each processor is viewed as a Node and will be initialized at the driver as one thread. 
 * Node will maintain three queue to keep track of the message: failMsgQue, lockMsgQue, wainingQue
 */
public class Node implements Runnable {
	/**
	 * Interface of await. 
	 * Abstract method condition will be implemented due to various await condition.
	 */
	public interface Waitable {
		public boolean condition();
	}
	/**
	 *  Await function, program will continue when the condition is satisfied.
	 * @param w interface Waitable, method condition will be implemented at CS.
	 */
	public void await(Waitable w) {
		while (!w.condition()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// Porcess ID
	int id = 0;
	// Message Counter
	int msgCount = 0;
	// Lamport timestamp, using AtomicInteger to ensure safety.
	static AtomicInteger ts = new AtomicInteger();
	Queue<Request> lockMsgQue = null;
	PriorityBlockingQueue<Request> waitingQue = null;
	Queue<Request> failMsgQue = null;
	boolean INQsent = false;
	Request cur_locking_request = null;
	boolean in_cs = false;
	boolean ReleSent = false;
	int req_quota = 0;
	ArrayList<Node> quorum = null;
	public Queue<Request> messageQue = null;
	/**
	 * Initialization function
	 * @param id Porcessor id
	 * @param R list of nodes in quorum
	 * @param reqQuota number of total quest for this processor
	 */
	public void init(int id, ArrayList<Node> R, int reqQuota) {
		this.id = id;
		quorum = R;
		req_quota = reqQuota;
		lockMsgQue = new ConcurrentLinkedQueue<Request>();
		Comparator<Request> comparator = new RequestComparator();
		waitingQue = new PriorityBlockingQueue<Request>(3, comparator);
		failMsgQue = new ConcurrentLinkedQueue<Request>();
		messageQue = new ConcurrentLinkedQueue<Request>();
	}

	@Override
	/**
	 * function run will execute CS.
	 * After each execution, it will sleep for several seconds
	 */
	public void run() {
		new Thread(new MsgListener()).start();
		for (int i = 0; i < req_quota; i++) {
			CS();
		}
		while (true) {
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	void onRequest(Request r) {
		if (cur_locking_request == null) {
			sendMsg(r.node, "lock", 0);
			cur_locking_request = r;
		} else {
			waitingQue.add(r);
			Request item = waitingQue.peek();
			if (r.ts > cur_locking_request.ts || r.ts > item.ts) {
				sendMsg(r.node, "fail", 0);
			} else {
				if (INQsent == false) {
					sendMsg(r.node, "inquire", cur_locking_request.ts);
					INQsent = true;
				}
			}
		}
	}

	void onRelease(Request r) {
		if (waitingQue.size() > 0) {
			cur_locking_request = waitingQue.poll();
			sendMsg(cur_locking_request.node, "lock", 0);
		} else {
			cur_locking_request = null;
		}
	}

	void onFail(Request r) {
		failMsgQue.add(r);
	}

	void onLock(Request r) {
		lockMsgQue.add(r);
	}

	void onInquire(Request r) {
		if (ReleSent == false) {
			if (failMsgQue.size() > 0) {
				sendMsg(r.node, "yield", 0);
				for (Node n : quorum) {
					if (lockMsgQue.contains(n)) {
						lockMsgQue.remove(n);
					}
				}
			} else if (lockMsgQue.size() >= quorum.size()) {
				if (in_cs == false) {
					sendMsg(r.node, "release", 0);
					ReleSent = true;
				}
			}
		}
	}

	void onYield(Request r) {
//	    if(!r.equals(cur_locking_request)){
//	    	System.out.println("=================sdfasdfasgdag");
//	    }
		Request tempReq = cur_locking_request;
		cur_locking_request = waitingQue.poll();
		waitingQue.add(tempReq);
		sendMsg(cur_locking_request.node, "lock", 0);
	}

	class MsgListener implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				Request r = messageQue.poll();
				if (r != null) {
					System.out.println("-------- " + Main.msgCount.incrementAndGet() + " --------");
					System.out.println(id + " receives " + r.type + " from " + r.node.id + ".");
					Main.output(id + " receives " + r.type + " from " + r.node.id + ".");
					switch (r.type) {
					case "request":
						onRequest(r);
						break;
					case "lock":
						onLock(r);
						break;
					case "inquire":
						onInquire(r);
						break;
					case "fail":
						onFail(r);
						break;
					case "yield":
						onYield(r);
						break;
					case "release":
						onRelease(r);
						break;
					}
				}
			}

		}
	}

	private void sendMsg(Node n, String type, int ts) {
		n.messageQue.add(new Request(this, ts, type));
	}

	public void CS() {
		int cur_ts = ts.getAndIncrement();
		for (Node n : quorum) {
			sendMsg(n, "request", cur_ts);
		}
		Main.output(this.id + " requesting CS");
		await(new Waitable() {

			@Override
			public boolean condition() {
				return lockMsgQue.size() >= quorum.size();
			}
		});
		System.out.println(this.id + " in CS");
		Main.output(this.id + " in CS");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		System.out.println(this.id + " exits CS");
		Main.output(this.id + " exits CS");
		for (Node n : quorum) {
			sendMsg(n, "release", cur_ts);
		}
		lockMsgQue.clear();
		failMsgQue.clear();
		INQsent = false;
		//cur_locking_request = null;
		ReleSent = false;
		
		Main.numberOfReqDone++;
		if(Main.numberOfReqDone == Main.numberOfReq){
			double duration = (System.currentTimeMillis() - Main.startTime)/1000.0;
			System.out.println("[Total time] " + String.valueOf(duration) + "s.");
		}
	}

}

class RequestComparator implements Comparator<Request> {

	public int compare(Request req0, Request req1) {
		if (req0.ts > req1.ts) {
			return 1;
		} else if (req0.ts < req1.ts) {
			return -1;
		} else {
			if (req0.node.id > req1.node.id) {
				return 1;
			} else if (req0.node.id < req1.node.id) {
				return -1;
			}
		}
		return 0;
	}
}
