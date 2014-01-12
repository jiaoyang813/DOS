package project1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

//implement a small database to load files on disc
// and processing simple query
public class myDB {
	private static myDB db;
	HashMap<String, Tuple> dbfile; 
	
	//singleton pattern
	public static myDB getInstance()
	{
		if(db == null)
			db = new myDB();
		return db;
	}
	
	private myDB()
	{
		//init myDB, load from disc
		dbfile = new HashMap<String, Tuple>();
		loadDB(dbfile);
		
	}
	
	public void loadDB(HashMap<String, Tuple> dbfile)
	{
		//read file from disc
		try (BufferedReader inputFile = 
				new BufferedReader(new FileReader("dbfile.txt")))
		{
 
			String inputLine;
 
			while ((inputLine = inputFile.readLine()) != null
					                  ) {
				
				String[] token = parseUserInput(inputLine);
				//for(String s : token)
					//System.out.print(s+" ");
				//System.out.println();
				// format must match : ID NAME MAJOR
				Tuple record = new Tuple(token[0], token[1], token[2]);
				dbfile.put(token[0], record);
 				//System.out.println(inputLine);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		
	}
	
	public void saveDB()
	{
		
		//save file to disc
		try {
		   BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream("dbfile.txt"), "utf-8"));
		    Iterator<Entry<String, Tuple>> it = dbfile.entrySet().iterator();
		    while(it.hasNext())
		    {
		    	 Entry<String, Tuple> thisEntry = (Entry<String, Tuple>) it.next();
		    	 Tuple record = (Tuple)thisEntry.getValue();
		    	 String outputLine;
		    	 outputLine = record.elem1+" "+record.elem2+" "+ record.elem3;
		    	 writer.write(outputLine);
		    	 //may add a new line at file bottom 
		    	 writer.newLine();	 
		    }
		    
		    
		    writer.close();
		} catch (IOException ex) {
		  // report
		}
	}
	
	public void printALL()
	{
		System.out.println("---START OF FILE---");
		Iterator<Entry<String, Tuple>> it = dbfile.entrySet().iterator();
		while(it.hasNext())
	    {
	    	 Entry<String, Tuple> thisEntry = (Entry<String, Tuple>) it.next();
	    	 Tuple record = (Tuple)thisEntry.getValue();
	    	 System.out.println(record.toString());
	    }
		
		System.out.println("---END OF FILE---");
		//print result
	}
	
	public Tuple search(Tuple t)
	{
		//search
		String key = t.elem1;
		if(dbfile.containsKey(key))
		{
			if(dbfile.get(key).isEqual(t))
				return dbfile.get(key);
			else return null;
		}
		else
			return null;
		
	}
	
	public boolean delete(Tuple t)
	{
		//delete tuples
		if(search(t) != null)
		{
			dbfile.remove(t.elem1);
			return true;
		}
		
		return false;
	
	}
	
	public boolean insert(Tuple t)
	{
		//insert tuples
		//check if item conflicts
		if(dbfile.containsKey(t.elem1))
			return false;
		else
			dbfile.put(t.elem1, t);
		return true;
		//dbfile on disc is OUT OF DATE now
	}
	
	
	
	public void closeDB()
	{
		saveDB();
	}
	
	public static String[] parseUserInput(String in)
	{
		String[] result=null;
		String delim=" ";
		result = in.split(delim);
		return result;
	}
	
}
