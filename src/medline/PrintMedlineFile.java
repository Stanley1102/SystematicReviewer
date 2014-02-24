package medline;

import util.Sid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.WebPageFetcher;

public class PrintMedlineFile {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws MalformedURLException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws FileNotFoundException, MalformedURLException, UnsupportedEncodingException {
		Scanner sc = new Scanner(new File("../SystematicReviewer_data/UrinaryIncontinence/systematic_review.txt"), "UTF-8");
		
		String outputDir = "../SystematicReviewer_data/UrinaryIncontinence/txt/";
		new File(outputDir).mkdirs();
		
		Pattern titlePattern = Pattern.compile("<ArticleTitle>(.+)</ArticleTitle>",Pattern.DOTALL);
		Pattern abstractPattern = Pattern.compile("<AbstractText[^>]*>(.+?)</AbstractText>",Pattern.DOTALL);
		
		while(sc.hasNextLong()){
			long pmid = sc.nextLong();
			if(new File(outputDir+pmid+".txt").exists())
				continue;
			Sid.log(pmid);
			WebPageFetcher ft = new WebPageFetcher("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&retmode=xml&id="+pmid);
			String text = ft.getPageContent();
			Matcher titleMatcher = titlePattern.matcher(text);
			
			PrintWriter pwr = new PrintWriter(new File(outputDir+pmid+".txt"), "UTF-8");
			
			if(titleMatcher.find()){
				//Sid.log("title:"+titleMatcher.group(1));
				pwr.println(titleMatcher.group(1));
			}
			Matcher abstractMatcher = abstractPattern.matcher(text);
			while(abstractMatcher.find()){
				//Sid.log("abstract:"+abstractMatcher.group(1));
				pwr.println(abstractMatcher.group(1));
			}
			if(!abstractPattern.matcher(text).find()){
				//Sid.log("abstract:");
				
			}
			pwr.close();
		}

	}

}
