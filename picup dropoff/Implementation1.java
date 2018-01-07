import java.awt.AWTException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.IntStream;

public class Implementation1 {
	
	
	static World locations[] = new World[26];
	static int totalReward = 0;
	static double [][]QTable1 = new double[26][6];
	static double [][]QTable2 = new double[26][6];
	static File file1 = new File("QTable1.txt");
	static File file2 = new File("QTable2.txt");
	static File fileStep1 = new File("stepCount_Reward1.csv");
	static File fileStep2 = new File("stepCount_Reward2.csv");
	static File sr0=new File("presenstate0.csv");
	static File sr3=new File("presenstate1.csv");

	static int fileCount=0;
	static HashMap<Integer, Integer> dropoff = new HashMap<Integer, Integer>();
	static HashMap<Integer, Integer> pickup = new HashMap<Integer, Integer>();
	
	
	static double alpha = 0.3;
	static double gamma = 0.5;
	static boolean blockPickedUp = false;
	private static String exptType;
	static int randomCount = 0;
	static int greedyCount = 0;
	
	public static void main(String[] args) throws IOException, AWTException, InterruptedException {
		 HashMap<String, int[]> successorStates = new HashMap<String, int[]>();
		
		System.out.println("enter expt number..");
		Scanner sc = new Scanner(System.in);
		exptType = sc.nextLine();
		
		if(file1.exists())
			file1.delete();
		if(file2.exists())
			file2.delete();
		if(sr0.exists())
			sr0.delete();
		if(sr3.exists())
			sr3.delete();
		
		
		
		initialiseLocations();
		
		int presentState = restart();
		
		//Initialising the Successor states
		successorStates = initialiseSuccessorStates(successorStates);
	    
	    //Initialising Qtables with null values
	    initialiseQTables(QTable1,QTable2,successorStates);
	    
	   algorithm(successorStates,presentState);
	   
	}

	private static void algorithm(HashMap<String, int[]> successorStates, int presentState) throws IOException, AWTException, InterruptedException {
		// TODO Auto-generated method stub
		
		
		 //creating and opening text files for output
	    FileWriter f = new FileWriter("total reward.txt");
	    PrintWriter pw = new PrintWriter(f);
		int restartCounter = 0;
	    FileWriter f1 = new FileWriter("steps.txt");
	    PrintWriter pw1 = new PrintWriter(f1);
	    
	    PrintStream printStep1 = new PrintStream(new FileOutputStream(fileStep1,true));
	    PrintStream printStep2 = new PrintStream(new FileOutputStream(fileStep2,true));


	    
	    
	    for(int runCount=0;runCount<2;runCount++){
	    	int count=0;
	    	presentState=5;
	    	String tem = "successorStatesOf";
			String action = null ;
			pw.println("restart counter = "+restartCounter);
	    	while(count<6000){
	    		// To check whether it has reached terminal state i.e., all pickups are = 0 and all dropoffs are = max
	    		if(count==3000){
	    			pw.println("total reward for expt"+exptType+" and execution"+(runCount+1)+" after "+count+" steps: "+totalReward);
	    			pw.println("world has been reset for expt"+exptType+" and execution"+(runCount+1)+"after 3000 steps : "+restartCounter);
	    			displayQTables(successorStates, "3000");
	    			restartCounter=0;
	    		}
	    		PrintStream ps0;
	    		if(runCount==0)
	    		{
	    			ps0=new PrintStream(new FileOutputStream(sr0,true));
	    		}
	    		else
	    		{
	    			ps0=new PrintStream(new FileOutputStream(sr3,true));
	    		}
	    		ps0.print(presentState);
	    		ps0.printf("\n");
				int sumP=0, sumD =0;
	    		Set<Integer> pickupKeys = pickup.keySet();
	    		Iterator<Integer> iterator = pickupKeys.iterator();
	    		while(iterator.hasNext())
	    			sumP += pickup.get(iterator.next());
	    		Set<Integer> dropoffKeys = dropoff.keySet();
	    		iterator = dropoffKeys.iterator();
	    		while(iterator.hasNext())
	    			sumD += dropoff.get(iterator.next());
	    		if(sumP==0 && sumD==16){ // if sum of pickups=0 and sum of dropoffs = 16(max), then reset the agent and world
	    			presentState = restart();
	    			restartCounter++;
	    		}
	    		else{// else choose one of the expt number and choose any action based on expt
	    			action = getActionFromExpt(successorStates, presentState,count,action);
	    			
	    			pw1.print(presentState+" -> ");
		    		presentState = calculateQValue(successorStates,presentState,action,count);
		    		pw1.println(presentState+" : "+totalReward);
		    		if(runCount==0){
	    				printStep1.print(count+","+totalReward);
	    				printStep1.print("\n");
	    			}
		    		else{
		    			printStep2.print(count+","+totalReward);
	    				printStep2.print("\n");
		    		}
		    		count++;
	    		}
	    	}
	    	pw.println("total reward for expt"+exptType+" and execution"+(runCount+1)+" after 6000 steps: "+totalReward);
			pw.println("world has been reset for expt"+exptType+" and execution"+(runCount+1)+"after 6000 steps : "+restartCounter);
			pw.println("agent chose random selection for "+randomCount+" times and greedy selection for "+greedyCount+" times");
			totalReward=0;
			restartCounter=0;
			randomCount=0;
			greedyCount=0;
			displayQTables(successorStates,"6000");
			VisualiseMovement visualize = new VisualiseMovement();
			visualize.visualise(runCount);
			restart();
	    }
		pw1.flush();
		pw1.close();
		System.out.println(restartCounter);
		pw.flush();
		pw.close();
		ShowQTABLES qtables = new ShowQTABLES();
		qtables.showQtables();
	}

