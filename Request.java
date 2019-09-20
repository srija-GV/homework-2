import org.w3c.dom.NodeList;

public class Request {

	Node node;
	int ts;
	String type;
	public Request(Node node, int ts, String type){
		this.node = node;
		this.ts = ts;
		this.type = type;
	}
}
