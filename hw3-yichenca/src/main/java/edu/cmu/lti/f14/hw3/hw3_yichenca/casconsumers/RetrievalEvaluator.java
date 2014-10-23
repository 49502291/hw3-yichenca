package edu.cmu.lti.f14.hw3.hw3_yichenca.casconsumers;

import java.awt.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f14.hw3.hw3_yichenca.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_yichenca.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_yichenca.utils.Utils;


public class RetrievalEvaluator extends CasConsumer_ImplBase {

	/** query id number **/
//	public ArrayList<Integer> qIdList;

	/** query and text relevant values **/
//	public ArrayList<Integer> relList;

	/** query vector **/
	public Map<String, Integer> qMap;
	/** document vector**/
	public Map<String, Integer> aMap;
	
	/** query list**/
	public ArrayList<DataType> qList;
	/** document list  **/
	public ArrayList<DataType> aList;
	
	private BufferedWriter write;
		
	public void initialize() throws ResourceInitializationException {

//		qIdList = new ArrayList<Integer>();

//		relList = new ArrayList<Integer>();

//		qMap =new HashMap<String, Integer>();
		
//		aMap =new HashMap<String, Integer>();
		
		qList = new ArrayList<DataType>();
		
		aList = new ArrayList<DataType>();
		
		String filename = "report.txt";
		
		 try {
				write = new BufferedWriter (new FileWriter(filename));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
	
		if (it.hasNext()) {
			Document doc = (Document) it.next();

			//Make sure that your previous annotators have populated this in CAS
			FSList fsTokenList = doc.getTokenList();
			ArrayList<Token>tokenList=Utils.fromFSListToCollection(fsTokenList, Token.class);

//			qIdList.add(doc.getQueryID());
//			relList.add(doc.getRelevanceValue());
			
			//Do something useful here
			
			HashMap<String, Integer> querMap =new HashMap<String, Integer>();
			
			HashMap<String, Integer> ansMap =new HashMap<String, Integer>();
			
			DataType data = new DataType();
			data.setqId(doc.getQueryID());
			data.setDoc(doc.getText());
			data.setRel(doc.getRelevanceValue());
			
				if(doc.getRelevanceValue()==99)
				{					
					for(int i=0;i<tokenList.size();i++)
					{
						querMap.put(tokenList.get(i).getText(), tokenList.get(i).getFrequency());				
					}
									
					data.setMap(querMap);
					qList.add(data);
				}
				
				else
				{				
				   for(int i=0;i<tokenList.size();i++)
				  {					
					   ansMap.put(tokenList.get(i).getText(), tokenList.get(i).getFrequency());					
				  }
				   data.setMap(ansMap);
				   aList.add(data);
				}
			
			
		}

	}

	/**
	 * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 */
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);
		
		// TODO :: compute the cosine similarity measure
		double cosine=0.0d;
		ArrayList<ArrayList<DataType>> result = new ArrayList<ArrayList<DataType>>();
		int j= 0;

		
		for (int i= 0; i<qList.size();i++){
			
			int qid = qList.get(i).getqId();
			qMap = qList.get(i).getMap();
			ArrayList<DataType> temp = new ArrayList<DataType>();
			
			for(;j<aList.size();j++){
				DataType da = aList.get(j);
				if(da.getqId()==qid)
				{
					aMap = da.getMap();
					cosine = computeCosineSimilarity(qMap,aMap);
					da.setCosine(cosine);
					temp.add(da);
					
				
				}
				else
					break;
			}
			
			   try {
				   Collections.sort(temp, new SortByCosine());				   
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Sort fail!");
				}
			   
			//System.out.println("Sort succeed!");
			
			result.add(temp);

		}
		
		
		
		// TODO :: compute the rank of retrieved sentences
		ArrayList<DataType> outArr;
		DataType dataArr;
		int rank=0;
		ArrayList<Integer> rankList = new ArrayList<Integer>(); 
				
		for(int outer = 0; outer< result.size(); outer ++)
		{
			outArr = result.get(outer);
			for (int inner =0 ; inner <outArr.size();inner++)
			{
				dataArr = outArr.get(inner);
				if(dataArr.getRel() == 1)
				{
					rank = inner+1;
					rankList.add(rank);
					
					Formatter formate = new Formatter();
					String output = "cosine="+ formate.format("%.4f", dataArr.getCosine())  + "\t" + 
									"rank="+rank + "\t" +
									"qid="+dataArr.getqId() + "\t" +
									"rel="+dataArr.getRel() + "\t" +
									 dataArr.getDoc() ;
				 
					System.out.println(output);
					
					 write.write(output);
					 write.newLine();
					 //write.flush();
				}
			}			
		}
		
	
		// TODO :: compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr(rankList);
		System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
		write.write(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
	   // write.newLine();
		write.close();
	}

	/**
	 * This method compute cosine similarity between query  and document
	 * @param Map<String, Integer> queryVector,Map<String, Integer> docVector
	 * @return cosine_similarity
	 */
	private double computeCosineSimilarity(Map<String, Integer> queryVector,
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
				
		return cosine_similarity ;
	}

	/**
	 * This methods compute the final MRR value
	 * @param ArrayList<Integer> arr
	 * @return mrr
	 */
	private double compute_mrr(ArrayList<Integer> arr) {
		double metric_mrr=0.0;
		double reciprocal =0.0;

		// TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
		for (int i = 0; i<arr.size(); i++){
			reciprocal+= 1.0/arr.get(i);
			
		}
		
		metric_mrr = reciprocal/arr.size();
		
		
		return Math.round(metric_mrr*10000)/10000.0;
	}

}