	private static void displayQTables(HashMap<String, int[]> successorStates, String heading) throws IOException {
		// TODO Auto-generated method stub
		fileCount++;
		File file3 = new File(heading+"Qtable1Visual"+fileCount+".csv");
		
		fileCount++;
		File file4 = new File(heading+"Qtable2Visual"+fileCount+".csv");
	
	    int xPoint = 0;
	    int yPoint = 0;
	    String temp = "successorStatesOf";
	    String temp2 = "";
	    PrintStream ps1 = new PrintStream(new FileOutputStream(file1,true));
	    PrintStream ps2 = new PrintStream(new FileOutputStream(file2,true));
	    PrintStream ps3 = new PrintStream(new FileOutputStream(file3));
	    PrintStream ps4 = new PrintStream(new FileOutputStream(file4));


	    ps1.printf("QTable1 after %s .\n", heading);
	    ps1.printf("\r\n");
	    ps1.printf("\t\t\t\t\t%12s%12s%12s%12s%12s%12s", "east", "west", "north", "south", "pickup", "dropoff");
	    ps1.printf("\r\n");
	    ps1.printf("-------------------------------------------------------------------------------------------------");
	    ps1.printf("\r\n");
	    
	    
	    ps2.println();
	    ps2.printf("QTable2 after %s .\n", heading);
	    ps2.printf("\r\n");
	    ps2.printf("\t\t\t\t\t%12s%12s%12s%12s%12s%12s", "east", "west", "north", "south", "pickup", "dropoff");
	    ps2.printf("\r\n");
	    ps2.printf("--------------------------------------------------------------------------------------------------");
	    ps2.printf("\r\n");
	    for (int i = 1; i < 26; i++) 
	    {
	        temp2 = temp + i;
	        xPoint = i / 5;
	        if( i % 5 == 0 ) xPoint --;
	        yPoint = (i - 1) % 5;

	        xPoint ++;
	        yPoint ++;
	        ps3.printf(xPoint+","+yPoint+",");
	        ps4.printf(xPoint+","+yPoint+",");
	        ps1.printf("%-3d%-3d%-5s", xPoint, yPoint, "blockPickedUp");
	        ps2.printf("%-3d%-3d%-5s", xPoint, yPoint, "blockNotPickedUp");
	        for (int j=0; j<6; j++)
	        {
	          if(successorStates.get(temp2)[j] == -1){
	        	  if(j!=5){
//		        	  bw3.write("N/A"+",");
//		        	  bw4.write("N/A"+",");
	        		  ps3.printf("N/A"+",");
	        		  ps4.printf("N/A"+",");

	        	  }
	        	  else{
//	        		  bw3.write("N/A");
//		        	  bw4.write("N/A");
	        		  ps3.printf("N/A");
	        		  ps4.printf("N/A");
	        	  }
	        	  ps1.printf("%12s", "N/A");
	        	  ps2.printf("%12s", "N/A");
	          }
	          else{
	        	  if(j!=5){
	        		  ps3.printf("%12.4f"+",",QTable1[i][j]);
	        		  ps4.printf("%12.4f"+",",QTable2[i][j]);

	        	  }
	        	  	
	        	  else{
	        		  ps3.printf("%12.4f",QTable1[i][j]);
	        		  ps4.printf("%12.4f",QTable2[i][j]);

	        	  }
	        	  ps1.printf("%12.4f",QTable2[i][j]);
	        	  ps2.printf("%12.4f", QTable1[i][j]);
	          }
	        }
	        ps1.print("\n");
	        ps2.print("\n");
	        ps3.print("\n");
	        ps4.printf("\n");
	        temp2 = "";
	        xPoint = 0;
	        yPoint = 0;
	    }

	    ps1.println();
	    ps1.flush();
	    ps1.close();
	    ps2.println();
	    ps2.flush();
	    ps2.close();
	    ps3.flush();
	    ps3.close();
	    ps4.flush();
	    ps4.close();
	    
		
	}

