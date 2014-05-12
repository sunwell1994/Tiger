package Types;

public class INT extends Type {
	public INT () {}                                      
	public boolean coerceTo(Type t) {return (t.actual() instanceof INT);}         //不支持强制类型转换
}

