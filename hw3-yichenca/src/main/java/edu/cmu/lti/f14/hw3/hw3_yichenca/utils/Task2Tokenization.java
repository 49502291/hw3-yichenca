package edu.cmu.lti.f14.hw3.hw3_yichenca.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Task2Tokenization {

	public static String DeleteCapitalCase (String s)
	{
		String result = s.toLowerCase();
		return result;
	}
	
	public static String DeletePunctuation(String s)
	{
		String result = s.replaceAll("\\pP", "");
		return result;
	}
	
	public static boolean CheckStopWord(String s)
	{
		String result;
		Set set = null;
		try {
			set = DictionaryReader();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(set.contains(s))
			return true;
		return false;

		
	}
	
	public static  Set<String> DictionaryReader() throws Exception 
	{
			
		Set<String> dictionary = new HashSet<String>();
		String sLine;
		
		  URL docUrl = Task2Tokenization.class.getResource("/stopwords.txt");
		    if (docUrl == null) {
		       throw new IllegalArgumentException("Error opening stopwords.txt");
		    }
				BufferedReader br = new BufferedReader(new InputStreamReader(docUrl.openStream()));
				while ((sLine =(String) br.readLine()) != null)   {
				  dictionary.add(sLine);
				}
				br.close();
				br=null;
		return dictionary;
	}

	
//	  public static void main(String args[]) {
//		
//		  String origText = "sdfa;sdfASDFDSF  DFD?";
//		    
//
//		    try {
//				System.out.println(Task2Tokenization.DictionaryReader());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		  }
	 
}