	private static void initialiseQTables(double[][] qTable12, double[][] qTable22,HashMap<String, int[]> successorStates) {
		// TODO Auto-generated method stub
		
		
		for(int i=1;i<26;i++){
	    	for(int j=0;j<6;j++){
	    		if(successorStates.get("successorStatesOf"+i)[j]!=-1){
	    			QTable1[i][j]=0.0;
	    			QTable2[i][j]=0.0;
	    		}
	    	}
	    }
		
	}

	private static HashMap<String, int[]> initialiseSuccessorStates(HashMap<String, int[]> successorStates) {
		// TODO Auto-generated method stub
		
		successorStates.put("successorStatesOf1", new int[] {2, -1, -1, 6, 1, -1});
	    successorStates.put("successorStatesOf2", new int[] {3, 1, -1, 7, -1, -1});
	    successorStates.put("successorStatesOf3", new int[] {4, 2, -1, 8, -1, -1});
	    successorStates.put("successorStatesOf4", new int[] {5, 3, -1, 9, -1, -1});
	    successorStates.put("successorStatesOf5", new int[] {-1, 4, -1, 10, -1, -1});
	    successorStates.put("successorStatesOf6", new int[] {7, -1, 1, 11, -1, -1});
	    successorStates.put("successorStatesOf7", new int[] {8, 6, 2, 12, -1, -1});
	    successorStates.put("successorStatesOf8", new int[] {9, 7, 3, 13, -1, -1});
	    successorStates.put("successorStatesOf9", new int[] {10, 8, 4, 14, -1, -1});
	    successorStates.put("successorStatesOf10", new int[] {-1, 9, 5, 15, -1, -1});
	    successorStates.put("successorStatesOf11", new int[] {12, -1, 6, 16, -1, -1});
	    successorStates.put("successorStatesOf12", new int[] {13, 11, 7, 17, -1, -1});
	    successorStates.put("successorStatesOf13", new int[] {14, 12, 8, 18, 13, -1});
	    successorStates.put("successorStatesOf14", new int[] {15, 13, 9, 19, -1, -1});
	    successorStates.put("successorStatesOf15", new int[] {-1, 14, 10, 20, -1, -1});
	    successorStates.put("successorStatesOf16", new int[] {17, -1, 11, 21, 16, -1});
	    successorStates.put("successorStatesOf17", new int[] {18, 16, 12, 22, -1, -1});
	    successorStates.put("successorStatesOf18", new int[] {19, 17, 13, 23, -1, -1});
	    successorStates.put("successorStatesOf19", new int[] {20, 18, 14, 24, -1, 19});
	    successorStates.put("successorStatesOf20", new int[] {-1, 19, 15, 25, -1, -1});
	    successorStates.put("successorStatesOf21", new int[] {22, -1, 16, -1, -1, 21});
	    successorStates.put("successorStatesOf22", new int[] {23, 21, 17, -1, -1, -1});
	    successorStates.put("successorStatesOf23", new int[] {24, 22, 18, -1, -1, -1});
	    successorStates.put("successorStatesOf24", new int[] {25, 23, 19, -1, -1, -1});
	    successorStates.put("successorStatesOf25", new int[] {-1, 24, 20, -1, 25, -1});
		
	    return successorStates;
	}

