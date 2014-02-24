package edu.mayo.bmi.nlp.util;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
 
public class ParsePDFXML {
 
  public static void main(String argv[]) {
 
    try {
 
	File fXmlFile = new File("/home/shenfc/workspace/PDFTest/0001.pdf.xml");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
 
	//optional, but recommended
	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	doc.getDocumentElement().normalize();
 
	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
 
	NodeList nList = doc.getElementsByTagName("article-title");
	
	NodeList AuthorList = doc.getElementsByTagName("contrib");
	
	NodeList AbstractList = doc.getElementsByTagName("abstract");
 
	System.out.println("----------------------------");
 
	for (int temp = 0; temp < nList.getLength(); temp++) {
 
		Node nNode = nList.item(temp);
 
	//	System.out.println("\nCurrent Element value:" + nNode.getNodeName());
		System.out.println("title: " + nNode.getTextContent());
		
		
 
	/*	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
			Element eElement = (Element) nNode;
 
			System.out.println("Staff id : " + eElement.getAttribute("id"));
			System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
			System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
			System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
			System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
 
		}*/
	}
	
	for (int temp = 0; temp < AuthorList.getLength(); temp++) {
		 
		Node nNode = AuthorList.item(temp);
 
	//	System.out.println("\nCurrent Element value:" + nNode.getNodeName());
		//System.out.println("author is: " + nNode.getTextContent());
	
	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
			Element eElement = (Element) nNode;
			System.out.println("author: " + eElement.getElementsByTagName("name").item(0).getTextContent());
		}
	}
	
	for (int temp = 0; temp < AbstractList.getLength(); temp++) {
		 
		Node nNode = AbstractList.item(temp);
 
	//	System.out.println("\nCurrent Element value:" + nNode.getNodeName());
		System.out.println("abstract: " + nNode.getTextContent());
	

	}
    } catch (Exception e) {
	e.printStackTrace();
    }
  }
 
}