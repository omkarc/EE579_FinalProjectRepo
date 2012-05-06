import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class search{
	
	public static void main(String[] args){
		
		String t = "Alvida.mp3";
		boolean found = false;
		for(int i = 1; i<4;i++)
		{
			Integer j;
			j = i;
			String s = j.toString() + ".txt";
			//System.out.println(s);
			
			File folder = new File(s);
			try{
				  // Open the file that is the first 
				  // command line parameter
				  FileInputStream fstream = new FileInputStream(s);
				  // Get the object of DataInputStream
				  DataInputStream in = new DataInputStream(fstream);
				  BufferedReader br = new BufferedReader(new InputStreamReader(in));
				  String strLine;
				  //Read File Line By Line
				  while ((strLine = br.readLine()) != null)   {
				  // Print the content on the console
				  //System.out.println (strLine);
				  
				  	if(strLine.equalsIgnoreCase(t))
				  	{
				  		System.out.println ("Match Found in " + s);
				  		found= true;
				  		break;
				  	}
				  }
				  //Close the input stream
				  in.close();
			  }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			}
		}
		if(!found)
			System.out.println ("Match Not Found");
		
	}
}