	private static void initialiseLocations() {
		// TODO Auto-generated method stub
		
		for(int i=1;i<26;i++){
			locations[i] = new World();
		}
		locations[1].north=false;
		locations[1].west=false;
		locations[1].pickup=true;								
		
		locations[2].north=false;
		locations[3].north=false;
		locations[4].north=false;
		locations[5].north=false;
		locations[5].east=false;

		locations[6].west=false;
		locations[10].east=false;
		locations[11].west=false;
		locations[13].pickup=true;
		locations[15].east=false;

		locations[16].west=false;
		locations[16].pickup=true;
		locations[19].dropoff=true;
		locations[20].east=false;

		locations[21].west=false;
		locations[21].south=false;
		locations[21].dropoff=true;
		locations[22].south=false;
		locations[23].south=false;
		locations[24].south=false;
		locations[25].south=false;
		locations[25].east=false;
		locations[25].pickup=true;
		
	}

	private static String getActionFromExpt(HashMap<String, int[]> successorStates, int presentState, int count, String action) {
		// TODO Auto-generated method stub
		double randValue = Math.random();
		switch(exptType)
          {
			
            case "1" : action = (count < 3000) ? chooseRandAction(successorStates, presentState) : findNextActionWithHighestQ(successorStates, presentState, getMaxQValue(presentState, successorStates));
            			break;
            case "2" : action = (count < 200) ? chooseRandAction(successorStates, presentState) : (randValue <= 0.85) ? findNextActionWithHighestQ(successorStates, presentState, getMaxQValue(presentState, successorStates)) : chooseRandAction(successorStates, presentState); 
            			break;
            case "3" : action = (count < 200) ? chooseRandAction(successorStates, presentState) : (randValue <= 0.85) ? findNextActionWithHighestQ(successorStates, presentState, getMaxQValue(presentState, successorStates)) : chooseRandAction(successorStates, presentState); 
						break;        
            default : System.out.println("invalid entry of expt number");
            			System.exit(0);
          }
		return action;
	}

	private static int calculateQValue(HashMap<String, int[]> successorStates, int presentState2, String action, int count) throws IOException {
		// TODO Auto-generated method stub
		int nextState = 0 ;
		if(action.equals("pickup")){
			return pickupAction(action,presentState2,successorStates,count,nextState);
		}
		if(action.equals("dropoff")){
			return dropoffAction(action,presentState2,successorStates,count,nextState);
		}
		else{
			String temp = "successorStatesOf";
			totalReward-=1;
			switch(action){
				case "east" : return eastAction(action,presentState2,successorStates,count,nextState,temp);
				case "west" : return westAction(action,presentState2,successorStates,count,nextState,temp);
				case "north" :return northAction(action,presentState2,successorStates,count,nextState,temp);
				case "south" :return southAction(action,presentState2,successorStates,count,nextState,temp);
			}
		}

		return -1;
		
	}

	private static int southAction(String action, int presentState2, HashMap<String, int[]> successorStates, int count,
			int nextState, String temp) {
		// TODO Auto-generated method stub
		
		nextState = successorStates.get(temp+presentState2)[3];
		   if(!blockPickedUp){
		   if(exptType.equals("1")||exptType.equals("2"))
				QTable1[presentState2][3] = ((1 - alpha) * (QTable1[presentState2][3])) + ((alpha) * (-1 + (gamma * getMaxQValue(nextState, successorStates))));
		  else
				QTable1[presentState2][3] = ((1 - alpha) * (QTable1[presentState2][3])) + ((alpha) * (-1 + (gamma * QTable1[nextState][mapActionToInteger(getActionFromExpt(successorStates, nextState, count, ""))])));
			
			System.out.println(presentState2+" -> "+nextState+" : "+ QTable1[presentState2][3]); 
		    return nextState;
		   }
		   else{
			   if(exptType.equals("1")||exptType.equals("2"))
					QTable2[presentState2][3] = ((1 - alpha) * (QTable2[presentState2][3])) + ((alpha) * (-1 + (gamma * getMaxQValue(nextState, successorStates))));
			   else
					QTable2[presentState2][3] = ((1 - alpha) * (QTable2[presentState2][3])) + ((alpha) * (-1 + (gamma * QTable2[nextState][mapActionToInteger(getActionFromExpt(successorStates, nextState, count, ""))])));
		
			   System.out.println(presentState2+" -> "+nextState+" : "+ QTable2[presentState2][3]);  
			    return nextState;
		   }
		   
	}

