package edu.nyu.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Board {
	
	final int EMPTY=0;
	final int BLACK=1;
	final int WHITE=2;
	int MOVES=40;
	private String aiColor;
	private String playerColor;
	int[][] board;
	int boardRow;
	int boardCol;
	int currPlayer;
	int diff=1;
	ArrayList<Position> strongIntersections;
	HashSet<Position> previousPositions;
	HashSet<String> previousDirections;
	
	public Board(){
	
		strongIntersections=new ArrayList<Position>();
		strongIntersections.add(new Position(0,0));
		strongIntersections.add(new Position(0,2));
		strongIntersections.add(new Position(0,4));
		strongIntersections.add(new Position(1,1));
		strongIntersections.add(new Position(1,3));
		strongIntersections.add(new Position(2,0));
		strongIntersections.add(new Position(2,2));
		strongIntersections.add(new Position(2,4));
		strongIntersections.add(new Position(3,1));
		strongIntersections.add(new Position(3,3));
		strongIntersections.add(new Position(4,0));
		strongIntersections.add(new Position(4,2));
		strongIntersections.add(new Position(4,4));
		
	}
	
	public String getAiColor() {
		return aiColor;
	}
	public void setAiColor(String aiColor) {
		this.aiColor = aiColor;
	}
	public String getPlayerColor() {
		return playerColor;
	}
	public void setPlayerColor(String playerColor) {
		this.playerColor = playerColor;
	}
	
	
	// Board initialization code after user chooses 3X3 or 5X5
	void initializeBoard(int columns){
		board= new int[columns][columns];
		this.boardRow=columns;
		this.boardCol=columns;
		System.out.println("Board"+columns+"X"+columns);
		int middle=columns/2;
		int nextBoardColor=BLACK;
		previousPositions = new HashSet<Position>();
		previousDirections= new HashSet<String>();
		for (int row=0; row<columns;row++){
			for (int column=0;column<columns;column++){
				if(row<middle){
					board[row][column]=BLACK;
				}
				else if(row>middle){
					board[row][column]=WHITE;
				}
				else{
					if (column==middle){
						board[row][column]=EMPTY;
					}
					else{
						board[row][column]=nextBoardColor;
						if (nextBoardColor==BLACK){
							nextBoardColor=WHITE;
						}
						else{
							nextBoardColor=BLACK;
						}
					}
				}
			}
		}
		
	}
	
	// Method to copy board state from int[][] to int[][]
	
	public int[][] copy(int[][] input) {
	      int[][] target = new int[input.length][];
	      for (int i=0; i <input.length; i++) {
	        target[i] = Arrays.copyOf(input[i], input[i].length);
	      }
	      return target;
	}
	
	
	//Method to make the COmputer/AI move
	void makeAIMove(){
		
		System.out.println("\nComputer started to think.....");
		AI a=new AI();
		Node rootNode=new Node(this.copy(board));
		rootNode.setCurrentPlayer(aiColor);
		long startTime = System.nanoTime();
		a.buildTree(rootNode,10);
		if(this.diff==3){
			this.diff=10;
		}
		else if(this.diff==2){
			this.diff=5;
		}
		
		a.alphaBeta(rootNode, this.diff, -99999,99999,aiColor);
		this.board=a.getBestMove(rootNode);
		long endTime = System.nanoTime();
		System.out.println("Time taken:"+(endTime - startTime)/1000000000+" seconds");
		System.out.println("Total generated nodes: "+(rootNode.getNoOfGeneratedNode()+1));
//		System.out.println("Total Depth:"+rootNode.getTreeDepth());
		System.out.println("Cuttoff used:"+a.cuttoff);
		System.out.println("Total Max Prunning:"+a.maxPrune);
		System.out.println("Total Min Prunning:"+a.minPrune);
		
		System.out.println("Computer moved.\n");
		
		this.printBoard();
	}
	
	
	//Method to make a user/player move
	
	int makePlayerMove(Position p1,Position p){
		
		int flag=0;
		ArrayList<Position> pos=this.getPossiblePlaces(playerColor);
		if(pos.contains(p1)){
			if(this.getNeighbours(p1).contains(p)){
				
			if(isPaikaAllowed(playerColor) && previousPositions.size()==0){
				if (playerColor.equals("white")){
					board[p1.getRow()][p1.getCol()]=EMPTY;
					board[p.getRow()][p.getCol()]=WHITE;
				}
				else{
					board[p1.getRow()][p1.getCol()]=EMPTY;
					board[p.getRow()][p.getCol()]=BLACK;this.currPlayer=WHITE;
				}
			}
			else{	
				if(this.isApproachMove(p1, p)){
					this.captureApproach(p1, p);
					
					if(this.isAbleToCapture(p)){
						flag=1;
						previousPositions.add(p1);
						previousDirections.add(this.getDirection(p1, p));
					}							
				}
				else if(this.isWithdrawMove(p1, p)){
					this.printBoard();
					this.captureWithdraw(p1, p);
					//System.out.println(""+p.getRow()+","+p.getCol());
					if(this.isAbleToCapture(p)){
						flag=1;
						previousPositions.add(p1);
						previousDirections.add(this.getDirection(p1, p));
					}
				}
				else{System.out.println("Wrong move!! Enter valid move.");
				flag=1;}
			
			}
			
			}
			else{
				System.out.println("Wrong move!! Enter valid move.");
				flag=1;
			}
		}
		else{
			System.out.println("Wrong move!! Enter valid move.");
			flag=1;
		}
		
		if (flag!=1){
			System.out.println("Player moved.");
		}
		this.printBoard();
		return flag;

	}
	
	
	// Method to check that given color is allowed to make a Paika move
	
	boolean isPaikaAllowed(String selectedColor){
		ArrayList<Position> pos=this.getPossiblePlaces(selectedColor);
		boolean b=true;
		
		for (Position p2:pos){
			if(this.isAbleToCapture(p2)){
				b=false;
			}
		}			
		return b;
	}
	
	//Method to print the current board state
	
	void printBoard(){

		char charToPrint;
		for (int row=0; row<boardRow;row++){
			if(boardRow==3){
				System.out.println("-------------");
			}
			else{
				System.out.println("--------------------------");
			}
			
			for (int column=0;column < boardCol;column++){
				if(board[row][column]==1){
					charToPrint='B';
				}
				else if(board[row][column]==2){
					charToPrint='W';
				}
				else{
					charToPrint='-';
				}
				
				System.out.print(""+charToPrint+"     ");	
				
			}
			System.out.print("");
			System.out.println("");
		}
		if(boardRow==3){
			System.out.println("-------------");
		}
		else{
			System.out.println("--------------------------");
		}
	}
	
	// Method to get the direction from current move and next move
	
	String getDirection(Position pos1,Position pos2){
		String direction=null;
		if(pos1.getRow()-1==pos2.getRow() && pos1.getCol()==pos2.getCol()){
			direction="u";
		}
		else if(pos1.getRow()+1==pos2.getRow() && pos1.getCol()==pos2.getCol()){
			direction="d";
		}
		else if(pos1.getRow()==pos2.getRow() && pos1.getCol()-1==pos2.getCol()){
			direction="l";
		}
		else if(pos1.getRow()==pos2.getRow() && pos1.getCol()+1==pos2.getCol()){
			direction="r";
		}
		else if(pos1.getRow()+1==pos2.getRow() && pos1.getCol()+1==pos2.getCol()){
			direction="dr";
		}
		else if(pos1.getRow()+1==pos2.getRow() && pos1.getCol()-1==pos2.getCol()){
			direction="dl";
		}
		else if(pos1.getRow()-1==pos2.getRow() && pos1.getCol()+1==pos2.getCol()){
			direction="ur";
		}
		else if(pos1.getRow()-1==pos2.getRow() && pos1.getCol()-1==pos2.getCol()){
			direction="ul";
		}
			
		return direction;		
	}

	
	//Method to get all possible neighbours of the given position
	
	ArrayList<Position> getNeighbours(Position pos){
		ArrayList<Position> a=new ArrayList<Position>();
		ArrayList<String> directions= new ArrayList<String>();
		directions.add("u");
		directions.add("d");
		directions.add("l");
		directions.add("r");
		if(strongIntersections.contains(pos)){
			directions.add("ul");
			directions.add("ur");
			directions.add("dl");
			directions.add("dr");
		}
		for (String direction:directions){
			Position newPos=getNewPostion(pos, direction);
			if(isValidPosition(newPos) && isPositionEmpty(newPos) && !isSameDirection(direction) && !isSamePosition(newPos)){
				a.add(newPos);
			}
		}
		return a;
		
	}
	
	
	//Method to get the next position from current position and given direction
	
	Position getNewPostion(Position p, String c){
		Position np=new Position();
		if(c.equals("u")){
			np.setRow(p.getRow()-1);
			np.setCol(p.getCol());
		}
		else if(c.equals("d")){
			np.setRow(p.getRow()+1);
			np.setCol(p.getCol());
		}
		else if(c.equals("l")){
			np.setRow(p.getRow());
			np.setCol(p.getCol()-1);
		}
		else if(c.equals("r")){
			np.setRow(p.getRow());
			np.setCol(p.getCol()+1);
		}
		else if(c.equals("ul")){
			np.setRow(p.getRow()-1);
			np.setCol(p.getCol()-1);
		}
		else if(c.equals("ur")){
			np.setRow(p.getRow()-1);
			np.setCol(p.getCol()+1);
		}
		else if(c.equals("dl")){
			np.setRow(p.getRow()+1);
			np.setCol(p.getCol()-1);
		}
		else if(c.equals("dr")){
			np.setRow(p.getRow()+1);
			np.setCol(p.getCol()+1);
		}
		return np;
	}
	
	//Method to check the whether the given position is valid or not depending on the board size
	
	boolean isValidPosition(Position p){
		if((p.getRow()>=0 && p.getCol()>=0 && p.getRow()<boardRow && p.getCol()<boardCol)){
			return true;
		}
		else{
			return false;
		}
	}
	
	//Method to check that position is empty or not
	
	boolean isPositionEmpty(Position pos){
		if( board[pos.getRow()][pos.getCol()] == 0 )
			return true;
		else
			return false;
	}
	
	// Method to check the given direction previously visited or not
	
	boolean isSameDirection(String d){
		boolean b=false;
		if(this.previousDirections!=null){
			for(String direction:this.previousDirections){
				if(d.equals(direction)){
					b=true;
				}			
			}
		}	
		return b;
	}
	
	//Method to check the given position previously visited or not
	
	boolean isSamePosition(Position p1){
		boolean b=false;
		if(this.previousPositions!=null){
			for(Position p:this.previousPositions){
				if(p.equals(p1)){
					b=true;
				}
			}
		}
		return b;
	}

	//Method to check that from given position one can make a capture move or not
	
	boolean isAbleToCapture(Position p){
		ArrayList<Position> possibleMoves = this.getNeighbours(p);
	    for (Position nextPos : possibleMoves){
	      if (isMoveAbleToCapture(p,nextPos) != 0) {
	    	  
	        return true;
	      }
	    }
	    return false;
	}
	
	//Method to make a withdraw move
	
	void captureWithdraw(Position currentPosition,Position nextPosition){
		int currentRow = currentPosition.getRow();
		int currentCol = currentPosition.getCol();
		int nextRow = nextPosition.getRow();
	    int nextCol = nextPosition.getCol();
		int diffRow = nextRow-currentRow;
		int diffCol = nextCol-currentCol;
		for(int i=1; i<boardRow;i++){
			Position cap=new Position(currentRow-i*diffRow,currentCol-i*diffCol);
			if (isValidPosition(cap)){
				Position nextCap=new Position(currentRow-(i+1)*diffRow,currentCol-(i+1)*diffCol);
				this.board[cap.getRow()][cap.getCol()]=0;
				if(isValidPosition(nextCap)){
					int w1=this.board[currentRow-(i+1)*diffRow][currentCol-(i+1)*diffCol];
					if(w1==0||w1==this.board[currentRow][currentCol]){
						break;
					}
				}
				else{
					break;
				}
			}
		}
		
		
		
		if(this.board[currentPosition.getRow()][currentPosition.getCol()]==1){
			this.board[nextPosition.getRow()][nextPosition.getCol()]=1;
			this.board[currentPosition.getRow()][currentPosition.getCol()]=0;
		}
		else if(this.board[currentPosition.getRow()][currentPosition.getCol()]==2){
			this.board[nextPosition.getRow()][nextPosition.getCol()]=2;
			this.board[currentPosition.getRow()][currentPosition.getCol()]=0;
		}
	}

	
	//Method to make the approach move
	
	void captureApproach(Position currentPosition,Position nextPosition){
		
		int currentRow = currentPosition.getRow();
		int currentCol = currentPosition.getCol();
		int nextRow = nextPosition.getRow();
	    int nextCol = nextPosition.getCol();
		int diffRow = nextRow-currentRow;
		int diffCol = nextCol-currentCol;
		for(int i=2; i<boardRow;i++){
			Position cap=new Position(currentRow+i*diffRow,currentCol+i*diffCol);
			if (isValidPosition(cap)){
				Position nextCap=new Position(currentRow+(i+1)*diffRow,currentCol+(i+1)*diffCol);
				this.board[cap.getRow()][cap.getCol()]=0;
				
				if(isValidPosition(nextCap)){
					int w1=this.board[currentRow+(i+1)*diffRow][currentCol+(i+1)*diffCol];
					if(w1==0||w1==this.board[currentRow][currentCol]){
						break;
					}
				}
				else{
					break;
				}
			}
		}
		if(this.board[currentPosition.getRow()][currentPosition.getCol()]==1){
			this.board[nextPosition.getRow()][nextPosition.getCol()]=1;
			this.board[currentPosition.getRow()][currentPosition.getCol()]=0;
		}
		else if(this.board[currentPosition.getRow()][currentPosition.getCol()]==2){
			this.board[nextPosition.getRow()][nextPosition.getCol()]=2;
			this.board[currentPosition.getRow()][currentPosition.getCol()]=0;
		}
	}
				
	
	// Method to check that given positions represent withdraw move or not
	
	boolean isWithdrawMove(Position currentPosition,Position nextPosition){
		boolean flag=false;
		int currentRow = currentPosition.getRow();
		int currentCol = currentPosition.getCol();
		int nextRow = nextPosition.getRow();
	    int nextCol = nextPosition.getCol();
		int diffRow = nextRow-currentRow;
		int diffCol = nextCol-currentCol;
		Position p=new Position(currentRow-diffRow,currentCol-diffCol);
		if(isValidPosition(p)){
			int w=this.board[p.getRow()][p.getCol()];
			
			if(w!=0 && w!=this.board[currentPosition.getRow()][currentPosition.getCol()]){
				flag=true;
			}
		}
		return flag;
	}
	
	
	//Method to check that given positions represent approach move or not
	
	boolean isApproachMove(Position currentPosition,Position nextPosition){
		boolean flag=false;
		int currentRow = currentPosition.getRow();
		int currentCol = currentPosition.getCol();
		int nextRow = nextPosition.getRow();
	    int nextCol = nextPosition.getCol();
		int diffRow = nextRow-currentRow;
		int diffCol = nextCol-currentCol;
		Position capPos=new Position(currentRow+2*diffRow,currentCol+2*diffCol);
		if(isValidPosition(capPos)){
			int a=this.board[capPos.getRow()][capPos.getCol()];
			if(a!=0 && a!=this.board[currentPosition.getRow()][currentPosition.getCol()]){
				flag=true;
			}
		}
		return flag;
	}
	
	
	//Method to get the move type from the given positions 
	
	int isMoveAbleToCapture(Position currentPosition,Position nextPosition){
		int flag=0;
		int currentRow = currentPosition.getRow();
		int currentCol = currentPosition.getCol();
		int nextRow = nextPosition.getRow();
	    int nextCol = nextPosition.getCol();
		int diffRow = nextRow-currentRow;
		int diffCol = nextCol-currentCol;
		Position p=new Position(currentRow-diffRow,currentCol-diffCol);
		if(isValidPosition(p)){
			int w=this.board[p.getRow()][p.getCol()];
			if(w!=0 && w!=this.board[currentRow][currentCol]){
				flag=2;
			}
		}
		Position capPos=new Position(currentRow+2*diffRow,currentCol+2*diffCol);
		if(isValidPosition(capPos)){
			int a=this.board[capPos.getRow()][capPos.getCol()];
			if(a!=0 && a!=this.board[currentRow][currentCol]){
				flag=1;
			}
		}
		
		return flag;
	}
	
	
	//Method to check the paika move
	
	public boolean isPaika(String color){
		ArrayList<Position> positionList=this.getPossiblePlaces(color);
	    for (Position p1:positionList){
	      if (isAbleToCapture(p1)){
	        return false;
	      }
	    }
	    return true;
	}
	
	
	//Method to get the all possible position for the given color
	
	ArrayList<Position> getPossiblePlaces(String color){
		ArrayList<Position> p=new ArrayList<Position>();
		int check;
		if(color.equals("black")){
			check=1;
		}
		else{
			check=2;
		}
		for (int row=0; row<boardRow;row++){
			for (int column=0;column < boardCol;column++){
				if(check==board[row][column]){
					Position pos=new Position(row,column);
					p.add(pos);
				}									
			}
		}
		return p;
	}	
	
	
	
	//Method to check the goal state
	public int checkGoalState()
	  {
		if (MOVES<1){
			return 1;
		}
	    int whiteCount = 0;int blackCount = 0;
	    for (int row=0; row<boardRow;row++){
			for (int column=0;column < boardCol;column++){
				if(board[row][column]==2){
					
	        whiteCount++;
	      } else if(board[row][column]==1){
	        blackCount++;
	      }
	    }}
	    if (whiteCount == 0)
	    {
	     // System.out.println("Black won!");
	      return 2;
	    }
	    if (blackCount == 0)
	    {
	    //  System.out.println("White won!");
	      return 3;
	    }
	    return 0;
	  }
	
	
	//Method to generate the multiple moves
	
	int[][] generateMultipleMoves(int[][] b, Position p2){
		
		this.board=b;
		
		if(this.isAbleToCapture(p2)&&!this.previousPositions.contains(p2)&& !this.previousDirections.contains(p2)){
			ArrayList<Position> p=this.getNeighbours(p2);
			for(Position p3:p){
				if(!this.previousDirections.contains(this.getDirection(p2, p3))){
					int i=this.isMoveAbleToCapture(p2,p3);
					if(i==1){
						this.captureApproach(p2, p3);
						this.previousPositions.add(p2);
						this.previousDirections.add(this.getDirection(p2, p3));
						//this.printBoard();
						this.generateMultipleMoves(b, p3);
						break;
					}
					else if(i==2){
						this.captureWithdraw(p2,p3);
						this.previousPositions.add(p2);
						this.previousDirections.add(this.getDirection(p2, p3));
						//this.printBoard();
						this.generateMultipleMoves(b, p3);
						break;
					}
				}
				else{	
					break;
				}
			}
		}
		else{
			return b;
		}
		
		return b;
	}
	

	

	
	
	
}



