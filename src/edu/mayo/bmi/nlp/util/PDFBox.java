package edu.mayo.bmi.nlp.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class PDFBox {
	
	public static void main(String[] args) throws IOException {
	
				// String line="";
		
	/*PDDocument doc=PDDocument.load("/home/shenfc/workspace/PDFTest/notwork/1573.pdf");   
	  PDDocumentInformation   pdfInfo=doc.getDocumentInformation();   
	 
	  System.out.println("Title:");
	  System.out.println(pdfInfo.getTitle()); 
	  System.out.println("Author:");
	  System.out.println(pdfInfo.getAuthor()); 
	  System.out.println("Key words:");
	  System.out.println(pdfInfo.getKeywords()); 
	 
	 
		int pageNum = 1;
		PdfReader pdfReader = new PdfReader("E:/Dingcheng paper/4460811.pdf");
		PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfReader);
		String textFromPage = pdfTextExtractor.getTextFromPage(pageNum);
		String[] lines = textFromPage.split("\n");
		for(String line0 : lines){
			if(line0.trim().length()==0){
				continue;
			}
			System.out.println(line0);
		}
		*/
		
		String line="";
		String folder1="/home/shenfc/workspace/PDFOnly";
		BufferedWriter bw=null;
      	try
      	{
    		File folder = new File(folder1);
    		File[] listOfFiles = folder.listFiles();
    		
    		for (File file : listOfFiles) {
    		    if (file.isFile()) {
    		        System.out.println(file.getName());
    		        if(!file.getName().contains(".pdf"))
    		        {
    		        	continue;
    		        }
    		        
    			/*	
    		    	PDDocument doc=PDDocument.load(folder1+"/"+file.getName());   
    		    	  PDDocumentInformation   pdfInfo=doc.getDocumentInformation();   
    		    	 
    		    	  System.out.println("Title:");
    		    	  System.out.println(pdfInfo.getTitle()); 
    		    	  System.out.println("Author:");
    		    	  System.out.println(pdfInfo.getAuthor()); 
    		    	  System.out.println("Key words:");
    		    	  System.out.println(pdfInfo.getKeywords()); */
      		
      		Process process = Runtime.getRuntime().exec ("curl --data-binary @"+folder1+"/"+file.getName()+" -H Content-Type:application/pdf -L http://pdfx.cs.man.ac.uk");
      	//	Process process = Runtime.getRuntime().exec ("curl www.google.com");
      		InputStreamReader ir=new InputStreamReader(process.getInputStream());
      	 LineNumberReader input = new LineNumberReader (ir);
      	  System.out.println("input:"+input.toString());
      	  long timer = System.currentTimeMillis()+5000;
      	  while(System.currentTimeMillis()<timer)
      	  {
      		System.out.println("input:"+input.readLine());
      	  }
      	
      	  if(input.readLine().toString().contains("error"))
      	  {
      		  File originalFile = new File("/home/shenfc/workspace/PDFOnly/"+file.getName());
      		  File notworkFile = new File("/home/shenfc/workspace/PDFTest/notwork/"+file.getName());
      		  InputStream instream = new FileInputStream(originalFile);
      		  OutputStream outStream = new FileOutputStream(notworkFile);
      		  byte[] buffer = new byte[1024];
      		  int length;
      		  while((length=instream.read(buffer))>0)
      		  {
      			  outStream.write(buffer,0,length);
      		  }
      		  instream.close();
      		  outStream.close();
      		  System.out.println("This not work");
      		  
      		  continue;
      	  }
      	 
      	File xmlfile = new File("/home/shenfc/workspace/PDFTest/"+file.getName()+".xml");
      //	File xmlfile = new File("/home/shenfc/workspace/PDFTest/test.xml");
      	if(!xmlfile.exists())
      	{
      		xmlfile.createNewFile();
      	}
      	
        FileWriter fw = new FileWriter(xmlfile.getAbsoluteFile());
         bw = new BufferedWriter(fw);
     
       
      	
      	 while((line=input.readLine())!=null)
      	 {
      	   bw.write(line);
      	  
      	 }
      	bw.close();
      	 
      	line="";
      	
      	 File originalFile = new File("/home/shenfc/workspace/PDFOnly/"+file.getName());
 		  File workFile = new File("/home/shenfc/workspace/PDFTest/work"+file.getName());
 		  InputStream instream = new FileInputStream(originalFile);
 		  OutputStream outStream = new FileOutputStream(workFile);
 		  byte[] buffer = new byte[1024];
 		  int length;
 		  while((length=instream.read(buffer))>0)
 		  {
 			  outStream.write(buffer,0,length);
 		  }
 		  instream.close();
 		  outStream.close();
 		  System.out.println("This works");
      	
    		    }
      	}
    		 
      	}
      	catch (java.io.IOException e){
      	 System.err.println ("IOException " + e.getMessage());
      	 
      	}
		
		//createPDF();

	}
	
	
	
	private static void createPDF() {
        try {

            // 1) Load DOCX into WordprocessingMLPackage
            InputStream is = new FileInputStream(
                    new File("E:/Dingcheng paper/All studies.doc"));
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
                    .load(is);

            // 2) Prepare Pdf settings
            PdfSettings pdfSettings = new PdfSettings();

            // 3) Convert WordprocessingMLPackage to Pdf
            OutputStream out = new FileOutputStream(new File(
                    "E:/Dingcheng paper/All.pdf"));
            PdfConversion converter = new org.docx4j.convert.out.pdf.viaXSLFO.Conversion(
                    wordMLPackage);
            converter.output(out, pdfSettings);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
