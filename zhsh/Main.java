package zhsh;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;


/* usage : javac -cp ejml-0.23.jar:stanford-parser.jar:stanford-parser-3.8.0-models.jar: -d . zhsh/*.java 
         : java -cp ejml-0.23.jar:stanford-parser.jar:stanford-parser-3.8.0-models.jar: zhsh.Main edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz testcase.txt
*/
public class Main {

	public static int length1;
	public static int length2;
	int j, k;

	public static void main(String[] args) throws IOException {

		int distance_temp, distance;
		float similarity; 
		float similarity_core = 0;

		/* for test multiple cases with single command. */
		for (int i = 1; i < args.length ; i++){

			ArrayList<String> arr1 = new ArrayList<String>();
			ArrayList<String> arr2 = new ArrayList<String>();

			ArrayList<String> subtrees1 = new ArrayList<String>();
			ArrayList<String> subtrees2 = new ArrayList<String>();

			/*	The program will open the file whose name is same as the second argument
			 *	Following block will make a parse tree using Stanford Parser and my own defined function: dfs. */
			System.out.println("input file name : " + args[i]);
			System.out.println("--------------------------------");
			System.out.println();

			int nSentence = 0;  /* for print two sentences */

			String grammar = args.length > 0 ? args[0] : "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
  			String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
			LexicalizedParser lp = LexicalizedParser.loadModel(grammar, options);
			TreebankLanguagePack tlp = lp.getOp().langpack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();

			Iterable<List<? extends HasWord>> sentences;
			DocumentPreprocessor dp = new DocumentPreprocessor(args[i]);

			List<List<? extends HasWord>> tmp = new ArrayList<>();

			for (List<HasWord> sentence : dp) {
				tmp.add(sentence);
			}
			sentences = tmp;
			ArrayList<String> str = new ArrayList<String>();
		
			System.out.println();

			for (List<? extends HasWord> sentence : sentences) {
				nSentence++;
				System.out.println((nSentence > 1? "Second" : "First ") + " sentence : " + sentence);
				Tree parse = lp.parse(sentence);
				GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
				List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
				String parsed = "";
				str.add(nSentence == 1? dfs(parse, parsed, arr1) : dfs(parse, parsed, arr2));
			}

			/*	Parse trees are printed on following two lines*/
			System.out.println();
			System.out.println("Parse tree for the first sentence  : " + str.get(0));
			System.out.println("Parse tree for the second sentence : " + str.get(1));
			System.out.println();

			/*	Getting similarity between two sentence.
			 *	Similarity is defined as (1 - (tree edit distance between two parse trees) / (tree edit distance between the larger tree and a tree which has only temp node)) * 100
			 */
			String temp = "temp";
			ZTree tree1 = new ZTree(str.get(0));
			length1 = ZTree.ntoken;
			System.out.println("Number of tokens for the first sentence  : " + length1);
			ZTree tree2 = new ZTree(str.get(1));
			length2 = ZTree.ntoken - length1;
			System.out.println("Number of tokens for the second sentence : " + length2);
			ZTree tree_temp = new ZTree(temp);
			System.out.println();	
			ZTree.ntoken = 0;

			/* distance_temp : the tree edit distance from zero to longest sentence. */
			distance_temp = ZTree.ZhangShasha((length1 > length2 ? tree1 : tree2), tree_temp);

			distance = ZTree.ZhangShasha(tree1, tree2);

			/* First approach : similarity can be defined with the tree edit distance 
				between two sentences divided by distance_temp. */
			similarity = (1-(distance/(float)distance_temp)) * 100;

			System.out.println("Similarity : " 
				+ String.format("%.1f", similarity) + "%");

			/* new lines for next testcase. */
			System.out.println();
			System.out.println();
      
        	/* print all the rooted sub-trees */
        	System.out.println("--------------------------------------");
        	System.out.println("Rooted sub-trees of the first sentence");
        	System.out.println(str.get(0));
        	System.out.println("--------------------------------------");
       	 	SubtreeFactory.enumSubtrees(arr1, subtrees1);
       	 	System.out.println("--------------------------------------");
 			System.out.println();      	 	
			System.out.println();

       	 	System.out.println("---------------------------------------");
        	System.out.println("Rooted sub-trees of the second sentence");
        	System.out.println(str.get(1));
        	System.out.println("---------------------------------------");
       	 	SubtreeFactory.enumSubtrees(arr2, subtrees2);
       	 	System.out.println("---------------------------------------");
       		System.out.println();

       		/* find core kernel */

       		String coreSubtree1 = "";
       		String coreSubtree2 = "";

       		for (String s1 : subtrees1){
       			for (String s2 : subtrees2){

					ZTree testtree1 = new ZTree(s1);
					length1 = ZTree.ntoken;
					System.out.println("subtree1 : " + s1);
					ZTree testtree2 = new ZTree(s2);
					length2 = ZTree.ntoken - length1;
					System.out.println("subtree2 : " + s2);
					ZTree testtree_temp = new ZTree(temp);
					System.out.println("-----------------------------------------------");	
					ZTree.ntoken = 0;

					distance_temp = ZTree.ZhangShasha((length1 > length2 ? testtree1 : testtree2), testtree_temp);

					distance = ZTree.ZhangShasha(testtree1, testtree2);

					similarity = (1-(distance/(float)distance_temp)) * 100;

					System.out.println("keep finding core kernel ,,, Similarity : " 
						+ String.format("%.1f", similarity) + "%");

					if (similarity_core < similarity) {

						similarity_core = similarity;
						coreSubtree1 = s1;
						coreSubtree2 = s2;
					}

					System.out.println();
					System.out.println();
       			}
       		}
       		ArrayList<String> s1 = new ArrayList<String>();
       		ArrayList<String> s2 = new ArrayList<String>();
       		System.out.println("Number of subtrees: " + SubtreeFactory.enumSubtrees(arr1, s1));
       		System.out.println("Number of subtrees: " + SubtreeFactory.enumSubtrees(arr2, s2));
       		System.out.println();
			System.out.println("=========================");
			System.out.println("  ->   Core kernel   <-  ");
			System.out.println("=========================");
			System.out.println();
			System.out.println("Similarity of the core kernel : " + String.format("%.1f", similarity_core) + "%");
			System.out.println();
			System.out.println("Core kernel of the first sentence");
			System.out.println("----> " + coreSubtree1);
			System.out.println("Core kernel of the second sentence");
			System.out.println("----> " + coreSubtree2);
			System.out.println();
			System.out.println();
		}
	}

