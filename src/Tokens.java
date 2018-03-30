
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class Tokens {
    private String[]codeName;
    private int tokeNum;
    private int keyWordNum;
    private String tokenFileName;
    private String keyWordsFileName;
    ArrayList<Token>tokenArray = new ArrayList<>();     //token集
    ArrayList<Chars>charArray = new ArrayList<>();      //字符集
    private int charsNum;                               //字符集长度
    private int tokensNum;                              //token集长度
    private HashMap<String,String>myTokens;
    private HashMap<String,String>myKeyWords;
    Tokens(String tokenFileName,String keyWordsFileName) throws IOException {
        this.tokenFileName = tokenFileName;
        this.keyWordsFileName = keyWordsFileName;
        tokeNum=0;
        charsNum=0;
        initTokens();
        initKeyWords();
    }
    private void initTokens() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(tokenFileName));
        String oneLine;
        tokeNum = 0;
        myTokens = new HashMap<>();
        while((oneLine=br.readLine())!=null){
            String[]tmp = oneLine.split("\t");
            myTokens.put(tmp[1],tmp[0]);
            tokeNum++;
        }
        codeName = new String[tokeNum];
        for(int i =0;i<tokeNum;i++){
            codeName[i]=myTokens.get(""+(1+i));
        }
    }
    private void initKeyWords() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(keyWordsFileName));
        String oneLine;
        keyWordNum = 0;
        myKeyWords = new HashMap<>();
        while((oneLine=br.readLine())!=null){
            String[] tmp = oneLine.split("\t");
            myKeyWords.put(tmp[0],tmp[1]);
            keyWordNum++;
        }
    }
    int findKeyWords(String keyword){
        String tmp = myKeyWords.get(keyword);
        if(tmp==null){
            return -1;
        }
        return Integer.valueOf(tmp);
    }
    int addChars(int i,String s){
        int index;
        for(index =0;index<charsNum;index++){
            Chars tmp = charArray.get(index);
            if(tmp.a.equals(s)){
                return index;
            }
        }
        charArray.add(new Chars(i,s));
        charsNum++;
        return index;
    }
    void addToken(int i,String s){
        tokenArray.add(new Token(i,s));
    }
    boolean StringInCArray(Chars T){
        for(Chars tmp:charArray){
            if((tmp.a.equals(T.a)) && (tmp.i == T.i))
                return true;
        }
        return false;
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