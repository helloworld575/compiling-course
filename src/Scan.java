import java.io.*;

class Scan {
    private BufferedReader inputBr;
    private int line = 1;
    private int nowChar;
    private String errorMsg = "";
    private int errorCode = 0;          //1:程序范围出现错误 2:程序字符出现错误 3:程序数字出现错误 4:无法识别字符 5:关键字错误 6:多行字符串错误
    private int exCode = -1;            //0:程序开始 1:程序正常结束 2:程序异常结束 -1:程序未开始
    private String inputFileName;
    private String outputFileName;
    private Tokens tokens;

    private static String digitDFA[] = {
            "#d#####",
            "#d.#e##",//end
            "###d###",
            "###de##",//end
            "#####ad",
            "######d",
            "######d" //end
    };

    Scan(String inputFileName,String outputFileName,String tokenFileName,String keyWordsFileName) throws IOException {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.tokens = new Tokens(tokenFileName,keyWordsFileName);
    }

    void readTxt() throws IOException {
        FileReader inputFile = new FileReader(inputFileName);
        inputBr = new BufferedReader(inputFile);
        exCode = programStart();
        nowChar = inputBr.read();
        if(nowChar==-1){
            exCode = 1;
            errorCode = 1;
            errorMsg = errorMsg+"程序未找到！\n";
            PrintError();
            inputBr.close();
            inputFile.close();
            return;
        }
        while (nowChar != -1 && exCode == 0) {
            charSort((char) nowChar);
            if(exCode==2){
                System.out.println("程序异常结束！");
                PrintError();
            }
            if(exCode==1){
                System.out.println("程序正常结束");
            }
        }
        if(exCode==0){
            errorCode = 1;
            errorMsg = errorMsg+"程序未正常结束，请在程序尾添加end结束程序,错误码："+errorCode+"\n";
            PrintError();
        }
        inputBr.close();
        inputFile.close();
    }
    private int programStart() throws IOException {
        String word;
        while((word = inputBr.readLine())!=null){
            if(word.equals("begin")){
                return 0;
            }
        }
        return -1;
    }
    private void charSort(char a) throws IOException {
        if (a == '&')
            recordID(a);
        else if (Character.isDigit(a)) {
            recordNum(a);
        } else if (a == '\'')
            recordString(a);
        else if (a == '\"')
            recordString(a);
        else if (a == '{')
            recordSym(a, 20);
        else if (a == '}')
            recordSym(a, 21);
        else if (a == '(')
            recordSym(a, 22);
        else if (a == ')')
            recordSym(a, 23);
        else if (a == ';')
            recordCom(a);
        else if (a == '>')
            judgeAndRecord(a);
        else if (a == '<')
            judgeAndRecord(a);
        else if (a == '+')
            recordSym(a, 27);
        else if (a == '-')
            recordSym(a, 28);
        else if (a == '*')
            recordSym(a, 29);
        else if (a == '/')
            recordSym(a, 30);
        else if (a == '=')
            judgeAndRecord(a);
        else if (a == '!')
            judgeAndRecord(a);
        else if (nowChar == 32 || a == '\t'||nowChar==13) {
            nowChar = inputBr.read();
        } else if (a == '\n') {
            nowChar = inputBr.read();
            line++;
        } else if (Character.isLetter(a)) {
            recordKeyword(a);
        } else {
            exCode = 2;
            errorCode = 3;
            errorMsg = errorMsg + "第" + line + "行字符无法识别\n出错码："+errorCode+"\n";
        }
    }

    private void recordID(char a) throws IOException {
        String word = "" + a;
        nowChar = inputBr.read();
        char tmpA = (char) nowChar;
        while ((nowChar >= 48 && nowChar <= 57) || (nowChar >= 65 && nowChar <= 90) || (nowChar >= 97 && nowChar <= 122)) {
            word = word + tmpA;
            nowChar = inputBr.read();
            tmpA = (char) nowChar;
        }

        int CPlace=tokens.addChars(1,word);
        tokens.addToken(1,""+CPlace);
    }

    private void recordNum(char a) throws IOException {
        int isFloat = 0;
        String word = "" + a;
        nowChar = inputBr.read();
        char tmpA = (char) nowChar;
        int state = 1;
        while (Character.isDigit(tmpA) || tmpA == '+' || tmpA == '-' || tmpA == 'e' || tmpA == 'E' || tmpA == '.') {
            state = getDigitNum(tmpA, state);
            if(tmpA=='.' || tmpA == 'e')
                isFloat = 1;
            word = word + tmpA;
            nowChar = inputBr.read();
            tmpA = (char) nowChar;
            if (state == -1) {
                exCode = 2;
                errorCode = 2;
                errorMsg = errorMsg + "第" + line + "行数字发生错误\n出错码："+errorCode+"\n";
                break;
            }
        }
        if (state == 1 || state == 3 || state == 6) {
            if(isFloat==0){
                tokens.addToken(2,word);
            }
            else if(isFloat==1){
                tokens.addToken(3,word);
            }
        }
        else{
            exCode = 2;
            errorCode = 2;
            errorMsg = errorMsg+ "第" + line + "行数字发生错误\n出错码："+errorCode+"\n";
        }
    }

