import javazoom.jl.player.Player;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Data {
    public static boolean isPlaying = false;
    public static long totalLength;
    public static long pause;
    public static Player player;
    public static FileInputStream fileInputStream;
    public static BufferedInputStream bufferedInputStream;
    public static JProgressBar progressBar;
    public static JLabel timeLabel;
    public static int seconds = 0;
    public static int minutes = 0;
}
