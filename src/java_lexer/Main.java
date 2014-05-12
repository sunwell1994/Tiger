package java_lexer;
import  ErrorMsg.*;
import	java_parser.sym;

public class Main {

  public static void main(String argv[]) throws java.io.IOException {
      String filename = argv[0];
      ErrorMsg errorMsg = new ErrorMsg(filename);
      java.io.InputStream inp=new java.io.FileInputStream(filename);
      java.io.PrintWriter	oup=new java.io.PrintWriter("test1.lexer");
      Lexer lexer = new Lexer(inp,errorMsg);
      java_cup.runtime.Symbol tok;
      do { 
          
    	  tok=lexer.next_token();
          oup.write(symnames[tok.sym] + " " + tok.left+'\n');
         
	     System.out.println(symnames[tok.sym] + " " + tok.left);
      } while (tok.sym != sym.EOF);
      oup.close();
      inp.close();
  }
  static String symnames[] = new String[100];
  static {
     
     symnames[sym.FUNCTION] = "FUNCTION";
     symnames[sym.EOF] = "EOF";
     symnames[sym.INT] = "INT";
     symnames[sym.GT] = "GT";
     symnames[sym.DIVIDE] = "DIVIDE";
     symnames[sym.COLON] = "COLON";
     symnames[sym.ELSE] = "ELSE";
     symnames[sym.OR] = "OR";
     symnames[sym.NIL] = "NIL";
     symnames[sym.DO] = "DO";
     symnames[sym.GE] = "GE";
     symnames[sym.error] = "error";
     symnames[sym.LT] = "LT";
     symnames[sym.OF] = "OF";
     symnames[sym.MINUS] = "MINUS";
     symnames[sym.ARRAY] = "ARRAY";
     symnames[sym.TYPE] = "TYPE";
     symnames[sym.FOR] = "FOR";
     symnames[sym.TO] = "TO";
     symnames[sym.TIMES] = "TIMES";
     symnames[sym.COMMA] = "COMMA";
     symnames[sym.LE] = "LE";
     symnames[sym.IN] = "IN";
     symnames[sym.END] = "END";
     symnames[sym.ASSIGN] = "ASSIGN";
     symnames[sym.STRING] = "STRING";
     symnames[sym.DOT] = "DOT";
     symnames[sym.LPAREN] = "LPAREN";
     symnames[sym.RPAREN] = "RPAREN";
     symnames[sym.IF] = "IF";
     symnames[sym.SEMICOLON] = "SEMICOLON";
     symnames[sym.ID] = "ID";
     symnames[sym.WHILE] = "WHILE";
     symnames[sym.LBRACK] = "LBRACK";
     symnames[sym.RBRACK] = "RBRACK";
     symnames[sym.NEQ] = "NEQ";
     symnames[sym.VAR] = "VAR";
     symnames[sym.BREAK] = "BREAK";
     symnames[sym.AND] = "AND";
     symnames[sym.PLUS] = "PLUS";
     symnames[sym.LBRACE] = "LBRACE";
     symnames[sym.RBRACE] = "RBRACE";
     symnames[sym.LET] = "LET";
     symnames[sym.THEN] = "THEN";
     symnames[sym.EQ] = "EQ";
   }

}


