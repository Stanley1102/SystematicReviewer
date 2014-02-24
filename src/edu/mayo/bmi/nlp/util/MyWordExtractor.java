package edu.mayo.bmi.nlp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFComment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
//http://stackoverflow.com/questions/3604799/how-to-extract-docx-word-2007-above-using-apache-poi
//http://blog.csdn.net/njchenyi/article/details/6901605
public class MyWordExtractor {

	public static void main(String[] args) {
		//File file = new File("E:\\POI\\word\\JBoss3.0 下配置和部署EJB简介.doc");
		//File file = new File("/Users/m048100/Documents/corpora/SystematicReview/Data/AABB/PROTOCOL_AABB.doc");
		String filePath = "/Users/m048100/Documents/corpora/SystematicReview/Data/CCM readmission/CCM Readmissions ManuscriptV6.docx";
		File file = new File("/Users/m048100/Documents/corpora/SystematicReview/Data/CCM readmission/CCM Readmissions ManuscriptV6.docx");
		file = new File("/Users/m048100/Desktop/All studies.docx");
		try {
			FileInputStream fis = new FileInputStream(file);
			//WordExtractor wordExtractor = new WordExtractor(fis);
			//OPCPackage opcPackage = new OPCPackage(file); 
			XWPFDocument docx = new XWPFDocument(fis);
			XWPFWordExtractor wordxExtractor = new XWPFWordExtractor(docx);
			String text = wordxExtractor.getText();

			//			org.apache.poi.xwpf.extractor.XWPFWordExtractor docx = new XWPFWordExtractor(POIXMLDocument.openPackage(filePath));
			//XWPFComment[] comments = ((XWPFDocument) docx.getDocument()).getComments();
//			for(XWPFComment comment:comments){ 
//				comment.getId();//提取批注Id 
//				comment.getAuthor();//提取批注修改人 
//				comment.getText();//提取批注内容 
//			} 
			int pages = docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();//总页数 
			int characters = docx.getProperties().getExtendedProperties().getUnderlyingProperties().getCharacters();// 忽略空格的总字符数 另外还有getCharactersWithSpaces()方法获取带空格的总字数。
			Iterator<XWPFTable> ti = docx.getTablesIterator();
			int tiNum = 0;
			while(ti.hasNext()){
				System.out.println("tiNum: "+tiNum);
				tiNum++;
				XWPFTable table = ti.next();
				int rowN = table.getNumberOfRows();
				System.out.println("rowN: "+rowN);
				for(int i=0;i<rowN;i++){
					XWPFTableRow row = table.getRow(i);
					//int height = row.getHeight();
					List<XWPFTableCell> cells =  row.getTableCells();
					CTCustomXmlCell ctcxc = row.getCtRow().;
					System.out.println(row.getCtRow());
				}
			}
			
			//System.out.println("adding docx " + text);
			//d.add(new Field("content", text, Field.Store.NO, Field.Index.ANALYZED));
			//XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(fis); 
			//System.out.println("【 使用getText()方法提取的Word文件的内容如下所示：】");
			//System.out.println(wordExtractor.getText());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
