//importing all necessary packages
import javazoom.jl.decoder.JavaLayerException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

//implementing ActionListener interface
public class GUI implements ActionListener, ListSelectionListener {

    //GUI components
    Desktop desktop;
    HashMap<String, File> songs = new HashMap<>();
    DefaultListModel<String> l1 = new DefaultListModel<>();
    JFrame frame;
    JFrame secondWindow;
    JLabel songNameLabel=new JLabel();
    JLabel extraInfo = new JLabel();
    JButton selectButton=new JButton("Select Mp3");
    JButton addButton = new JButton("Add");
    JButton deleteButton = new JButton("Delete");
    JButton shareButton = new JButton("Share");
    JButton playButton=new JButton("Play");
    JButton pauseButton=new JButton("Pause");
    JButton resumeButton=new JButton("Resume");
    JButton stopButton=new JButton("Stop");
    JButton enQueue = new JButton("Queue");
    JList playList = new JList(l1);
    JFileChooser fileChooser;
    File myFile=null;
    String filename;
    String filePath;

    ThreadClass play;
    ThreadClass resume;
    ThreadClass progressBarThread;
    ThreadClass audio;

    GUI() throws IOException {
        prepareGUI();
        addActionEvents();
    }

    public void prepareGUI() throws IOException {
        // Just a trivial GUI setup..
        frame=new JFrame();
        frame.setTitle("GOAT Mp3 Player");
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.magenta);
        frame.setSize(700, 270);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Data.progressBar = new JProgressBar();
        Data.timeLabel = new JLabel();

        secondWindow = new JFrame();
        secondWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        secondWindow.setSize(250, 200);
        secondWindow.setTitle("Visual Sound");
        Data.visualAudio = new VisualAudio();
        Data.visualAudio.setPreferredSize(new Dimension(300, 300));
        secondWindow.getContentPane().add(Data.visualAudio, BorderLayout.CENTER);
        secondWindow.setLocation(670, 570);

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

        playList.setBounds(480, 10, 200, 200);
        frame.add(playList);

        addButton.setBackground(Color.green);
        addButton.setBounds(410, 10, 70, 40);
        frame.add(addButton);

        deleteButton.setBackground(Color.red);
        deleteButton.setBounds(410, 50, 70, 40);
        frame.add(deleteButton);

        enQueue.setBackground(Color.yellow);
        enQueue.setBounds(410, 130, 70 ,40);
        frame.add(enQueue);

        shareButton.setForeground(Color.white);
        shareButton.setBackground(Color.blue);
        shareButton.setBounds(410,90,70,40);
        frame.add(shareButton);

        frame.setVisible(true);
    }
    public void addActionEvents(){
        // Attaching action listeners to buttons and such
        selectButton.addActionListener(this);
        playButton.addActionListener(this);
        pauseButton.addActionListener(this);
        resumeButton.addActionListener(this);
        stopButton.addActionListener(this);
        addButton.addActionListener(this);
        deleteButton.addActionListener(this);
        shareButton.addActionListener(this);
        playList.addListSelectionListener( this);
        enQueue.addActionListener(this);

    }

    // If a song in the play list has been selected
    @Override
    public void valueChanged(ListSelectionEvent e) {
        myFile = songs.get(playList.getSelectedValue());
        filename = (String) playList.getSelectedValue();
    }

    //Selecting file with home directory as start
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==selectButton){
            fileChooser=new JFileChooser();
            fileChooser.setCurrentDirectory(new File("/home"));
            fileChooser.setDialogTitle("Select Mp3");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Mp3 files","mp3"));
            if(fileChooser.showOpenDialog(selectButton)==JFileChooser.APPROVE_OPTION){
                myFile=fileChooser.getSelectedFile();
                filename=fileChooser.getSelectedFile().getName();
                filePath=fileChooser.getSelectedFile().getPath();
            }
        }

        //add seong to playlist
        else if (e.getSource()==addButton){
            //code for selecting our mp3 file from dialog window
            fileChooser=new JFileChooser();
            fileChooser.setCurrentDirectory(new File("/home/gurk/Downloads"));
            fileChooser.setDialogTitle("Add song to playlist");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Mp3 files","mp3"));
            if(fileChooser.showOpenDialog(selectButton)==JFileChooser.APPROVE_OPTION){
                songs.put(fileChooser.getSelectedFile().getName(), fileChooser.getSelectedFile()); // store file name in hashmap
                l1.addElement(fileChooser.getSelectedFile().getName()); //add to play list
                filename = fileChooser.getSelectedFile().getName(); //update current file name
            }
        }

        //remove song from play list
        else if (e.getSource() == deleteButton){
            if (playList.getSelectedValue() != null){
                l1.removeElementAt(playList.getSelectedIndex());
            }
            else{
                extraInfo.setText("Please select song to delete");
            }
        }

        // share by email
        else if (e.getSource() == shareButton){
            try {
                URI mailTo = new URI("mailto:john@example.com?subject=Hello%20World");
                desktop = Desktop.getDesktop();
                desktop.mail();
                desktop.mail(mailTo);
            } catch (URISyntaxException | IOException ex) {
                ex.printStackTrace();
            }
        }

        // que song to be played next
        else if (e.getSource() == enQueue){
            if (playList.getSelectedValue() != null){
                Data.queue.add(songs.get(playList.getSelectedValue()));
            }
        }



        try {
            // Create different objects of ThreadClass
            play = new ThreadClass("play", myFile, frame);
            resume = new ThreadClass("resume", myFile, frame);
            progressBarThread = new ThreadClass("progressBar", myFile, frame);
            audio = new ThreadClass("Visual-Audio", myFile, secondWindow);
        } catch (IOException | JavaLayerException ex) {
            ex.printStackTrace();
        }

        if(e.getSource()==playButton){
            // If song a song isn't already playing, start thread to play song
            if(!Data.isPlaying){
                try {
                    Data.fileInputStream = new FileInputStream(myFile);
                    Data.bufferedInputStream = new BufferedInputStream(Data.fileInputStream);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                songNameLabel.setText("now playing : "+filename);
                audio.start(); // start visual audio thread
                play.start(); // start playing thread
                progressBarThread.start(); // start progress bar thread
            }
            else{
                extraInfo.setText("Song is already playing!");
            }
        }

        //pausing song
        if(e.getSource()==pauseButton){
            //pause player, if it is active
            if(Data.player != null){
                try {
                    Data.pause = Data.fileInputStream.available(); // get current position in song (input stream)
                    Data.player.close(); // close the player
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        }

        //resume song
        if(e.getSource()==resumeButton){
            // if data is not playing - resume
            if (!Data.isPlaying){
                try {
                    Data.fileInputStream = new FileInputStream(myFile); // make new input stream for this thread
                    Data.bufferedInputStream = new BufferedInputStream(Data.fileInputStream);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                resume.start(); // start thread for playing song from saved position
                progressBarThread.start(); // start new progressbar
            }
            else{
                extraInfo.setText("Song is already playing!");
            }
        }

        //stop song
        if(e.getSource()==stopButton){
            //code for stop button
            if(Data.player!=null){
                Data.player.close(); // close player
                Data.stop = true; // flag used when visualizing audio
            }
        }
    }


}
