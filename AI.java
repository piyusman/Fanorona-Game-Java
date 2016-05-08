package edu.nyu.main;

import java.util.ArrayList;

public class AI {
	
	public void buildTree(Node currentState,int depth){
		if(depth==0 ){
			return;
		}
		
//		System.out.println("depth:"+depth);
		Board b=new Board();
		
		b.boardCol=currentState.getBoard().length;
		b.boardRow=currentState.getBoard().length;
		b.board=currentState.getBoard();
//		b.printBoard();
		if(b.checkGoalState()==2 || b.checkGoalState()==1 ||b.checkGoalState()==3){
			return;
		}
		ArrayList<Position> p=b.getPossiblePlaces(currentState.getCurrentPlayer());
		ArrayList<Position> j=new ArrayList<Position>();
		ArrayList<Position> k=new ArrayList<Position>();
		ArrayList<Integer> l=new ArrayList<Integer>();
	//	System.out.println(""+p.size());
		for(Position p1:p){
			ArrayList<Position> possibleMoves = b.getNeighbours(p1);
	//	System.out.println(""+possibleMoves.size());
			for (Position nextPos : possibleMoves){
		
		    	int i=b.isMoveAbleToCapture(p1, nextPos);
		    	
		      if (i !=0){
		    	  j.add(p1);
		    	  l.add(i);
		    	  k.add(nextPos);
		    	 // System.out.println("able to capture from "+"("+p1.getRow()+","+p1.getCol()+")"+" to ("+nextPos.getRow()+","+nextPos.getCol()+")");
		      }
		      
		      else{ 
		    	  if(b.isPaikaAllowed(currentState.getCurrentPlayer())){
		    		 // System.out.println("yes");
		    		  j.add(p1);
			    	  l.add(i);
			    	  k.add(nextPos);
		    	  }
		    	  
		      }
		      
	    	 
		      
		    }
		}
//		System.out.println("move size:"+l.size());
		for(int i=0;i<l.size();i++){
			Position p1=j.get(i);
			Position p2=k.get(i);
			int move=l.get(i);
			Board b2=new Board();
			b2.board=b.copy(b.board);
			b2.boardCol=currentState.getBoard().length;
			b2.boardRow=currentState.getBoard().length;
			Node newNode=new Node(b2.board);
			if(currentState.getCurrentPlayer().equals("black")){
				
				newNode.setCurrentPlayer("white");
				//System.out.println("COLOR:"+currentState.getCurrentPlayer()+":::"+newNode.getCurrentPlayer());
			}
			else{
				newNode.setCurrentPlayer("black");
				//System.out.println("COLOR:"+currentState.getCurrentPlayer()+":::"+newNode.getCurrentPlayer());
			}
			if(move==1){
				
				b2.captureApproach(p1,p2);
			
				//b2.printBoard();
				//System.out.println("board size:"+b2.board.length);
				
				//b2.printBoard();
			}
			else if(move==2){
				b2.captureWithdraw(p1, p2);

			}
			else{}

//			newNode.setAlpha(this.utility_difference(b2.board));
			newNode.setAlpha(this.utility_opponentCount(b2.board,currentState.getCurrentPlayer()));
			currentState.addSubNode(newNode);
			if(currentState.getCurrentPlayer().equals("black")){
					b2.board[p2.getRow()][p2.getCol()]=1;
					b2.board[p1.getRow()][p1.getCol()]=0;
				}
				else{
					b2.board[p2.getRow()][p2.getCol()]=2;
					b2.board[p1.getRow()][p1.getCol()]=0;
				}
			//b2.printBoard();
//			System.out.println("loop:"+i+":::"+currentState.getCurrentPlayer());

		}
//		System.out.println("-----------------------------------");
		
		for(Node n:currentState.getSubNodes()){
//			if (currentState.getCurrentPlayer().equals("black")){
//				n.setCurrentPlayer("white");
//				
//			}
//			else{
//				n.setCurrentPlayer("black");
//			}
			//System.out.println("loopes");
			
//			System.out.println("Utilvalue:"+n.getUtilVar());
			this.buildTree(n, depth-1);
			//System.out.println("---------------------------------------------------------------------");
		}
		
	}

	public int alphaBeta(Node node, int depth, int alpha, int beta, String player){
		if ((node.getSubNodes().size() == 0) || (depth == 0)) {
	      return node.getUtilVar();
	    }
	    ArrayList<Node> subNodes = node.getSubNodes();
	    if (player.equals("black"))
	    {
	      for (Node n : subNodes)
	      {
	        alpha = Math.min(alpha, alphaBeta(n, depth - 1, alpha, beta, "white"));
	        n.setAlpha(alpha);
	        if (alpha <= beta) {
	        	System.out.println("pruning Max");
	          break;
	        }
	      }
	      return alpha;
	    }
	    else{
	    for (Node n : subNodes)
	    {
	      beta = Math.max(beta, alphaBeta(n, depth - 1, alpha, beta, "black"));
	      n.setAlpha(beta);
	      if (alpha <= beta) {
	    	  System.out.println("prunning Min");
	        break;
	      }
	    }
	    return beta;}
	  }
	
	
	public int[][] getBestMove(Node rootNode)
	  {
	   
	   
	    int utilVal = 0;
	    Board b=new Board();
	    ArrayList<Node> subNodes = rootNode.getSubNodes();
	    if (rootNode.getCurrentPlayer().equals("black")) {
	      for (int i = 0; i < subNodes.size(); i++) {
	        if (((Node)subNodes.get(i)).getUtilVar() < utilVal)
	        {
	          utilVal = ((Node)subNodes.get(i)).getUtilVar();
	          b.board=(subNodes.get(i)).getBoard();
	        }
	      }
	    } 
	    else {
	      for (int i = 0; i < subNodes.size(); i++) {
	        if (((Node)subNodes.get(i)).getUtilVar() > utilVal)
	        {
	          utilVal = ((Node)subNodes.get(i)).getUtilVar();
	          b.board=((Node)subNodes.get(i)).getBoard();
	        }
	      }
	    }
	    //System.out.println("board"+b.boardCol);
	   return b.board;
	   // System.out.println("result:"+resultIdx);
	   // return (Move)this.rootNode.getBoard().getAllPossibleMoves().get(resultIdx);
	  }
	
	public int utility_difference(int[][] board)
	  {
	    int utility = 0;
	    int white = 0;
	    int black = 0;
	    for (int row=0; row<board.length;row++){
				for (int column=0;column < board[row].length;column++){
					if(board[row][column]==1){
						black++;
					}
					else if(board[row][column]==2){
						white++;
					}
				}
		
			}
		
	    
	    utility = white - black;
	    //System.out.println("--"+utility);
	    return utility;
	  }
}
