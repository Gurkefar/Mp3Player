//importing all necessary packages
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

//implementing ActionListener interface
public class GUI implements ActionListener, ListSelectionListener {
    Desktop desktop;
    HashMap<String, File> songs = new HashMap<>();
    DefaultListModel<String> l1 = new DefaultListModel<>();


    JFrame frame;
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
    JList playList = new JList(l1);
    JFileChooser fileChooser;
    File myFile=null;
    String filename;
    String filePath;
    ThreadClass play;
    ThreadClass resume;
    ThreadClass progressBarThread;
    Semaphore sem = new Semaphore(2);
    GUI() throws IOException, JavaLayerException {
        prepareGUI();
        addActionEvents();

    }

    public void prepareGUI() throws IOException {
        frame=new JFrame();
        frame.setTitle("GOAT Mp3 Player");
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.magenta);
        frame.setSize(700,270);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Image image = Toolkit.getDefaultToolkit().getImage("resources/GOAT.png");
        frame.setIconImage(image);
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

        playList.setBounds(480, 10, 200, 200);
        frame.add(playList);

        addButton.setBackground(Color.green);
        addButton.setBounds(410, 10, 70, 40);
        frame.add(addButton);

        deleteButton.setBackground(Color.red);
        deleteButton.setBounds(410, 50, 70, 40);
        frame.add(deleteButton);

        shareButton.setForeground(Color.white);
        shareButton.setBackground(Color.blue);
        shareButton.setBounds(410,90,70,40);
        frame.add(shareButton);
    }
    public void addActionEvents(){
        //registering action listener to buttons
        selectButton.addActionListener(this);
        playButton.addActionListener(this);
        pauseButton.addActionListener(this);
        resumeButton.addActionListener(this);
        stopButton.addActionListener(this);
        addButton.addActionListener(this);
        deleteButton.addActionListener(this);
        shareButton.addActionListener(this);
        playList.addListSelectionListener( this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        System.out.println("hello");
        myFile = songs.get(playList.getSelectedValue());
        filename = (String) playList.getSelectedValue();
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
        else if (e.getSource()==addButton){
            //code for selecting our mp3 file from dialog window
            fileChooser=new JFileChooser();
            fileChooser.setCurrentDirectory(new File("/home/gurk/Downloads"));
            fileChooser.setDialogTitle("Add song to playlist");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Mp3 files","mp3"));
            if(fileChooser.showOpenDialog(selectButton)==JFileChooser.APPROVE_OPTION){
                songs.put(fileChooser.getSelectedFile().getName(), fileChooser.getSelectedFile());
                l1.addElement(fileChooser.getSelectedFile().getName());
                filename = fileChooser.getSelectedFile().getName();
            }
        }
        else if (e.getSource() == deleteButton){
            if (playList.getSelectedValue() != null){
                l1.removeElementAt(playList.getSelectedIndex());
            }
            else{
                extraInfo.setText("Please select song to delete");
            }
        }
        else if (e.getSource() == shareButton){
            try {
                URI mailTo = new URI("mailto:john@example.com?subject=Hello%20World");
                desktop.mail();
                desktop = Desktop.getDesktop();
                desktop.mail(mailTo);
            } catch (URISyntaxException | IOException ex) {
                ex.printStackTrace();
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
            //pause player, if it is active
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
            // if data is not playing - resume
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
