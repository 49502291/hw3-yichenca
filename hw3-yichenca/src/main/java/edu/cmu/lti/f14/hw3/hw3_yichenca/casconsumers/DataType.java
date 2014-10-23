package edu.cmu.lti.f14.hw3.hw3_yichenca.casconsumers;

import java.util.Comparator;
import java.util.Map;



/**
 * This class stores all the useful information for computing similarity.
 * The class attribute includes cosine, rel, doc, qId, vector, jaccard and BM25Score.
 */

public class DataType {
	

	private double cosine;
	private int rel;
	private String doc;
	private int qId;
	private Map<String, Integer> map;
	private double jaccard;
	private double BM25Score;

	public double getJaccard() {
		return jaccard;
	}
	public void setJaccard(double jaccard) {
		this.jaccard = jaccard;
	}
	public double getBM25Score() {
		return BM25Score;
	}
	public void setBM25Score(double bM25Score) {
		BM25Score = bM25Score;
	}
	public double getCosine() {
		return cosine;
	}
	public void setCosine(double cosine) {
		this.cosine = cosine;
	}
	public int getRel() {
		return rel;
	}
	public void setRel(int rel) {
		this.rel = rel;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	
	
	public int getqId() {
		return qId;
	}
	public void setqId(int qId) {
		this.qId = qId;
	}
	
	public Map<String, Integer> getMap() {
		return map;
	}
	public void setMap(Map<String, Integer> map) {
		this.map = map;
	}

}

class SortByCosine implements Comparator<DataType>{   
public int compare(DataType d1,DataType d2){     
  if(d1.getCosine()>d2.getCosine())   
   return -1;   
  else if(d1.getCosine()<d2.getCosine())
	return 1;
  else  
   return 0;   
}   
}   


class SortByJaccard implements Comparator<DataType>{   
public int compare(DataType d1,DataType d2){     
  if(d1.getJaccard()>d2.getJaccard())   
   return -1;   
  else if(d1.getJaccard()<d2.getJaccard())
	return 1;
  else  
   return 0;   
}   
} 

class SortByBM25 implements Comparator<DataType>{   
public int compare(DataType d1,DataType d2){     
  if(d1.getBM25Score()>d2.getBM25Score())   
   return -1;   
  else if(d1.getBM25Score()<d2.getBM25Score())
	return 1;
  else  
   return 0;   
}   
}   
