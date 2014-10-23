package edu.cmu.lti.f14.hw3.hw3_yichenca.casconsumers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This class includes my implemented document similarity methods :cosine similarity,
 * Jaccard coefficient and BM25 similarity. 
 */

public class ComputeSimilarity {
	
	
	public static double computeJaccardSimilarity(Map<String, Integer> queryVector, Map<String, Integer> docVector) {
		double unionSum = 0.0;
		double intersectionSum = 0.0;
		double jaccardSimilarity =0.0;
		Set<String> jaccardSet = new HashSet<String>();

		jaccardSet.addAll(queryVector.keySet());
		jaccardSet.addAll(docVector.keySet());
		
		for (String key : jaccardSet) {
			int qf = 0;
			int df = 0;
			if (queryVector.containsKey(key)) {
				qf = queryVector.get(key);
			}
			if (docVector.containsKey(key)) {
				df = docVector.get(key);
			}
			unionSum += Math.max(qf, df);
			intersectionSum += Math.min(qf, df);
		}
		jaccardSimilarity= intersectionSum / unionSum;
		
		return Math.round(jaccardSimilarity *10000)/10000.0;
	}
	
	public static void computeBM25Similarity( ArrayList<ArrayList<DataType>> result, ArrayList<DataType> qList)
	{
        for(int index =0 ; index < result.size(); index++){
			
			ArrayList<DataType> docList =result.get(index);
			int totalFilelength =0;
			Map<String, Integer> dfMap = new HashMap<String, Integer>();
			Map<String, Integer> queryMap = qList.get(index).getMap();
			List<Integer> documentLength =new ArrayList<Integer>();
			
			for(String Key : queryMap.keySet()){
				dfMap.put(Key, 0);
			}
			
			// Calculate inverse document frequency 
			for(DataType dt : docList){
				Map<String, Integer> tfMap =dt.getMap();
				int currentLength = 0; 
							
				for(String Key : queryMap.keySet()){
					if(tfMap.containsKey(Key))
						dfMap.put(Key, dfMap.get(Key)+1);
				}
				
				for(String Key : tfMap.keySet())
				{
					currentLength += tfMap.get(Key);
				}
				
				totalFilelength += currentLength;
				
				documentLength.add(currentLength);

			}
			// Calculate average file length
			int averageFilelength = totalFilelength / docList.size();
			
			//Calculate IDF(qi)
			Map<String, Double> idfMap = new HashMap<String, Double>();
			for (String key : dfMap.keySet()) {
				idfMap.put(key, Math.round(Math.log( docList.size() +0.5 / (dfMap.get(key) +0.5)) * 10000 )/ 10000.0);
//				System.out.println(Math.round(Math.log( docList.size() +1 / (dfMap.get(key) +1)) * 10000 )/ 10000.0);
			}
			
			//Calculate BM25 score for document dt 
			for(int docIndex =0 ; docIndex<docList.size(); docIndex++){
				
				Map<String, Integer> vector = docList.get(docIndex).getMap();
				//Map<String, Double> tfIdfVector = new HashMap<String, Double>();
				int docLength = documentLength.get(docIndex);
				double score = 0.0;
				
				for(String Key : queryMap.keySet()){
					double IDF = idfMap.get(Key);
					double frequency =0.0;
					if(vector.containsKey(Key))
						frequency = vector.get(Key);
					score += IDF * (frequency *2.2) / (frequency + 1.2 * (0.25 + 0.75* docLength / averageFilelength));
				}
				
				score =Math.round(score*10000)/10000.0;
				docList.get(docIndex).setBM25Score(score);
			}
			
			 try {
						   
						   Collections.sort(docList, new SortByBM25());	
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("Sort fail!");
						}
			
		}
	}

	public static double computeCosineSimilarity(Map<String, Integer> queryVector,
			Map<String, Integer> docVector) {
		double cosine_similarity=0.0;
		double numerator= 0.0, d1 =0.0, d2=0.0;
		int value1, value2;

		// TODO :: compute cosine similarity between two sentences
		Map<String,Integer> query = new HashMap<String, Integer>(queryVector);
		Map<String,Integer> answer = new HashMap<String, Integer>(docVector);
		
		if(queryVector.isEmpty()|| docVector.isEmpty())
			return 0.0;
		
		for (Map.Entry<String, Integer> entry : query.entrySet()) { 
			value1 =entry.getValue();
			if(answer.containsKey(entry.getKey()))
				value2 = answer.get(entry.getKey());
			else
				value2=0;
			
			answer.remove(entry.getKey());
			
			numerator += value1* value2;
			d1 += value1*value1;
			d2 += value2*value2;
		}
		
		for (Map.Entry<String, Integer> entry : answer.entrySet()) { 
			value2 = entry.getValue();
			d2 += value2*value2;
			
		}
		
		cosine_similarity =numerator / (Math.sqrt(d1*d2));
		
		return Math.round(cosine_similarity*10000)/10000.0;
	}

	

}
