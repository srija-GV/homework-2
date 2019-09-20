import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
	
	static AtomicInteger msgCount = new AtomicInteger();
	static Long startTime;
	static int numberOfNodes = 13;
	static int numberOfReq = 65;
	static int numberOfReqDone = 0;
	static String addr = "54.200.55.63";
	static int port = 59144;
	
	public static void output(String s){
		byte[] b = s.getBytes();
		
		try {
			DatagramSocket udp = new DatagramSocket();
			DatagramPacket pkt = new DatagramPacket(b, b.length, InetAddress.getByName(addr), port);
			udp.send(pkt);
			udp.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		// int[][] quorums = { { 0, 2 }, { 0, 1 }, { 1, 2 } };
		// int[][] quorums = { { 0, 3, 5 }, { 0, 1, 4 }, { 0, 2, 6 }, { 1, 3, 6
		// }, { 2, 3, 4 }, { 1, 2, 5 }, { 4, 5, 6 } };
		int[][] quorums = { { 0, 4, 7, 10 }, { 0, 1, 5, 6 }, { 0, 9, 2, 8 }, { 0, 12, 3, 11 }, { 4, 1, 9, 12 },
				{ 4, 5, 2, 3 }, { 4, 6, 8, 11 }, { 7, 1, 2, 11 }, { 7, 5, 8, 12 }, { 7, 6, 9, 3 }, { 10, 8, 3, 1 },
				{ 10, 5, 9, 11 }, { 10, 6, 2, 12 } };
		
		ArrayList<ArrayList<Node>> lists = new ArrayList<ArrayList<Node>>();
		Thread[] threads = new Thread[numberOfNodes];

		Node[] ps = new Node[numberOfNodes];
		int k = 0;
		for (int i = 0; i < numberOfNodes; i++) {
			ps[i] = new Node();
			lists.add(new ArrayList<Node>());
		}
		for (Node p : ps) {
			lists.get(quorums[k][0]).add(p);
			lists.get(quorums[k][1]).add(p);
			lists.get(quorums[k][2]).add(p);
			lists.get(quorums[k][3]).add(p);
			k++;
		}

		for (int i = 0; i < ps.length; i++) {
			ps[i].init(i, lists.get(i), 5);
		 }

		startTime = System.currentTimeMillis();
		
		for (int i = 0; i < ps.length; i++) {
			threads[i] = new Thread(ps[i]);
			threads[i].start();
		}

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}
	}
}
