import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;


class ThreadClass extends Thread {
    String threadName;
    File myFile;
    JFrame frame;

    public ThreadClass(String threadName, File myFile, JFrame frame) throws IOException, JavaLayerException {
        super(threadName);
        this.threadName = threadName;
        this.myFile = myFile;
        this.frame = frame;
    }

    //threads are automatically destroyed, when the run() method of a given thread has completed
    @Override
    public void run() {
        // Thread play processing
        if(this.getName().equals("play")) {
            playSong(); // play new (or) same song
            if (!Data.queue.isEmpty()) { // if we have queued song, make new input stream for the file and play it
                try {
                    Data.fileInputStream = new FileInputStream(Data.queue.poll());
                    Data.bufferedInputStream = new BufferedInputStream(Data.fileInputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                playSong(); // play the queued song
            }
            System.out.println(threadName + "thread ended");
        }

        // Thread resume processing
        else if (this.getName().equals("resume"))  {
            try{
                Data.semaphore.acquire(); //acquire permit (max of 1) by decrementing semaphore
                System.out.println("Start thread: " + threadName);
                Data.player = new Player(Data.bufferedInputStream); // make new player from the new buffered input stream
                Data.fileInputStream.skip(Data.totalLength - Data.pause); // skip to position in song, where it was paused
                Data.isPlaying = true;
                Data.stop = false;
                Data.player.play(); // play the song
                Data.isPlaying = false;
            } catch (JavaLayerException | IOException | InterruptedException e){
                System.out.println(e);
            }
            finally {
                System.out.println(threadName + "thread ended");
                Data.semaphore.release(); // release semaphore when done
            }
        }

        //Thread Visual-Audio processing
        else if (this.getName().equals("Visual-Audio")){
            try {
                Data.semaphore.acquire(); // acquire semaphore, so 2 threads aren't reading/changing the same input stream at the same time
                System.out.println("Start thread: " + threadName);

                // From line 65 to 82 is purely to read the data from the file input stream into an array, which we can use to display the data
                AudioFormat audioFormat = new AudioFormat(BasicMP3Encoding.MP3, 8000.0F, -1, 1, -1, -1, false); // get the audioformat - in this case Mp3
                Data.audioInputStream = new AudioInputStream(Data.fileInputStream, audioFormat, Data.fileInputStream.available());
                int frameLength = (int) Data.audioInputStream.getFrameLength(); // get the frame length, which is used to make our byte-array
                byte[] bytes = new byte[frameLength];
                int[] toReturn = new int[frameLength]; // array, which we use, when displaying the audio
                Data.fileInputStream.read(bytes); // read the file input stream into our byte-array
                int sampleIndex = 0; // now we sample the signal by taking 2 concurrent elements of our byte array
                // and adding them together, by zero-extending with least- and most-significant bits for high and low respectively
                for (int i = 0; i < bytes.length - 1;){
                    int low = bytes[i];
                    i++;
                    int high = bytes[i];
                    i++;
                    int sample = getSixteenBitSample(high, low);
                    toReturn[sampleIndex] = sample;
                    sampleIndex++;
                }

                Data.semaphore.release(); // remember to release semaphore, when we have processed the data
                frame.setVisible(true);
                int position = 0;
                sleep(200); // wait for play thread to process input stream (this is an approximation
                // and probably not the most optimal solution)
                    while (Data.isPlaying && !Data.stop) {
                        sleep(100); // visualize audio every 100 milliseconds
                        //show sixteen values of our data (toReturn), and shift 160 positions to the right (position += 160) every 100 milliseconds
                        // 160 values in the data corresponds approx. to 100 milliseconds, when read from the stream
                        Data.visualAudio.setLine((toReturn[position] / 1000) + 100, (toReturn[position + 1] / 1000) + 100, ((toReturn[position + 2]) / 1000) + 100, ((toReturn[position + 3]) / 1000) + 100,
                                ((toReturn[position + 4]) / 1000) + 100, ((toReturn[position + 5]) / 1000) + 100, ((toReturn[position + 6]) / 1000) + 100,
                                ((toReturn[position + 7]) / 1000) + 100, ((toReturn[position + 8]) / 1000) + 100, ((toReturn[position + 9]) / 1000) + 100,
                                ((toReturn[position + 10]) / 1000) + 100, ((toReturn[position + 11]) / 1000) + 100, ((toReturn[position + 12]) / 1000) + 100,
                                ((toReturn[position + 13]) / 1000) + 100, ((toReturn[position + 14]) / 1000) + 100, ((toReturn[position + 15]) / 1000) + 100);
                        while (!Data.isPlaying) {
                            System.out.println("waiting...");
                        }
                        position += 160;
                    }
                    System.out.println("hello");

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            Data.stop = false;
            System.out.println(threadName + " thread ended");
        }

        //Thread progress bar processing
        else if (this.getName().equals("progressBar")){
            //doesn't need any acquiring of semaphore, since it doesn't need access to data
            System.out.println("Start thread: " + threadName);
            try {
                sleep(1000); // wait for play thread
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(Data.isPlaying){
                try {
                    sleep(1000); // update bar every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Data.seconds++;
                if (Data.seconds > 60){
                    Data.minutes++;
                    Data.seconds = 0;
                }
                Data.timeLabel.setText(Data.minutes + " : " + Data.seconds);
                // Get the position of our player as a relation between position/1000 and seconds
                // which is the total length times our scaling factor (0.00006241)
                Data.progressBar.setValue((int) ((Data.player.getPosition() / 1000)*100 / (Data.totalLength * 0.00006241)));
            }
        }


    }

    private void playSong() {
        try {
            Data.semaphore.acquire();
            Data.seconds = 0;
            Data.minutes = 0;
            System.out.println("Start thread: " + threadName);
            Data.fileInputStream = new FileInputStream(myFile);
            Data.bufferedInputStream = new BufferedInputStream(Data.fileInputStream);
            Data.totalLength = Data.fileInputStream.available();
            Data.player = new Player(Data.bufferedInputStream);
            Data.isPlaying = true;
            Data.player.play();
            Data.semaphore.release();
            Data.isPlaying = false;
        } catch (JavaLayerException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int getSixteenBitSample(int high, int low) {
        return (high << 8) + (low & 0x00ff);
    }

}


