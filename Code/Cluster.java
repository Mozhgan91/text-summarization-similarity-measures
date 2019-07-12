import java.util.ArrayList;
import java.util.List;

public class Cluster
{
	double sum_similarity;
	public List<Edge> cluster_members = new ArrayList<Edge>();
	public List<Integer> cluster_sen = new ArrayList<Integer>() ;
	public boolean is_valid = true;
	public boolean is_deleted = false;
	public int sentences_for_summary = 0;
	
	public Boolean sen_occur(Integer I)
	{
		for (int k=0; k< this.cluster_sen.size(); k++)
		{
			if (this.cluster_sen.get(k)== I)
				return true;
		}
		return false;
	}
	
	public Boolean Is_occur (Similarity S)
	{
		for (int k=0; k< this.cluster_members.size(); k++)
	       {
	    	   if(this.cluster_members.get(k).Source == S.S1 
	    			   ||this.cluster_members.get(k).Source == S.S2
	    			   || this.cluster_members.get(k).Destination == S.S1
	    			   || this.cluster_members.get(k).Destination== S.S2
	    			   )
	    		   return true;
	       }
		
			return false;
	}
	
	public Boolean Is_occur_XOR (Similarity S)
	{
		for (int k=0; k< this.cluster_members.size(); k++)
	       { 
			
			if(((this.cluster_members.get(k).Source == S.S1 || this.cluster_members.get(k).Destination== S.S2 ) && !(this.cluster_members.get(k).Source == S.S1 && this.cluster_members.get(k).Destination== S.S2))
	    		   || ((this.cluster_members.get(k).Source== S.S2 || this.cluster_members.get(k).Destination== S.S1 ) && !(this.cluster_members.get(k).Source == S.S2 && this.cluster_members.get(k).Destination== S.S1))  )
	    		   return true;
	       }
		
			return false;
	}
	
	public double intra_similarity()
	{
		sum_similarity=0.0;
		for (int k=0; k< this.cluster_members.size(); k++)
		{
			sum_similarity += this.cluster_members.get(k).weight;
		}
		
		return sum_similarity;
	}
	
	public double inter_similarity(List<Similarity> cluster_sim ,Cluster c)
	{
		int l2=c.cluster_sen.size();
		double S=0;
		for(int i=0; i<this.cluster_sen.size(); i++)
		{
			for(int j=0; j<l2; j++)
			{
				for(int k=0; k<cluster_sim.size(); k++)
				{
					if((this.cluster_sen.get(i) == cluster_sim.get(k).S1 && c.cluster_sen.get(j) == cluster_sim.get(k).S2)
							|| (this.cluster_sen.get(i) == cluster_sim.get(k).S2 && c.cluster_sen.get(j) == cluster_sim.get(k).S1))
					{
						S += cluster_sim.get(k).sim;
					}
				}
			}
		}
		return S;
	}
	
	public void merge_clusters(Cluster second_cluster)
	{
		for(int i=0; i<second_cluster.cluster_sen.size(); i++)
		{
			this.cluster_sen.add(second_cluster.cluster_sen.get(i));
		}
	}

}