    private int getDigitNum(char a, int s) {
        for (int i = 0; i < 7; i++) {
            if (digitDFA[s].charAt(i) == 'd' && Character.isDigit(a))
                return i;
            if (digitDFA[s].charAt(i) == '.' && a == '.')
                return i;
            if (digitDFA[s].charAt(i) == 'e' && (a == 'e' || a == 'E'))
                return i;
            if (digitDFA[s].charAt(i) == 'a' && (a == '+' || a == '-' || Character.isDigit(a)))
                return i;
        }
        return -1;
    }

    private void recordString(char b) throws IOException {
        String word = "";
        while ((nowChar = inputBr.read()) != b) {
            if((char)nowChar!='\n'){
                word = word+(char)nowChar;
            }
            else{
                line+=1;
                exCode = 2;
                errorCode = 6;
                errorMsg = errorMsg+ "第" + line + "行字符串未正常结束\n出错码："+errorCode+"\n";
                return;
            }
        }
        nowChar = inputBr.read();
        tokens.addToken(5,word);
    }

    private void recordSym(char a, int n) throws IOException {
        String word = ""+a;
        tokens.addToken(n,word);
        nowChar = inputBr.read();
    }

    private void judgeAndRecord(char a) throws IOException {
        nowChar = inputBr.read();
        if(a=='>'){
            if((char)nowChar=='='){
                tokens.addToken(34,">=");
                nowChar = inputBr.read();
            }
            else{
                tokens.addToken(25,">");
            }
        }
        if(a=='<'){
            if((char)nowChar=='='){
                tokens.addToken(35,"<=");
                nowChar = inputBr.read();
            }
            else{
                tokens.addToken(26,"<");
            }
        }
        if(a=='='){
            if((char)nowChar=='='){
                tokens.addToken(32,"==");
                nowChar = inputBr.read();
            }
            else{
                tokens.addToken(31,"=");
            }
        }
        if(a=='!') {
            if ((char) nowChar == '=') {
                tokens.addToken(33,"!=");
                nowChar = inputBr.read();
            } else {
                errorCode = 3;
                exCode = 2;
                errorMsg = errorMsg + "第" + line + "行无法识别字符！\n出错码："+errorCode+"\n";
            }
        }
    }

    private void recordCom(char a) throws IOException {
        while((char)nowChar!='\n'){
            if(nowChar==-1){
                exCode = 2;
                return;
            }
            nowChar = inputBr.read();
        }
        line++;
        nowChar = inputBr.read();
    }

    private void recordKeyword(char a) throws IOException {
        String word = "";
        while(Character.isLetter(nowChar)){
            word = word+(char)nowChar;
            nowChar = inputBr.read();
        }
        int n = tokens.findKeyWords(word);
        if(n==19){
            exCode=1;
            return;
        }
        if(n==-1){
            if(word.equals("true")||word.equals("false")){
                tokens.addToken(4,word);
            }
            else{
                exCode = 2;
                errorCode = 5;
                errorMsg = errorMsg+"第"+line+"行语法错误\n出错码："+errorCode+"\n";
            }
        }
        else{
            tokens.addToken(n,word);
        }
    }

    private void PrintError() {
        System.out.println("程序编译出错！");
        System.out.println(errorMsg);
        System.out.println("出错字符码："+nowChar+"\n");
    }

    void outPut() throws FileNotFoundException {
        FileOutputStream output = new FileOutputStream(outputFileName);
        PrintStream p = new PrintStream(output);
        System.out.println("===========输出token集=============");
        p.println("=====================token集=====================");
        for(Token t:tokens.tokenArray){
            System.out.println("("+t.i+","+t.a+")");
            p.println("("+t.i+","+t.a+")");
        }
        System.out.println("===========token集输出结束=============");
        p.println("=====================token集结束=====================");
        System.out.println("===========字符集输出开始=============");
        p.println("=====================字符集============================");
        for(Chars t:tokens.charArray){
            System.out.println("("+t.a+","+t.i+")");
            p.println("("+t.a+","+t.i+")");
        }
        System.out.println("===========字符集输出结束=============");
        p.println("=====================字符集结束=====================");
    }
}