	public static String dfs(Tree node, String parsed, ArrayList<String> arr) {

		if (node.isLeaf()) return parsed;

		Tree[] child = node.children();

		if (node.value().equals(",")) {
			parsed += "COMMA";
			arr.add("COMMA");
		}
		else if (node.value().equals(".")) {
			parsed += "PERIOD";
			arr.add("PERIOD");
		}
		else if (node.value().equals("PRP$")) {
			parsed += "PRPDOLLAR";
			arr.add("PRPDOLLAR");
		}
		else if (node.value().equals("WP$")) {
			parsed += "WPDOLLAR";
			arr.add("WPDOLLAR");
		}
		else {
			parsed += node.value();
			arr.add(node.value());
		}

		if (child.length > 0 && !child[0].isLeaf()) {
			parsed += "(";
			arr.add("(");
		}
		for(int i = 0; i < child.length; i++) {
			if(i > 0) {parsed += " "; arr.add(" ");}
			parsed = dfs(child[i], parsed, arr);
		}
		if (child.length > 0 && !child[0].isLeaf()) {
			parsed += ")";
			arr.add(")");
		}
			return parsed;
	}

    private static void enumSubtrees(ArrayList<String> arr, ArrayList<String> subtrees){

    	int i, j, k, num, nSubtrees, exception_offset, exception2_counter;
    	int len = arr.size();
    	boolean exception1, exception2;
    	num = nSubtrees = 0;
    	ArrayList<String> subtree;

    	/* ROOT(S( ,,,,,, PERIOD)) is obvious */
    	for (k=4; k<len-4; k++){
    		if (isTargetNode(arr.get(k)))
    			for (i = k; i < len-4; i++){

    				j = exception_offset = exception2_counter = 0; 
    				exception1 = exception2 = false;

    				subtree = new ArrayList<String>();

    				if (isTargetNode(arr.get(i))){ 

    					System.out.println("target value : " + arr.get(i));

    					/* every cases except the three special cases are covered by 
    						the algorithm */

    					if (arr.get(i-1).equals("(") && arr.get(i+1).equals(")")){
    						/*         simple exception case 
                   			     have to delete one more ( at the left
             		         ----------------------------------------------
            		                for example : ,,,,(node),,,,,,  
    						*/
    						exception_offset++;
    					}

    					if (arr.get(i-1).equals(" ") && arr.get(i+1).equals(")")){
    						/*           exception case 1
    						     not have to delete one more ) at the right
    						   ----------------------------------------------
    						         for example : ,,,,,( ,,,,node),,,,,
    						 */
    						exception_offset++;
    						exception1 = true;
    					}

    					if (arr.get(i-1).equals(" ") && arr.get(i+1).equals("(")){
    						/*               exception case 2
    						     have to delete one more blank at the rigth
    						   ----------------------------------------------
    						      for example : ,,,,,( ,,,,node(,,,)),,,,
    						 */
    						exception_offset++;
    						exception2 = true;
    						exception2_counter = i;
    						while (arr.get(exception2_counter) != ")"){
    							exception2_counter++;
    			  			}
    					}

    					/* space casting */
    					for (; j < i-exception_offset; j++) {
    						System.out.print(arr.get(j));
    						subtree.add(arr.get(j));
    					}

    					/* for dealing with exception 1 */
    					if (exception1) {
    						System.out.print(")");
    						subtree.add(")");
    					}

    					/* skip one blank if required */
    					j += (exception_offset+1);
    	
    					/* skip the target node and it's children */
    					if (j > 0 && arr.get(j).equals("(")) num++;
    		
    					while (num != 0){

    						j++;

    						if (arr.get(j).equals("(")) num++;
    						else if (arr.get(j).equals(")")) num--;
    				
    						if (num == 0 && arr.get(j+1).equals(" ") && !exception2) j++;
    					}

    					/* print remain things */
    					for (++j; j < len; j++) {
    						System.out.print(arr.get(j));
    						subtree.add(arr.get(j));
    					}
    		

    					String result = String.join("", subtree);

    					nSubtrees++;

    					subtrees.add(result);

    					System.out.println();
    			}
    		}
    	}System.out.println("number of subtrees : " + nSubtrees);
	}

    private static boolean isSpecial(String S){

    	return S.equals("(") || S.equals(")") || S.equals(" ");
    }

    /* TargetNode is the node which value is not Special and root and s */
    private static boolean isTargetNode(String S){

    	return !isSpecial(S) && !S.equals("ROOT") && !S.equals("S");
    }
}
