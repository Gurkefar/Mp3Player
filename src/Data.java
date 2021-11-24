import javazoom.jl.player.Player;

import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Data {
    public static Semaphore semaphore = new Semaphore(1);
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
    public static Queue<File> queue = new LinkedList<>();
    public static AudioInputStream audioInputStream;
    public static VisualAudio visualAudio;
    public static boolean stop = false;
}
