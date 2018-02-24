package zhsh;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;



public class SubtreeFactory {

	/** 
	 * The method name : enumSubtrees
	 * 
	 * The method enumarates all the valid subtrees.
	 * The valid subtree is the subtree which is rooted and connected.
   * 
   * ======================================================================================
   *
   *                                      Pseudo Code
   *
   * int enumSubtrees(tokens of the very original sentence, array list to store subtrees)
   *
   *    let nSubtrees be the number of total subtree and initialize it to zero.
   *
   *    if (start of the method)
   *      initialize variables and add given sentence to the subtrees
   *
   *    for (every token in the sentence)
   *
   *        if (the token is the target node to skip)
   *          skip the node
   *
   *        while (the next token is the child of the skipped node)
   *          skip the token
   *
   *        if (the result sentence is not in the original subtrees list)
   *          add the result
   *          nSubtrees += enumSubtrees(result, subtrees)
   *        
   *     return nSubtrees
	 *
   *
   * ======================================================================================
   *
   * example sentence for explanation ,,,, ROOT(S(NP(DT NN) VP(VBZ ADJP(JJ)) PERIOD))
   *
   * The main idea : the specific node's children can be notified with the right next parenthesis.
   *                  when the node is skipped, if there is a '(' right next to the skipped 
   *                  node, meaning that there exists a child or children, skip from that '('
   *                  token until find the matched ')' token.
   *
   *
	 * @param		tokens		The ArrayList<String> type parsed sentence, each of 
	 *							       the String contains the token of the sentence. 	
	 *				
	 *				subtrees 	 The ArrayList<String> type list where the result subtrees
	 *							      to be stored. Each list have each complete subtree, not token.
	 *							      (call by reference)
   *
   * @return  nSubtrees   The total number of enumarated subtrees.
	 */

    public static int enumSubtrees(ArrayList<String> tokens, ArrayList<String> subtrees){

    	int i, j, k, num, exception_offset, exception2_counter;
    	int len = tokens.size();
      int nSubtrees = 0;
    	boolean exception1, exception2;
    	num = 0;
    	ArrayList<String> subtree;

    	String temp = String.join("", tokens);

    	if ((nSubtrees >= 1000) && (nSubtrees % 1000 == 0))
    		System.out.println("now " + nSubtrees + " subtrees are generated.");

    	/* for cover the very first given sentence & initialize nSubtrees to zero */
    	if (!subtrees.contains(temp) && !temp.contains("()")){

    		nSubtrees = 0;
    		nSubtrees++;
    		subtrees.add(temp);
    	}
    	
    	/* ROOT( ,,,,,, ) is obvious so skip 'ROOT' & the last ')' */
    	for (i = 1; i < len-1; i++){

    		j = exception_offset = exception2_counter = 0; 
   			exception1 = exception2 = false;

   			subtree = new ArrayList<String>();

   			if (isTargetNode(tokens.get(i))){ 

    			/* every cases except the three special cases are covered by 
   					the algorithm without more exception handling */

   				if (tokens.get(i-1).equals("(") && tokens.get(i+1).equals(")")){
   				/**      	 
   				 *				 simple exception case 
           *
           *		have to delete one more ( at the left
           *	----------------------------------------------
       		 *			for example : ,,,,(node),,,,,,  
    			 */
    				exception_offset++;
   				}

    			if (tokens.get(i-1).equals(" ") && tokens.get(i+1).equals(")")){
    			/**  	         
    			 *					complicate exception case 1
           *
    			 *		not have to delete one more ) at the right
    			 *	---------------------------------------------------
   				 *			for example : ,,,,,( ,,,,node),,,,,
   				 */
    				exception_offset++;
    				exception1 = true;
    			}

    			if (tokens.get(i-1).equals(" ") && tokens.get(i+1).equals("(")){
    			/**          
    			 *					complicate exception case 2
           *
    			 *		have to delete one more blank at the rigth
   				 *	---------------------------------------------------
   				 *			for example : ,,,,,( ,,,,node(,,,)),,,,
   				 */
    				exception_offset++;
    				exception2 = true;
    				exception2_counter = i;
    				while (tokens.get(exception2_counter) != ")"){
    					exception2_counter++;
   					}
   				}

    			/* space casting */
    			for (; j < i-exception_offset; j++) 
            subtree.add(tokens.get(j));		
    			
    			/* for dealing with exception 1 */
    			if (exception1) 
            subtree.add(")");

    			/* skip one blank if required */
    			j += (exception_offset+1);
    	
    			/* skip the target node and it's children */
    			if (j > 0 && tokens.get(j).equals("(")) num++;
    		
   				while (num != 0){

    				j++;

   					if (tokens.get(j).equals("(")) num++;
   					else if (tokens.get(j).equals(")")) num--;
    				
   					if (num == 0 && tokens.get(j+1).equals(" ") && !exception2) j++;
   				}

   				/* add remain tokens */
   				for (++j; j < len; j++)
   					subtree.add(tokens.get(j));
   				
    			String result = String.join("", subtree);

    			if(!subtrees.contains(result) && !result.contains("()")){
    				
    				nSubtrees++;
    				subtrees.add(result);
    				nSubtrees += enumSubtrees(subtree, subtrees);
   				}
    		}
    	}return nSubtrees;
   	}
	

    private static boolean isSpecial(String S){

    	return S.equals("(") || S.equals(")") || S.equals(" ");
    }

    /* TargetNode is the node which value is not Special and root and s */
    private static boolean isTargetNode(String S){

    	return !isSpecial(S) && !S.equals("ROOT");
    }

    /* print all subtrees in the arraylist */
    public static void printSubtrees(ArrayList<String> subtrees){

    	int count = 0;

    	for (String subtree : subtrees){

				++count;
				System.out.println(count + " subtree : " + subtree);
		}
		System.out.println("=============================");
		System.out.println(count + " subtrees are printed.");
    }

}
