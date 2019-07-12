
public class Similarity {

	public int S1; 
	public int S2;
	public double sim;
	public double part_concept;
	public double part_semantic;
	
	//jaccard similarity
	public void Similarity_func(int com_freqItem, int freqItem_num1, int freqItem_num2){
		
		sim = (com_freqItem / (((double)freqItem_num1 + (double)freqItem_num2) - com_freqItem) );
	}
	
	//textrank frequentitem
	public void Sim_T_F(int com_freqItem, int freqItem_num1, int freqItem_num2)
	{
		sim = (com_freqItem/(Math.log((double)freqItem_num1 * (double)freqItem_num2)));
		
	}
	
	//textrank concept
		public void Sim_T_c(int com_concept,int concept_num1, int concept_num2)
		{
			sim = (com_concept/(Math.log((double)concept_num1 * (double)concept_num2)));
			
		}
		
		//textrank semantic
		public void Sim_T_s(int com_sem, int sem_num1, int sem_num2)
		{
			
			sim = (com_sem/(Math.log((double)sem_num1 * (double)sem_num2)));
		
		}
		
		//textrank both
		public void Sim_T_b(double Alpha, int com_concept, int com_sem, int concept_num1, int concept_num2, int sem_num1, int sem_num2)
		{
			part_concept = (com_concept/(Math.log((double)concept_num1 * (double)concept_num2)));
			part_semantic = (com_sem/(Math.log((double)sem_num1 * (double)sem_num2)));
			sim = (Alpha * part_concept) + ((1.0 - Alpha)* part_semantic);
		}
		
		
		//jaccard for concepts
		public void Simi_J_c(int com_concept, int concept_num1, int concept_num2)
		{
			sim = (com_concept / (((double)concept_num1 + (double)concept_num2) - com_concept) );
			
		}
		
		//jaccard for semantic
		public void Sim_J_s(int com_sem, int sem_num1, int sem_num2)
		{
			sim = (com_sem / (((double)sem_num1 + (double)sem_num2) - com_sem) );
			
		}
		
		//jaccard for both
		
		public void Sim_J_b(double Alpha, int com_concept, int com_sem, int concept_num1, int concept_num2, int sem_num1, int sem_num2)
		{
			part_concept = (com_concept / (((double)concept_num1 + (double)concept_num2) - com_concept) );
			part_semantic = (com_sem / (((double)sem_num1 + (double)sem_num2) - com_sem) );
			sim = (Alpha * part_concept) + ((1.0 - Alpha)* part_semantic);
			
		}
		
		//positional similarity
	    public void Sim_P(int sen_num1, int sen_num2)
	    {
	    	int x;
	    	if (sen_num1 <= sen_num2 )
	    	{
	    		x = sen_num2 - sen_num1;
	    	}
	    	else
	    		x = sen_num1 - sen_num2;
	    	 
	    	sim = 1/((double)x);
	    }
	
}
