//importing all necessary packages
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.concurrent.Semaphore;

//implementing ActionListener interface
public class GUI implements ActionListener {
    JFrame frame;
    JLabel songNameLabel=new JLabel();
    JLabel extraInfo = new JLabel();
    JButton selectButton=new JButton("Select Mp3");
    JButton playButton=new JButton("Play");
    JButton pauseButton=new JButton("Pause");
    JButton resumeButton=new JButton("Resume");
    JButton stopButton=new JButton("Stop");
    JFileChooser fileChooser;
    File myFile=null;
    String filename;
    String filePath;
    boolean flag = false;
    Player player;
    ThreadClass play;
    ThreadClass resume;
    ThreadClass progressBarThread;
    Semaphore sem = new Semaphore(1);
    GUI() throws IOException, JavaLayerException {
        prepareGUI();
        addActionEvents();



    }
    public void prepareGUI() throws IOException {
        frame=new JFrame();
        frame.setTitle("Music Player");
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.magenta);
        frame.setSize(440,270);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Data.progressBar = new JProgressBar();
        Data.timeLabel = new JLabel();

        selectButton.setBounds(160,10,100,40);
        frame.add(selectButton);

        songNameLabel.setBounds(70,50,300,30);
        frame.add(songNameLabel);

        extraInfo.setBounds(70,70,300,30);
        frame.add(extraInfo);

        playButton.setBounds(30,110,100,30);
        frame.add(playButton);

        pauseButton.setBounds(120,110,100,30);
        frame.add(pauseButton);

        resumeButton.setBounds(210,110,100,30);
        frame.add(resumeButton);

        stopButton.setBounds(300,110,100,30);
        frame.add(stopButton);

        Data.progressBar.setBounds(70, 150, 300, 30);
        frame.add(Data.progressBar);

        Data.timeLabel.setBounds(210, 180, 300, 30);
        frame.add(Data.timeLabel);
    }
    public void addActionEvents(){
        //registering action listener to buttons
        selectButton.addActionListener(this);
        playButton.addActionListener(this);
        pauseButton.addActionListener(this);
        resumeButton.addActionListener(this);
        stopButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==selectButton){
            //code for selecting our mp3 file from dialog window
            fileChooser=new JFileChooser();
            fileChooser.setCurrentDirectory(new File("/home/gurk/Downloads"));
            fileChooser.setDialogTitle("Select Mp3");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Mp3 files","mp3"));
            if(fileChooser.showOpenDialog(selectButton)==JFileChooser.APPROVE_OPTION){
                myFile=fileChooser.getSelectedFile();
                filename=fileChooser.getSelectedFile().getName();
                filePath=fileChooser.getSelectedFile().getPath();
            }
        }
        try {
            play = new ThreadClass(sem, "play", myFile);
            resume = new ThreadClass(sem, "resume", myFile);
            progressBarThread = new ThreadClass(sem, "progressBar", myFile);
        } catch (IOException | JavaLayerException ex) {
            ex.printStackTrace();
        }


        if(e.getSource()==playButton){

            //starting play thread
            if(!Data.isPlaying){
                try {
                    Data.fileInputStream = new FileInputStream(myFile);
                    Data.bufferedInputStream = new BufferedInputStream(Data.fileInputStream);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                songNameLabel.setText("now playing : "+filename);
                play.start();
                progressBarThread.start();
            }
            else{
                extraInfo.setText("Song is already playing!");
            }
        }


        if(e.getSource()==pauseButton){
            //code for pause button
            if(Data.player != null){
                try {
                    Data.pause = Data.fileInputStream.available();
                    Data.player.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        }
        if(e.getSource()==resumeButton){
            if (!Data.isPlaying){
                try {
                    Data.fileInputStream = new FileInputStream(myFile);
                    Data.bufferedInputStream = new BufferedInputStream(Data.fileInputStream);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                resume.start();
                progressBarThread.start();
            }
            else{
                extraInfo.setText("Song is already playing!");
            }

        }

        if(e.getSource()==stopButton){
            //code for stop button
            if(Data.player!=null){
                Data.player.close();
            }
        }

    }
}
