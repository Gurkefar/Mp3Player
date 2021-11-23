import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.*;
import java.io.*;
import java.util.concurrent.Semaphore;

class ThreadClass extends Thread {
    Semaphore sem;
    String threadName;
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
        // Thread play processing
        if(this.getName().equals("play"))  {
            try {
                sem.acquire();
                Data.seconds = 0;
                Data.minutes = 0;
                System.out.println("Start thread: " + threadName);
                Data.player = new Player(Data.bufferedInputStream);
                Data.totalLength = Data.fileInputStream.available();
                Data.isPlaying = true;
                Data.player.play();
                Data.isPlaying = false;
            } catch (JavaLayerException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(threadName + "thread ended");
            sem.release();
        }
        // Thread resume processing
        else if (this.getName().equals("resume"))  {
            try{
                sem.acquire();
                System.out.println("Start thread: " + threadName);
                Data.player = new Player(Data.bufferedInputStream);
                Data.fileInputStream.skip(Data.totalLength - Data.pause);
                Data.isPlaying = true;
                Data.player.play();
                Data.isPlaying = false;
            } catch (JavaLayerException | IOException | InterruptedException e){
                System.out.println(e);
            }
            finally {
                System.out.println(threadName + "thread ended");
                sem.release();
            }
        }
        //Thread progress bar processing
        else if (this.getName().equals("progressBar")){
            System.out.println("Start thread: " + threadName);
            try {
                sem.acquire();
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
            sem.release();
        }
    }
}
