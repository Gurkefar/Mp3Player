import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.*;
import java.io.*;
import java.util.concurrent.Semaphore;

class ThreadClass extends Thread {
    Semaphore sem;
    String threadName;
    private static volatile boolean flag = false;
    File myFile;

    public ThreadClass(Semaphore sem, String threadName, File myFile) throws IOException, JavaLayerException {
        super(threadName);
        this.sem = sem;
        this.threadName = threadName;
        this.myFile = myFile;
    }

    //threads are automatically destroyed, when the run() method has completed
    @Override
    public void run() {
        // Thread T1 processing
        if(this.getName().equals("play"))  {
            try {
                    if (!flag) {
                        System.out.println("Start: " + threadName);
                        Data.player = new Player(Data.bufferedInputStream);
                        System.out.println("Channel for thread playing: " + Data.fileInputStream.getChannel());
                        flag = true;
                        Data.totalLength = Data.fileInputStream.available();
                        System.out.println("Total length: " + Data.totalLength);
                        Data.isPlaying = true;
                        Data.player.play();
                        flag = false;
                        Data.isPlaying = false;
                    }
            } catch (JavaLayerException | IOException e) {
                e.printStackTrace();
            }
        }
        // Thread T2 processing
        else if (this.getName().equals("resume"))  {
            try{
                if(!Data.isPlaying) {
                    Data.player = new Player(Data.bufferedInputStream);
                    Data.fileInputStream.skip(Data.totalLength - Data.pause);
                    Data.isPlaying = true;
                    Data.player.play();
                    Data.isPlaying = false;
                }
                else{
                    System.out.println("Song is already playing!");
                }
            } catch (JavaLayerException | IOException e){
                System.out.println(e);
            }
            // Release the permit.
            System.out.println(threadName + ":Released the permit.");
        }
        else if (this.getName().equals("progressBar")){
            try {
                sleep(1000); // wait for play thread
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(Data.isPlaying){
                try {
                    sleep(1000); // wait for play thread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Data.seconds++;
                if (Data.seconds > 60){
                    Data.minutes++;
                    Data.seconds = 0;
                }
                Data.timeLabel.setText(Data.minutes + " : " + Data.seconds);
                Data.progressBar.setValue((int) ((Data.player.getPosition() / 1000)*100 / (Data.totalLength * 0.00006241)));
            }
        }
    }
}
