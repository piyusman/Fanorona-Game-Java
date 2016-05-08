package edu.nyu.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;

public class Fanorona {

	// Main method takes input from user
	
	public static void main(String[] args) 
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		 
	    String game = null;
	    
	    try 
	    {
	    	System.out.println("Choose game type 3 (3X3) or 5 (5X5) :");
	    	game = br.readLine();
	    	int abc=Integer.parseInt(game);
	    	Board b=new Board();
	    	b.initializeBoard(abc);
	    	
	    	System.out.println("Choose difficulty 1, 2 or 3:");
	    	String difficulty = br.readLine();
	    	int diff=Integer.parseInt(difficulty);
	    	b.diff=diff;
	    	System.out.println("Choose color black or white :");
	    	game = br.readLine();
	    	
	    	if (game.equalsIgnoreCase("black")){
	    		b.setPlayerColor(game);
	    		b.setAiColor("white");
	    		b.makeAIMove();
	    	}
	    	else{
	    		b.setPlayerColor(game);
	    		b.setAiColor("black");
	    	}
	    	
	    	b.printBoard();
	    	
	    	String cmd=null;
	    	System.out.println("\n\n\nPlease enter player move in the following format:");
	    	System.out.println("(from position)<space>(to position)");
	    	System.out.println("a,b c,d\n");
	    	System.out.println("Type \"quit\" to exit.\n\n\n");
	    	
	    	do{
	    		Position nextPosition=null;
	    		b.previousPositions=new HashSet<Position>();
	    		b.previousDirections=new HashSet<String>();
	    		int a=0;
	    		do{
	    			
	    			System.out.println("Enter a player move:");
	    			cmd=br.readLine();
	    			if (cmd.equals("quit"))
	    				System.exit(0);
	    			else if(cmd.equals("")){
	    				break;
	    			}
	    			try{
	    			String[] num=cmd.split(" ");
	    			int row1 = Integer.parseInt(num[0].split(",")[0])-1;
	    			int col1= Integer.parseInt(num[0].split(",")[1])-1;
	    			int row = Integer.parseInt(num[1].split(",")[0])-1;
	    			int col= Integer.parseInt(num[1].split(",")[1])-1;
	    			Position pos=new Position(row, col);
	    			Position p1=new Position(row1,col1);
	    			
	    			if(nextPosition!=null){
	    				if(nextPosition.equals(p1)|| !b.previousPositions.contains(p1) || !b.previousDirections.contains(b.getDirection(p1, pos))){
	    	    		
	    					nextPosition=pos;
	    					a=b.makePlayerMove(p1,pos);
	    					b.previousPositions.add(p1);
	    					b.previousDirections.add(b.getDirection(p1, pos));
	    				}
	    				else{
	    					System.out.println("Invalid Move! Enter valid move.");
	    					continue;
	    				}	    			
	    			}
	    			else{
	    				nextPosition=pos;
	    				a=b.makePlayerMove(p1,pos);
	    			}
	    			int gameFlag=b.checkGoalState();
		    		if(gameFlag==1){
		    			System.out.println("Its a draw!!");
		    			System.exit(0);
		    		}
		    		else if(gameFlag==2){
		    			System.out.println("Black won!!");
		    			System.exit(0);
		    		}
		    		else if(gameFlag==3){
		    			System.out.println("White won!!");
		    			System.exit(0);
		    		}}catch(Exception e){System.out.println("Something went wrong!!");}

	    		}while(a!=0);
	    		b.MOVES--;
	    		b.makeAIMove();
	    		b.MOVES--;
	    		int gameFlag=b.checkGoalState();
	    		if(gameFlag==1){
	    			System.out.println("Its a draw!!");
	    			System.exit(0);
	    		}
	    		else if(gameFlag==2){
	    			System.out.println("Black won!!");
	    			System.exit(0);
	    		}
	    		else if(gameFlag==3){
	    			System.out.println("White won!!");
	    			System.exit(0);
	    		}
	    		
	    	}while(!cmd.equals("quit"));
	    } 
	    catch (Exception ioe){
	         System.out.println("Something went wrong!!");
	         System.exit(1);
	    }
	}
	
	

}



