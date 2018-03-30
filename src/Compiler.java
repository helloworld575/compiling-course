import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Compiler {
    public static void main(String[] args) throws IOException {
        Compiler compiler = new Compiler();
        compiler.start();
    }

    private void start() throws IOException {
        String[]Files = readConfig();
        Scan sc = new Scan(Files[0],Files[1],Files[2],Files[3]);
        sc.readTxt();
        sc.outPut();
    }

    private String[] readConfig() throws IOException {
        String[]Files = new String[4];
        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream("/compiler.conf");
        prop.load(in);
        Files[0] = prop.getProperty("inputFileName");
        Files[1] = prop.getProperty("outputFileName");
        Files[2] = prop.getProperty("tokenFileName");
        Files[3] = prop.getProperty("keyWordsFileName");
        return Files;
    }
}


