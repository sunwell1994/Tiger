package java_semantic;
import Types.ARRAY;
import Types.INT;
import Types.NAME;
import Types.RECORD;
import Types.STRING;
import Types.Type;
import Types.VOID;
import ErrorMsg.*;
import MyException.*;

public class Semant {
	private static final String Object = null;
	Env env;
	Semant(Env e) {env=e;}
	Types.Type INT =new Types.INT();
	Types.Type STRING=new Types.STRING();
	Types.Type NIL=new Types.NIL();
	Types.Type VOID=new Types.VOID();
	Types.Type NAME=new Types.NAME(null);
	
 
	int loopLevel=0;
	
	public Semant(ErrorMsg err) 
	{ 
		this(new Env(err));
	}
//**********************************************************************************************************
	Expty transExp(Absyn.Exp e) throws SExcep 
	{	
		if(e instanceof Absyn.StringExp)           //string-constant
			return transExp((Absyn.StringExp)e);
		if(e instanceof Absyn.IntExp)              //int-constant
			return transExp((Absyn.IntExp)e);
		if(e instanceof Absyn.NilExp)              //nil
			return transExp((Absyn.NilExp)e);
		if (e instanceof Absyn.VarExp)			   //lvaue
			return transExp((Absyn.VarExp) e);     
		if(e instanceof Absyn.OpExp) 			   //op,-expr一元
			return transExp((Absyn.OpExp)e);		
		if (e instanceof Absyn.AssignExp)          //assign(lvalue:=expr)
			return transExp((Absyn.AssignExp) e); 		
		if (e instanceof Absyn.CallExp)			   //id ( expr-listopt )
			return transExp((Absyn.CallExp) e);
		if (e instanceof Absyn.SeqExp)				//( expr-seqopt )
			return transExp((Absyn.SeqExp) e);     
		if (e instanceof Absyn.RecordExp)   		//type-id { field-listopt }
			return transExp((Absyn.RecordExp) e);		
		if (e instanceof Absyn.ArrayExp)			//type-id [ expr ] of expr
			return transExp((Absyn.ArrayExp) e);		
		if(e instanceof Absyn.IfExp)               //if expr then expr
			return transExp((Absyn.IfExp)e);       //if expr then expr else expr
		if (e instanceof Absyn.WhileExp)           //while expr do expr
			return transExp((Absyn.WhileExp) e);
		if (e instanceof Absyn.ForExp)			   //for id := expr to expr do expr
			return transExp((Absyn.ForExp) e);				
		if (e instanceof Absyn.BreakExp)           //break
			return transExp((Absyn.BreakExp) e);
		if (e instanceof Absyn.LetExp)			   //let declaration-list in expr-seqopt end
			return transExp((Absyn.LetExp) e);
		env.errorMsg.error(e.pos,"transExp");
		throw  new SExcep();
	//	return null;
	}
//**********************************处理StringExp,IntExp,NilExp****************************************************
	Expty transExp(Absyn.StringExp e) throws SExcep 
	{
		return new Expty(null,STRING);
	}
	Expty transExp(Absyn.IntExp e) throws SExcep
	{  
		return new Expty(null,INT);
	}
	Expty transExp(Absyn.NilExp e) throws SExcep
	{
		return new Expty(null,NIL); 
	}
//********************************处理左值lvaue表达式***********************************************************************
	Expty transExp(Absyn.VarExp e) throws SExcep
	{
		return transVar(e.var);
	}
//********************************处理OpExp(+,-,*./,<,<=,>=,=,!=)****************************************************
	Expty transExp(Absyn.OpExp e) throws SExcep
	{
		Expty left=transExp(e.left);
		Expty right=transExp(e.right);
		switch(e.oper)
		{
		 	case Absyn.OpExp.PLUS:
		 	case Absyn.OpExp.MINUS:
		 	case Absyn.OpExp.MUL:
		 	case Absyn.OpExp.DIV:
		 			if(!(left.ty.coerceTo(INT)))
		 			{	 				
		 				env.errorMsg.error(e.left.pos,"算数运算左分量必须是整数_transExp(Absyn.OpExp e)");
		 				throw new SExcep(); 
		 			}
		 	        if(!(right.ty.coerceTo(INT)))
		 	        {
		 	        	env.errorMsg.error(e.right.pos,"算数运算右分量必须是整数_transExp(Absyn.OpExp e)");
		 	        	throw new SExcep();
		 	        }
		 	        return new Expty(null,INT);
		 	        
		 	case Absyn.OpExp.EQ:
		 	case Absyn.OpExp.NE:
		 		   if(left.ty.coerceTo(VOID))         //左边是void  
		 		   {
		 				   env.errorMsg.error(e.left.pos, "EQ or NEQ左边的值不能是VOID_transExp(Absyn.OpExp e)");
		 				   throw new SExcep();
		 		   }
                   if(right.ty.coerceTo(VOID))       //右边是void
                   {
                	       env.errorMsg.error(e.right.pos, "void can't used to the right of EQ or NEQ");
                	       throw new SExcep();
                   }
		 		   if((left.ty.actual() instanceof Types.NIL)&&(right.ty.actual() instanceof Types.NIL))  //左右都是nil
		 		   {
                	       env.errorMsg.error(e.pos, "can't  both sides of EQ or NEQ be nill");
                	       throw new SExcep();
		 		   }
		 		   if((left.ty.actual() instanceof Types.NIL)&&(!(right.ty.actual() instanceof Types.RECORD))) 
		 		   {
                           env.errorMsg.error(e.right.pos, "nil must be EQ or NEQ record");
                           throw new SExcep();
		 		   }
		 		   if((right.ty.actual() instanceof Types.NIL)&&(!(left.ty.actual() instanceof Types.RECORD)))
		 		   {   
                           env.errorMsg.error(e.left.pos, "nil must be EQ or NEQ record or Array");
                           throw new SExcep();
		 		   }
		 		   if(!(left.ty.coerceTo(right.ty)||right.ty.coerceTo(left.ty)))
		 		   {
                	       env.errorMsg.error(e.pos, "等号或不等号两边的类型不相同");
                	       throw new SExcep();
		 		   }
    			   return new Expty(null,INT);  
    			   
		 	case Absyn.OpExp.LT:
		 	case Absyn.OpExp.LE:
		 	case Absyn.OpExp.GT:
		 	case Absyn.OpExp.GE:
		 			if(	!
	 		    		(
	 		    				((left.ty.coerceTo(INT))&&(right.ty.coerceTo(INT)))
	 		    		 ||
	 		    		 		((left.ty.coerceTo(STRING))&&(right.ty.coerceTo(STRING)))
	 		    		)
		 			  )
		 			{
		 				env.errorMsg.error(e.left.pos,">,>=,<,<= interger or string requiredd");
		 				throw new SExcep();
		 			}
		 			return new Expty(null,INT);
		 		    
		}
		throw new Error("transExp.OpExp");
	}
//*********************************处理AssingExp**************************************************************************
	Expty transExp(Absyn.AssignExp e) throws SExcep
	{
	     //如果 t1 是简单变量并且在 vEnv 中查得它是 LoopVarEntry,则报错: 不能给循环变量赋值
		Expty var = transVar(e.var);
		Expty exp = transExp(e.exp);
		if (e.var instanceof Absyn.SimpleVar && env.vene.get(((Absyn.SimpleVar)e.var).name) instanceof LoopVarEntry)
		{
			env.errorMsg.error(e.pos, "循环变量不可以赋值");
			throw new SExcep();
		}
		if(var.ty.actual() instanceof Types.VOID||exp.ty.actual() instanceof Types.VOID)
		{
			env.errorMsg.error(e.pos, "变量类型和表达式类型不能为VOID");
			throw new SExcep();			
		}
		if(var.ty.actual() instanceof Types.NIL ) System.out.print("ok"+'\n');
		if(!checkType(var.ty,exp.ty))
		{
			env.errorMsg.error(e.pos, "变量类型和表达式类型不同");
			throw new SExcep();
		}
		return new Expty(null, VOID);         
	}
	boolean checkType(Types.Type t1,Types.Type t2) throws SExcep
	{
	    	if(t2.actual().coerceTo(t1.actual())&&!(t1.actual().coerceTo(NIL)))
	    	return true;
	    	if(t2.actual().coerceTo(NIL)&&t1.actual() instanceof Types.RECORD)
	    	return true;	
	    	return false;
	}
///********************************CallExp**********************************************************************/
	Expty transExp(Absyn.CallExp e) throws SExcep
	{
		Entry fun = (Entry)env.vene.get(e.func);
		if (fun == null || !(fun instanceof FunEntry)) {
			env.errorMsg.error(e.pos, "函数必须要先定义");
			throw new SExcep(); 
			//return new Expty(null, INT);
		}
		Absyn.ExpList arg = e.args;
		Types.RECORD formal = ((FunEntry)fun).formals;
		Expty temp = null;
		for (; arg != null; arg = arg.tail, formal = formal.tail) {
			if (formal == null) {
				System.out.print(e.func.toString()+'\n');
				env.errorMsg.error(e.pos, "实参和形参个数不同");
				throw new SExcep();
			//	break;
			}
			temp = transExp(arg.head);
			if(!temp.ty.actual().coerceTo(formal.fieldType.actual()))
			{
				env.errorMsg.error(e.pos,"实参和型参的类型不匹配");
				throw new SExcep();
			}
		}
		if (formal!= null)
		{
			env.errorMsg.error(e.pos,"实参和型参的个数不同");
			throw new SExcep();
		}
		return new Expty(null, ((FunEntry)fun).result.actual());
	}
//*********************************SeqExp*************************************************************************
	Expty transExp(Absyn.SeqExp e) throws SExcep
	{
		Absyn.ExpList temp=null;
		Expty ty=null;
		for(temp=e.list;temp!=null;temp=temp.tail)
		{
			ty=transExp(temp.head);
		}
		if(ty==null) return new Expty(null,new Types.VOID());
		return ty;
	}
//*********************************RecordExp*********************************************************************
	Expty transExp(Absyn.RecordExp e) throws SExcep
	{
		Types.Type ty=(Types.Type)env.tenv.get(e.typ);
		if(ty==null)
		{
			env.errorMsg.error(e.pos, "没有定义的结构体不能使用");
			throw new SExcep();
		}
		if(!(ty.actual() instanceof Types.RECORD))
		{
			env.errorMsg.error(e.pos, "此类型并不是结构体类型");
			throw new SExcep();
		}
		Absyn.FieldExpList p=e.fields;
		Types.RECORD temp=(Types.RECORD)ty.actual();
		for(;p!=null;p=p.tail,temp=temp.tail)
		{
			if(temp==null)
			{
				env.errorMsg.error(e.pos, "参数和结构体参数个数定义不符");
				throw new SExcep();
			}
		    if(p.name!=temp.fieldName)
		    {
		    	env.errorMsg.error(p.pos, "此结构体中没有定义这个变量");
		    	throw new SExcep();
		    }
		    Expty init=transExp(p.init);
		    if(!checkType(temp.fieldType.actual(),init.ty.actual()))	
		    {
		    	env.errorMsg.error(p.pos, "结构体成员变量类型和赋值不付");
		    	throw new SExcep();
		    }
		    
		}
		if(temp!=null)
		{
			env.errorMsg.error(e.pos, "参数和结构体参数个数定义不符");
			throw new SExcep();
		}
		return new Expty(null,ty);
	}
//*********************************ArrayExp*********************************************************************
	Expty transExp(Absyn.ArrayExp e) throws SExcep
	{
		Types.Type ty=(Types.Type)env.tenv.get(e.typ);
		if(ty==null)
		{
			env.errorMsg.error(e.pos, "此类型变量尚未定义_transExp(Absyn.ArrayExp e)");
			throw new SExcep();
		}
		if(!(ty.actual() instanceof Types.ARRAY))
		{
			env.errorMsg.error(e.pos, "此类型不是数组类型_transExp(Absyn.ArrayExp e)");
			throw new SExcep();
		}
		Expty e1=transExp(e.size);
		Expty e2=transExp(e.init);
		if(!(e1.ty.actual().coerceTo(INT)))
		{
				env.errorMsg.error(e.pos, "数组下标类型必须是整数_transExp(Absyn.ArrayExp e)");
				throw new SExcep();
		}
		Types.ARRAY t=(Types.ARRAY)ty.actual();
		if(!e2.ty.actual().coerceTo(t.element))
		{
				env.errorMsg.error(e.pos, "数组类型和定义类型不同");
				throw new SExcep();
		}
		return new Expty(null,ty);
	}
//*********************************处理IfExp*********************************************************************
	Expty transExp(Absyn.IfExp e) throws SExcep
	{
		Expty T=transExp(e.test);
		if(!(T.ty.actual().coerceTo(INT)))
		{
			env.errorMsg.error(e.test.pos, "If条件的返回值不是整数");
			throw new SExcep();
		}
		if((e.elseclause==null)&&!(transExp(e.thenclause).ty.actual() instanceof Types.VOID))
		{
			env.errorMsg.error(e.thenclause.pos, "If没有else子句且then子句有返回值");
			throw new SExcep();
		}
		if((e.elseclause!=null)&&(!checkType(transExp(e.thenclause).ty.actual(),transExp(e.elseclause).ty.actual())))
		{
			env.errorMsg.error(e.thenclause.pos, "then和else子句的返回类型不同");
			throw new SExcep();
		}
		if(e.elseclause==null)
		return new Expty(null,VOID);
		else
		return new Expty(null,transExp(e.thenclause).ty.actual());
//	    throw new Error("transIfExp");
	}
//************************************处理WhileExp******************************************************************
	Expty transExp(Absyn.WhileExp e) throws SExcep
	{
		if(!(transExp(e.test).ty.actual().coerceTo(INT)))
			env.errorMsg.error(e.test.pos, "While测试条件的返回值不是int类型的");
		loopLevel+=1;
		if(!(transExp(e.body).ty instanceof Types.VOID))
			env.errorMsg.error(e.body.pos, "While循环体的返回类型必须是VOID");
		loopLevel-=1;
		return new Expty(null,VOID);
	}
//**************************************For************************************************************************
	Expty transExp(Absyn.ForExp e) throws SExcep
	{
		env.vene.beginScope();
		env.tenv.beginScope();
		Expty  init= transExp(e.var.init);
		if (init.ty.actual() != INT)
		{
			env.errorMsg.error(e.var.pos, "循环变量必须初始化为整数");
			throw new SExcep();
		}
		env.vene.put(e.var.name, new LoopVarEntry(init.ty.actual()));
		
		Expty hi= transExp(e.hi);
		if(hi.ty.actual() !=INT)
		{
			env.errorMsg.error(e.hi.pos, "循环变量必须为整数");
			throw new SExcep();
		}
		loopLevel+=1;
		Expty body = transExp(e.body);
		if (!body.ty.actual().coerceTo(VOID))
		{
			env.errorMsg.error(e.body.pos, "For expression should return void");
			throw new SExcep();
		}
		loopLevel-=1;
		env.vene.endScope();
		env.tenv.endScope();
		return new Expty(null, VOID);
	}
//**************************************Break**********************************************************************	
	Expty transExp(Absyn.BreakExp e) throws SExcep
	{
		if(loopLevel==0)
		{
			env.errorMsg.error(e.pos,"没有处于循环之中，不能break");
			throw new SExcep();
		//	return new Expty(null,INT);
		}
		else 
			return new Expty(null,VOID);
	}
//**************************************处理LetExp***************************************************************************	
	Expty transExp(Absyn.LetExp e) throws SExcep
	{
		env.vene.beginScope();
		env.tenv.beginScope();
		if(e.decs!=null)
		for(Absyn.DecList p=e.decs;p!=null;p=p.tail)
			transDec(p.head);
		Expty et=transExp(e.body);
		env.vene.endScope();
		env.tenv.endScope();
		return new Expty(null,et.ty);
	}
//*****************************************************************************************************************
//**************************************处理变量(或左值)*****************************************************************
	Expty transVar(Absyn.Var v) throws SExcep {
		if (v instanceof Absyn.SimpleVar)
			return transVar((Absyn.SimpleVar) v);
		if (v instanceof Absyn.SubscriptVar)
			return transVar((Absyn.SubscriptVar) v);
		if (v instanceof Absyn.FieldVar)
			return transVar((Absyn.FieldVar) v);
		env.errorMsg.error(v.pos,"Translating variable");
		throw new SExcep();
	//	return null;
	}
	Expty transVar(Absyn.SimpleVar v) throws SExcep
	{
		Entry x = (Entry)env.vene.get(v.name);
		if(x instanceof VarEntry)
		{
			VarEntry ent=(VarEntry)x;
			return new Expty(null,ent.ty.actual());
		}
		else 
		{
			env.errorMsg.error(v.pos, "没有定义的简单变量_transVar(Absyn.SimpleVar v)");
			throw new SExcep();
		}
	}
	Expty transVar(Absyn.FieldVar v) throws SExcep
	{
		Expty f=transVar(v.var);
		Expty result=null;
		if(f.ty instanceof Types.RECORD)
		{
			 Types.RECORD r=null;
		     for(r=(Types.RECORD)f.ty;r!=null;r=r.tail)
		    	 if(r.fieldName==v.field)
		    		 break;
		     if(r==null)
		     {
		    	env.errorMsg.error(v.pos, "结构体中没有此变量");
		    	throw new SExcep();
		     }
		     else
		    	result=new Expty(null,r.fieldType.actual());	
		}
		else
		{
			env.errorMsg.error(v.pos, "Record required!");
			throw new SExcep();
		}
		return result;
	}
	Expty transVar(Absyn.SubscriptVar v) throws SExcep
	{
		Expty result=null;
		Expty b=transVar(v.var);
		Expty i=transExp(v.index);
		if(!(b.ty instanceof Types.ARRAY))
		{               
			env.errorMsg.error(v.pos, "必须是数组 transVar subscriptvar");
			throw new SExcep();
			//	result=new Expty(null,INT); 
		}
		else
		{
			if(!(i.ty.actual().coerceTo(INT)))
			{
				env.errorMsg.error(v.pos, "数组下表必须是整数transVar subscriptvar");
				throw new SExcep();
			}
			else 
			{
				Types.ARRAY temp=(Types.ARRAY)b.ty;
				result=new Expty(null,temp.element.actual());
			}
		}
		return result;
	}
//*************************************处理声明******************************************************************
	Translate.Exp transDec(Absyn.Dec d) throws SExcep {
		if (d instanceof Absyn.VarDec)
			return transDec((Absyn.VarDec) d);
		if (d instanceof Absyn.TypeDec)
			return transDec((Absyn.TypeDec) d);
		if (d instanceof Absyn.FunctionDec)
			return transDec((Absyn.FunctionDec) d);
		env.errorMsg.error(d.pos,"Translating declaration");
		throw new SExcep();
	//	return null;
	}
//********************************************处理变量声明************************************************************************
	Translate.Exp transDec(Absyn.VarDec d) throws SExcep
	{		
		Expty e=transExp(d.init);
		if(d.typ==null)
		{
			if(e.ty.actual() instanceof Types.NIL)
			{
				env.errorMsg.error(d.pos, "返回类型为nil transDecVarDec");
				throw new SExcep();
			}
			else
				env.vene.put(d.name, new VarEntry(e.ty.actual()));
		}
		else
		{
			if(!checkType(transTy(d.typ).actual(),e.ty.actual()))
			{
				env.errorMsg.error(d.pos, "定义类型和赋值类型不同");	
				throw new SExcep();
			}
			else 
				env.vene.put(d.name, new VarEntry(transTy(d.typ).actual()));
		}

		return null;
	}
//***************************处理类型声明******************************************************************
	Translate.Exp transDec(Absyn.TypeDec d) throws SExcep
	{
	    java.util.Dictionary mylist = new java.util.Hashtable();
	    Absyn.TypeDec p;
	    for(p=d;p!=null;p=p.next)
	    {
	    	if(mylist.get(p.name)!=null)
	    	{
	    		env.errorMsg.error(p.pos, "次此类性已经声明过了_transDec(Absyn.TypeDec d)");
	    		throw new SExcep();
	    	}
	    	else
	    		{
	    		   mylist.put(p.name, p.name);
	    		   env.tenv.put(p.name,new Types.NAME(p.name));    		   
	    		}
	    }
	    for(p=d;p!=null;p=p.next)
	    {
	    	((NAME)env.tenv.get(p.name)).bind(transTy(p.ty));
	    }
	    for(p=d;p!=null;p=p.next)
	    {
	    	Types.NAME temp=(Types.NAME)env.tenv.get(p.name);
	    	if(temp.isLoop())
	    	{
	    		env.errorMsg.error(p.pos, "存在变量的循环定义");
	    		throw new SExcep();
	    	}
	    }
		return null;
	}
	Types.Type transTy(Absyn.Ty t) throws SExcep
	{
		if(t instanceof Absyn.NameTy)
			return transTy((Absyn.NameTy)t);
		if(t instanceof Absyn.RecordTy)
			return transTy((Absyn.RecordTy)t);
		if(t instanceof Absyn.ArrayTy)
			return transTy((Absyn.ArrayTy)t);
		throw new SExcep();
	//	new Error("transTy");
	//	return null;
	}
	Types.Type transTy(Absyn.NameTy t)
	{
		Types.Type result=(Type)env.tenv.get(t.name);
		if(result==null) 
		{
			env.errorMsg.error(t.pos, "Transty(Absyn.NameTy)");
			return INT;
		}
		else return result;
	}
	Types.Type transTy(Absyn.ArrayTy t)      //type a=array
	{
		Types.Type result=(Type)env.tenv.get(t.typ);
		if(result==null)
		{
			env.errorMsg.error(t.pos, "transTy");
			return INT;
		}
		else return new Types.ARRAY(result);
	}
	Types.Type transTy(Absyn.RecordTy t) throws SExcep
	{
		Types.Type result=transFieldList(t.fields);
		return result;
	}
	Types.RECORD transFieldList(Absyn.FieldList f) throws SExcep
	{
		Types.RECORD result=null,pointer=null;
		Absyn.FieldList p;
	//	Types.Type temp;
	    java.util.Dictionary mylt = new java.util.Hashtable();
	    for(p=f;p!=null;p=p.tail)
	    {
	    	if(mylt.get(p.name)!=null)
	    	{
	    		env.errorMsg.error(p.pos, "transFieldList,record类型重定义");
	    		throw new SExcep();
	    	}
	    	if((Types.Type)env.tenv.get(p.typ)==null)
	    	{
	    		env.errorMsg.error(p.pos, "transFieldList,record类型没有定义");
	    		throw new SExcep();	    		
	    	}
	    	mylt.put(p.name,p.name);
			if (pointer == null)
				result=pointer= new RECORD(p.name,(Types.Type)env.tenv.get(p.typ),null);
			else {
				pointer.tail = new RECORD(p.name,(Types.Type)env.tenv.get(p.typ), null);
				pointer= pointer.tail;
			}
	    	
	    }
	    return result;
	}
//**********************处理函数声明***************************************************************************************
    Translate.Exp transDec(Absyn.FunctionDec d) throws SExcep
    {
	    java.util.Dictionary funlist = new java.util.Hashtable();
	    Absyn.FunctionDec p;

	    for(p=d;p!=null;p=p.next)
	    {
	    	if(p.inline) continue;
			if(env.vene.get(p.name)!= null && env.vene.get(p.name) instanceof StdFunEntry)
			{
				env.errorMsg.error(p.pos, "函数名和库函数冲突！ transDecFunctionDec");
				throw new SExcep();
			}
	    	if(funlist.get(p.name)!=null)
	    	{
                   env.errorMsg.error(p.pos, "函数重定义 transDec");
                   throw new SExcep();
	    	}
	    	else
	    	{
	    		funlist.put(p.name, p.name);
	    		Types.Type re=(p.result==null)?new Types.VOID():transTy(p.result).actual();
	    		env.vene.put(p.name, new FunEntry(transFieldList(p.params),re));
	    	}
	    }
	    for(p=d;p!=null;p=p.next)
	    {
			if (p.inline) continue;
			FunEntry f = (FunEntry)env.vene.get(p.name);
			env.vene.beginScope();
			for (Absyn.FieldList pointer = p.params; pointer != null; pointer= pointer.tail) {
				Type ty = (Type)env.tenv.get(pointer.typ);
				if (ty == null) {
				
						env.errorMsg.error(p.pos, "参数类型未定义 transDecFun");
						throw new SExcep();

			//		env.vene.endScope();
			//		return null;
				}
				else {
					env.vene.put(pointer.name, new VarEntry(ty.actual()));
				}
			}
			Expty et = transExp(p.body);
			env.vene.endScope();
			if(!checkType(f.result.actual(),et.ty.actual()))
			{
				env.errorMsg.error(p.pos, "返回表达式和返回的类型不同");
				throw new SExcep();
			}
				
	    }
	    return null;
    }
 
}
