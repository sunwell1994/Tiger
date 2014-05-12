package java_semantic;
import Symbol.*;
import Types.VOID;
import ErrorMsg.*;
class Env 
{
	Table vene;
	Table tenv;
	ErrorMsg errorMsg;
	Env(ErrorMsg err)
	{
		errorMsg=err;  
		vene =new Table();
		tenv =new Table();
		
		tenv.put(Symbol.symbol("int"), new Types.INT());
		tenv.put(Symbol.symbol("string"), new Types.STRING());
		
		vene.put(Symbol.symbol("print"), 
				 new StdFunEntry(new Types.RECORD(Symbol.symbol("s"), (Types.STRING)tenv.get(Symbol.symbol("string")), null), new Types.VOID()));

		vene.put(Symbol.symbol("printi"), 
				 new StdFunEntry(new Types.RECORD(Symbol.symbol("i"), (Types.INT)tenv.get(Symbol.symbol("int")), null),new Types.VOID()));

		vene.put(Symbol.symbol("flush"), 
				 new StdFunEntry(null, new Types.VOID()));

		vene.put(Symbol.symbol("getchar"), 
				 new StdFunEntry(null, new Types.STRING()));

		vene.put(Symbol.symbol("ord"), 
				 new StdFunEntry(new Types.RECORD(Symbol.symbol("s"), (Types.STRING)tenv.get(Symbol.symbol("string")), null), new Types.INT()));

		vene.put(Symbol.symbol("chr"),
				new StdFunEntry( new Types.RECORD(Symbol.symbol("i"), (Types.INT)tenv.get(Symbol.symbol("int")), null), new Types.STRING()));

		vene.put(Symbol.symbol("size"), 
				new StdFunEntry(new Types.RECORD(Symbol.symbol("s"), (Types.STRING)tenv.get(Symbol.symbol("string")), null), new Types.INT()));

		vene.put(Symbol.symbol("substring"), 
				new StdFunEntry(new Types.RECORD(Symbol.symbol("s"), (Types.STRING)tenv.get(Symbol.symbol("string")),
				new Types.RECORD(Symbol.symbol("f"), new Types.INT(), 
				new Types.RECORD(Symbol.symbol("n"), new Types.INT(), null))), new Types.STRING()));

		vene.put(Symbol.symbol("concat"), 
				new StdFunEntry(new Types.RECORD(Symbol.symbol("s1"), (Types.STRING)tenv.get(Symbol.symbol("string")),
				new Types.RECORD(Symbol.symbol("s2"), (Types.STRING)tenv.get(Symbol.symbol("string")), null)),
				new Types.STRING()));

		vene.put(Symbol.symbol("not"), 
				 new StdFunEntry(new Types.RECORD(Symbol.symbol("i"), (Types.INT)tenv.get(Symbol.symbol("int")), null),new Types.INT()));

		vene.put(Symbol.symbol("exit"), 
				 new StdFunEntry(new Types.RECORD(Symbol.symbol("i"), (Types.INT)tenv.get(Symbol.symbol("int")), null), new Types.VOID()));
	
	}
}
