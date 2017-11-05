/*
 * Amy Dinh
 * Class FinPaths keeps track of total time and cost. Also contains a string of the final path from a start
 * location to an end location. Boolean variable isTime shows whether the node should be sorted by time
 * or cost.
 */
public class FinPaths implements Comparable<FinPaths>
{
	//private variables for class
	private int cost;
	private int time;
	private String p;
	private boolean isTime;
	
	//constructor
	public FinPaths(boolean m){
		cost = 0;
		time = 0;
		p = "";
		isTime = m;
	}
	
	//add a String to the String being stored in object
	public void addToLine(String s)
	{
		p += s;
	}
	
	//set the cost to given parameter
	public void setCost(int c)
	{
		cost = c;
	}
	
	//set time to given parameter
	public void setTime(int t)
	{
		time = t;
	}
	
	//returns cost
	public int getCost()
	{
		return cost;
	}
	
	//returns time
	public int getTime()
	{
		return time;
	}
	
	//returns String
	public String getLine()
	{
		return p;
	}

	//method overrides Comparator compareTo method to either sort by time or cost
	@Override
	public int compareTo(FinPaths o) 
	{
		if (!isTime){
			int comparedSize = o.cost;
			if (this.cost > comparedSize) {
				return 1;
			} else if (this.cost == comparedSize) {
				return 0;
			} else {
				return -1;
			}
		}
		else
		{
			int comparedSize = o.time;
			if (this.time > comparedSize) {
				return 1;
			} else if (this.time == comparedSize) {
				return 0;
			} else {
				return -1;
			}
		}
	}
	
	

}
