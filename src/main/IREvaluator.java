package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import util.Sid;

public class IREvaluator {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		//Scanner sc=new Scanner(new File("../SystematicReviewer_data/UrinaryIncontinence/output.txt"));
		//in my computer, args[0] should be 
		Scanner sc=new Scanner(new File(args[0]));
		int totalP=78, totalN=249;
			

		float tp=0,fp=0;
		//Sid.log("recall\tprecision\tpercentage");
		Sid.log("recall/tprecision/tpercentage");
		while(sc.hasNextLine()){
			if(sc.nextLine().endsWith("true"))
				tp++;
			else 
				fp++;
			
			float fn=totalP-tp, tn = totalN - fp;
			float recall=tp/(tp+fn);
			float precision=tp/(tp+fp);
			float percentage=(tp+fp)*100/(totalN+totalP);
			Sid.log(recall+"\t"+precision+"\t"+percentage);
			
		}

	}

}
