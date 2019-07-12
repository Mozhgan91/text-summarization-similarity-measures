
import java.util.ArrayList;
import java.util.List;
public class FPNode{
	 // item id
	String itemID = null; 
	// support of that node
	int counter = 1;  
	
	// reference to the parent node or null if this is the root
	FPNode parent = null; 
	// references to the child(s) of that node if there is some
	List<FPNode> childs = new ArrayList<FPNode>();
	
	FPNode nodeLink = null; // link to next node with the same item id (for the header table).
	
	FPNode(){
		
	}

	FPNode getChildWithID(String id) {
		// for each child node
		for(FPNode child : childs){
			// if the id is the one that we are looking for
			if(child.itemID.equals(id)){
				// return that node
				return child;
			}
		}
		// if not found, return null
		return null;
	}

}
