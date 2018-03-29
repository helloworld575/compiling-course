import java.util.ArrayList;
import java.util.HashMap;

class Tokens {
    private String[]codeName = {"identifer","intDef","floatDef","booleanDef","stringDef",
                                "int","float","bool","string","while","if","input","output",
                                "not","or","and","else","begin","end","{","}","(",")",";",
                                ">","<","+","-","*","/","=","==","!=",">=","<=","'","\"",
                       };
    private HashMap<String,String>codingSet;
    ArrayList<Token>tokenArray = new ArrayList<>();//token集
    ArrayList<Chars>charArray = new ArrayList<>();//字符集
    Tokens(){
        codingSet = new HashMap<>();
        int TOTALNUM = 37;
        for(int i = 0; i< TOTALNUM; i++){
            codingSet.put(codeName[i],""+(i+1));
        }
    }
    int findKeyWords(String keyword){
        for(int i=5;i<=18;i++){
            if(keyword.equals(codeName[i])){
                return i;
            }
        }
        return -1;
    }
    public boolean StringInCArray(Chars T){
        for(Chars tmp:charArray){
            if((tmp.a.equals(T.a)) && (tmp.i == T.i))
                return true;
        }
        return false;
    }
    void printCodingSet(){
        System.out.println("============输出编码集============");
        for(String key : codingSet.keySet()){
            System.out.println(key+"-->"+codingSet.get(key));
        }
        System.out.println("===============编码集输出结束=================");
    }
}
class Token{
    int i;
    String a;
    Token(int i,String a){
        this.i = i;
        this.a = a;
    }
}
class Chars{
    int i;
    String a;
    Chars(int i,String a){
        this.i=i;
        this.a=a;
    }
}