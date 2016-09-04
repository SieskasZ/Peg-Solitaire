// I worked with Seara Chen and Abhijit Singh Chhabra

// My memory management policy: make the child node null if:
// 1 - a node with the same board as a child is in the OPEN list
	// and that node has a lower f than child
// 2 - a node with the same board as child is in the CLOSED list
	// and that node has a lower f than child
// 3 - a node is the same as the parent node

// My strategy
// I initialize the tree with the parent node with the config board
// I use the A* algorithm
// g = level of the node in the tree
// h = number of white pixels 


import java.util.*;
public class tester {
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// Code given by the assignment
	public static void main(String[] args) {

		class HiRiQ
		{
			//int is used to reduce storage to a minimum...
			public int config;
			public byte weight;

			//initialize to one of 5 reachable START config n=0,1,2,3,4
			// n=-1
			HiRiQ(byte n)
			{
				if (n==0)
				{config=65536/2;weight=1;}
				else
					if (n==1)
					{config=1626;weight=6;}
					else
						if (n==2)
						{config=-1140868948; weight=10;}
						else
							if (n==3)
					         {config=-411153748; weight=13;}
					        else
					         {config=-2147450879; weight=32;}
								
			}

			boolean IsSolved()
			{
				return( (config==65536/2) && (weight==1) );
			}

			//transforms the array of 33 booleans to an (int) cinfig and a (byte) weight.
			// Converts a boolean array into a HiRiQ object
			public void store(boolean[] B)
			{
				int a=1;
				config=0;
				weight=(byte) 0;
				if (B[0]) {weight++;}
				for (int i=1; i<32; i++)
				{
					if (B[i]) {config=config+a;weight++;}
					a=2*a;
				}
				if (B[32]) {config=-config;weight++;}
			}

			//transform the int representation to an array of booleans.
			//the weight (byte) is necessary because only 32 bits are memorized
			//and so the 33rd is decided based on the fact that the config has the
			//correct weight or not.

			// Converts a HiRiQ object into a boolean array
			public boolean[] load(boolean[] B)
			{
				byte count=0;
				int fig=config;
				B[32]=fig<0;
				if (B[32]) {fig=-fig;count++;}
				int a=2;
				for (int i=1; i<32; i++)
				{
					B[i]= fig%a>0;
					if (B[i]) {fig=fig-a/2;count++;}
					a=2*a;
				}
				B[0]= count<weight;
				return(B);
			}

			//prints the int representation to an array of booleans.
			//the weight (byte) is necessary because only 32 bits are memorized
			//and so the 33rd is decided based on the fact that the config has the
			//correct weight or not.
			public void printB(boolean Z)
			{if (Z) {System.out.print("[ ]");} else {System.out.print("[@]");}}

			public void print()
			{
				byte count=0;
				int fig=config;
				boolean next,last=fig<0;
				if (last) {fig=-fig;count++;}
				int a=2;
				for (int i=1; i<32; i++)
				{
					next= fig%a>0;
					if (next) {fig=fig-a/2;count++;}
					a=2*a;
				}
				next= count<weight;

				count=0;
				fig=config;
				if (last) {fig=-fig;count++;}
				a=2;

				System.out.print("      ") ; printB(next);
				for (int i=1; i<32; i++)
				{
					next= fig%a>0;
					if (next) {fig=fig-a/2;count++;}
					a=2*a;
					printB(next);
					if (i==2 || i==5 || i==12 || i==19 || i==26 || i==29) {System.out.println() ;}
					if (i==2 || i==26 || i==29) {System.out.print("      ") ;};
				}
				printB(last); System.out.println() ;

			}

			// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
			class Node {
				Node parent; // Store the parent of the Node (the neighborhood of the parent)
				
				ArrayList<Node> children; // Store the children, they won't necessarily go into "open" list

				String substitutions; // Store the substitutions to return

				boolean[] board; // Store the board/neighborhood
				
				int blackSq; // Store the number of black squares: the solution has 32
				
				int blueCells; // Store the number of white pixels on "blue cells": from 0 to 11
				int redCells; // Store the number of white pixels on "red cells": from 0 to 11
				int yellowCells; // Store the number of white pixels on "yellow cells": from 0 to 11

				int g; //  Cost it took to get to the node

				int h; // Heuristic cost: our guess as to 
				// how much it will cost to reach the goal from that node
				// Number of white squares

				int f; // Final cost f = g+h 

				// Constructor
				Node(boolean[] B) {
					this.board = new boolean[33];
					this.board = B;
					this.children = new ArrayList<Node>();
				}
			}

			
			// Takes as input a boolean array of 33 spaces
			public String solve(boolean[] config) {
				
				// Array of the 38 triplets
				int[][] triplets = {{0,1,2},{3,4,5},{6,7,8},{7,8,9},{8,9,10},{9,10,11},{10,11,12},{13,14,15},{14,15,16},{15,16,17},{16,17,18},{17,18,19},{20,21,22},{21,22,23},{22,23,24},{23,24,25},{24,25,26},{27,28,29},{30,31,32},{12,19,26},{11,18,25},{2,5,10},{5,10,17},{10,17,24},{17,24,29},{24,29,32},{1,4,9},{4,9,16},{9,16,23},{16,23,28},{23,28,31},{0,3,8},{3,8,15},{8,15,22},{15,22,27},{22,27,30},{7,14,21},{6,13,20}};
				
				// Array Lists of the blue cells
				ArrayList<Integer> blue = new ArrayList<Integer>();
				blue.addAll(Arrays.asList(0,5,6,9,12,15,18,21,24,28,30));
				
				
				// Same for the yellow cells
				ArrayList<Integer> yellow = new ArrayList<Integer>();
				yellow.addAll(Arrays.asList(1,3,7,10,13,16,19,22,25,29,31));
				
				// Same for the red cells
				ArrayList<Integer> red = new ArrayList<Integer>();
				red.addAll(Arrays.asList(2,4,8,11,14,17,20,23,26,27,32));
			
				// Initialize Array List open to store the set of nodes to be evaluated
				ArrayList<Node> open = new ArrayList<Node>();

				// Initialize Array List white to store the set of nodes where the parity is not respected
				// and where the number of black squares is smaller than their parent's
				ArrayList<Node> white = new ArrayList<Node>();

				// Create an Array List to store the set of nodes already evaluated
				ArrayList<Node> closed = new ArrayList<Node>();

				// String to return to show the substitutions
				String subs="";

				// Initialize the parent Node with config
				Node parent = new Node(config);
				parent.substitutions="";
				
				// First, check if it's the board we're looking for
				HiRiQ parentBoard = new HiRiQ((byte) 0);
				parentBoard.store(parent.board);
				if (parentBoard.IsSolved()) {
					subs += "No substitutions needed, the original puzzle is solved.";
					return subs;
				}
				
				// Then initialize the number of blue, yellow, and red cells with white pixels
				// and the number of black pixels
				for (int cell=0; cell < 33; cell++) {
					
					// If there's a white pixel on the cell
					if (parent.board[cell]) {
						if (blue.contains(cell)) {
							parent.blueCells++;
						}
						else if (yellow.contains(cell)) {
							parent.yellowCells++;
						}
						else {
							parent.redCells++;
						}	
					}
					
					// Else if there's a black pixel
					else {
						parent.blackSq++;			
					}
				}
				// Initialize g, h and f
				parent.g=0;
				parent.h = 33-parent.blackSq;
				parent.f = parent.h;
				
				// Put the starting Node on the open list
				open.add(parent);
				
				// Count the level
				int level=0;
					
				// While the open list or the white list is not empty
				while(!open.isEmpty() || !white.isEmpty()) {
					
					// Find the node with the least h value on the open list, call it "current"		
					int leastH;
					Node current=new Node(config);

					// If open is empty, use the nodes in white
					if (open.isEmpty()) {
						
						leastH = white.get(0).h;
						current = white.get(0);

						for (int i=1; i<white.size(); i++) {
							if (white.get(i).h < leastH) {
								leastH = white.get(i).h;
								current = white.get(i);
							}
						}

						// Pop current off the white list
						white.remove(current);		
						open.add(current);
					}
					
				
					// Find the node with the least h on the open list, call it "current"
					leastH = open.get(0).h;
					current = open.get(0);

					for (int i=1; i<open.size(); i++) {
						if (open.get(i).h < leastH) {
							leastH = open.get(i).h;
							current = open.get(i);
						}
					}
					
					// Pop current off the open list
					open.remove(current);

					// Generate current's neighborhoods and set their parents to current
					
					// Evaluate all the 38 triplets and see if you can perform a substitution
					for (int i=0;i<triplets.length; i++) {
						
						// Case 1A TrueTrueFalse: Children with more black squares than the parent
						if (current.board[triplets[i][0]] && current.board[triplets[i][1]] && !current.board[triplets[i][2]]) {

							// Get the boolean array of the parent
							boolean[] newBoard = new boolean[33];
							for (int ada=0; ada<33; ada++) {
								newBoard[ada] = current.board[ada];
							}
							
							// Perform a B-Substitution
							newBoard[triplets[i][0]] = false;
							newBoard[triplets[i][1]] = false;
							newBoard[triplets[i][2]] = true;
							
							Node child = new Node(newBoard);
							
							// Update number of blue, red and yellow cells
							child.blueCells = current.blueCells;
							child.redCells = current.redCells;
							child.yellowCells = current.yellowCells;
							
							// For blue cells
							if (blue.contains(triplets[i][0])) {
								child.blueCells--;
							}
							if (blue.contains(triplets[i][1])) {
								child.blueCells--;
							}
							if (blue.contains(triplets[i][2])) {
								child.blueCells++;
							}
							
							// For red cells
							if (red.contains(triplets[i][0])) {
								child.redCells--;
							}
							if (red.contains(triplets[i][1])) {
								child.redCells--;
							}
							if (red.contains(triplets[i][2])) {
								child.redCells++;
							}
							
							// For yellow cells
							if (yellow.contains(triplets[i][0])) {
								child.yellowCells--;
							}
							if (yellow.contains(triplets[i][1])) {
								child.yellowCells--;
							}
							if (yellow.contains(triplets[i][2])) {
								child.yellowCells++;
							}
							
							child.substitutions = triplets[i][0] + "B" + triplets[i][2];
							child.parent = current;
							child.blackSq = current.blackSq + 1;
									
							// Add the child to current's list of children
							current.children.add(child);
						}
						
						// Case 1B FalseTrueTrue: Children with more black squares than the parent
						if (!current.board[triplets[i][0]] && current.board[triplets[i][1]] && current.board[triplets[i][2]]) {

							// Get the boolean array of the parent
							boolean[] newBoard = new boolean[33];
							for (int ada=0; ada<33; ada++) {
								newBoard[ada] = current.board[ada];
							}

							// Perform a B-Substitution
							newBoard[triplets[i][0]] = true;
							newBoard[triplets[i][1]] = false;
							newBoard[triplets[i][2]] = false;

							Node child = new Node(newBoard);
							
							// Update number of blue, red and yellow cells
							child.blueCells = current.blueCells;
							child.redCells = current.redCells;
							child.yellowCells = current.yellowCells;
							
							// For blue cells
							if (blue.contains(triplets[i][0])) {
								child.blueCells++;
							}
							if (blue.contains(triplets[i][1])) {
								child.blueCells--;
							}
							if (blue.contains(triplets[i][2])) {
								child.blueCells--;
							}
							
							// For red cells
							if (red.contains(triplets[i][0])) {
								child.redCells++;
							}
							if (red.contains(triplets[i][1])) {
								child.redCells--;
							}
							if (red.contains(triplets[i][2])) {
								child.redCells--;
							}
							
							// For yellow cells
							if (yellow.contains(triplets[i][0])) {
								child.yellowCells++;
							}
							if (yellow.contains(triplets[i][1])) {
								child.yellowCells--;
							}
							if (yellow.contains(triplets[i][2])) {
								child.yellowCells--;
							}
							
							child.substitutions = triplets[i][0] + "B" + triplets[i][2];
							child.parent = current;
							child.blackSq = current.blackSq + 1;
							
							// Add the child to current's list of children
							current.children.add(child);
						}

						// Case 2A FalseFalseTrue: Children with less black squares than the parent
						if (!current.board[triplets[i][0]] && !current.board[triplets[i][1]] && current.board[triplets[i][2]]) {

							// Create a new Node to store into PriorityQueue white
							// Get the boolean array of the parent
							boolean[] newBoard = new boolean[33];
							for (int ada=0; ada<33; ada++) {
								newBoard[ada] = current.board[ada];
							}

							// Perform a B-Substitution
							newBoard[triplets[i][0]] = true;
							newBoard[triplets[i][1]] = true;
							newBoard[triplets[i][2]] = false;

							Node child = new Node(newBoard);	
							
							// Update number of blue, red and yellow cells
							child.blueCells = current.blueCells;
							child.redCells = current.redCells;
							child.yellowCells = current.yellowCells;
							
							// For blue cells
							if (blue.contains(triplets[i][0])) {
								child.blueCells++;
							}
							if (blue.contains(triplets[i][1])) {
								child.blueCells++;
							}
							if (blue.contains(triplets[i][2])) {
								child.blueCells--;
							}
							
							// For red cells
							if (red.contains(triplets[i][0])) {
								child.redCells++;
							}
							if (red.contains(triplets[i][1])) {
								child.redCells++;
							}
							if (red.contains(triplets[i][2])) {
								child.redCells--;
							}
							
							// For yellow cells
							if (yellow.contains(triplets[i][0])) {
								child.yellowCells++;
							}
							if (yellow.contains(triplets[i][1])) {
								child.yellowCells++;
							}
							if (yellow.contains(triplets[i][2])) {
								child.yellowCells--;
							}
							
							child.substitutions = triplets[i][0] + "W" + triplets[i][2];
							child.parent = current;
							child.blackSq = current.blackSq - 1;
							
							// Add the child to current's list of children
							current.children.add(child);

						}
						
						// Case 2B TrueFalseFalse: Children with less black squares than the parent
						if (current.board[triplets[i][0]] && !current.board[triplets[i][1]] && !current.board[triplets[i][2]]) {

							// Create a new Node to store into PriorityQueue white
							// Get the boolean array of the parent
							boolean[] newBoard = new boolean[33];
							for (int ada=0; ada<33; ada++) {
								newBoard[ada] = current.board[ada];
							}

							// Perform a B-Substitution
							newBoard[triplets[i][0]] = false;
							newBoard[triplets[i][1]] = true;
							newBoard[triplets[i][2]] = true;

							Node child = new Node(newBoard);
							
							// Update number of blue, red and yellow cells
							child.blueCells = current.blueCells;
							child.redCells = current.redCells;
							child.yellowCells = current.yellowCells;
							
							// For blue cells
							if (blue.contains(triplets[i][0])) {
								child.blueCells--;
							}
							if (blue.contains(triplets[i][1])) {
								child.blueCells++;
							}
							if (blue.contains(triplets[i][2])) {
								child.blueCells++;
							}
							
							// For red cells
							if (red.contains(triplets[i][0])) {
								child.redCells--;
							}
							if (red.contains(triplets[i][1])) {
								child.redCells++;
							}
							if (red.contains(triplets[i][2])) {
								child.redCells++;
							}
							
							// For yellow cells
							if (yellow.contains(triplets[i][0])) {
								child.yellowCells--;
							}
							if (yellow.contains(triplets[i][1])) {
								child.yellowCells++;
							}
							if (yellow.contains(triplets[i][2])) {
								child.yellowCells++;
							}
							
							child.substitutions = triplets[i][0] + "W" + triplets[i][2];
							child.parent = current;
							child.blackSq = current.blackSq - 1;
							
							// Add the child to current's list of children
							current.children.add(child);
						}
					}
					
					// For each child
					for (int i=0; i < current.children.size(); i++) {
						
						Node child = current.children.get(i);
						
						// If the child is the goal, stop the search
						
						// Store the child.board as a HiRiQ object
						HiRiQ childBoard = new HiRiQ((byte) 0);;
						childBoard.store(child.board);
						
						if (childBoard.IsSolved()) {

							// Store the substitutions into an array
							String[] listSubs = new String[level+1]; 
							int idx=0;
							
							// While we're not at the config board from the beginning
							while(child != parent) {
								listSubs[idx]=child.substitutions;
								child=child.parent;
								idx++;		  
							}
							
							// Concatenate the strings in reverse order
							for (int j=listSubs.length-1; j >= 0; j--) {
								if (listSubs[j]==null) {
									continue;
								}
								subs += listSubs[j];
								subs += " ";
							}
							
							return subs;
						}
						
						
						child.g = current.g + 1; // 1 is the distance between the child and current
						child.h = 33 - child.blackSq;// distance from goal to child = number of white squares
						child.f = child.g + child.h;
						
						
						boolean addChild = true;
						
						// If a node with the same board as child is in the OPEN list
						// which has a lower f than child, skip this child
						
						for (int k=0; k<open.size() && addChild ;k++) {
							
							if(Arrays.equals(child.board, open.get(k).board) && child.f > open.get(k).f) {
								child=null;
								addChild = false;
							}
						}
						
						// If a node with the same board as child is in the WHITE list
						// skip this child
						for (int k=0; k < white.size() && addChild ; k++) {
							if (Arrays.equals(child.board, white.get(k).board)) {
								child = null;
								addChild = false;
							}
						}
						
						
						// If a node with the same board as child is in the CLOSED list
						// which has a lower f than child, skip this child
						
						for (int k=0; k<closed.size() && addChild;k++) {
							
							if(Arrays.equals(child.board, closed.get(k).board) && child.f > closed.get(k).f) {
								child=null;
								addChild = false;
							}
						}
						
						
						// If the node is the same as the parent node, skip the child
						if ( addChild && Arrays.equals(child.board, parent.board)) {
							child =null;
							addChild = false;
						}
						
						
						// Otherwise add the node to either open or white
						if (addChild) {
							
							// Check if the number of black squares of the child is higher than the parent's
							// Check if the parity of blue cells = parity of red cells != parity of yellow cells
							if (child.blackSq > current.blackSq && (child.blueCells%2==child.redCells%2) && (child.blueCells%2 != child.redCells%2)) {	
								open.add(child);									
							}
							
							else {
								white.add(child);
							}
						}
						
													
					}
					
					// Store current into the closed list
					closed.add(current);
					
					// Increment level
					level++;
					
				}
				return subs;
			}
			

		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
		// Tests
		boolean[] B=new boolean[33];
		HiRiQ W=new HiRiQ((byte) 0) ;
		W.print(); System.out.println(W.IsSolved());	
		W.load(B);
		System.out.println("this is W " + Arrays.toString(B));
		System.out.println(W.solve(B));
		
		HiRiQ X=new HiRiQ((byte) 1) ;
		X.print(); System.out.println(X.IsSolved());
		X.load(B);
		System.out.println("this is X " + Arrays.toString(B));
		System.out.println(X.solve(B));
		
		
		HiRiQ Y=new HiRiQ((byte) 2) ;
		Y.print(); System.out.println(Y.IsSolved());
		Y.load(B);
		System.out.println("this is Y " + Arrays.toString(B));
		System.out.println(Y.solve(B));
		
		
		HiRiQ Z=new HiRiQ((byte) 3) ;
		Z.print(); System.out.println(Z.IsSolved());
		Z.load(B);
		System.out.println("this is Z " + Arrays.toString(B));
		System.out.println(Z.solve(B));
		
		
		HiRiQ V=new HiRiQ((byte) 4) ;
		V.print(); System.out.println(V.IsSolved());
		V.load(B);
		System.out.println("this is V " + Arrays.toString(B));
		System.out.println(V.solve(B));
		
	}
}