	private static int northAction(String action, int presentState2, HashMap<String, int[]> successorStates, int count,
			int nextState, String temp) {
		// TODO Auto-generated method stub
		
		 nextState = successorStates.get(temp+presentState2)[2];
		   if(!blockPickedUp){
		  if(exptType.equals("1")||exptType.equals("2"))
				QTable1[presentState2][2] = ((1 - alpha) * (QTable1[presentState2][2])) + ((alpha) * (-1 + (gamma * getMaxQValue(nextState, successorStates))));
		  else
				QTable1[presentState2][2] = ((1 - alpha) * (QTable1[presentState2][2])) + ((alpha) * (-1 + (gamma * QTable1[nextState][mapActionToInteger(getActionFromExpt(successorStates, nextState, count, ""))])));
			
		   System.out.println(presentState2+" -> "+nextState+" : "+ QTable1[presentState2][2]); 
		    return nextState;
		   }
		   else{
				 if(exptType.equals("1")||exptType.equals("2"))
						QTable2[presentState2][2] = ((1 - alpha) * (QTable2[presentState2][2])) + ((alpha) * (-1 + (gamma * getMaxQValue(nextState, successorStates))));
				  else
						QTable2[presentState2][2] = ((1 - alpha) * (QTable2[presentState2][2])) + ((alpha) * (-1 + (gamma * QTable2[nextState][mapActionToInteger(getActionFromExpt(successorStates, nextState, count, ""))])));
			
				  System.out.println(presentState2+" -> "+nextState+" : "+ QTable2[presentState2][2]);  
				    return nextState;
			   
		   }
		 
	}

	private static int westAction(String action, int presentState2, HashMap<String, int[]> successorStates, int count,
			int nextState, String temp) {
		// TODO Auto-generated method stub
		
		 nextState = successorStates.get(temp+presentState2)[1];
		  if(!blockPickedUp){
		  if(exptType.equals("1")||exptType.equals("2"))
				QTable1[presentState2][1] = ((1 - alpha) * (QTable1[presentState2][1])) + ((alpha) * (-1 + (gamma * getMaxQValue(nextState, successorStates))));
			else
				QTable1[presentState2][1] = ((1 - alpha) * (QTable1[presentState2][1])) + ((alpha) * (-1 + (gamma * QTable1[nextState][mapActionToInteger(getActionFromExpt(successorStates, nextState, count, ""))])));
			
		  System.out.println(presentState2+" -> "+nextState+" : "+ QTable1[presentState2][1]); 
		  return nextState;
	      }
		  else{
			  if(exptType.equals("1")||exptType.equals("2"))
					QTable2[presentState2][1] = ((1 - alpha) * (QTable2[presentState2][1])) + ((alpha) * (-1 + (gamma * getMaxQValue(nextState, successorStates))));
			  else
					QTable2[presentState2][1] = ((1 - alpha) * (QTable2[presentState2][1])) + ((alpha) * (-1 + (gamma * QTable2[nextState][mapActionToInteger(getActionFromExpt(successorStates, nextState, count, ""))])));
			
			  System.out.println(presentState2+" -> "+nextState+" : "+ QTable2[presentState2][1]);  
			    return nextState;
		  }
		
	}

