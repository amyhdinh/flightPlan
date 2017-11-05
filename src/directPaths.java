/*
 * Amy Dinh
 * Class directPaths is an object that contains a Location and keep tracks of time and cost from main
 * Location node.
 */
public class directPaths 
{
	//variable details for direct paths of locations
	private Location p;
	private int time;
	private int cost;
	
	//constructor 
	public directPaths(String loc, int cost, int time)
	{
		p = new Location(loc);
		this.time = time;
		this.cost = cost;
	}
	
	//method returns location
	public String getLocationName()
	{
		return p.getName();
	}
	//method returns time
	public int getTime()
	{
		return time;
	}
	//method returns cost
	public int getCost()
	{
		return cost;
	}
	
	//method sets time
	public void setTime(int t)
	{
		time = t;
	}
	//method sets cost
	public void setCost(int c)
	{
		cost = c;
	}
	
	//mark location in directPath as known
	public void markKnown()
	{
		p.markKnown();
	}
	
	//mark location in directPath as unknown
	public void markUnknown()
	{
		p.markUnknown();
	}
	
	//get whether location in directPath is known or not
	public boolean getKnown()
	{
		return p.getKnown();
	}	
}
