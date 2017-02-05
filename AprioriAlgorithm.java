import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class AprioriAlgorithm {
	static TreeMap <String, Double> FreqItemSetMap = new TreeMap<String, Double>();
	static TreeMap <String, Double> AssociationRuleMap = new TreeMap<String, Double>();
	static TreeMap <Integer, ArrayList<String>> nonFreqItemSetMap = new TreeMap<Integer, ArrayList<String>>();
	static TreeMap <Integer, ArrayList<String>> prevFreqGroupsMap = new TreeMap<Integer, ArrayList<String>>();
	static ArrayList<String> listOfItems = new ArrayList<String>();
	static ArrayList<String> candidateItemSets = new ArrayList<String>();
	
	//static ArrayList<String> prevFreqGroups = new ArrayList<String>();
	static double supportReq=0;
	static double confidenceReq=0;
	static String dataFile="";
	static int totalTransCount=0;
	static int maxDepthOfFreqSets=0;
	
	public static void getInputValues(){
		//***** Getting support & Confidence
		
		// create a scanner so we can read the command-line input
	    Scanner scanner = new Scanner(System.in);
	    
	    //Getting support
	    System.out.print("Enter Minimum Support in Percent: ");
	    supportReq = scanner.nextDouble();
	    
	    //Getting confidence
	    System.out.print("Enter Minimum Confidence in Percent: ");
	    confidenceReq = scanner.nextDouble();
	}
	public static void getDatabasetoPick(){
		int dbSelection;
		Scanner scanner = new Scanner(System.in);
	    //Getting DataBase
	    System.out.print("Choose Database: ");
		System.out.print("\n1-Sample: ");
		System.out.print("\n2-Amazon: ");
		System.out.print("\n3-Walmart: ");
		System.out.print("\n4-Shoprite: ");
		System.out.print("\n5-BestBuy: ");
		System.out.print("\n6-Adidas:\n");
		dbSelection = scanner.nextInt();
		switch(dbSelection){
	    case 1 :
	       dataFile="sample_transaction.txt";
	       break; //optional
	    case 2 :
	       dataFile="amazon.txt";
	       break; //optional
	    case 3 :
	    	dataFile="walmart.txt";
	       break; //optional
		case 4 :
			dataFile="shoprite.txt";
	       break; //optional
		case 5 :
			dataFile="bestbuy.txt";
	       break; //optional
	    case 6 :
	    	dataFile="adidas.txt";
	    break; //optional
	    
	    default : //Optional
	    	System.exit(1);
	}
	}
	//**** Getting Distinct Elements of Transaction Database
		public static void getDistinctTransactElem()    {
			
			//***** Reading File and forming arrayList
	   	 	// creating  input stream
			BufferedReader inputStream = null;
	        try{
	        	inputStream = new BufferedReader(new FileReader(dataFile));
	        	String transReader; //File pointer for Transaction file
	        	while ((transReader = inputStream.readLine()) != null) {
	    			 //System.out.println(transReader);
	    			 String[] transactionsArray = transReader.split(":", -1);
	    			 String[] TransactionItemsArray = transactionsArray[1].split(",", -1);
	    			 //System.out.println(Arrays.toString(TransactionItemsArray));
	    			
	    			 for(String tItems:TransactionItemsArray){
	    				 if(!listOfItems.contains(tItems)){
	    					 listOfItems.add(tItems);
	    				 }
	    			 }
	    			 totalTransCount++; 
	    		 } //*** while loop ends
	    		 Collections.sort(listOfItems);
	        	 inputStream.close();
	        }catch(Exception e){
	        	System.out.println("Error opening data file: "+ e.getMessage());
	        }
	    	 
		}
		//**** Getting Single elements of Candidate Item sets
		public static ArrayList<String> getDistinctSingleElementsOfCandidateSet(ArrayList <String> prevFreqcandItems,int setDepth){
			ArrayList<String> distinctItemsList = new ArrayList<String>();
			for(int i=0;i<prevFreqcandItems.size();i++){
				String[] tempArray = prevFreqcandItems.get(i).split("-");
				for(String tItems:tempArray){
					 if(!distinctItemsList.contains(tItems)){
						 distinctItemsList.add(tItems);
					 }
				 }
			}// Outer for loop ends
			Collections.sort(distinctItemsList);
			return distinctItemsList;
		}
		
	//**** Getting Frequent Item sets
		public static void getFrequentItemSets(ArrayList <String> candidateItems,int totalItems,int setDepth, double supportReq) {
			ArrayList<String> tempFreqItemsList = new ArrayList<String>();
			ArrayList<String> tempNonFreqGroups = new ArrayList<String>();
			//***** Checking transactions for one item set
	   	 	//Loop through elements.
	   	 	try{
	   	 		for (int i = 0; i < candidateItems.size(); i++) {
	   	 			int countOccurence=0;
	   	 			double support=0;
	   	 			String value = candidateItems.get(i);
		 		    Scanner file = new Scanner(new File(dataFile));
		 		   while(file.hasNextLine()){
		 			   int elemOccurence=0;
	 		           String fileLine = file.nextLine();
	 		           String[] tempArray = value.split("-");
	 					for(String tItems:tempArray){
	 						if(fileLine.indexOf(tItems) != -1){	
		 		            	elemOccurence++;
							 }
	 					 } //for loop end
	 					if(elemOccurence==setDepth){
	 						countOccurence++;
	 					}
	 		            
				}//*** while loop ends
			    support=((double)countOccurence/(double)totalItems) * 100;
			    if(support >= supportReq){
		    	  tempFreqItemsList.add(value);
		    	  FreqItemSetMap.put(value, support); //adding support
			    }
			    
			    file.close();
		 	 } //*** for loop ends
		 		 
			}catch(Exception e){
			System.out.println("Error opening1 file: "+ e.getMessage());
			}
	   	 	//Saving Non-Freq Groups
	   	 	tempNonFreqGroups=candidateItems;
	   	 	tempNonFreqGroups.removeAll(tempFreqItemsList);
	   	 	nonFreqItemSetMap.put(setDepth, tempNonFreqGroups);	
	   	 	// Saving Freq Groups
	   	 	prevFreqGroupsMap.put(setDepth, tempFreqItemsList);
		}
		//**** Start Candidate Item generation
		
 public static void startCandidateItemSetGen(){
	//Get Candidate Item Sets
 	candidateItemSets=listOfItems;
 	int totalElements=listOfItems.size();
	for(int i=1;i<=totalElements;i++){
   	 	ArrayList<String> depthListofItems = new ArrayList<String>(); // for holding distinct item sets at i-th depth
   	 	if(i>1){
   	 		depthListofItems=getDistinctSingleElementsOfCandidateSet(prevFreqGroupsMap.get(i-1),i);
   	 	}else{
   	 		depthListofItems=candidateItemSets;
   	 	}
		if(depthListofItems.size()>i){
			if(i>1){
 				String[] arr = new String[depthListofItems.size()];
 				for(int l = 0; l < depthListofItems.size(); l++) arr[l] = depthListofItems.get(l);
 				
 				//Getting Candidate Item Sets with non_freq superSets Removed
 				candidateItemSets.clear();
 				getCandidateItemSets(arr, i, 0, new String[i]);
			}
			//get Frequent Items sets
			getFrequentItemSets(candidateItemSets,totalTransCount,i,supportReq);
		}else{
			break;
		}
	} // Main for loop ends
 }

		//**** Getting Candidate Item sets
		
	 public static void getCandidateItemSets(String[] arr, int len, int startPosition, String[] result){
	       if (len == 0){
	        	//System.out.println("called");
	        	String cleme=String.join("-", result);
	        	int addFlag=1;
	        	int setDepth=result.length;
	        	if(setDepth>2){ //calling only if nonFreq has two element sets
	        		ArrayList<String> prevNonFreqList = new ArrayList<String>();
	        		String[] tempNonFreqSetElements;
	        		int ncCount=0;
	        		//*** Next removing supersets of non-freq sets
	        		for(int h=0;h<prevNonFreqList.size();h++){ // looping non-freq. array ABC etc
	        		  tempNonFreqSetElements=prevNonFreqList.get(h).split("-"); //Splittng each element of non freq AC or frigdeTV
	        		  for(String ncelem:tempNonFreqSetElements){
	        			  if(cleme.contains(ncelem)){
	        				  ncCount++;
	        			  }
	        		    } //For Loop ends
	            		 if(ncCount==(setDepth-1)){ //even if it is a superset of one neglect
	            			addFlag=0;
	            			break;
	            		 }
	            		 
	            	 }
		        	
	        	}//Super-Set check if closed
	        	if(addFlag==1){
	        		candidateItemSets.add(cleme);
	        	}
	        	return;
	        }       
	        for (int i = startPosition; i <= arr.length-len; i++){
	            result[result.length - len] = arr[i];
	            getCandidateItemSets(arr, len-1, i+1, result);
	        }
	       
	    }
						  /*************************************
						  *********PRINTING ASSOCIATIONS********
						  *************************************/
	 
	 public static String[] permuteSets(int i) {
			int length = (int) Math.pow(2, i);
			length--;
			String temp = Integer.toBinaryString(length);
			length = temp.length();
			String[] arr = new String[(int) (Math.pow(2, i) - 2)];
			for (int j = 0; j < arr.length; j++) {

				arr[j] = Integer.toBinaryString(j + 1);
				int alen = length - arr[j].length();
				for (int k = 0; k < alen; k++) {
					arr[j] = "0".concat(arr[j]);
				}
			}
			return arr;
		}
	 public static void startPrintingAssoRules(){
		 try{
		 for(int j=2;j<=maxDepthOfFreqSets;j++){ //Outer Map
			 ArrayList<String> FreqItemsSetAssc = new ArrayList<String>();
			 FreqItemsSetAssc=prevFreqGroupsMap.get(j); // FreqItem set of depth j
			 String[] permutationsOfDepth;
			 permutationsOfDepth=permuteSets(j);
			 for(int indElemInSet=0;indElemInSet<FreqItemsSetAssc.size();indElemInSet++){
				 double confidence=0;
				 String[] FreqGroupElemSplit=FreqItemsSetAssc.get(indElemInSet).split("-");
				 for(int k=0;k<permutationsOfDepth.length;k++){ //Permutations
					 //Array for Left Strings
					 ArrayList<String> leftGroupAsso = new ArrayList<String>();
					 ArrayList<String> rightgroupAsso = new ArrayList<String>();
					 String leftGroup="";
					 String rightGroup="";
					 String[] elementsOfEachPermuteString;
					 elementsOfEachPermuteString=permutationsOfDepth[k].split("");
					 for(int l=0;l<elementsOfEachPermuteString.length;l++){ //Permuted Elements
						 if(elementsOfEachPermuteString[l].equals("0")){
							 leftGroupAsso.add(FreqGroupElemSplit[l]);
						 }else if(elementsOfEachPermuteString[l].equals("1")){
							 rightgroupAsso.add(FreqGroupElemSplit[l]);
						 }
					 } //Permuted Elements ForLoop ends
					 for(int m=0;m<leftGroupAsso.size();m++){
						 leftGroup+=leftGroupAsso.get(m)+"-";
					 }
					 for(int n=0;n<rightgroupAsso.size();n++){
						 rightGroup+=rightgroupAsso.get(n)+"-";
					 }
					 if (leftGroup != null && leftGroup.length() > 0 && leftGroup.charAt(leftGroup.length()-1)=='-') {
						 leftGroup = leftGroup.substring(0, leftGroup.length()-1);
					    }
					 if (rightGroup != null && rightGroup.length() > 0 && rightGroup.charAt(rightGroup.length()-1)=='-') {
						 rightGroup = rightGroup.substring(0, rightGroup.length()-1);
					    }
					 //**** Checking Confidence and add to TreeMap
					 confidence=FreqItemSetMap.get(FreqItemsSetAssc.get(indElemInSet))/FreqItemSetMap.get(leftGroup)*100;
					 if(confidence>confidenceReq){
						 AssociationRuleMap.put(leftGroup+"--->"+rightGroup, confidence);
					 }
				 	} //Permute ForLoop ends
			 } // Each Group of Freq Set Split loop
		 } //Outer Map ForLoop ends
		}catch(Exception e){
			//System.out.println("Exception in Assoc rule gen: "+ e.getMessage());
		}
	 }
	 public static void main(String args[]) {
		//Getting Database
		getDatabasetoPick();
		System.out.print("\nChoosen Database: "+ dataFile +"\n");
		//getting Input values	
		getInputValues();
		
		//Getting Distinct Transaction Elements
		getDistinctTransactElem();
		
		//Starting Candidate Item Set Generation
		startCandidateItemSetGen();
		
		//**** Printing Generated  Frequent Item Sets
		System.out.println("*******************************************************************");
		System.out.println("Frequent Item Sets in Increasing order of Set-Depth:");
		System.out.println("*******************************************************************");
	   	for (Map.Entry<Integer, ArrayList<String>> entry : prevFreqGroupsMap.entrySet()) { 
	   		 maxDepthOfFreqSets++;
	         System.out.println("Set "+entry.getKey()+" : "+entry.getValue());
	     }
	   	
		System.out.println("*******************************************************************");
		System.out.println("Frequent Item Sets with Support:");
		System.out.println("*******************************************************************");
	   	 for (Map.Entry<String, Double> entry : FreqItemSetMap.entrySet()) {     
	         System.out.println("ElementGroup: "+entry.getKey()+"; Support: "+entry.getValue());
	     }
	   	
	   	//**** if only One item sets Print no Association rule
	   	if(maxDepthOfFreqSets==1){
	   		System.out.println("No Association rules can be generated. Only one Item sets are Frequent");
	   	}else if(maxDepthOfFreqSets>1){
		   	//**** Starting Association Rule Printing
		   	startPrintingAssoRules();
			//Printing Rules
		   	System.out.println("*******************************************************************");
		   	System.out.println("Generated Association Rules:");
		   	System.out.println("*******************************************************************");
		   	for (Map.Entry<String, Double> entry : AssociationRuleMap.entrySet()) {     
			 System.out.println("Association Rule: "+entry.getKey()+"; Confidence: "+entry.getValue());
		   	}
	   	
	     }
	   	
	}//Main Ends
}
