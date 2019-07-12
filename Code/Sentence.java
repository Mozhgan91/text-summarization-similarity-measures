
import java.util.ArrayList;
import java.util.List;


public class Sentence {
	
	public int sentence_number;
	public String sentence_text;
	public List<Concept> concept_list = new ArrayList<Concept>();
	public List<Itemset> FrequentItemsSen = new ArrayList<Itemset>();
	public double sentence_score;
	public int unique_sem_num = 0;
	public List<Semantic> semantic_list = new ArrayList<Semantic>();
	
	public void AddNewConcept(Concept new_concept)
	{
		
		Boolean already_exist = false;
		
		if(!(new_concept.semantic_type.equals("qnco")
				|| new_concept.semantic_type.equals("qlco")
				|| new_concept.semantic_type.equals("tmco")
				|| new_concept.semantic_type.equals("ftcn")
				|| new_concept.semantic_type.equals("idcn")
				|| new_concept.semantic_type.equals("inpr")
				|| new_concept.semantic_type.equals("menp")
				|| new_concept.semantic_type.equals("spco")
				|| new_concept.semantic_type.equals("lang")))
		{
			for(int i = 0; i < concept_list.size(); i++)
			{
				if(new_concept.concept_name.equals(concept_list.get(i).concept_name) && new_concept.semantic_type.equals(concept_list.get(i).semantic_type))
				{
					already_exist = true;
					concept_list.get(i).frequency ++;
					break;
				}
			}
			
			if(already_exist == false)
				
			{   
				new_concept.frequency = 1;
				Boolean IsUniqueSem = true;
				Semantic temp_semantic = new Semantic();
				temp_semantic.semantic_type= new_concept.semantic_type;
				for(int i=0; i < concept_list.size(); i++)
				{
					if(new_concept.semantic_type.equals(concept_list.get(i).semantic_type))
					{		IsUniqueSem = false;
					        for (int j=0; j <semantic_list.size(); j++)
					        {
					        	if (new_concept.semantic_type.equals(semantic_list.get(j).semantic_type))
					        			{
					        	         	semantic_list.get(j).frequency ++;
					        			}
					        			
					        }
					        break;
					}
				}
				if (IsUniqueSem == true)
				{
					unique_sem_num ++;
					temp_semantic.frequency =1;
					this.semantic_list.add(temp_semantic);
				}
				
				this.concept_list.add(new_concept);
			}
		}
	}
	public Boolean IsContainItem(String item_name)
	{
		for(int i = 0; i < concept_list.size(); i++)
		{
			if (item_name.equals(concept_list.get(i).semantic_type))
				return true;
		}
		return false;
	}
	
	//checks if a sentence covers a specific Frequent itemset or not
	public Boolean ItemsetOccurance(Itemset a)
	{
		int matchCount=0;
		for(int i=0; i<a.Itemsets.size(); i++)
		{
			for(int j=0; j<this.concept_list.size(); j++)
			{
				if(a.Itemsets.get(i).equals(this.concept_list.get(j).concept_name))
				{
					matchCount++;
					break;
				}
			}
		}
		if(matchCount == a.Itemsets.size())
			return true;
		else
		    return false;
	}
	
	
	//calculate the number of common frequent itemsets between 2 sentences
	public int Count_FreqItems(Sentence S2)
	{
		int Count_FI=0,L2;
		L2= S2.FrequentItemsSen.size();
		for (int i=0; i<L2; i++ )
		{
			if (this.ItemsetOccurance(S2.FrequentItemsSen.get(i)))
			{
				Count_FI++;
			}
		}
		
		return Count_FI;
	}
	
	public int   Count_Concept(Sentence S2) 
	{ int Count_C=0,L1,L2,i,j;
	   L1= this.concept_list.size();
	   L2=S2.concept_list.size();
	   for (i=1;i<L1;i++)
	   {for (j=1;j<L2;j++)
		 {
		   if(this.concept_list.get(i).concept_name.equals(S2.concept_list.get(j).concept_name))
			   Count_C++;
		 } 
	   }
	   return Count_C;
	}
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////
	public int   Count_Semantic(Sentence S2) 
	{ int Count_S=0,L1,L2,i,j;
	   L1= this.concept_list.size();
	   L2=S2.concept_list.size();
	   for (i=1;i<L1;i++)
	   {for (j=1;j<L2;j++)
		 {
		   if(this.concept_list.get(i).semantic_type.equals(S2.concept_list.get(j).semantic_type))
			   Count_S++;
		 } 
	   }
	   return Count_S;
	   
		
	}
	

