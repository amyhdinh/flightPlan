/*
 * Amy Dinh
 * Class flightPlan calculates best three paths from a list of requests based on a flight data file. Best 
 * paths are dependent on time, and paths are calculated using an iterative backtracking stack. 
 * Compile all 4 .java files and have .txt files in appropriate directory.
 * Program will run from command prompt "java flightPlan <FlightDataFile> <PathsToCalculateFile> <OutputFile>".
 * (No .txt extension need at end of file names).
 * 
 */
import java.util.*;
import java.io.*;

public class flightPlan 
{
	public static void main(String[] args) throws FileNotFoundException
	{	
		//get file names
		File file1 = new File("FlightDataFile.txt");
		File file2 = new File("PathsToCalculateFile.txt");
		//file to write to
		PrintWriter writer = new PrintWriter("Final.txt");
		
		//get the requests from getPathsToCalculate method
		LinkedList<ArrayList<String>> startAndEnd = getPathsToCalculate(file2);
	
		//for loop goes through all requests in startAndEnd LinkedList
		for (int i = 0; i < startAndEnd.size(); i++)
		{
			//ArrayList to carry all resulting paths at current request
			ArrayList<Stack<directPaths>> solutions = new ArrayList<Stack<directPaths>>();
			
			//getLocAndPaths gets LinkedList of LinkedList of all the locations and connections
			LinkedList<Location> loc = getLocAndPaths(file1);
			
			//Starting name is in index 0, end name is in index 1, and time/cost var is in index 2
			String start = startAndEnd.get(i).get(0);
			String end = startAndEnd.get(i).get(1);
			String timeCost = startAndEnd.get(i).get(2);
			String word = "";
			
			//Printing for either time or cost and what flight number
			if (timeCost.equalsIgnoreCase("T"))
				word = "Time";
			else
				word = "Cost";
			writer.println("Flight " + (i+1) + ": " + start + ", " + end + " (" + word + ")");
			
			//start by adding start node to stacks and mark as known
			directPaths first = new directPaths(start, 0, 0);
			first.markKnown();
			//find at what index the start Location is at in the Location LinkedList
			int find = findLocationIndex(start, loc);
			loc.get(find).markKnown();
			Location m = loc.get(find);
			Stack<directPaths> stack = new Stack<directPaths>();
			Stack<Location> stack2 = new Stack<Location>();
			stack.push(first);
			stack2.push(m);
			
			//backtracking algorithm
			while(!stack.isEmpty())
			{
				LinkedList<directPaths> path = stack2.peek().getDirectPaths();
				//if the current node is your goal node
				if (stack.peek().getLocationName().equals(end))
				{
					//copy the solution to stack of solutions
					Stack<directPaths> solution = new Stack<directPaths>();
					solution.addAll(stack);
					solutions.add(solution);
					
					//then pop off your ending node
					stack.pop();
					stack2.pop();
					
					//update your current location
					find = findLocationIndex(stack2.peek().getName(), loc);
				}
				//if current node is not goal node
				else
				{
					//see of current Location has directPaths that have not been looked at yet
					if (loc.get(find).getUntriedBool())
					{
						//get the index of the next unknown directPath
						int u = loc.get(find).getUntried();
						//then find that node in the Location LinkedList and mark as known
						int temp = findLocationIndex(loc.get(find).getDirectPaths().get(u).getLocationName(), loc);
						loc.get(find).getDirectPaths().get(u).markKnown();
						path.get(u).markKnown();
						directPaths un = path.get(u);
						//if that next node in the Location LinkedList is known just skip pushing(avoid infinite loops
						if (!loc.get(temp).getKnown())
						{
							//update current location
							find = temp;
							loc.get(find).markKnown();
							//don't mark the ending Location as known so it can be reached again
							if (loc.get(find).getName().equalsIgnoreCase(end))
								loc.get(find).markUnknown();
							//push your current Location into the stacks
							Location l = loc.get(find);
							stack2.push(l);
							stack.push(un);
						}
					}
					//if your current has no unknown directPaths left
					else
					{
						int replace = findLocationIndex(stack.peek().getLocationName(), loc);
						//pop your current location off to back track
						stack.pop();
						stack2.pop();
						//make sure the Location popped off is marked back to unknown so it can be reached from other paths
						loc.get(replace).clear();
						
						//update current location if possible
						if (!stack.isEmpty())
							find = findLocationIndex(stack.peek().getLocationName(), loc);
					}
					
				}
			}
			
			//list of paths to be printed and stored
			LinkedList<FinPaths> fin2 = new LinkedList<FinPaths>();
			for(Stack<directPaths> o  : solutions)
			{
				int totCost = 0;
				int totTime = 0;
				LinkedList<directPaths> fin = new LinkedList<directPaths>();
				//go through stack of solutions and add to FinPaths while keeping track of time and cost
				while(!o.isEmpty())
				{
					totCost += o.peek().getCost();
					totTime += o.peek().getTime();
					fin.addFirst(o.pop());
				}
				
				//add the paths to be printed
				FinPaths x = new FinPaths(timeCost.equalsIgnoreCase("T"));
				for (int y = 0; y < fin.size(); y++)
				{
					x.addToLine(fin.get(y).getLocationName());
					if (y != fin.size()-1)
						x.addToLine(" -> ");
					else
						x.addToLine(".");
				}
				//set time and cost
				x.setCost(totCost);
				x.setTime(totTime);
				fin2.add(x);
			}
			//sort
			Collections.sort(fin2);
			//then print to file
			for (int q = 0; q < 3 && q < fin2.size(); q++){
				writer.print("Path" + (q+1) + ": ");
				FinPaths o = fin2.get(q);
				writer.print(o.getLine());
				writer.println(" Time: " + o.getTime() + " Cost: " + o.getCost() + "\n");
			}
			writer.println();
		}	
		writer.close();
	}
	
	
	//method returns the LinkedList of LinkedList of all the Locations and their directPaths
	static public LinkedList<Location> getLocAndPaths(File file) throws FileNotFoundException
	{
		LinkedList<Location> locs = new LinkedList<Location>();
		
		Scanner fileRead1 = new Scanner(file);
		
		fileRead1.nextLine();
		
		//read in file
		while(fileRead1.hasNextLine())
		{
			//delimit locations and numbers with pipe
			Scanner scan = new Scanner(fileRead1.nextLine());
			scan.useDelimiter("\\|");
			
			String name = scan.next();
			String dname = scan.next();
			int cost = scan.nextInt();
			int time = scan.nextInt();
			
			Location city = new Location(name);
			boolean repeat = false;
			int repeatNum = 0;
			
			//check if location is in LinkedList already
			for (int i = 0; i < locs.size(); i++)
			{
				if (locs.get(i).getName().equalsIgnoreCase(name))
				{
					repeatNum = i;
					repeat = true;
				}
			}
			
			//add new city with its path to the list
			if (!repeat)
			{
				directPaths p = new directPaths(dname, cost, time);
				city.addDirectPaths(p);
				locs.add(city);
			}
			//else find the city already in list and update
			else
			{
				city = locs.get(repeatNum);
				directPaths p = new directPaths(dname, cost, time);
				city.addDirectPaths(p);
				locs.set(repeatNum, city);
			}
			
			//Do the same from the opposite pathway
			String temp = name;
			name = dname;
			dname = temp;
			
			city = new Location(name);
			repeat = false;
			repeatNum = 0;
			
			for (int i = 0; i < locs.size(); i++)
			{
				if (locs.get(i).getName().equalsIgnoreCase(name))
				{
					repeatNum = i;
					repeat = true;
				}
			}
			
			if (!repeat)
			{
				directPaths p = new directPaths(dname, cost, time);
				city.addDirectPaths(p);
				locs.add(city);
			}
			else
			{
				city = locs.get(repeatNum);
				directPaths p = new directPaths(dname, cost, time);
				city.addDirectPaths(p);
				locs.set(repeatNum, city);
			}
			
			

			scan.close();
		}
		fileRead1.close();
		return locs;
	}
	
