package Types;

public class ARRAY extends Type {
   public Type element;                         //指名元素的类型
   public ARRAY(Type e) {element = e;}
   public boolean coerceTo(Type t) {
	return this==t.actual();                   //Array只能和本类型的进行强制类型转换，实际上是不支持强制类型转换??????
   }
}

