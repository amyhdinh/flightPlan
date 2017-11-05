/*
 * Amy Dinh
 * Class Location is a location object that contains a name and a LinkedList of directPaths
 * Object can be marked as known/unknown, and traverse through its directPaths(where the Location can get to directly).
 */
import java.util.*;

class Location 
{
	//variable details for direct paths of locations
	private String name;
	private LinkedList<directPaths> paths;
	private boolean known;
	
	//constructor
	public Location(String name)
	{
		this.name = name;
		paths = new LinkedList<directPaths>();
		known = false;
	}
	
	//method for add directPaths to linked list
	public void addDirectPaths(directPaths p)
	{
		paths.add(p);
	}
	
	//get list of directPaths
	public LinkedList<directPaths> getDirectPaths()
	{
		return paths;
	}
	
	
	//get name of city location
	public String getName()
	{
		return name;
	}
	
	//mark known as true
	public void markKnown()
	{
		known = true;
	}
	
	//mark known as false
	public void markUnknown()
	{
		known = false;
	}
	
	//get whether location has been marked known or not
	public boolean getKnown()
	{
		return known;
	}
	
	//get the index of the first node in paths that has been marked as unknown
	public int getUntried(){
		for(int i = 0; i < paths.size(); i++)
		{
			if (!paths.get(i).getKnown())
				return i;
		}
		return 100;
	}
	
	//return if paths have an directPath that has been marked unknown
	public boolean getUntriedBool(){
		for(int i = 0; i < paths.size(); i++)
		{
			if (!paths.get(i).getKnown())
				return true;
		}
		return false;
	}
	
	//change this location and all its directPaths to unknown
	public void clear()
	{
		known = false;
		for(int i = 0; i < paths.size(); i++)
		{
			paths.get(i).markUnknown();
		}
		
	}
}
