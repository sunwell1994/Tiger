package Types;

public class NAME extends Type {
   public Symbol.Symbol name;
   private Type binding;
   public NAME(Symbol.Symbol n) {name=n;}
   public boolean isLoop() {
      Type b = binding;       //事件上binding相当于是一个链
      boolean any;
      binding=null;
      if (b==null) any=true;
      else if (b instanceof NAME)
            any=((NAME)b).isLoop();
      else any=false;
      binding=b;
      return any;
     }
     
   public Type actual() {return binding.actual();}   //沿着指针向上找，如果发现是Name类型的则
   													 //继续向上找，直到不是Name类型的则返回本类型的指针
   public boolean coerceTo(Type t) {
	return this.actual().coerceTo(t);                 //强制转换类型和实际类型的强制转换类型相同
   }
   public void bind(Type t) {binding = t;}
}
