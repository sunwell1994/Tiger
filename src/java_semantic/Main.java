package java_semantic;
import Absyn.Print;
import ErrorMsg.*;
import java_lexer.Lexer;
import java_parser.Parse;
import java_parser.parser;
import MyException.*;
public class Main  {

public static void main(String[] args) throws java.io.IOException{
		
		ErrorMsg err=new ErrorMsg(args[0]);
		Semant semant=new Semant(new Env(err));
	    java.io.InputStream inp;
	    
	    String tiger=new String(args[0]);
	    String abs=new String(tiger.substring(0,tiger.length()-4)+".abs");
	    
//	    java.io.PrintWriter	oup=new java.io.PrintWriter(abs);
//	    Print printer=new Print(oup);
	    try 
	    {
	    	inp=new java.io.FileInputStream(args[0]);}
	    	catch (java.io.FileNotFoundException e) 
	        {throw new Error("File not found: " + args[0]);}
	    	
	        parser myparser = new parser(new Lexer(inp,err), err);
	        
	    	try 
	        {
	          myparser.parse();
	        } 
	    	catch (Throwable e) 
	        {
	    	   inp.close();
	    	   System.exit(1);
	        } 
	        try {inp.close();} 
	        catch (java.io.IOException e) {}
	        try
	        {  semant.transExp(myparser.myparser);}
	        catch(SExcep e)
	        { System.exit(1);}
//	        printer.prExp(myparser.myparser, 4);
//	        oup.close();
	        System.exit(0);
		}
}
