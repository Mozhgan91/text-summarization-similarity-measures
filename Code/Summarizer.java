import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


public class Summarizer {
	
       public static void main(String[] args){
    	   try{
    		   
    		   double minsup;// the threshold for finding frequent itemsets
    		   int doc_num;
    		   int compresion_rate=30;
    		   int cluster_number;
    		   int alpha=5;
    		   
    		   for(cluster_number = 3; cluster_number <=3; cluster_number++)
    		   {
    			   for(minsup = 0.1; minsup <= 0.1; minsup++)
    			   {
    				   for(doc_num=1; doc_num<=350; doc_num++)
    	    		   {
    	    			   //System.out.println("\n************************* Document: " + doc_num + " *************************");
    						
    						
    						SAXBuilder file_builder = new SAXBuilder();
    						File input_file = new File("Input/" + doc_num + ".xml");
    						
    						org.jdom2.Document input_document = (org.jdom2.Document) file_builder.build(input_file);
    						
    						org.jdom2.Element Document = input_document.getRootElement();
    						
    						List Sentences = Document.getChildren("Sentence");
    						
    						ArrayList<Sentence> sentence_list = new ArrayList<Sentence>();
    						
    						int sentence_num = 0;
    						for(int i = 0; i < Sentences.size(); i++)
    						{
    							sentence_num++;
    							org.jdom2.Element Sentence = (org.jdom2.Element) Sentences.get(i);
    			        		
    			        		Sentence temp_sentence = new Sentence();
    			        		temp_sentence.sentence_number = sentence_num;
    			        		temp_sentence.sentence_score = 0;
    			        		temp_sentence.sentence_text = Sentence.getChildText("SentenceText");
    			        		sentence_list.add(temp_sentence);
    			        		
    			        		//System.out.println("Sentence Number : " + temp_sentence.sentence_number);
    			        		//System.out.println("Text : " + temp_sentence.sentence_text);
    			        		//System.out.println("\n*******************************************************************");
    			        		//------------------------------------------------------------------------------------------
    			        		
    			        		List ConceptList = Sentence.getChildren("ConceptList");
    			        		for(int i2 = 0; i2 < ConceptList.size(); i2++)
    			        		{
    			        			org.jdom2.Element concept_list = (org.jdom2.Element)ConceptList.get(i2);
    			        			List Concepts = concept_list.getChildren("Concept");
    				        		
    				        		for(int i3 = 0; i3 < Concepts.size(); i3++)
    				        		{
    				        			org.jdom2.Element Concept = (org.jdom2.Element) Concepts.get(i3);
    				        			String ConceptName = Concept.getChildText("ConceptName");
    				        			String SemanticType = Concept.getChildText("SemanticType");
    				        			
    				        			if(!(SemanticType.equals("fndg") && ConceptName.endsWith("%")))
    				        			{
    				        				Concept temp_concept = new Concept(ConceptName, SemanticType);
    						  	            sentence_list.get(sentence_num-1).AddNewConcept(temp_concept);
    				        			}
    				        		}
    			        		}
    						}
    						//end of concept and semantic type extraction
    						/*
    						//print list of concepts and semantic types of every sentence
    						for(int i = 0; i <sentence_list.size() ; i++)
    				        {
    				        	int line_num1=0;
    				        	System.out.println("*****************************************************************");
    				        	System.out.println("Sentence No: " + sentence_list.get(i).sentence_number + ", Text: " + sentence_list.get(i).sentence_text);
    				        	System.out.println("\nUnique Concepts:");
    				        	
    				        	for (int j = 0; j < sentence_list.get(i).concept_list.size(); j++)
    				        	{
    				        		line_num1 ++;
    				        	   System.out.println(line_num1 + ": " + sentence_list.get(i).concept_list.get(j).concept_name + "........" + sentence_list.get(i).concept_list.get(j).semantic_type );
    				        	   
    				        	}
    				        }
    						*/
    				        //================================================================================================================
    						//finding frequent itemsets using FP-Growth algorithm   
    						FPGrowth algo = new FPGrowth();
    						algo.runAlgorithm(sentence_list, minsup);
    						algo.printStats();
    						
    						//coping the frequent itemsets into an array 
    						ArrayList<Itemset> FrequentItemsDoc = new ArrayList<Itemset>();
    						algo.copyfrequentarray(FrequentItemsDoc);
    					    
    						//print the frequent items
    						for(int i=0; i<FrequentItemsDoc.size(); i++)
    							{
    								for(int j=0; j<FrequentItemsDoc.get(i).Itemsets.size(); j++)
    								{
    									System.out.println(FrequentItemsDoc.get(i).Itemsets.get(j));
    								}
    								System.out.println(FrequentItemsDoc.get(i).support);
    								System.out.println("============");
    							}
    						
    						//end of FPGrowth algorithm 
    						//==========================================================================================================
    						
    						//Graph creating, nodes are the sentences 
    						
    						//find the frequent itemsets covered by each node(sentence)
    				        for(int i=0; i<sentence_list.size(); i++)
    				        {
    				        	for(int j=0; j<FrequentItemsDoc.size(); j++)
    				        	{
    				        		if(sentence_list.get(i).ItemsetOccurance(FrequentItemsDoc.get(j)))
    				        		{
    				        			sentence_list.get(i).FrequentItemsSen.add(FrequentItemsDoc.get(j));
    				        		
    				        		}
    				        	}
    				        	
    				        }
    						/*
    				        //Print the frequent itemsets of each sentence
    				        for(int i=0; i<sentence_list.size(); i++)
    				        {
    				        	System.out.println("Sentence number: " + sentence_list.get(i).sentence_number );
    				        	for(int j=0; j<sentence_list.get(i).FrequentItemsSen.size(); j++)
    				        	{
    				        		for(int k=0; k<sentence_list.get(i).FrequentItemsSen.get(j).Itemsets.size(); k++ )
    				        		{
    				        			System.out.println(sentence_list.get(i).FrequentItemsSen.get(j).Itemsets.get(k));
    				        		}
    				        		System.out.println("---------------");
    				        	}
    				        	System.out.println("==================================================================");
    				        }
    				        */
    				        Boolean already_exist1;
					        ArrayList<Concept> isf = new ArrayList<Concept>();
					        for (int i=0; i<sentence_list.size(); i++)
					        {
					        	already_exist1= false ;
					        	for (int j=0; j<sentence_list.get(i).concept_list.size(); j++)
					        	{
					        		Concept temp_c = new Concept(sentence_list.get(i).concept_list.get(j).concept_name, sentence_list.get(i).concept_list.get(j).semantic_type);
					        		for(int k=0; k<isf.size(); k++)
					        		{
					        			if(sentence_list.get(i).concept_list.get(j).concept_name.equals(isf.get(k).concept_name))
					        			{
					        				already_exist1=true;
					        				isf.get(k).frequency++;
					        			}
					        			
					        		}
					        		if(already_exist1==false)
				        			{
				        				temp_c.frequency=1;
				        				isf.add(temp_c);
				        			}
					        	}
					        }
					        
					        Boolean already_exist2;
					        ArrayList<Semantic> isf_semantic = new ArrayList<Semantic>();
					        for (int i=0; i<sentence_list.size(); i++)
					        {
					        	already_exist2= false ;
					        	for (int j=0; j<sentence_list.get(i).semantic_list.size(); j++)
					        	{
					        		Semantic temp_s = new Semantic();
					        		temp_s.semantic_type = sentence_list.get(i).semantic_list.get(j).semantic_type;
					        		for(int k=0; k<isf_semantic.size(); k++)
					        		{
					        			if(sentence_list.get(i).semantic_list.get(j).semantic_type.equals(isf_semantic.get(k).semantic_type))
					        			{
					        				already_exist2=true;
					        				isf_semantic.get(k).frequency++;
					        			}
					        			
					        		}
					        		if(already_exist2==false)
				        			{
				        				temp_s.frequency=1;
				        				isf_semantic.add(temp_s);
				        			}
					        	}
					        }
    				        
    				        
    				        //calculating the number of common frequent itemsets between every 2 sentences.
    					    List<ComFreqItemCount> CommonFI = new ArrayList<ComFreqItemCount>();
    					    for (int i=0; i<sentence_list.size(); i++)
    					    {
    					    	for(int j=i+1; j<sentence_list.size(); j++)
    					    	{
    					    		ComFreqItemCount temp_count = new ComFreqItemCount();
    				        		temp_count.S1 = sentence_list.get(i).sentence_number;
    				        		temp_count.S2 = sentence_list.get(j).sentence_number;
    				        		temp_count.Com_FreqItem = sentence_list.get(i).Count_FreqItems(sentence_list.get(j));
    				        		temp_count.Common_Concept_num=sentence_list.get(i).Count_Concept(sentence_list.get(j));
					        		temp_count.Common_Semtype_num=sentence_list.get(i).Count_Semantic(sentence_list.get(j));
    				        		
    				        		CommonFI.add(temp_count);
    					    	}
    					    }
    					    
    				        /*
    					    //print the number of common frequent itemsets between every 2 sentences.
    					    for(int i=0; i< CommonFI.size(); i++)
    				        {
    				        	System.out.println("\nSentence num:" + CommonFI.get(i).S1 + "........... Sentence num:" + CommonFI.get(i).S2);
    				        	System.out.println("Common FreqItems num: " + CommonFI.get(i).Com_FreqItem);
    				        	
    				        }*/
    				       
    					   //calculate similarities between sentences and making edges of the graph 
    				       List<Edge> Edge_list = new ArrayList<Edge>();
    				       List<Similarity> SimilaritySen = new ArrayList<Similarity>();
    				       for (int i=0; i<CommonFI.size(); i++)
    				       {
    				    	   double x=0, y=0;
    				    	   Similarity temp_sim = new Similarity();
    				    	   temp_sim.S1 = CommonFI.get(i).S1;
    				    	   temp_sim.S2 = CommonFI.get(i).S2;
    				    	   //temp_sim.Similarity_func(CommonFI.get(i).Com_FreqItem, sentence_list.get(temp_sim.S1-1).FrequentItemsSen.size(), sentence_list.get(temp_sim.S2-1).FrequentItemsSen.size());
    				    	    //temp_sim.Sim_T_b(((double)alpha)/10, CommonFI.get(i).Common_Concept_num, CommonFI.get(i).Common_Semtype_num, sentence_list.get(temp_sim.S1-1).concept_list.size(), sentence_list.get(temp_sim.S2-1).concept_list.size(), sentence_list.get(temp_sim.S1-1).unique_sem_num, sentence_list.get(temp_sim.S2-1).unique_sem_num);
				        	    //temp_sim.Sim_T_F(CommonFI.get(i).Com_FreqItem, sentence_list.get(temp_sim.S1-1).FrequentItemsSen.size(), sentence_list.get(temp_sim.S2-1).FrequentItemsSen.size());
    				    	    //temp_sim.Simi_J_c(CommonFI.get(i).Common_Concept_num, sentence_list.get(temp_sim.S1-1).concept_list.size(), sentence_list.get(temp_sim.S2-1).concept_list.size());
				        	    //temp_sim.Sim_J_s(CommonFI.get(i).Common_Semtype_num, sentence_list.get(temp_sim.S1-1).unique_sem_num,  sentence_list.get(temp_sim.S2-1).unique_sem_num);
				        	    //temp_sim.Sim_J_b(((double)alpha)/10, CommonFI.get(i).Common_Concept_num, CommonFI.get(i).Common_Semtype_num, sentence_list.get(temp_sim.S1-1).concept_list.size(), sentence_list.get(temp_sim.S2-1).concept_list.size(), sentence_list.get(temp_sim.S1-1).unique_sem_num, sentence_list.get(temp_sim.S2-1).unique_sem_num);
				        	    //temp_sim.Sim_T_c(CommonFI.get(i).Common_Concept_num, sentence_list.get(temp_sim.S1-1).concept_list.size(), sentence_list.get(temp_sim.S2-1).concept_list.size());
				        	    //temp_sim.Sim_T_s(CommonFI.get(i).Common_Semtype_num, sentence_list.get(temp_sim.S1-1).unique_sem_num,  sentence_list.get(temp_sim.S2-1).unique_sem_num);
				        	    //temp_sim.Sim_P(sentence_list.get(temp_sim.S1-1).sentence_number, sentence_list.get(temp_sim.S2-1).sentence_number);
				        	    x = sentence_list.get(temp_sim.S1-1).cosine(sentence_list.get(temp_sim.S2-1), isf, sentence_list.size());
				        	    y = sentence_list.get(temp_sim.S1-1).cosine_sem(sentence_list.get(temp_sim.S2-1), isf_semantic, sentence_list.size());
				        	    temp_sim.sim = (x*y)/2;
				        	    //temp_sim.sim =y;
    				    	   SimilaritySen.add(temp_sim);
    				           
    				    	   //create the edges
    				           Edge temp_edge = new Edge();
    				           if (temp_sim.sim > 0)
    				        	    {
    				        	    	temp_edge.Source = temp_sim.S1;
    				        	    	temp_edge.Destination = temp_sim.S2;
    				        	    	//temp_edge.weight = temp_sim.sim;
    				        	    	temp_edge.weight = Math.round((temp_sim.sim)* 100.0) / 100.0;
    				        	    	
    				        	    	Edge_list.add(temp_edge);
    				        	    	
    				        	    }
    				         }
    					   /*
    				       //print graph nodes and edges
    				       for(int i=0; i < Edge_list.size(); i++)
    				         {
    				        	 System.out.println("\n" + Edge_list.get(i).Source + " " + Edge_list.get(i).Destination + " " + Edge_list.get(i).weight);
    				         }
    				        */
    				       
    				       //end of graph creating
    				       //==========================================================================================================
    				       
    				       //clustering
    				       
    				       //initials
    				       List<Cluster> clusters = new ArrayList<Cluster>();
    				       List<Integer> Cl_sentences = new ArrayList<Integer>();
    				       double min_value, max_value;
    				       int s_counter = sentence_list.size();
    				       boolean already_clustered= false;
    				       boolean already_S1= false;
    				       boolean already_S2 = false;
    				       
    				      
    				       // find the min and max weight in the graph
    				       min_value = Edge_list.get(0).weight;
    				       max_value = Edge_list.get(0).weight;
    				       for (int i=0; i<Edge_list.size(); i++ )
    				       {
    				    	   if (Edge_list.get(i).weight < min_value )
    				    	   {
    				    		   min_value = Edge_list.get(i).weight;
    				    	   }
    				    	   else if (Edge_list.get(i).weight > max_value)
    				    	   {
    				    		   max_value = Edge_list.get(i).weight;
    				    	   }
    				       }
    				      //System.out.println(min_value);
    				      //System.out.println(max_value);
    				      
    				      
    				      List<Integer> ivalue_list = new ArrayList<Integer>();
    				      
    				      while (max_value!= min_value && s_counter!=0)
    				      {
    				    	  
    				    	 //System.out.println("####");
    				    	
    				    	 max_value = Edge_list.get(0).weight;
    				    	 for (int i=0; i<Edge_list.size(); i++ )
    					       {
    					    	   if (Edge_list.get(i).weight > max_value)
    					    	   {
    					    		   max_value = Edge_list.get(i).weight;
    					    	   }
    					       }
    				    	 
    				    	// System.out.println(max_value);
    				        
    				         
    				    	 for (int i=0; i< Edge_list.size(); i++)
    				    	 {
    				    		 
    				    		 already_clustered = false;
    				    		 already_S1= false;
    						     already_S2 = false;
    				    		 if ( Edge_list.get(i).weight == max_value)
    				    		 {
    				    			 //System.out.println("\n*******....." + i + ".....********\n");
    				    			 for (int j=0; j<Cl_sentences.size(); j++)
    				    			 {
    				    				 if (Cl_sentences.get(j) == Edge_list.get(i).Source || Cl_sentences.get(j) == Edge_list.get(i).Destination)
    				    				 {
    				    					 already_clustered = true;
    				    					 break;
    				    				 }
    				    			 }
    				    			 
    				    			 if (!already_clustered)
    				    			 {
    				    				 //System.out.println( Edge_list.get(i).Source + "..." +  Edge_list.get(i).Destination + "..." + i);
    				    				 Cluster temp_cluster = new Cluster();
    				    				 temp_cluster.cluster_members.add(Edge_list.get(i));
    				    				 temp_cluster.cluster_sen.add(Edge_list.get(i).Source);
    				    				 temp_cluster.cluster_sen.add(Edge_list.get(i).Destination);
    				    				 clusters.add(temp_cluster);
    				    				 Cl_sentences.add(Edge_list.get(i).Source);
    				    				 Cl_sentences.add(Edge_list.get(i).Destination);
    				    				 s_counter -=2;
    				    				 ivalue_list.add(i);
    				    				 //System.out.println( "fourth" + i);
    				    				 
    				    			 }
    				    			 else 
    				    			 {
    				    				 for (int j=0; j<Cl_sentences.size(); j++)
    				    				 {
    				    					if(Cl_sentences.get(j)== Edge_list.get(i).Source && !(Cl_sentences.get(j)== Edge_list.get(i).Destination))
    				    					{
    				    						already_S1 = true;
    				    					}
    				    					else if (!(Cl_sentences.get(j)== Edge_list.get(i).Source) && Cl_sentences.get(j)== Edge_list.get(i).Destination)
    				    					{
    				    						already_S2 = true;
    				    					}
    				    					else if((Cl_sentences.get(j)== Edge_list.get(i).Source) && (Cl_sentences.get(j)== Edge_list.get(i).Destination))
    				    					{
    				    						already_S1 = true;
    				    						already_S2 = true;
    				    					}
    				    				 }
    				    				 if(already_S1 && already_S2)
    				    				 {
    				    					 ivalue_list.add(i);
    				    					// System.out.println( "third" + i);
    				    				 }
    				    				 else if(already_S1)
    				    				 {
    				    					 int cluster_num=0;
    				    					 for(int k=0; k<clusters.size(); k++)
    				    					 {
    				    						 for(int l=0; l<clusters.get(k).cluster_members.size(); l++)
    				    						 {
    				    							 if((clusters.get(k).cluster_members.get(l).Source == Edge_list.get(i).Source ) || (clusters.get(k).cluster_members.get(l).Destination == Edge_list.get(i).Source))
    				    							 {
    				    								 cluster_num = k;
    				    								 
    				    							 }
    				    								 
    				    						 }
    				    					 }
    				    					 //System.out.println( Edge_list.get(i).Source + "..." +  Edge_list.get(i).Destination + "..." + i);
    				    					 //System.out.println(cluster_num);
    				    					 clusters.get(cluster_num).cluster_members.add(Edge_list.get(i));
    				    					 clusters.get(cluster_num).cluster_sen.add(Edge_list.get(i).Destination);
    				    					 Cl_sentences.add(Edge_list.get(i).Destination);
    				    					 s_counter -=1;
    				    					 ivalue_list.add(i);
    				    					 //System.out.println( "first"+ i );
    				    				 }
    				    				 else if(already_S2)
    				    				 {

    				    					 int cluster_num=0;
    				    					 for(int k=0; k<clusters.size(); k++)
    				    					 {
    				    						 
    				    						 for(int l=0; l<clusters.get(k).cluster_members.size(); l++)
    				    						 {
    				    							 if((clusters.get(k).cluster_members.get(l).Source == Edge_list.get(i).Destination ) || (clusters.get(k).cluster_members.get(l).Destination == Edge_list.get(i).Destination))
    				    							 {
    				    								 cluster_num = k;
    				    							 }
    				    								 
    				    						 }
    				    					 }
    				    					 //System.out.println( Edge_list.get(i).Source + "..." +  Edge_list.get(i).Destination + "..." + i);
    				    					 clusters.get(cluster_num).cluster_members.add(Edge_list.get(i));
    				    					 clusters.get(cluster_num).cluster_sen.add(Edge_list.get(i).Source);
    				    					 Cl_sentences.add(Edge_list.get(i).Source);
    				    					 s_counter -=1;
    				    					 ivalue_list.add(i);
    				    					 //System.out.println( "second" + i);
    				    				 }
    				    				 
    				    			 }

    				    		 }
    				    		 
    				    	 }
    				    	
    				    	 for (int q=0; q<ivalue_list.size(); q++)
    				    	 {	 
    				    		 Edge_list.get(ivalue_list.get(q)).weight= 0.0;
    				    	 }
    				    	
    				      }//end of while

    				         /*
    			    		 System.out.println( "*******\n");
    			    		 
    				    	 for (int i=0; i< Cl_sentences.size(); i++)
    				    	 {
    				    		 System.out.println(Cl_sentences.get(i));
    				    	 }
    				    	 */
    				    	// System.out.println("*******\n" + s_counter + "\n*******");
    				    	 
    				    	 for (int i=0; i< clusters.size(); i++)
    					       {
    					    	   for (int j=0; j< clusters.get(i).cluster_members.size(); j++)
    					    	   {
    					    		   for (int k=0; k<SimilaritySen.size(); k++)
    					    		   {
    					    			   if( (clusters.get(i).cluster_members.get(j).Source == SimilaritySen.get(k).S1) && (clusters.get(i).cluster_members.get(j).Destination == SimilaritySen.get(k).S2) )
    					    			   {
    					    				   clusters.get(i).cluster_members.get(j).weight =Math.round((SimilaritySen.get(k).sim)* 100.0) / 100.0;
    					    						  
    					    			   }
    					    		   }
    					    	   }
    					       }
    				    	 /*
    				    	 //print cluster members and nodes of each cluster
    				    	 for (int i=0; i< clusters.size(); i++)
    					       {
    					    	   for (int j=0; j< clusters.get(i).cluster_members.size(); j++)
    					    	   {
    					    		   
    					    		   System.out.println(clusters.get(i).cluster_members.get(j).Source + " ___ "+ clusters.get(i).cluster_members.get(j).Destination + " ___ "+ clusters.get(i).cluster_members.get(j).weight );
    					    	   }
    					    	   
    					    	   System.out.println("****\n");
    					    	   for (int j=0; j< clusters.get(i).cluster_sen.size(); j++)
    					    	   {
    					    		   System.out.println(clusters.get(i).cluster_sen.get(j));
    					    	   }
    					    	   
    					    	   System.out.println("****\n");
    					    	   System.out.println(clusters.get(i).intra_similarity());
    					    	   System.out.println("*************************************" + i);
    					       }
    				    	 */
    					      
    					    	 //end of clustering
    				    	     //==========================================================================================================
    					    	 
    				    	    //merging clusters
    				    	 
    				    	 
    				    	 //similarity of different nodes from different clusters
    				    	 List<Similarity> cluster_sim = new ArrayList<Similarity>();
    				    	 for(int i=0; i< clusters.size(); i++)
    				    	 {
    				    		 for(int j=i+1; j< clusters.size(); j++)
    				    		 {
    				    			 for(int m=0; m<clusters.get(i).cluster_sen.size(); m++)
    				    			 {
    				    				 for(int n=0; n<clusters.get(j).cluster_sen.size(); n++)
    				    				 {
    				    					 Similarity temp_similarity = new Similarity();
    				                    	 temp_similarity.S1 = clusters.get(i).cluster_sen.get(m);
    				                    	 temp_similarity.S2 = clusters.get(j).cluster_sen.get(n);
    				                    	 for(int k=0; k<SimilaritySen.size(); k++)
    				                    	 {
    				                    		 if((SimilaritySen.get(k).S1 == temp_similarity.S1 && SimilaritySen.get(k).S2 == temp_similarity.S2)
    				                    				 || (SimilaritySen.get(k).S1 == temp_similarity.S2 && SimilaritySen.get(k).S2 == temp_similarity.S1))
    				                    		 {
    				                    			 temp_similarity.sim = Math.round((SimilaritySen.get(k).sim)*100.0)/100.0;
    				                    		 }
    				                    	 }
    				                    	 cluster_sim.add(temp_similarity);
    				    				 }
    				    			 }
    				    		 }
    				    	 }
    				    	
    				    	 //merge clusters
    				    	 boolean merging_end=false;
    				         int merged_clusters;
    				         int available_clusters = clusters.size();
    				         
    				         int iteration=0;
    				         while(!merging_end)
    				         {
    				        	 iteration++;
    				        	 merged_clusters = 0;
    				        	 for(int i=0; i<clusters.size(); i++)
    				        	 {
    				        		 double maximum_inter_connectivity=0.0;
    			        			 int cluster_to_merge_index=-1;
    				        		 if(clusters.get(i).is_valid == true)
    				        		 {
    				        			 for(int j=0; j<clusters.size(); j++)
    					        		 {
    					        			 if(i!=j && clusters.get(j).is_valid == true)
    					        			 {
    					        				 if(clusters.get(i).inter_similarity(cluster_sim, clusters.get(j)) > maximum_inter_connectivity)
    					        				 {
    					        					 maximum_inter_connectivity = clusters.get(i).inter_similarity(cluster_sim, clusters.get(j));
    					        					 cluster_to_merge_index = j;
    					        				 }	
    					        				 
    					        			 }
    					        		 } 
    				        		 }
    				        		 if(maximum_inter_connectivity > 0.0) 
    		        				 {
    		        					 if(available_clusters > cluster_number)
    		        					 {
    		        						 clusters.get(i).merge_clusters(clusters.get(cluster_to_merge_index));
    		        						 available_clusters--;
    		            					 clusters.get(i).is_valid = false;
    		            					 //System.out.println(clusters.get(i).cluster_num + " and " + Clusters.get(cluster_to_merge_index).cluster_num + " merged");
    		            					 //System.out.println("Cluster " + Clusters.get(cluster_to_merge_index).cluster_num + " deleted");
    		            					 clusters.remove(cluster_to_merge_index);
    		            					 //System.out.println(iteration);
    		            					 
    		            					 merged_clusters++;
    		        					 }
    				        			 
    		        				 } 
    		        				 
    				        	 }
    				        	 
    				        	 for(int i=0; i<clusters.size(); i++)
    				        	 {
    				        		 clusters.get(i).is_valid = true;
    				        	 }
    				        	 if(merged_clusters == 0 || available_clusters <= cluster_number)
    				        		 merging_end = true;
    				         }
    				    	 /*
    				         //print final clusters
    				         System.out.println("\n************************* Cluster members ***************************");
    				         for(int i=0; i<clusters.size(); i++)
    				         {
    				        	 System.out.println("Cluster:" + i);
    				        	 for(int j=0; j<clusters.get(i).cluster_sen.size(); j++)
    				        	 {
    				        		 System.out.println(clusters.get(i).cluster_sen.get(j));
    				        	 }
    				        	 System.out.println("*****************************");
    				         }
    				    	*/
    				    	    //end of merging clusters 
    				            //==========================================================================================================
    				    	   
    				    	    //selecting sentences
    					    	 	
    					    	 int summary_sentences = ((sentence_num * compresion_rate) / 100) +1;
    					    	 List<Sentence> selected_sentences = new ArrayList<Sentence>();
    					    	 
    					    	 //sort clusters based on their size
    					    	 for (int i=0; i<clusters.size(); i++)
    					    	 {
    					    		 for (int j=0; j<clusters.size(); j++)
    					    			 if (clusters.get(i).cluster_members.size() > clusters.get(j).cluster_members.size())
    					    			 {
    					        			 Cluster temp_cluster = new Cluster();
    					        			 temp_cluster = clusters.get(i);
    					        			 clusters.set(i, clusters.get(j));
    					        			 clusters.set(j, temp_cluster);
    					    			 }
    					    	 }
    					    	 
    					    	 for (int i=0; i<clusters.size(); i++)
    					    	 {
    					    		 for (int j=0; j<clusters.get(i).cluster_members.size(); j++)
    					    		 {
    					    			 System.out.println(clusters.get(i).cluster_members.get(j).Source + " ___ "+ clusters.get(i).cluster_members.get(j).Destination + " ___ "+ clusters.get(i).cluster_members.get(j).weight );
    					    		 }
    					    		 
    					    		 System.out.println("***********");
    					    	 }
    					    	 
    					    	 //heuristic 
    					    	 int all_clustered_sentences = 0;
    					         for(int i=0; i<clusters.size(); i++) //count number of sentences of all clusters
    					         {
    					        	 all_clustered_sentences += clusters.get(i).cluster_sen.size();
    					         }
    					         
    					         for(int i=0; i<clusters.size(); i++) //determine number of sentences from each cluster for summary
    					         {
    					        	 double portion = (double)clusters.get(i).cluster_sen.size() / (double)all_clustered_sentences;
    					        	 clusters.get(i).sentences_for_summary = ((int)(portion * summary_sentences)) + 1;
    					        	 
    					        	 System.out.println("Cluster: " + i + ", Sentences for summary: " + clusters.get(i).sentences_for_summary);
    					         }
    					         
    					         for(int i=0; i<clusters.size(); i++)
    					         {
    					        	 for(int j=0; j<clusters.get(i).sentences_for_summary; j++)
    					        	 {
    					        		 int selected_sentence_num = clusters.get(i).cluster_sen.get(j);
    					        		 selected_sentences.add(sentence_list.get(selected_sentence_num - 1));
    					        		 summary_sentences--;
    					        		 if(summary_sentences == 0)
    					        			 break;
    					        	 }
    					        	 if(summary_sentences == 0)
    				        			 break;
    					         }
    					    	 
    					         //sort selected sentences based on their number
    					         for(int i=0; i<selected_sentences.size(); i++)
    					         {
    					        	 for(int j=i+1; j<selected_sentences.size(); j++)
    					        	 {
    					        		 if(selected_sentences.get(i).sentence_number > selected_sentences.get(j).sentence_number)
    					        		 {
    					        			 Sentence temp_sentence = new Sentence();
    					        			 temp_sentence = selected_sentences.get(i);
    					        			 selected_sentences.set(i, selected_sentences.get(j));
    					        			 selected_sentences.set(j, temp_sentence);
    					        		 }
    					        	 }
    					         }
    					         
    					         System.out.println("\n************************* Selected sentences for summary ***************************");
    					         for(int i=0; i<selected_sentences.size(); i++)
    					         {
    					        	 System.out.println("Sentence: " + selected_sentences.get(i).sentence_number);
    					         }
    					         System.out.println(selected_sentences.get(1).sentence_number);
    					       //end of selecting sentences
    					       //==========================================================================================================
    				           
    					       //writing the summary into a file
    					       
    					         String final_summary ="<html>\n"
    						        		+ "<head>\n"
    						        		+ "</head>\n"
    						        		+ "<body bgcolor=\"white\">\n"
    						        		+ "<a name=\"1\">[1]</a> <a href=\"#1\" id=1>";
    						        for(int i=0; i<selected_sentences.size(); i++)
    						        {
    						        	final_summary += selected_sentences.get(i).sentence_text;
    						        }
    						        final_summary += "\n</a>\n"
    						        		+ "</body>\n"
    						        		+ "</html>";
    						        // Write summary in output file
    						      //  String filename = "Output/" + cluster_number + "m" + minsup + "-" + doc_num + ".html";
    						        String filename = "Output/" + doc_num + ".html";
    						        FileWriter filewriter = new FileWriter(filename);
    						        BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
    						        bufferedwriter.write(final_summary);
    						        bufferedwriter.close();
    					         
    				        
    	    		      }//end of the code for each document 
    		       	 }//end of cluster
    	       }//end of minsup

    	   }//end of try
					  catch (IOException e)
				   		{
				   	         e.printStackTrace();
				   	    }
				   		catch (JDOMException e)
				   		{
				   			e.printStackTrace();
				   		}
				       
				    	   
				       }
				}
					
