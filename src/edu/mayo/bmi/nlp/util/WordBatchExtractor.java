package edu.mayo.bmi.nlp.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.poi.hwpf.extractor.WordExtractor;

//http://hi.baidu.com/shirdrn/item/991b0fba6ed6bd41ba0e12a4
public class WordBatchExtractor {

	private String path;

	public void setPath(String path){
		this.path = path;
	}

	public FileInputStream getFileInputStream(File file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public void extractBatchWordFiles() throws IOException{
		File fileDir = new File(this.path);
		String path = fileDir.getPath()+"\\";
		if(fileDir.canRead()){
			if(fileDir.isDirectory()){
				String[] files = fileDir.list();
				if(files != null){
					for (int i = 0; i < files.length; i++){
						File file = new File(path+files[i]);
						FileInputStream fileInputStream = getFileInputStream(file);
						WordExtractor wordExtractor = new WordExtractor(fileInputStream);
						outputToPath(wordExtractor,file.getPath());
					}
				}
			}
		}
	}

	public void outputToPath(WordExtractor wordExtractor,String file) throws IOException{
		String outputFilePath = file.substring(0, file.lastIndexOf("."))+".txt";
		File outputFile = new File(outputFilePath);
		outputFile.createNewFile();
		System.out.println("正在处理Word文件 "+file+"(约"+new File(file).length()/1024+"KB) ...");
		String wordText = wordExtractor.getText();
		BufferedWriter bufferedWriter = null;
		FileWriter output = new FileWriter(new File(outputFilePath));
		bufferedWriter = new BufferedWriter(output);
		bufferedWriter.write(wordText);
		bufferedWriter.newLine();
		bufferedWriter.close();
	}
}


