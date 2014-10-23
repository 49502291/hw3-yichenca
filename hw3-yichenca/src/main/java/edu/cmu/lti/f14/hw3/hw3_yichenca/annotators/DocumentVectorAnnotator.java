package edu.cmu.lti.f14.hw3.hw3_yichenca.annotators;

import java.util.*;

import edu.cmu.lti.f14.hw3.hw3_yichenca.utils.*;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f14.hw3.hw3_yichenca.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_yichenca.typesystems.Token;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

	private Map<String,Integer> map = new HashMap();
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
		}

	}

	/**
   * A basic white-space tokenizer, it deliberately does not split on punctuation!
   *
	 * @param doc input text
	 * @return    a list of tokens.
	 */

	List<String> tokenize0(String doc) {
	  List<String> res = new ArrayList<String>();
	  
	  for (String s: doc.split("\\s+"))
	    res.add(s);
	  return res;
	}

	/**
	 * This methods construct a vector of tokens and update the tokenlist in CAS by
	 * using naive white space tokenizer. 
	 * 
	 * @param jcas
	 * @param doc
	 */

	private void createTermFreqVector(JCas jcas, Document doc) {

		String docText = doc.getText();
		List<String> list = tokenize0(docText);
		List<Token> listToken =new ArrayList<Token>();
	
	
		//TO DO: construct a vector of tokens and update the tokenList in CAS
        //TO DO: use tokenize0 from above 
		//List<String> token = new ArrayList<String>();

		map.clear();
		
		for (int i =0; i<list.size();i++)
		{
			String word = list.get(i);
			if(!map.containsKey(word))
			{
				map.put(word, 1);
			}
			else{
				map.put(word, map.get(word)+1);
			}
		}
			
	
		Iterator it =map.entrySet().iterator();
		
		while(it.hasNext())			
		{	
			Map.Entry<String, Integer> entry =(Map.Entry<String, Integer>)it.next();
			
			Token token =new Token(jcas);
			token.setText(entry.getKey());
			token.setFrequency(entry.getValue());
			listToken.add(token);
		//	System.out.println(token.getText()+" "+ token.getFrequency());
		}
		
		FSList fs =Utils.fromCollectionToFSList(jcas, listToken);
		doc.setTokenList(fs);

	}

}
