import java.io.*;

class Scan {
    private BufferedReader input_br;
    private int line = 1;
    private int now_char;
    private Tokens tokens = new Tokens();
    private String error_msg = "";
    private int ex_code = 0;
    private static String inputFileName = "F:\\important\\bianyiyuanli\\txtfiles\\input.txt";
    private static String outputFileName = "F:\\important\\bianyiyuanli\\txtfiles\\output.txt";
    private static String digitDFA[] = {
            "#d#####",
            "#d.#e##",//end
            "###d###",
            "###de##",//end
            "#####ad",
            "######d",
            "######d" //end
    };
    void readTxt() throws IOException {
        FileReader inputFile = new FileReader(inputFileName);
        input_br = new BufferedReader(inputFile);
        now_char = input_br.read();
        while (now_char != -1 && ex_code == 0) {
            charSort((char) now_char);
            if(ex_code!=0){
                PrintError();
            }
        }
        input_br.close();
        inputFile.close();
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
        else if (now_char == 32 || a == '\t'||now_char==13) {
            now_char = input_br.read();
        } else if (a == '\n') {
            now_char = input_br.read();
            line++;
        } else if (Character.isLetter(a)) {
            recordKeyword(a);
        } else {
            ex_code = 1;
            error_msg = error_msg + "第" + line + "行字符发生错误\n出错码：0\n";
        }
    }

    private void recordID(char a) throws IOException {
        String word = "" + a;
        now_char = input_br.read();
        char tmpA = (char) now_char;
        while ((now_char >= 48 && now_char <= 57) || (now_char >= 65 && now_char <= 90) || (now_char >= 97 && now_char <= 122)) {
            word = word + tmpA;
            now_char = input_br.read();
            tmpA = (char) now_char;
        }
        Token tmpT = new Token(1, word);
        tokens.tokenArray.add(tmpT);
        Chars tmpC = new Chars(1,word);
        if (!tokens.StringInCArray(tmpC))
            tokens.charArray.add(tmpC);
    }

    private void recordNum(char a) throws IOException {
        int isFloat = 0;
        String word = "" + a;
        now_char = input_br.read();
        char tmpA = (char) now_char;
        int state = 1;
        while (Character.isDigit(tmpA) || tmpA == '+' || tmpA == '-' || tmpA == 'e' || tmpA == 'E' || tmpA == '.') {
            state = getDigitNum(tmpA, state);
            if(tmpA=='.' || tmpA == 'e')
                isFloat = 1;
            word = word + tmpA;
            now_char = input_br.read();
            tmpA = (char) now_char;
            if (state == -1) {
                ex_code = 1;
                error_msg = error_msg + "第" + line + "行数字发生错误\n出错码：1\n";
                break;
            }
        }
        if (state == 1 || state == 3 || state == 6) {
            if(isFloat==0){
                Token tmpT = new Token(2, word);
                tokens.tokenArray.add(tmpT);
            }
            else if(isFloat==1){
                Token tmpT = new Token(3, word);
                tokens.tokenArray.add(tmpT);
            }
        }
        else{
            ex_code = 1;
            error_msg = error_msg+ "第" + line + "行数字发生错误\n出错码：2\n";
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
        while ((now_char = input_br.read()) != b) {
            if(Character.isLetterOrDigit((char)now_char)){
                word = word+(char)now_char;
            }
            else
                break;
        }
        now_char = input_br.read();
        Token tmpT = new Token(5, word);
        tokens.tokenArray.add(tmpT);
    }

    private void recordSym(char a, int n) throws IOException {
        String word = ""+a;
        Token tmpT = new Token(n, word);
        tokens.tokenArray.add(tmpT);
        now_char = input_br.read();
    }

    private void judgeAndRecord(char a) throws IOException {
        now_char = input_br.read();
        if(a=='>'){
            if((char)now_char=='='){
                Token tmpT = new Token(34, ">=");
                tokens.tokenArray.add(tmpT);
                now_char = input_br.read();
            }
            else{
                Token tmpT = new Token(25, ">");
                tokens.tokenArray.add(tmpT);
            }
        }
        if(a=='<'){
            if((char)now_char=='='){
                Token tmpT = new Token(35, "<=");
                tokens.tokenArray.add(tmpT);
                now_char = input_br.read();
            }
            else{
                Token tmpT = new Token(26, "<");
                tokens.tokenArray.add(tmpT);
            }
        }
        if(a=='='){
            if((char)now_char=='='){
                Token tmpT = new Token(32, "==");
                tokens.tokenArray.add(tmpT);
                now_char = input_br.read();
            }
            else{
                Token tmpT = new Token(31, "=");
                tokens.tokenArray.add(tmpT);
            }
        }
        if(a=='!') {
            if ((char) now_char == '=') {
                Token tmpT = new Token(33, "!=");
                tokens.tokenArray.add(tmpT);
                now_char = input_br.read();
            } else {
                ex_code = 1;
                error_msg = error_msg + "第" + line + "行语法错误\n出错码：3\n";
            }
        }
    }

    private void recordCom(char a) throws IOException {
        while((char)now_char!='\n'){
            if(now_char==-1){
                ex_code = 2;
                return;
            }
            now_char = input_br.read();
        }
        line++;
        now_char = input_br.read();
    }

    private void recordKeyword(char a) throws IOException {
        String word = "";
        while(Character.isLetter(now_char)){
            word = word+(char)now_char;
            now_char = input_br.read();
        }
        int n = tokens.findKeyWords(word);
        if(n==-1){
            if(word.equals("true")||word.equals("false")){
                Token tmpT = new Token(4, word);
                tokens.tokenArray.add(tmpT);
            }
            else{
                ex_code = 1;
                error_msg = error_msg+"第"+line+"行语法错误\n出错码：4\n";
            }
        }
        else{
            Token tmpT = new Token(n, word);
            tokens.tokenArray.add(tmpT);
        }
    }

    private void PrintError() {
        System.out.println("程序编译出错！");
        System.out.println(error_msg);
        System.out.println("出错字符码："+now_char+"\n");
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
