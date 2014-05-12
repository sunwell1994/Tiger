package java_semantic;

import Types.*;           //实现变量到类型的映射
class VarEntry extends Entry{
Types.Type ty;                   
VarEntry(Types.Type t) {ty=t;}
}
