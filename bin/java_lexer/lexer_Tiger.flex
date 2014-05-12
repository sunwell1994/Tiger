package java_lexer;	/*	change to your own package name	*/
import  java_cup.runtime.*;
import  java.io.*;
import  java_parser.sym;	/*	change to your own token definition	*/
import  ErrorMsg.*;

%%
%class Lexer
%public
%cup
%char
%line
%column

%eofval{
{
	if(yystate()==ST_COMMENT) {	err("COMMENT CAN'T MATCH");	}
	if(yystate()==ST_STRING)  {	err("STRING PRESENTATION ERROR!");	}
	if(yystate()==ST_STRING1) {	err("STRING PRESENTATION ERROR!");	}
	return lexer_Symbol(sym.EOF);
}		
%eofval}       


%state ST_COMMENT
%state ST_STRING
%state ST_STRING1

lexer_ID			=		[a-zA-Z] [a-zA-Z0-9_]*
lexer_IntLit		=		[0-9]*
lexer_WhiteSpace	=		[\n\r\040\t\b\012]+     /*   回车，换行，空格，tab,退格，分页  */
lexer_NewLine		=		\n | \r | \n\r | \r\n 
/*   ---------------------------------------------my code ----------------------------------------------------------------------------------------- */		
lexer_CommentStart	=		"/*"
lexer_CommentEnd	=		"*/"
/*   ---------------------------------------------------------------------------------------------------------------------------------------------- */

%{
	private	int lexer_Level;
	private ErrorMsg errorMsg;
	StringBuffer lexerString = new StringBuffer();
	private void newline() 
	{
		errorMsg.newline(yychar);
	}
	private void err(int pos, String s) 
	{
		errorMsg.error(pos,s);
	}
	private void err(String s) {
		err(yychar,s);
	}
 
	public Lexer( java.io.InputStream lexer_Reader,ErrorMsg e)
	{
		this(lexer_Reader);
		errorMsg=e;
		/*	add anything you want	*/
	}
	
	private Symbol lexer_Symbol(int lexer_t_TOKEN, Object lexer_t_value)
	{
		return new Symbol(lexer_t_TOKEN, yychar, yychar+yylength(), lexer_t_value);
	}
	
	private Symbol lexer_Symbol(int lexer_t_TOKEN)
	{
		return new Symbol(lexer_t_TOKEN,yychar, yychar+yylength());
	}
	
%}



%% 



<YYINITIAL> 
{


/*		Reserved Words		*/

	"while"			{	return lexer_Symbol(sym.WHILE);		}
	"for"			{	return lexer_Symbol(sym.FOR);		}
	"to"			{	return lexer_Symbol(sym.TO);		}
	"break"			{	return lexer_Symbol(sym.BREAK);		}
	"let"			{	return lexer_Symbol(sym.LET);		}
	"in"			{	return lexer_Symbol(sym.IN);		}
	"end"			{	return lexer_Symbol(sym.END);		}
	"function"		{	return lexer_Symbol(sym.FUNCTION);	}
	"var"			{	return lexer_Symbol(sym.VAR);		}
	"type"			{	return lexer_Symbol(sym.TYPE);		}
	"array"			{	return lexer_Symbol(sym.ARRAY);		}
	"if"			{	return lexer_Symbol(sym.IF);		}
	"then"			{	return lexer_Symbol(sym.THEN);		}
	"else"			{	return lexer_Symbol(sym.ELSE);		}
	"do"			{	return lexer_Symbol(sym.DO);		}
	"of"			{	return lexer_Symbol(sym.OF);		}
	"nil"			{	return lexer_Symbol(sym.NIL);		}
	","			{	return lexer_Symbol(sym.COMMA);		}  /*		Punctuation Symbols		*/  
                                                                                   /*	add anything else that you need	       */
	                                                                           /*this example shows how to use pre-defined regEx, and how to use yytext()*/
	{lexer_ID}              {	return lexer_Symbol(sym.ID, yytext());	}      /*		Identifiers		*/
	{lexer_IntLit}	 
                                {	
						Integer lexer_TmpInt = new Integer(0);
						lexer_TmpInt = Integer.parseInt(yytext());
						return lexer_Symbol(sym.INT, lexer_TmpInt);
				}                                                  /*		Integer Literals		*/
/*   -----------------------------------------------my     code-------------------------------------------------------------------------------------*/
	":"                     {	return lexer_Symbol(sym.COLON);		}
	";"			{	return lexer_Symbol(sym.SEMICOLON);	}
	"("			{	return lexer_Symbol(sym.LPAREN);	}
	")"			{	return lexer_Symbol(sym.RPAREN);	}
	"["			{	return lexer_Symbol(sym.LBRACK);	}
	"]"			{	return lexer_Symbol(sym.RBRACK);	}
	"{"			{	return lexer_Symbol(sym.LBRACE);	}
	"}"			{	return lexer_Symbol(sym.RBRACE);	}
    "."			{	return lexer_Symbol(sym.DOT);		}
	"+"			{	return lexer_Symbol(sym.PLUS);		}
	"-"			{	return lexer_Symbol(sym.MINUS);		}
	"*"			{	return lexer_Symbol(sym.TIMES);		}
	"/"			{	return lexer_Symbol(sym.DIVIDE);	}
	"="			{	return lexer_Symbol(sym.EQ);		}
	"<>"		{	return lexer_Symbol(sym.NEQ);		}
	"<"			{	return lexer_Symbol(sym.LT);		}				
	"<="		{	return lexer_Symbol(sym.LE);		}
	">"			{	return lexer_Symbol(sym.GT);		}
	">="		{	return lexer_Symbol(sym.GE);		}
	"&"			{	return lexer_Symbol(sym.AND);		}
	"|"			{	return lexer_Symbol(sym.OR);		}
	":="		{	return lexer_Symbol(sym.ASSIGN);	}
	"\""		{	lexerString.setLength(0);yybegin(ST_STRING); }

    {lexer_WhiteSpace}      {	}
    {lexer_NewLine}         {	}
	{lexer_CommentStart}	{	lexer_Level=1;	yybegin(ST_COMMENT);	}
	{lexer_CommentEnd}		{	err("COMMENT CAN'T MATCH!");		}
	[^] 					{	err("Illegal character < "+yytext()+" >!");	}

/*   -----------------------------------------------------------------------------------------------------------------------------------------------*/
}
<ST_STRING>
{
	\"						{	yybegin(YYINITIAL);return lexer_Symbol(sym.STRING,lexerString.toString());}
	\\[0-9][0-9][0-9]		
   							{ 
   							   int temp=Integer.parseInt(yytext().substring(1, 4));
							   if(temp>255) err("EXCEED \\ddd"); else lexerString.append((char)temp);
							}
	[^\n\t\"\\]+			{	lexerString.append(yytext());	}      
	\\t 					{	lexerString.append('\t');		}
	\\n 					{	lexerString.append('\n');		}
	\\\" 					{	lexerString.append('\"');		}
	\\\\ 					{	lexerString.append('\\');		}
	{lexer_NewLine} 		{	err("String presentation error!");	}
	\\						{	yybegin(ST_STRING1);		}

}
<ST_STRING1>
{
	{lexer_WhiteSpace} 		{		} 
	\\ 						{	yybegin(ST_STRING);		}
	\" 						{	err("\\dont match");	}
	[^] 					{	lexerString.append(yytext());	}
}
<ST_COMMENT>
{
    {lexer_CommentStart}		{	lexer_Level++;	}
    {lexer_CommentEnd}		{	lexer_Level--; if(lexer_Level==0) yybegin(YYINITIAL);}	
    [^] {	}	
}

