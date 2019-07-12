import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FPGrowth {

	
	// for statistics
	private long startTimestamp; // start time of the latest execution
	private long endTime; // end time of the latest execution
	private int transactionCount = 0; // transaction count in the database
	private int itemsetCount; // number of freq. itemsets found
	public ArrayList<Itemset> FrequentItemsets = new ArrayList<Itemset>();//list of frequent itemsets found
	
	// minimum support threshold
	public int relativeMinsupp;
	
	

	/**
	 * Default constructor
	 */
	public FPGrowth() {
		
	}

	public void runAlgorithm(ArrayList<Sentence> input, double minsupp) throws FileNotFoundException, IOException {
		// record the start time
		startTimestamp = System.currentTimeMillis();
		// reinitialize the number of itemsets found to 0
		itemsetCount =0; 
		
		final Map<String, Integer> mapSupport = new HashMap<String, Integer>();
		// call this method  to perform the database scan
		scanDatabaseToDetermineFrequencyOfSingleItems(input, mapSupport);
		
		this.relativeMinsupp = (int) Math.ceil(minsupp * transactionCount);
		
		// create the FPTree
		FPTree tree = new FPTree();
		for (int i=0 ; i<input.size(); i++)
		{
			List<String> transaction = new ArrayList<String>();
			for(int j=0; j<input.get(i).concept_list.size(); j++)
			{
				if(mapSupport.get(input.get(i).concept_list.get(j).concept_name) >= relativeMinsupp)
				{
					transaction.add(input.get(i).concept_list.get(j).concept_name );
				}
			}
			Collections.sort(transaction, new Comparator<String>(){
				public int compare(String item1, String item2){
					// compare the support
					int compare = mapSupport.get(item2) - mapSupport.get(item1);
					// if the same support, we check the lexical ordering!
					if(compare == 0)
					{ 
						return item1.compareTo(item2);
					}
					// otherwise use the support
					return compare;
				}
			});
			tree.addTransaction(transaction);
		}
		
		// We create the header table for the tree
		tree.createHeaderList(mapSupport);
		
		// (5) We start to mine the FP-Tree by calling the recursive method.
		// Initially, the prefix alpha is empty.
		String[] prefixAlpha = new String[0];
		if(tree.headerList.size() > 0) {
			fpgrowth(tree, prefixAlpha, transactionCount, mapSupport);
		}
		
		// record the end time
		endTime= System.currentTimeMillis();
		
//		print(tree.root, " ");
	}//end of run algorithm

//	private void print(FPNode node, String indentation) {
//		System.out.println(indentation + "NODE : " + node.itemID + " COUNTER" + node.counter);
//		for(FPNode child : node.childs) {
//			print(child, indentation += "\t");
//		}
//	}

	/**
	 * This method scans the input database to calculate the support of single items
	 * @param input the path of the input file
	 * @param mapSupport a map for storing the support of each item (key: item, value: support)
	 * @throws IOException  exception if error while writing the file
	 */
	private void scanDatabaseToDetermineFrequencyOfSingleItems(ArrayList<Sentence> input,
			final Map<String, Integer> mapSupport)
			throws FileNotFoundException, IOException {
		
		for (int i=0 ; i<input.size(); i++)
		{
			for(int j=0; j<input.get(i).concept_list.size() ; j++)
			{
				Integer count = mapSupport.get(input.get(i).concept_list.get(j).concept_name);
				if (count == null)
				{
					mapSupport.put(input.get(i).concept_list.get(j).concept_name, 1);
				}
			   else
			   {
				mapSupport.put(input.get(i).concept_list.get(j).concept_name, ++count);
			   }
			}
			transactionCount++;
		}
	}

	private void fpgrowth(FPTree tree, String[] prefixAlpha, int prefixSupport, Map<String, Integer> mapSupport) throws IOException {
		// We need to check if there is a single path in the prefix tree or not.
		if(tree.hasMoreThanOnePath == false){
			// That means that there is a single path, so we 
			// add all combinations of this path, concatenated with the prefix "alpha", to the set of patterns found.
			addAllCombinationsForPathAndPrefix(tree.root.childs.get(0), prefixAlpha); // CORRECT?
			
		}else{ // There is more than one path
			fpgrowthMoreThanOnePath(tree, prefixAlpha, prefixSupport, mapSupport);
		}
	}
	
	private void fpgrowthMoreThanOnePath(FPTree tree, String [] prefixAlpha, int prefixSupport, Map<String, Integer> mapSupport) throws IOException {
		// We process each frequent item in the header table list of the tree in reverse order.
		for(int i= tree.headerList.size()-1; i>=0; i--){
			String item = tree.headerList.get(i);
			
			int support = mapSupport.get(item);
			// if the item is not frequent, we skip it
			if(support <  relativeMinsupp){
				continue;
			}
			// Create Beta by concatening Alpha with the current item
			// and add it to the list of frequent patterns
			String [] beta = new String[prefixAlpha.length+1];
			System.arraycopy(prefixAlpha, 0, beta, 0, prefixAlpha.length);
			beta[prefixAlpha.length] = item;
			
			// calculate the support of beta
			int betaSupport = (prefixSupport < support) ? prefixSupport: support;
			// save beta to an array
			writeItemsetToArray(beta, betaSupport);
			// === Construct beta's conditional pattern base ===
			// It is a subdatabase which consists of the set of prefix paths
			// in the FP-tree co-occuring with the suffix pattern.
			List<List<FPNode>> prefixPaths = new ArrayList<List<FPNode>>();
			FPNode path = tree.mapItemNodes.get(item);
			while(path != null){
				// if the path is not just the root node
				if(path.parent.itemID != null){
					// create the prefixpath
					List<FPNode> prefixPath = new ArrayList<FPNode>();
					// add this node.
					prefixPath.add(path);   // NOTE: we add it just to keep its support,
					// actually it should not be part of the prefixPath
					
					//Recursively add all the parents of this node.
					FPNode parent = path.parent;
					while(parent.itemID != null){
						prefixPath.add(parent);
						parent = parent.parent;
					}
					// add the path to the list of prefixpaths
					prefixPaths.add(prefixPath);
				}
				// We will look for the next prefixpath
				path = path.nodeLink;
			}
			
			// (A) Calculate the frequency of each item in the prefixpath
			Map<String, Integer> mapSupportBeta = new HashMap<String, Integer>();
			// for each prefixpath
			for(List<FPNode> prefixPath : prefixPaths){
				// the support of the prefixpath is the support of its first node.
				int pathCount = prefixPath.get(0).counter;  
				 // for each node in the prefixpath,
				// except the first one, we count the frequency
				for(int j=1; j<prefixPath.size(); j++){ 
					FPNode node = prefixPath.get(j);
					// if the first time we see that node id
					if(mapSupportBeta.get(node.itemID) == null){
						// just add the path count
						mapSupportBeta.put(node.itemID, pathCount);
					}else{
						// otherwise, make the sum with the value already stored
						mapSupportBeta.put(node.itemID, mapSupportBeta.get(node.itemID) + pathCount);
					}
				}
			}
			
			// (B) Construct beta's conditional FP-Tree
			FPTree treeBeta = new FPTree();
			// add each prefixpath in the FP-tree
			for(List<FPNode> prefixPath : prefixPaths){
				treeBeta.addPrefixPath(prefixPath, mapSupportBeta, relativeMinsupp); 
			}  
			// Create the header list.
			treeBeta.createHeaderList(mapSupportBeta); 
			
			// Mine recursively the Beta tree if the root as child(s)
			if(treeBeta.root.childs.size() > 0){
				// recursive call
				fpgrowth(treeBeta, beta, betaSupport, mapSupportBeta);
			}
		}
		
	}

	private void addAllCombinationsForPathAndPrefix(FPNode node, String[] prefix) throws IOException {
		// Concatenate the node item to the current prefix
		String [] itemset = new String[prefix.length+1];
		System.arraycopy(prefix, 0, itemset, 0, prefix.length);
		itemset[prefix.length] = node.itemID;

		// save the resulting itemset to an array with its support
		writeItemsetToArray(itemset, node.counter);	
		if(node.childs.size() != 0) {
			addAllCombinationsForPathAndPrefix(node.childs.get(0), itemset);
			addAllCombinationsForPathAndPrefix(node.childs.get(0), prefix);
		}
	}
	

	/**
	 *write a frequent itemset into an array
	 * @param itemset different items in an existing itemset
	 * @param support of the itemset
	 * @throws IOException
	 */

	public void writeItemsetToArray(String [] itemset, int support) throws IOException
	{
		itemsetCount++;
		Itemset temp_member = new Itemset();
		for(int i=0; i<itemset.length; i++)
		{
			temp_member.Itemsets.add(itemset[i]);
		}
		double z=Math.round(((double)support/transactionCount) * 1000.0)/1000.0;
		temp_member.support = z;
		FrequentItemsets.add(temp_member);
	}
	
	
	/**
	 * copy the results in an public array in the main code
	 * @param a an array to copy in
	 */
	public void copyfrequentarray(ArrayList<Itemset> a)
	{
		for(int i=0; i<FrequentItemsets.size(); i++)
		{
			Itemset temp_member = new Itemset();
			for(int j=0; j<FrequentItemsets.get(i).Itemsets.size(); j++)
			{
				
				temp_member.Itemsets.add(FrequentItemsets.get(i).Itemsets.get(j));
			}
			temp_member.support = FrequentItemsets.get(i).support;
			a.add(temp_member);
			
		}
	}
	
	
	
	/**
	 * Print statistics about the algorithm execution to System.out.
	 */
	public void printStats() {
		System.out
				.println("=============  FP-GROWTH - STATS =============");
		long temps = endTime - startTimestamp;
		System.out.println(" Transactions count from database : " + transactionCount);
		System.out.println(" Frequent itemsets count : " + itemsetCount); 
		System.out.println(" Total time ~ " + temps + " ms");
		System.out
				.println("===================================================");
	}
}
