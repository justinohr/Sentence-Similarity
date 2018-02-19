package zhsh;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;



public class ZTree {

	Node root = new Node();
	// function l() which gives the leftmost child
	ArrayList<Integer> l = new ArrayList<Integer>();
	// list of keyroots, i.e., nodes with a left child and the ZTree root
	ArrayList<Integer> keyroots = new ArrayList<Integer>();
	// list of the labels of the nodes used for node comparison
	ArrayList<String> labels = new ArrayList<String>();

	/* cost for each operations */
	private static final int DEFAULT_INSERT = 4;
	private static final int DEFAULT_DELETE = 4;
	private static final int DEFAULT_RELABEL = 3;

	/* count tokens to find longer sentence */
	public static int ntoken = 0;

	// the following constructor handles preorder notation. E.g., f(a b(c))
	public ZTree(String s) throws IOException {
		StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(s));
		tokenizer.nextToken();
		root = parseString(root, tokenizer);
		if (tokenizer.ttype != StreamTokenizer.TT_EOF) {
			throw new RuntimeException("Leftover token: " + tokenizer.ttype);
		}
	}

	private static Node parseString(Node node, StreamTokenizer tokenizer) throws IOException {
		node.label = tokenizer.sval;
		tokenizer.nextToken();
		if (tokenizer.ttype == '(') {
			tokenizer.nextToken();
			do {
				node.children.add(parseString(new Node(), tokenizer));
				ntoken++;
			} while (tokenizer.ttype != ')');
			tokenizer.nextToken();
		}
		return node;
	}

	public void traverse() {
		// put together an ordered list of node labels of the ZTree
		traverse(root, labels);
	}

	private static ArrayList<String> traverse(Node node, ArrayList<String> labels) {
		for (int i = 0; i < node.children.size(); i++) {
			labels = traverse(node.children.get(i), labels);
		}
		labels.add(node.label);
		return labels;
	}

	public void index() {
		// index each node in the ZTree according to traversal method
		index(root, 0);
	}

	private static int index(Node node, int index) {
		for (int i = 0; i < node.children.size(); i++) {
			index = index(node.children.get(i), index);
		}
		index++;
		node.index = index;
		return index;
	}

	public void l() {
		// put together a function which gives l()
		leftmost();
		l = l(root, new ArrayList<Integer>());
	}

	private ArrayList<Integer> l(Node node, ArrayList<Integer> l) {
		for (int i = 0; i < node.children.size(); i++) {
			l = l(node.children.get(i), l);
		}
		l.add(node.leftmost.index);
		return l;
	}

	private void leftmost() {
		leftmost(root);
	}

	private static void leftmost(Node node) {
		if (node == null)
			return;
		for (int i = 0; i < node.children.size(); i++) {
			leftmost(node.children.get(i));
		}
		if (node.children.size() == 0) {
			node.leftmost = node;
		} else {
			node.leftmost = node.children.get(0).leftmost;
		}
	}

	public void keyroots() {
		// calculate the keyroots
		for (int i = 0; i < l.size(); i++) {
			int flag = 0;
			for (int j = i + 1; j < l.size(); j++) {
				if (l.get(j) == l.get(i)) {
					flag = 1;
				}
			}
			if (flag == 0) {
				this.keyroots.add(i + 1);
			}
		}
	}

	static int[][] TD;

	public static int ZhangShasha(ZTree tree1, ZTree tree2) {
		tree1.index();
		tree1.l();
		tree1.keyroots();
		tree1.traverse();
		tree2.index();
		tree2.l();
		tree2.keyroots();
		tree2.traverse();

		ArrayList<Integer> l1 = tree1.l;
		ArrayList<Integer> keyroots1 = tree1.keyroots;
		ArrayList<Integer> l2 = tree2.l;
		ArrayList<Integer> keyroots2 = tree2.keyroots;

		// space complexity of the algorithm
		TD = new int[l1.size() + 1][l2.size() + 1];

		// solve subproblems
		for (int i1 = 1; i1 < keyroots1.size() + 1; i1++) {
			for (int j1 = 1; j1 < keyroots2.size() + 1; j1++) {
				int i = keyroots1.get(i1 - 1);
				int j = keyroots2.get(j1 - 1);
				TD[i][j] = treedist(l1, l2, i, j, tree1, tree2);
			}
		}

		return TD[l1.size()][l2.size()];
	}

	private static int treedist(ArrayList<Integer> l1, ArrayList<Integer> l2, int i, int j, ZTree tree1, ZTree tree2) {
		int[][] forestdist = new int[i + 1][j + 1];

		// costs of the three atomic operations
		int Delete = DEFAULT_DELETE;
		int Insert = DEFAULT_INSERT;
		int Relabel = DEFAULT_RELABEL;

		forestdist[0][0] = 0;
		for (int i1 = l1.get(i - 1); i1 <= i; i1++) {
			forestdist[i1][0] = forestdist[i1 - 1][0] + Delete;
		}
		for (int j1 = l2.get(j - 1); j1 <= j; j1++) {
			forestdist[0][j1] = forestdist[0][j1 - 1] + Insert;
		}
		for (int i1 = l1.get(i - 1); i1 <= i; i1++) {
			for (int j1 = l2.get(j - 1); j1 <= j; j1++) {
				int i_temp = (l1.get(i - 1) > i1 - 1) ? 0 : i1 - 1;
				int j_temp = (l2.get(j - 1) > j1 - 1) ? 0 : j1 - 1;
				if ((l1.get(i1 - 1) == l1.get(i - 1)) && (l2.get(j1 - 1) == l2.get(j - 1))) {

					switch (matchedChar(tree1.labels.get(i1-1),tree2.labels.get(j1-1))) 
					{
						case 0:
							Relabel = 3;
							break;
						case 1:
							Relabel = 2;
							break;
						default:
							Relabel = 1;
							break;
						}

					int Cost = (tree1.labels.get(i1 - 1).equals(tree2.labels.get(j1 - 1))) ? 0 : Relabel;
					forestdist[i1][j1] = Math.min(
							Math.min(forestdist[i_temp][j1] + Delete, forestdist[i1][j_temp] + Insert),
							forestdist[i_temp][j_temp] + Cost);
					TD[i1][j1] = forestdist[i1][j1];
				} else {
					int i1_temp = l1.get(i1 - 1) - 1;
					int j1_temp = l2.get(j1 - 1) - 1;

					int i_temp2 = (l1.get(i - 1) > i1_temp) ? 0 : i1_temp;
					int j_temp2 = (l2.get(j - 1) > j1_temp) ? 0 : j1_temp;

					forestdist[i1][j1] = Math.min(
							Math.min(forestdist[i_temp][j1] + Delete, forestdist[i1][j_temp] + Insert),
							forestdist[i_temp2][j_temp2] + TD[i1][j1]);
				}
			}
		}
		return forestdist[i][j];
	}

	/* matchedChar : for give different cost to each node ,,, par : (label of the two nodes), ret : num_matchedCh */
	public static int matchedChar(String word1, String word2) {

		int matched = 0;

		for (int i = 0; i < word1.length(); i++){
			char c1 = word1.charAt(i);
			/* decrease cost if it starts with N, V, W, P */
			if (i == 0 && (c1 == 'N' || c1 == 'V' || c1 == 'W' || c1 == 'P')){
				for (int j = 0; j < word2.length(); j++){
					char c2 = word2.charAt(j);

					if (c1 == c2) {
						matched++;
						break;
					}
				}	
			}
		}
		return matched;
	}
}