	private static int eastAction(String action, int presentState2, HashMap<String, int[]> successorStates, int count,
			int nextState, String temp) {
		// TODO Auto-generated method stub
		
		nextState = successorStates.get(temp+presentState2)[0];
		  if(!blockPickedUp){
		  if(exptType.equals("1")||exptType.equals("2"))
				QTable1[presentState2][0] = ((1 - alpha) * (QTable1[presentState2][0])) + ((alpha) * (-1 + (gamma * getMaxQValue(nextState, successorStates))));
			else
				QTable1[presentState2][0] = ((1 - alpha) * (QTable1[presentState2][0])) + ((alpha) * (-1 + (gamma * QTable1[nextState][mapActionToInteger(getActionFromExpt(successorStates, nextState, count, ""))])));
				
		  System.out.println(presentState2+" -> "+nextState+" : "+ QTable1[presentState2][0]); 
		  return nextState;
		  }
		  else{
			  if(exptType.equals("1")||exptType.equals("2"))
					QTable2[presentState2][0] = ((1 - alpha) * (QTable2[presentState2][0])) + ((alpha) * (-1 + (gamma * getMaxQValue(nextState, successorStates))));
			  else
					QTable2[presentState2][0] = ((1 - alpha) * (QTable2[presentState2][0])) + ((alpha) * (-1 + (gamma * QTable2[nextState][mapActionToInteger(getActionFromExpt(successorStates, nextState, count, ""))])));
			
			  System.out.println(presentState2+" -> "+nextState+" : "+ QTable2[presentState2][0]);  
			    return nextState;
		  }
		
	}

	private static int dropoffAction(String action, int presentState2, HashMap<String, int[]> successorStates,int count, int nextState) {
		// TODO Auto-generated method stub
		nextState = presentState2;
		dropoff.put(presentState2, dropoff.get(presentState2)+1);
		totalReward+=12;
		if(exptType.equals("1")||exptType.equals("2"))
			QTable2[presentState2][5] = ((1 - alpha) * (QTable2[presentState2][5])) + ((alpha) * (12 + (gamma * getMaxQValue(nextState, successorStates))));
		else
			QTable2[presentState2][5] = ((1 - alpha) * (QTable2[presentState2][5])) + ((alpha) * (12 + (gamma * QTable2[nextState][mapActionToInteger(getActionFromExpt(successorStates, nextState, count, ""))])));
		blockPickedUp = false;
	    System.out.println(presentState2+" -> "+nextState+" : "+ QTable2[presentState2][5]);  
		return nextState;
	}

	private static int pickupAction(String action, int presentState2, HashMap<String, int[]> successorStates, int count, int nextState) {
		// TODO Auto-generated method stub
		nextState = presentState2;
		pickup.put(presentState2, pickup.get(presentState2)-1);
		totalReward+=12;
		if(exptType.equals("1")||exptType.equals("2"))
			QTable1[presentState2][4] = ((1 - alpha) * (QTable1[presentState2][4])) + ((alpha) * (12 + (gamma * getMaxQValue(nextState, successorStates))));
		else
			QTable1[presentState2][4] = ((1 - alpha) * (QTable1[presentState2][4])) + ((alpha) * (12 + (gamma * QTable1[nextState][mapActionToInteger(getActionFromExpt(successorStates, nextState, count, ""))])));

		blockPickedUp = true;
	    System.out.println(presentState2+" -> "+nextState+" : "+ QTable1[presentState2][4]); 
		return nextState; 
	}

	private static int mapActionToInteger(String actionRecieved) {
		// TODO Auto-generated method stub
		switch(actionRecieved){
		case "east" : return 0;
		case "west" : return 1;
		case "north" : return 2;
		case "south" : return 3;
		case "pickup" : return 4;
		case "dropoff" : return 5;
		default: return -1;
		}
	}

	private static double getRandomQValue(int nextState, HashMap<String, int[]> successorStates) {
		// TODO Auto-generated method stub
		String temp = "successorStatesOf";
		ArrayList<Double> randArray = new ArrayList<>();
		for(int i=0;i<6;i++){
			if((i==5&&successorStates.get(temp+nextState)[i]!=-1 && !blockPickedUp) || (i==4&&successorStates.get(temp+nextState)[i]!=-1 && blockPickedUp)){
				continue;
			}
			if(blockPickedUp)
				if(successorStates.get(temp+nextState)[i]!=-1)
					randArray.add(QTable2[nextState][i]);
			else
				if(successorStates.get(temp+nextState)[i]!=-1)
					randArray.add(QTable1[nextState][i]);
					
		}
		Random rand = new Random();
		int randomNumber = rand.nextInt(randArray.size());
		return randArray.get(randomNumber);
	}

