package java_parser;
import java.io.PrintWriter;

import	java_lexer.*;
import	Absyn.Print;
public class Parse {

  public ErrorMsg.ErrorMsg errorMsg;
  
  public Parse (String filename) 
  {
       errorMsg = new ErrorMsg.ErrorMsg(filename);
       java.io.InputStream inp;
       Print printer=new Print(System.out);
       try 
       { inp=new java.io.FileInputStream(filename); }
       catch (java.io.FileNotFoundException e) 
       {throw new Error("File not found: " + filename);}
       parser myparser = new parser(new Lexer(inp,errorMsg), errorMsg);
       try 
       {
          myparser.parse();
       } catch (Throwable e) 
       {
    	   e.printStackTrace();
    	   throw new Error(e.toString());
       } 
       finally 
       {
         try {inp.close();} catch (java.io.IOException e) {}
       }
       printer.prExp(myparser.myparser, 4);
  //     oup.close();
  }

}
   

