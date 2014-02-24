package edu.mayo.bmi.nlp.util;

import java.io.File;
import java.io.IOException;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class WordExtractorExample {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//"/Users/m048100/Dropbox/SystematicReview/Data/Bartonella/test.docx"
//"/Users/m048100/Documents/corpora/SystematicReview/Data/AABB/PROTOCOL_AABB.doc"
//		OPCPackage pkg = OPCPackage.open(args[0]);
//		POIXMLProperties props = new POIXMLProperties(pkg);
//		System.out.println("The title is " + props.getCorePart().getTitle());
		///Users/m048100/Documents/corpora/SystematicReview/Data/ACRO Radio
	//!	args[0] = "E:/Dingcheng paper/All studies.doc";
		String filename = "E:/Dingcheng paper/BHI2014.doc";
		File file = new File(filename);
		NPOIFSFileSystem fs = new NPOIFSFileSystem(file);
		WordExtractor extractor = new WordExtractor(fs.getRoot());
		//97-2003
		//WordExtractor doc = new WordExtractor(new FileInputStream(filePath));//.doc格式Word文件提取器 
		int pages = extractor.getSummaryInformation().getPageCount();//总页数 
		int wordCount = extractor.getSummaryInformation().getWordCount();//总字符数 
		
		
		
		///// Read footnodeText
		String footArr[]=extractor.getFootnoteText();
		System.out.println("Foot Note:");
		for(int m=0;m<footArr.length;m++)
		{
			System.out.println(footArr[m]);
		}
		
		///// Read endnoteText
		String endArr[]=extractor.getEndnoteText();
		System.out.println("End Note:");
		for(int m=0;m<endArr.length;m++)
		{
			System.out.println(endArr[m]);
		}
	
		
		
		
		for(String rawText : extractor.getParagraphText()) {
			String text = extractor.stripFields(rawText);
			System.out.println(text);
		}
	}

}
