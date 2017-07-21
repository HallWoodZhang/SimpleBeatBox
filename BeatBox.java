
/*************************************************************************
 > File Name: BeatBox.java
 > Author: HallWood
 > Mail: hallwoodzhang@gmail.com
 > Created Time: 2017年07月18日 星期二 18时57分49秒
 ************************************************************************/



import javax.sound.midi.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;


public class BeatBox {

    JPanel mainPanel;
    ArrayList<JCheckBox> checkBoxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame frame;

    String[] instrumentList = {
            "Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",
            "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap",
            "Low-mid Tom", "High Agogo", "Open High conga"
    };

    int[] instruments = {32, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 37};

    public static void main(String[] args) {
        new BeatBox().buildGui();
    } // close main

    public void buildGui() {
        frame = new JFrame("DK BeatBox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel backGround = new JPanel(layout);
        backGround.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // 设定空白的边缘

        checkBoxList = new ArrayList<JCheckBox>();
        Box btnBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start!");
        start.addActionListener(new MyStartListener());
        btnBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListenr());
        btnBox.add(stop);

        JButton upTempo = new JButton("Tempo up!");
        upTempo.addActionListener(new MyUpTempoListener());
        btnBox.add(upTempo);

        JButton downTempo = new JButton("Tempo down!");
        downTempo.addActionListener(new MyDownTempoListener());
        btnBox.add(downTempo);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for(int i = 0; i < 16; ++i) {
            nameBox.add(new Label(instrumentList[i]));
        }

        backGround.add(BorderLayout.EAST, btnBox);
        backGround.add(BorderLayout.WEST, nameBox);

        frame.getContentPane().add(backGround);

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        backGround.add(BorderLayout.CENTER, mainPanel);

        for(int i = 0; i < 256; ++i) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkBoxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        frame.setBounds(50, 50, 300, 300);
        frame.pack();
        frame.setVisible(true);
    } // close buildGui

    public void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    } // close setupMidi

    public void buildTrackAndStart() {
        int[] trackList = null;
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for(int i = 0; i < 16; ++i) {
            trackList = new int[16];
            int key = instruments[i];

            for(int j = 0; j < 16; ++j) {
                JCheckBox jc = (JCheckBox) checkBoxList.get(j + (16*i));
                // 模拟16 * 16矩阵的行为(the 16 * 16 martrix)
                if(jc.isSelected()) { // seleted instruments work
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            } // close the inner loop

            makeTracks(trackList);
            track.add(makeEvent(176, 1,127,0,16));

        } // close the outer loop

        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch(Exception ex) {
            ex.printStackTrace();
        }


    } // close buildTrackAndStart

    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            buildTrackAndStart();
        }
    } // close inner class MyStartListener

    public class MyStopListenr implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
        }
    } // close inner class MyStopListener

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * 1.03));

        }
    } // close inner class MyUpTempoListener

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * 0.97));
        }
    } // close inner class MyDownTempoLsitener

    public void makeTracks(int[] list) {
        for(int i = 0; i < 16; ++i) {
            int key = list[i];

            if(key != 0) {
                track.add(makeEvent(144,9,key,100, i));
                track.add(makeEvent(128,9,key,100,i + 1));
            }
        } // close loop
    } // close makeTracks

    public MidiEvent makeEvent(int cmd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(cmd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return event;
    } // close makeEvent

} // close class BeatBox