//  Comments  //

//System.out.println("i"+i);
//System.out.println("captured from"+ p2.getRow()+","+p2.getCol()+"    "+ p3.getRow()+","+p3.getCol());
//this.printBoard();

//	System.out.println("captured from"+ p2.getRow()+","+p2.getCol());
//System.out.println("FDSFADFSDF");
//this.printBoard();
//this.generateMultipleMoves(b, p3);

//	System.out.println("size"+this.previousPositions.size());
//	System.out.println(p2.getRow()+","+p2.getCol());

//ArrayList<Position> p=this.getPossiblePlaces(aiColor);

//System.out.println(p.toString());
//int[][] b=new int[this.boardRow][this.boardCol];
//b=this.copy(this.board);
//b[0][0]=10;
//Node n=new Node(b);
//n.setCurrentPlayer(aiColor);
//
//for (Position p2:p){
////	System.out.println("("+p2.getRow()+","+p2.getCol()+")");
////	ArrayList<Position> positions=this.getNeighbours(p2);
//	if(this.isAbleToCapture(p2)){
//	//	System.out.println("here");
//	}
////	for(Position p3:positions){
////		
////		System.out.println("from "+"("+p2.getRow()+","+p2.getCol()+")"+"to ("+p3.getRow()+","+p3.getCol()+")");
////	}
//}


