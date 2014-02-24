package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.FSDirectory;

import edu.pitt.search.semanticvectors.VectorUtils;

import util.Sid;

import edu.pitt.search.semanticvectors.CompoundVectorBuilder;
import edu.pitt.search.semanticvectors.ObjectVector;
import edu.pitt.search.semanticvectors.SearchResult;
import edu.pitt.search.semanticvectors.VectorSearcher;
import edu.pitt.search.semanticvectors.VectorStore;
import edu.pitt.search.semanticvectors.VectorStoreRAM;
import edu.pitt.search.semanticvectors.ZeroVectorException;

public class MetaAnalyzer {

	ArrayList<String> allPMIDS = new ArrayList<String>();
	ArrayList<String> positivePMIDS = new ArrayList<String>();
	
	VectorStore termVecReader =null; VectorStore docVecReader = null;
	
	String query;
	float[] queryVector;

	int numSims = 20;//default - KNN
	
	IndexReader reader = null;
	HashMap<String,Integer> pathNum = new HashMap<String, Integer>(); 
	
	public MetaAnalyzer(String allPMIDsfileName, 
			String positivePMIDSfileName,
			String termVectorFile,
			String docVectorFile,
			String queryFile,
			String indexDir) throws IOException{
		
		System.err.println("Opening term vector store from file: " + termVectorFile);
		termVecReader = new VectorStoreRAM();
		termVecReader.InitFromFile(termVectorFile);

		System.err.println("Opening doc vector store from file: " + docVectorFile);
		docVecReader = new VectorStoreRAM();
		docVecReader.InitFromFile(docVectorFile);
		
		Scanner sc1 = new Scanner(new File(allPMIDsfileName), "UTF-8");
		while(sc1.hasNextLine()){
			allPMIDS.add(sc1.nextLine().trim());
		}
		sc1.close();
		
		sc1 = new Scanner(new File(positivePMIDSfileName), "UTF-8");
		while(sc1.hasNextLine()){
			positivePMIDS.add(sc1.nextLine().trim());
		}
		sc1.close();
		
		query="";
		if(new File(queryFile).exists()){
			sc1 = new Scanner(new File(queryFile), "UTF-8");
			while(sc1.hasNextLine()){
				query+=sc1.nextLine().trim()+" ";
			}
			query=query.trim().toLowerCase();
			sc1.close();
		}
		
		this.queryVector = CompoundVectorBuilder.getQueryVector(termVecReader,
				null,
				query.split("\\s+"));
		
		this.reader = IndexReader.open(FSDirectory.open(new File(indexDir)));
		int numDocs=reader.numDocs();
		for (int i = 0; i < numDocs; i++) {
			String path=reader.document(i).get("path");
			pathNum.put(path, i);
		}
		
	}
	
	public ArrayList<String> relevanceFeedBackSearch2() throws IOException{
		ArrayList<String> results = new ArrayList<String>();
		
		Enumeration<ObjectVector> docVecEnum = docVecReader.getAllVectors();
		HashMap<String, float[]> pathVectMap = new HashMap<String, float[]>();
		while (docVecEnum.hasMoreElements()) {
			ObjectVector docElement = docVecEnum.nextElement();
			String path = (String) docElement.getObject();
			float[] vector = docElement.getVector();
			pathVectMap.put(path, vector);
		}	
		
		HashSet<String> posTerms = new HashSet<String>();
		posTerms.addAll(Arrays.asList(query.split("\\s+")));
		HashSet<String> negTerms = new HashSet<String>();
		
		while(results.size() < docVecReader.getNumVectors()){
			posTerms.removeAll(negTerms);
			posTerms.remove("not"); //this is not only an irrelevant term, but also sets wrong impressions
			float[] qvector = CompoundVectorBuilder.getQueryVector(termVecReader,
					null,
					posTerms.toArray(new String[0]));
			String maxDocPath = getMostRelDoc(qvector,pathVectMap);			
			String pmid = maxDocPath.substring(maxDocPath.lastIndexOf("\\") + 1,
					maxDocPath.length() - 4);
			System.out.println(pmid + "\t"
					+ (this.positivePMIDS.contains(pmid) ? "true" : ""));
			results.add(pmid);
			
			String[] maxTerms = reader.getTermFreqVectors(pathNum.get(maxDocPath))[0].getTerms();
			if(this.positivePMIDS.contains(pmid))
				posTerms.addAll(Arrays.asList(maxTerms));
			else
				negTerms.addAll(Arrays.asList(maxTerms));
			pathVectMap.remove(maxDocPath);
		}
		
		
		return results;
	}
	
	
	
	
	private String getMostRelDoc(float[] qvector,
			HashMap<String, float[]> pathVectMap) {
		float maxScore = -1; String maxPath=null;
		for (String path:pathVectMap.keySet()) {
			float score = VectorUtils.scalarProduct(qvector, pathVectMap.get(path));
			if(score>maxScore){
				maxScore=score;
				maxPath=path;
			}
		}
		return maxPath;
	}

