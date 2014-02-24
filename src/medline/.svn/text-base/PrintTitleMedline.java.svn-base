package medline;

import util.Sid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.WebPageFetcher;

public class PrintTitleMedline {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws MalformedURLException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws FileNotFoundException, MalformedURLException, UnsupportedEncodingException {
		Scanner sc1 = new Scanner(new File("inputs/NLP_systematic_review_positive_Titles.txt"), "UTF-8");
		ArrayList<String> titles = new ArrayList<String>(); 
		while(sc1.hasNextLine()){
			titles.add(sc1.nextLine().trim());
		}
		sc1.close();
		
		HashMap<String, String> titleMedline = new HashMap<String, String>();
		String outputDir = "outputs/NLP_systematic_review/";
		String[] files=new File(outputDir).list();
		PrintWriter pwr = new PrintWriter(new File("inputs/NLP_systematic_review_positive.txt"), "UTF-8");
		for (String file : files) {
			if(file.endsWith(".txt")){
				String pmid=file.substring(0, file.length()-4);
				Scanner sc = new Scanner(new File(outputDir+file), "UTF-8");
				if(sc.hasNextLine()){
					String title = sc.nextLine();
					titleMedline.put(title, pmid);
					if(titles.contains(title))
						pwr.println(pmid);
					else
						Sid.log("");
				}
				sc.close();
					
			}
		}
		pwr.close();

		for (String title : titles) {
			if(!titleMedline.containsKey(title))
				Sid.log("");
		}
		
	}

}
