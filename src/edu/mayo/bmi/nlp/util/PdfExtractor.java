package edu.mayo.bmi.nlp.util;

import java.io.IOException;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

public class PdfExtractor {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// all text on the page, regardless of position
		int pageNum = 1;
		PdfReader pdfReader = new PdfReader(args[0]);
		PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfReader);
		String textFromPage = pdfTextExtractor.getTextFromPage(pageNum);
		String[] lines = textFromPage.split("\n");
		for(String line : lines){
			if(line.trim().length()==0){
				continue;
			}
			System.out.println(line);
		}
		//System.out.println(textFromPage);
	}

}
