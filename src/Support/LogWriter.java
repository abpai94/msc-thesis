package Support;

import java.io.*;

/**
 * Created by Abhishek Pai on 06/07/2017.
 */
public class LogWriter {

    public void writer(String filename, String output) {
        FileWriter file = null;
        try {
            file = new FileWriter("D:\\Users\\Abhishek Pai\\Google Drive\\Computer Science\\Summer Project\\SillysoftSDK\\" + filename + ".txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printFile = new PrintWriter(file);
        printFile.append(output + "\n");
        printFile.close();
    }
}
