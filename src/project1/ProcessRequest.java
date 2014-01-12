package project1;

public class ProcessRequest {
	String client = null;
	int port = 0;
	String result = null;
	myDB db;
	ProcessRequest()
	{
		db = myDB.getInstance();
	}
	
	
	public String processInputline(String[] cmd)
	{
		//implement a switch statement: insert, delete, match.
		if(cmd.length <= 0)
			return "INVALID INPUT";
		
		switch(cmd[0])
		{
		//client auto register itself
		case "REGISTER":
			client = cmd[1];
			port = Integer.parseInt(cmd[2]);
			return "REGISTERED";
		case "BYE":// client disconnect from server
			db.closeDB();
			return "BYE";
		case "DISCONNECT":
			return "DISCONNECTED";
		case "SHOWALL":
			db.printALL();
			return "RESULT ON SERVER";
		case "SHUTDOWN":
			if(cmd[1].equals("SERVER") )//client make server shut down
			{
				db.closeDB();
				return "SERVER SHUTDOWN";
			}
		case "INSERT": 
			if(cmd.length != 4)
				return "Format Mismatch(INSERT STRING STRING STRING)";
			Tuple t = new Tuple(cmd[1],cmd[2],cmd[3]);
			if(db.insert(t))
				return "INSERT DONE";
			else
				return "Conflicts!";
		case "DELETE":
			if(cmd.length != 4)
				return "Format Mismatch(INSERT STRING STRING STRING)";
			Tuple toDel = new Tuple(cmd[1],cmd[2],cmd[3]);
			if(db.delete(toDel))
				return "DELETE DONE";
			else
				return "NO MATCH";
		case "MATCH":
			if(cmd.length != 4)
				return "Format Mismatch(INSERT STRING STRING STRING)";
			Tuple toMatch = new Tuple(cmd[1],cmd[2],cmd[3]);
			if(db.search(toMatch)!=null )
				return "MATCH: "+ db.search(toMatch).toString();
			else 
				return "NO MATCH";
		default:
			break;
		}
		
		return "INVALID INPUT";
	}
}