	public LinkedList<SearchResult> relevanceFeedBackSearch(String query, float threshold){
		VectorSearcher vecSearcher;
		LinkedList<SearchResult> results = new LinkedList<SearchResult>();
		LinkedList<SearchResult> refinedResults = new LinkedList<SearchResult>();
		
		try {
			vecSearcher =
				new VectorSearcher.VectorSearcherCosine(termVecReader,
						docVecReader,
						null,
						query.split("\\s"));
			System.err.print("Searching doc vectors, searchtype SUM ... \n");
			results = vecSearcher.getAllNeighbors(threshold);
		} catch (ZeroVectorException zve) {
			System.err.println(zve.getMessage());
			results = new LinkedList<SearchResult>();
		}

		ArrayList<float[]> foundPositives = new ArrayList<float[]>();
		ArrayList<float[]> foundNegatives = new ArrayList<float[]>();
		foundPositives.add(queryVector);//query vector is treated as a found positive
		
		int tp=0,fp=0,fn=0,tn=0;
		
		while (!results.isEmpty()) {
			
			float maxScore = 0;
			SearchResult maxResult = null;
			
			boolean criteria=false;
			
			for (SearchResult result : results) {
				float[] curVector = ((ObjectVector) result.getObject())
						.getVector();

				HashMap<float[], Float> maxs = new HashMap<float[], Float>();
				for (float[] pos : foundPositives) {
					float cosine = VectorUtils.scalarProduct(curVector, pos);
					addMax(maxs, pos, cosine);
				}
				/*for (float[] neg : foundNegatives) {
					float cosine = VectorUtils.scalarProduct(curVector, neg);
					addMax(maxs, neg, cosine);
				}*/

				float posCount = 0;
				//float negCount = 0;
				for (float[] neighbor : maxs.keySet()) {//printing both the neighbor and its similarity value
					if (foundPositives.contains(neighbor))
						//posCount++;
						posCount += maxs.get(neighbor);
					/*else if (foundNegatives.contains(neighbor))
						//negCount++;
						negCount += maxs.get(neighbor);
					*/
					else
						System.err.println("you reached a wrong place!");
				}

				if (maxScore < posCount) {
					maxScore = posCount;
					maxResult = result;
					criteria=(posCount>1||foundNegatives.size()==0);
				}
			}
			float score = maxResult.getScore();
			String file = ((ObjectVector) maxResult.getObject()).getObject()
					.toString();
			String pmid = file.substring(file.lastIndexOf("\\") + 1,
					file.length() - 4);
			System.out.println(score + "\t" + pmid + "\t"
					+ (this.positivePMIDS.contains(pmid) ? "true" : ""));
			results.remove(maxResult);
			refinedResults.add(maxResult);
			boolean actualPrediction = this.positivePMIDS.contains(pmid);
			if (actualPrediction)
				foundPositives.add(((ObjectVector) maxResult.getObject())
						.getVector());
			else
				foundNegatives.add(((ObjectVector) maxResult.getObject())
						.getVector());
			
			if(criteria&&actualPrediction) 
				tp++;
			else if(criteria&&!actualPrediction) 
				fp++;
			else if(!criteria&&actualPrediction) 
				fn++;
			else if(!criteria&&!actualPrediction) 
				tn++;
			
		}

		System.out.println("\ntp="+tp+"\nfp="+fp+"\nfn="+fn+"\ntn="+tn);
		return refinedResults;
	}
	
	
	private HashMap<float[], Float> addMax
		(HashMap<float[], Float> maxs,
			float[] vec, float sim) {
		if(maxs.size()<this.numSims)
			maxs.put(vec, sim);
		else{
			float min = Integer.MAX_VALUE;float[] minKey=null;
			Set<float[]> maxKeySet = maxs.keySet();
			for (float[] key : maxKeySet) {
				if(min>maxs.get(key)){
					min=maxs.get(key);
					minKey=key;
				}					
			}
			
			if(sim>min){
				maxs.remove(minKey);
				maxs.put(vec, sim);
			}
		}
		return maxs;
	}
	
	
	/**
	 * returns results for document search
	 * 
	 * @param query
	 * @param threshold
	 * @return results as LinkedList<SearchResult>, {@link http://semanticvectors.googlecode.com/svn/javadoc/latest-stable/pitt/search/semanticvectors/SearchResult.html}
	 */
	public LinkedList<SearchResult> docSearch(String query, float threshold){
		VectorSearcher vecSearcher;
		LinkedList<SearchResult> results = new LinkedList<SearchResult>();
		long starttime = System.currentTimeMillis();
		
		try {
			vecSearcher =
				new VectorSearcher.VectorSearcherCosine(termVecReader,
						docVecReader,
						null,
						query.split("\\s"));
			System.err.print("Searching doc vectors, searchtype SUM ... \n");
			results = vecSearcher.getAllNeighbors(threshold);
		} catch (ZeroVectorException zve) {
			System.err.println(zve.getMessage());
			results = new LinkedList<SearchResult>();
		}

		Sid.log("This Search took "+(System.currentTimeMillis()-starttime)+" milli-secs. \n" +
			"Saved the results.\n");
		return results;
	}

	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		MetaAnalyzer mAnalyzer = new MetaAnalyzer
//				("../SystematicReviewer_data/UrinaryIncontinence/systematic_review.txt", 
//				"../SystematicReviewer_data/UrinaryIncontinence/systematic_review_positive.txt", 
//				"../SystematicReviewer_data/UrinaryIncontinence/drxntermvectors.bin", 
//				"../SystematicReviewer_data/UrinaryIncontinence/incremental_docvectors.bin",
//				"../SystematicReviewer_data/UrinaryIncontinence/context.txt",
//				"../SystematicReviewer_data/UrinaryIncontinence/positional_index");
//in my computer, the arguments can be 	dict/NLP/NLP_systematic_review.txt,dict/NLP/NLP_systematic_review_positive.txt dict/NLP/context.txt dict/NLP/positional_index 
		MetaAnalyzer mAnalyzer = new MetaAnalyzer
				(args[0],args[1],args[2],args[3],args[4],args[5]);
		mAnalyzer.relevanceFeedBackSearch2();
	}
}