	//cosine similarity
	public double cosine(Sentence S2, ArrayList<Concept> isf, int sn_num)
	{
		//soorat
		double s=0,y=0,sum=0, f=0,x=0,f1=0;
		int l1= this.concept_list.size();
		int l2 = S2.concept_list.size();
		for(int i=0; i<l1;i++)
		{
			for(int j=0 ; j<l2; j++)
			{
				 if(this.concept_list.get(i).concept_name.equals(S2.concept_list.get(j).concept_name))
				 { 
					 s=this.concept_list.get(i).frequency;
				     y=S2.concept_list.get(j).frequency;
				     for(int k=0; k<isf.size(); k++)
				     {
				    	 if(isf.get(k).concept_name.equals(this.concept_list.get(i).concept_name))
				    	 {
				    		 f= Math.log(((double)sn_num)/isf.get(k).frequency);
				    	 }
				     }
				     f1=f*f;
				     x= (s*y)*f1;
				 } 
				 sum = sum +x;
			}
		}
		//makhraj
		double t1=0,t2=0,sq1=0, sq2=0, sum2=0,sum1=0,pr=0;
		for(int i=0; i<l1;i++)
		{
		   t1= (this.concept_list.get(i).frequency) * (this.concept_list.get(i).frequency);
		   sum1 = sum1 + t1;
		}
		sq1= Math.sqrt(sum1);
		for(int j=0; j<l2;j++)
		{
		   t2= (S2.concept_list.get(j).frequency) * (S2.concept_list.get(j).frequency);
		   sum2 = sum2 + t2;
		}
		sq2= Math.sqrt(sum2);
		pr = (sq1 * sq2);
		
		
		return (sum/pr); 
	}
	//cosine for semantics
	public double cosine_sem(Sentence S2, ArrayList<Semantic> isf, int sn_num)
	{
		//soorat
		double s=0,y=0,sum=0, f=0,x=0, f1=0;
		int l1= this.semantic_list.size();
		int l2 = S2.semantic_list.size();
		for(int i=0; i<l1;i++)
		{
			for(int j=0 ; j<l2; j++)
			{
				 if(this.semantic_list.get(i).semantic_type.equals(S2.semantic_list.get(j).semantic_type))
				 { 
					 s=this.semantic_list.get(i).frequency; 
					// System.out.println("s:" + s);
				     y=S2.semantic_list.get(j).frequency;
				     //System.out.println("y:" + y);
				     for(int k=0; k<isf.size(); k++)
				     {
				    	 if(isf.get(k).semantic_type.equals(this.semantic_list.get(i).semantic_type))
				    	 {
				    		 f= Math.log(((double)sn_num)/isf.get(k).frequency);
				    		// System.out.println("f:" + f);
				    	 }
				     }
				     f1=f*f;
				     x= (s*y)*f1;
				     //System.out.println("x:" + x);
				 } 
				 sum = sum +x;
			}
		}
		//makhraj
		double t1=0,t2=0,sq1=0, sq2=0, sum2=0,sum1=0,pr=0;
		for(int i=0; i<l1;i++)
		{
		   t1= (this.semantic_list.get(i).frequency) * (this.semantic_list.get(i).frequency);
		   sum1 = sum1 + t1;
		}
		sq1= Math.sqrt(sum1);
		for(int j=0; j<l2;j++)
		{
		   t2= (S2.semantic_list.get(j).frequency) * (S2.semantic_list.get(j).frequency);
		   sum2 = sum2 + t2;
		}
		sq2= Math.sqrt(sum2);
		pr = (sq1 * sq2);
		
		
		return (sum/pr); 
	}
	
	
}
	

