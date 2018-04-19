import java.io.*;
import java.util.Stack;

class Scan {
    private BufferedReader inputBr;
    private int line = 1;
    private int nowChar;
    private ErrorMsg errorMsg;
    private String inputFileName;
    private String outputFileName;
    private Tokens tokens;
    private Stack<TokenMatch> matchTokens;

    private static String digitDFA[] = {
            "#d#####",
            "#d.#e##",//end
            "###d###",
            "###de##",//end
            "#####ad",
            "######d",
            "######d" //end
    };

    Scan(String inputFileName, String outputFileName, String tokenFileName, String keyWordsFileName) throws IOException {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.tokens = new Tokens(tokenFileName, keyWordsFileName);
        this.errorMsg = new ErrorMsg();
        this.matchTokens = new Stack<>();
        this.matchTokens.push(new TokenMatch('\0', -1));
        readTxt();
    }

    private void readTxt() throws IOException {
        FileReader inputFile = new FileReader(inputFileName);
        inputBr = new BufferedReader(inputFile);
        errorMsg.setExCode(programStart());
        nowChar = inputBr.read();
        if (nowChar == -1) {
            errorMsg.setError(1, line, nowChar);
            inputBr.close();
            inputFile.close();
            return;
        }
        while (nowChar != -1 && errorMsg.getExCode() == 0) {
            charSort((char) nowChar);
            if (errorMsg.getExCode() == 1 && !(matchTokens.peek().line == -1)) {
                errorMsg.setError(8, matchTokens.peek().line, nowChar);
            }
        }
        if (errorMsg.getExCode() == 0) {
            errorMsg.setError(9, line, nowChar);
        }
        errorMsg.printError();
        inputBr.close();
        inputFile.close();
    }

    private int programStart() throws IOException {
        String word;
        while ((word = inputBr.readLine()) != null) {
            if (word.equals("begin")) {
                line++;
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
        else if (a == '[')
            recordSym(a, 38);
        else if (a == ']')
            recordSym(a, 39);
        else if (a == ';')
            recordCom();
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
        else if (nowChar == 32 || a == '\t' || nowChar == 13) {
            nowChar = inputBr.read();
        } else if (a == '\n') {
            nowChar = inputBr.read();
            line++;
        } else if (Character.isLetter(a)) {
            recordKeyword(a);
        } else {
            errorMsg.setError(3, line, nowChar);
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

        int CPlace = tokens.addChars(1, word);
        tokens.addToken(1, "" + CPlace);
    }

    private void recordNum(char a) throws IOException {
        int isFloat = 0;
        String word = "" + a;
        nowChar = inputBr.read();
        char tmpA = (char) nowChar;
        int state = 1;
        while (Character.isDigit(tmpA) || tmpA == '+' || tmpA == '-' || tmpA == 'e' || tmpA == 'E' || tmpA == '.') {
            state = getDigitNum(tmpA, state);
            if (tmpA == '.' || tmpA == 'e')
                isFloat = 1;
            word = word + tmpA;
            nowChar = inputBr.read();
            tmpA = (char) nowChar;
            if (state == -1) {
                errorMsg.setError(2, line, nowChar);
                break;
            }
        }
        if (state == 1 || state == 3 || state == 6) {
            if (isFloat == 0) {
                tokens.addToken(2, word);
            } else if (isFloat == 1) {
                tokens.addToken(3, word);
            }
        } else {
            errorMsg.setError(2, line, nowChar);
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
            if ((char) nowChar != '\n') {
                word = word + (char) nowChar;
            } else {
                line += 1;
                errorMsg.setError(6, line, nowChar);
                return;
            }
        }
        nowChar = inputBr.read();
        tokens.addToken(5, word);
    }

    private void recordSym(char a, int n) throws IOException {
        String word = "" + a;
        TokenMatch tmp;
        tokens.addToken(n, word);
        nowChar = inputBr.read();
        if (n == 20 || n == 22 || n == 38) {
            matchTokens.push(new TokenMatch(a, line));
        }
        if (n == 21) {
            tmp = matchTokens.peek();
            if (tmp.c == '{') {
                matchTokens.pop();
            } else {
                errorMsg.setError(8, line, nowChar);
            }
        }
        if (n == 23) {
            tmp = matchTokens.peek();
            if (tmp.c == '(') {
                matchTokens.pop();
            } else {
                errorMsg.setError(8, line, nowChar);
            }
        }
        if (n == 39) {
            tmp = matchTokens.peek();
            if (tmp.c == '[') {
                matchTokens.pop();
            } else {
                errorMsg.setError(8, line, nowChar);
            }
        }
    }

    private void judgeAndRecord(char a) throws IOException {
        nowChar = inputBr.read();
        if (a == '>') {
            if ((char) nowChar == '=') {
                tokens.addToken(34, ">=");
                nowChar = inputBr.read();
            } else {
                tokens.addToken(25, ">");
            }
        }

        if (a == '<') {
            if ((char) nowChar == '=') {
                tokens.addToken(35, "<=");
                nowChar = inputBr.read();
            } else {
                tokens.addToken(26, "<");
            }
        }
        if (a == '=') {
            if ((char) nowChar == '=') {
                tokens.addToken(32, "==");
                nowChar = inputBr.read();
            } else {
                tokens.addToken(31, "=");
            }
        }
        if (a == '!') {
            if ((char) nowChar == '=') {
                tokens.addToken(33, "!=");
                nowChar = inputBr.read();
            } else {
                errorMsg.setError(3, line, nowChar);
            }
        }
    }

    private void recordCom() throws IOException {
        while ((char) nowChar != '\n') {
            if (nowChar == -1) {
                errorMsg.setError(7, line, nowChar);
                return;
            }
            nowChar = inputBr.read();
        }
        line++;
        nowChar = inputBr.read();
    }

    private void recordKeyword(char a) throws IOException {
        String word = "";
        while (Character.isLetter(nowChar)) {
            word = word + (char) nowChar;
            nowChar = inputBr.read();
        }
        int n = tokens.findKeyWords(word);
        if (n == 19) {
            errorMsg.naturalFinish();
            return;
        }
        if (n == -1) {
            if (word.equals("true") || word.equals("false")) {
                tokens.addToken(4, word);
            } else {
                errorMsg.setError(5, line, nowChar);
            }
        } else {
            tokens.addToken(n, word);
        }
    }

    void outPut() throws FileNotFoundException {
        if (errorMsg.getExCode() == 1) {
            FileOutputStream output = new FileOutputStream(outputFileName);
            PrintStream p = new PrintStream(output);
            System.out.println("===========输出token集=============");
            p.println("=====================token集=====================");
            for (Token t : tokens.tokenArray) {
                System.out.println("(" + t.i + "," + t.a + ")");
                p.println("(" + t.i + "," + t.a + ")");
            }
            System.out.println("===========token集输出结束=============");
            p.println("=====================token集结束=====================");
            System.out.println("===========字符集输出开始=============");
            p.println("=====================字符集============================");
            for (Chars t : tokens.charArray) {
                System.out.println("(" + t.a + "," + t.i + ")");
                p.println("(" + t.a + "," + t.i + ")");
            }
            System.out.println("===========字符集输出结束=============");
            p.println("=====================字符集结束=====================");
        } else {
            System.out.println("程序错误!无法输出字符集和token集");
        }
    }
}
