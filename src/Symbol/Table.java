package Symbol;

class Binder {            //Binder链表
  Object value;         //值，如a->int的int
  Symbol prevtop;        //a
  Binder tail;           //构成链表
  Binder(Object v, Symbol p, Binder t) {
	value=v; prevtop=p; tail=t;
  }
}

/**
 * The Table class is similar to java.util.Dictionary, except that
 * each key must be a Symbol and there is a scope mechanism.
 */


public class Table {

  private java.util.Dictionary dict = new java.util.Hashtable();
  private Symbol top;
  private Binder marks;

  public Table(){}

 /**
  * Gets the object associated with the specified symbol in the Table.
  */
  public Object get(Symbol key) {
	Binder e = (Binder)dict.get(key);       //放进的即是一个Binder类型的
	if (e==null) return null;
	else return e.value;
  }	

 /**
  * Puts the specified value into the Table, bound to the specified Symbol.
  */
  public void put(Symbol key, Object value) {
	dict.put(key, new Binder(value, top, (Binder)dict.get(key)));  //在链表的头部加入一个节点
	top = key;                                                     //头节点的值变为key
  }

 /**
  * Remembers the current state of the Table.
  */
  public void beginScope() {marks = new Binder(null,top,marks); top=null;}

 /** 
  * Restores the table to what it was at the most recent beginScope
  *	that has not already been ended.
  */
  public void endScope() {
	while (top!=null) {
	   Binder e = (Binder)dict.get(top);
	   if (e.tail!=null) dict.put(top,e.tail);
	   else dict.remove(top);
	   top = e.prevtop;
	}
	top=marks.prevtop;
	marks=marks.tail;
  }
  
  /**
   * Returns an enumeration of the Table's symbols.
   */
  public java.util.Enumeration keys() {return dict.keys();}
}