//if (aiColor.equals("white"))
//{
////	board[p1.getRow()][p1.getCol()]=EMPTY;
////	board[p.getRow()][p.getCol()]=WHITE;
////	this.currPlayer=BLACK;
////
//	}
//else{
////	board[p1.getRow()][p1.getCol()]=EMPTY;
////	board[p.getRow()][p.getCol()]=BLACK;this.currPlayer=WHITE;
//	}
//this.printBoard();		



//lower comments	
//ArrayList<Position> p1=this.getNeighbours(p);
//for(Position p2:p1){
//	System.out.println(p2.getRow()+","+p2.getCol());
//}
//if(strongIntersections.contains(p1)){
//	
//}
//else{
//	
//}
//for (Position p2:pos)
//System.out.println("("+p2.getRow()+","+p2.getCol()+")");

//boolean flag=false;
//for (Position p2:pos){
//	//System.out.println("("+p2.getRow()+","+p2.getCol()+")");
//	ArrayList<Position> positions=this.getNeighbours(p2);
//	if(this.isAbleToCapture(p2)){
//	//	System.out.println("here");
//		flag=true;
//	}
//	
//}


//System.out.println("in"+p.getRow()+","+p.getCol());
//  System.out.println("able to capture from "+"("+p.getRow()+","+p.getCol()+")"+" to ("+nextPos.getRow()+","+nextPos.getCol()+")");
//System.out.println(""+this.board[currentPosition.getRow()][currentPosition.getCol()]);
//System.out.println(""+this.board[nextPosition.getRow()][nextPosition.getCol()]);
//System.out.println(this.board[nextCap.getRow()][nextCap.getCol()]);
//System.out.println("able to capture from "+"("+currentPosition.getRow()+","+currentPosition.getCol()+")"+" to ("+nextPosition.getRow()+","+nextPosition.getCol()+")");

//System.out.println(""+this.board[currentPosition.getRow()][currentPosition.getCol()]);
//System.out.println(""+this.board[nextPosition.getRow()][nextPosition.getCol()]);
//this.printBoard();
//System.out.println("hera in withdraw");
//System.out.println(""+this.board[currentPosition.getRow()][currentPosition.getCol()]);
//System.out.println(""+this.board[nextPosition.getRow()][nextPosition.getCol()]);
//	this.printBoard();
//System.out.println("able to capture from "+"("+currentPosition.getRow()+","+currentPosition.getCol()+")"+" to ("+nextPosition.getRow()+","+nextPosition.getCol()+")");




