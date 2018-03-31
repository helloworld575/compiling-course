class ErrorMsg {
    private String msg = "";
    private int errorCode = 0;
    /*
    1:程序未找到
    2:程序字符出现错误
    3:程序数字出现错误
    4:无法识别字符
    5:关键字错误
    6:多行字符串错误
    7:注釋后沒有正常結束文件
    8:程序边界符未匹配
    9:程序未以end结尾
    */
    private int exCode = -1;
    /*
    0:程序开始
    1:程序正常结束
    2:程序异常结束
    -1:程序未开始
     */
    private int errChar;
    void printError() {
        switch (exCode) {
            case 2:{
                System.out.println("程序编译出错！");
                System.out.println(msg);
                System.out.println("出错字符码："+errChar+"\n");
                break;
            }
            case 1:{
                System.out.println("程序正常结束！");
                break;
            }
            case 0:{
                System.out.println("程序词法分析进行中！");
                break;
            }
            case -1:{
                System.out.println("程序未开始！");
                break;
            }
        }

    }
    void setError(int errorCode,int line,int nowChar){
        this.exCode = 2;
        this.errorCode = errorCode;
        switch (errorCode){
            case 1: msg = "程序未找到，请以使程序以start开头,错误码："+errorCode+"\n";break;
            case 9: msg = "程序未正常结束，请在程序尾添加end结束程序,错误码："+errorCode+"\n";break;
            case 3: msg = "第" + line + "行字符无法识别\n出错码："+errorCode+"\n";break;
            case 2: msg = "第" + line + "行数字发生错误\n出错码："+errorCode+"\n";break;
            case 6: msg = "第" + line + "行字符串未正常结束\n出错码："+errorCode+"\n";break;
            case 8: msg = "第" + line + "行边界符未匹配\n出错码："+errorCode+"\n";break;
            case 7: msg = "第" + line + "行注释后未正常结束程序";break;
            case 5: msg = "第"+line+"行关键字错误\n出错码："+errorCode+"\n";break;
        }
        errChar = nowChar;
    }
    void naturalFinish(){
        this.exCode = 1;
    }
    void setExCode(int exCode){
        this.exCode = exCode;
    }
    int getExCode(){
        return this.exCode;
    }
    int getErrorCode(){
        return this.errorCode;
    }
}
class TokenMatch{
    char c;
    int line;
    TokenMatch(char c,int line){
        this.c = c;
        this.line = line;
    }
}