	private static String findNextActionWithHighestQ(HashMap<String, int[]> successorStates, int presentState2,double maxQValue) {
		// TODO Auto-generated method stub
		greedyCount++;
		String temp = "successorStatesOf";
		double max = -100.0;
		int direcOfMax = -1;
		if(successorStates.get(temp+presentState2)[5]!=-1&& blockPickedUp&&dropoff.get(presentState2)<8){
			return "dropoff";
		}
		if(successorStates.get(temp+presentState2)[4]!=-1 && !blockPickedUp&&pickup.get(presentState2)>0){
			return "pickup";
		}
		for(int i=0;i<4;i++){
			if(blockPickedUp && successorStates.get(temp+presentState2)[i]!=-1 && max<QTable2[presentState2][i] ){
				max=QTable2[presentState2][i];
				direcOfMax=i;
			}
			else if(!blockPickedUp && successorStates.get(temp+presentState2)[i]!=-1 && max<QTable1[presentState2][i]){
				max = QTable1[presentState2][i];
				direcOfMax=i;
			}
		}
		return selectActionFromNumber(direcOfMax);
	}

	private static double getMaxQValue(int nextState, HashMap<String, int[]> successorStates) {
		// TODO Auto-generated method stub
		String temp = "successorStatesOf";
		double max = -100.0;
		int length;
		if(successorStates.get(temp+nextState)[4]!=-1 && pickup.get(nextState)!=0 && !blockPickedUp)
			length=6;
		else if(successorStates.get(temp+nextState)[5]!=-1 && dropoff.get(nextState)!=8 && blockPickedUp)
			length=6;
		else
			length=4;
		for(int i=0;i<length;i++){
			if((i==5&&successorStates.get(temp+nextState)[i]!=-1 && !blockPickedUp) || (i==4&&successorStates.get(temp+nextState)[i]!=-1 && blockPickedUp)){
					continue;
				}
			if(blockPickedUp && successorStates.get(temp+nextState)[i]!=-1 && max<QTable2[nextState][i] )
				max=QTable2[nextState][i];
			else if(!blockPickedUp && successorStates.get(temp+nextState)[i]!=-1 && max<QTable1[nextState][i])
				max = QTable1[nextState][i];
		}
		return max;
	}

	private static String chooseRandAction(HashMap<String, int[]> successorStates, int presentState2) {
		// TODO Auto-generated method stub
		randomCount++;
		String tem = "successorStatesOf";
		int[] succStatesArr = successorStates.get(tem+presentState2);		
		ArrayList<Integer> randArr = new ArrayList<>();
		if(successorStates.get(tem+presentState2)[5]!=-1&& blockPickedUp&&dropoff.get(presentState2)<8){
			return "dropoff";
		}
		if(successorStates.get(tem+presentState2)[4]!=-1 && !blockPickedUp&&pickup.get(presentState2)>0){
			return "pickup";
		}
		int length;
		if((blockPickedUp&&successorStates.get(tem+presentState2)[4]!=-1)||(!blockPickedUp&&successorStates.get(tem+presentState2)[5]!=-1))
			length=4;
		else if((successorStates.get(tem+presentState2)[5]!=-1 && dropoff.get(presentState2)==8) ||  (successorStates.get(tem+presentState2)[4]!=-1 && pickup.get(presentState2)==0))
			length=4;
		else
			length=6;
		
		for(int i=0;i<length;i++){
			if(succStatesArr[i]!=-1)
				randArr.add(i);
		}
		Random rand = new Random();
		int randNumber = rand.nextInt(randArr.size());
		return selectActionFromNumber(randArr.get(randNumber));
	}

	private static String selectActionFromNumber(int i) {
		// TODO Auto-generated method stub
		switch(i){
		case 0: return "east";
		case 1: return "west";
		case 2: return "north";
		case 3: return "south";
		case 4: return "pickup";
		case 5: return "dropoff";
		default: return null;
		}
	}
	
	private static int restart() {
		// TODO Auto-generated method stub
		
		blockPickedUp = false;

		//Initialising the pickup locations
		pickup.put(1,4);
		pickup.put(13,4);
		pickup.put(16,4);
		pickup.put(25,4);
		
		//Initialising the dropoff locations
		dropoff.put(19, 0);
		dropoff.put(21, 0);
		return 5;

	}	

	


}
