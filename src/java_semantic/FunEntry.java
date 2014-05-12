package java_semantic;
import Temp.*;
class FunEntry extends Entry          //实现函数到类型的映射
{
//	public Label label;
 	Types.RECORD formals;   //参数的类型
	Types.Type result;       //返回值的类型
	public FunEntry(Types.RECORD f,Types.Type r)
	{
		formals=f; result=r;
	}
}