	//returns the list of paths to calculate and how to sort them
	static public LinkedList<ArrayList<String>> getPathsToCalculate(File file) throws FileNotFoundException
	{
		Scanner fileRead2 = new Scanner(file);
		
		fileRead2.nextLine();
		
		LinkedList<ArrayList<String>> paths = new LinkedList<ArrayList<String>>();
		
		//reading in file
		while(fileRead2.hasNext())
		{
			//delimit using pipe
			Scanner scan = new Scanner(fileRead2.nextLine());
			scan.useDelimiter("\\|");
			ArrayList<String> paths2 = new ArrayList<String>();
		
			//get all three need variables
			String start = scan.next();
			String end = scan.next();
			String timeCost = scan.next();
			
			//then add to the list
			paths2.add(start);
			paths2.add(end);
			paths2.add(timeCost);
			
			paths.add(paths2);
			scan.close();
			
		}
		fileRead2.close();
		return paths;
	}
	
	//method returns the index of where in the LinkedList that particular location is
	public static int findLocationIndex(String name, LinkedList<Location> loc)
	{
		//traverse through whole LinkedList
		for (int i = 0; i < loc.size(); i++)
		{
			//if match then return that index
			if(loc.get(i).getName().equalsIgnoreCase(name))
			{
				return i;
			}
		}
		return 100;
	}
}
