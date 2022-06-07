package Support;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Abhishek Pai on 04/08/2017.
 */
public class Robot {

    public static void main(String[] args) throws InterruptedException, AWTException, IOException, UnsupportedAudioFileException, LineUnavailableException {
        java.awt.Robot robot = new java.awt.Robot();
        int counter = 0;
        TimeUnit.SECONDS.sleep(2);
        robot.keyPress(KeyEvent.VK_ENTER);
        for (int i = 0; i < 500; i++) {
            robot.keyPress(KeyEvent.VK_ENTER);
            counter++;
            System.out.println(counter);
            TimeUnit.SECONDS.sleep(25);
        }
    }
}