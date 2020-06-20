/*
 * Application by Kavin Nguyen
 * 2016
 * Constructs the GUI for the application. The app is divided into 4 main sections:
 * the timer, the scramble, the scoring section, and the adjustments section.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class CFrame extends JFrame {
    private JPanel contentPane;
    private JLabel timerLabel;
    private Timer timer;
    private boolean running;
    private boolean press;
    private long startTime;
    private double avg;
    private String avgLabel;
    private JLabel infoLabel;
    private int count;
    private Font font;
    private ArrayList<Double> scores;
    private int numTurns;
    private JTextArea textArea;
    private Scramble scramble;
    private JTextPane scrambleSeq;
    private JSlider slider;
    private JSpinner spinner;
    private TimeFormat timeFormat;
    private Encourage msg;
    private boolean bksp;
    
    public CFrame() {
        super("Cubing App");
        numTurns = 20;                  // Sets default scramble length
        running = false;                // Keeps track of whether space should start or stop timer
        press = true;                   // Used to reject multiple events from holding a key down
        bksp = false;                   // Stops a new encouraging message when pressing backspace
        count = 0;
        timeFormat = new TimeFormat(avg);
        scramble = new Scramble();              // Creates new scramble object from Scramble class
        scores = new ArrayList<Double>();                   // Keeps track of scores
        try {msg = new Encourage();}                        // Encouragement class gives random messages
        catch (FileNotFoundException e) {}
        KeyHandler keyHandler = new KeyHandler();           // Handles keyboard events
        SlideHandler slideHandler = new SlideHandler();     // Handles slider events
        MouseHandler mouseHandler = new MouseHandler();     // Handles mouse events
        SpinHandler spinHandler = new SpinHandler();        // Handles spinner events
        ButtonHandler buttonHandler = new ButtonHandler();  // Handles button events
        this.addKeyListener(keyHandler);                    // Enables keyboard event handling
        
        /**
         * Main panel that the three subpanels go onto
         */
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);
        
        /**
         * 1st Main Subpanel. Deals with DISPLAYING THE SCRAMBLE
         */
        JPanel scramblePanel = new JPanel();
        scramblePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        contentPane.add(scramblePanel, BorderLayout.NORTH);
        scramblePanel.setLayout(new BorderLayout(0, 0));
        
        Component verticalStrut = Box.createVerticalStrut(10);
        scramblePanel.add(verticalStrut, BorderLayout.NORTH);
        Component horizontalStrut = Box.createHorizontalStrut(1);
        scramblePanel.add(horizontalStrut, BorderLayout.WEST);
        Component horizontalStrut_1 = Box.createHorizontalStrut(1);
        scramblePanel.add(horizontalStrut_1, BorderLayout.EAST);
        Component verticalStrut_1 = Box.createVerticalStrut(10);
        scramblePanel.add(verticalStrut_1, BorderLayout.SOUTH);
        
        scrambleSeq = new JTextPane();
        scrambleSeq.setEditable(false);
        scrambleSeq.setFont(new Font ("", Font.PLAIN, 18));
        scrambleSeq.setHighlighter(null);
        scrambleSeq.setEditable(false);
        scrambleSeq.setOpaque(false);
        scrambleSeq.setFocusable(false);
        scrambleSeq.addMouseListener(mouseHandler);     // Enables mouse event handling
        StyledDocument doc = scrambleSeq.getStyledDocument();
        // Aligns the JTextPane to the center
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        scramblePanel.add(scrambleSeq, BorderLayout.CENTER);
        displayNewScramble();
        
        /**
         * 2nd Main Subpanel. Deals with the SCORES PANEL
         */
        JPanel txtAreaPanel = new JPanel();
        contentPane.add(txtAreaPanel, BorderLayout.EAST);
        txtAreaPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        txtAreaPanel.setLayout(new BorderLayout(0, 5));
        
        Component horizontalStrut_4 = Box.createHorizontalStrut(10);
        txtAreaPanel.add(horizontalStrut_4, BorderLayout.WEST);
        Component horizontalStrut_6 = Box.createHorizontalStrut(10);
        txtAreaPanel.add(horizontalStrut_6, BorderLayout.EAST);
        
        // avgPanel contains the average time
        JPanel avgPanel = new JPanel();
        txtAreaPanel.add(avgPanel, BorderLayout.SOUTH);
        avgPanel.setLayout(new BorderLayout(5, 0));
        Component verticalStrut_5 = Box.createVerticalStrut(5);
        avgPanel.add(verticalStrut_5, BorderLayout.SOUTH);
        
        avgLabel = "AVG: --.--";
        infoLabel = new JLabel(avgLabel);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.addMouseListener(mouseHandler);
        font = infoLabel.getFont();     // Used to make avgLabel bold on mouse enter
        avgPanel.add(infoLabel, BorderLayout.CENTER);
        
        // Times Panel contains the discription label timesLabel
        JPanel timesPanel = new JPanel();
        timesPanel.setLayout(new BorderLayout(0, 0));
        txtAreaPanel.add(timesPanel, BorderLayout.NORTH);
        
        JLabel timesLabel = new JLabel("Times");
        timesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timesPanel.add(timesLabel);
        
        Component verticalStrut_2 = Box.createVerticalStrut(5);
        timesPanel.add(verticalStrut_2, BorderLayout.NORTH);
        
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 17));
        textArea.setColumns(8);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setMaximumSize(textArea.getPreferredSize());
        txtAreaPanel.add(textArea, BorderLayout.CENTER);
        
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        txtAreaPanel.add(scroll);
        
        /**
         * 3rd Main Subpanel. Deals with the TIMER and ADJUSTMENTS
         */
        JPanel aTPanel = new JPanel();
        contentPane.add(aTPanel, BorderLayout.CENTER);
        aTPanel.setBorder(null);
        aTPanel.setLayout(new BorderLayout(5, 5));
        
        JPanel timerPanel = new JPanel();
        timerPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        aTPanel.add(timerPanel, BorderLayout.CENTER);
        timerPanel.setLayout(new BorderLayout(5, 5));
        
        timerLabel = new JLabel("0");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setFont(new Font("Tahoma", Font.PLAIN, 105));
        timerPanel.add(timerLabel);
        
        JPanel adjustPanel = new JPanel();
        aTPanel.add(adjustPanel, BorderLayout.SOUTH);
        adjustPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        adjustPanel.setLayout(new BorderLayout(5, 5));
        
        JPanel spinnerPanel = new JPanel();
        adjustPanel.add(spinnerPanel, BorderLayout.WEST);
        spinnerPanel.setLayout(new BorderLayout(5, 5));
        
        JLabel label_1 = new JLabel("Scramble size:");
        spinnerPanel.add(label_1);
        
        Component horizontalStrut_10 = Box.createHorizontalStrut(5);
        spinnerPanel.add(horizontalStrut_10, BorderLayout.WEST);
        
        spinner = new JSpinner();
        spinner.setModel(new SpinnerNumberModel(numTurns, 1, 50, 1));
        //Next three lines prevent interacting with the spinner text field
        ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
        ((DefaultEditor) spinner.getEditor()).getTextField().setFocusable(false);
        ((DefaultEditor) spinner.getEditor()).getTextField().setHighlighter(null);
        spinnerPanel.add(spinner, BorderLayout.EAST);
        spinner.addChangeListener(spinHandler);
        
        Component verticalStrut_3 = Box.createVerticalStrut(10);
        spinnerPanel.add(verticalStrut_3, BorderLayout.NORTH);
        Component verticalStrut_4 = Box.createVerticalStrut(10);
        spinnerPanel.add(verticalStrut_4, BorderLayout.SOUTH);
        
        JPanel sliderPanel = new JPanel();
        adjustPanel.add(sliderPanel, BorderLayout.CENTER);
        sliderPanel.setLayout(new BorderLayout(5, 5));
        
        // Slider ranges from (font size) 5 to 50. Default is 18
        slider = new JSlider();
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setMinimum(5);
        slider.setMaximum(50);
        slider.setValue(18);
        slider.setFocusable(false);
        slider.addChangeListener(slideHandler);     // Allows slider event handling
        sliderPanel.add(slider, BorderLayout.CENTER);
        
        JLabel fontSize = new JLabel("  Font size:");
        sliderPanel.add(fontSize, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel();
        sliderPanel.add(buttonPanel, BorderLayout.EAST);
        buttonPanel.setLayout(new BorderLayout(0, 0));
        
        JButton button = new JButton("i");
        button.setFocusable(false);
        button.setFont(new Font("serif", Font.ITALIC, 15));
        buttonPanel.add(button);
        button.addActionListener(buttonHandler);    // Allows button press event handling
        
        Component verticalStrut_7 = Box.createVerticalStrut(10);
        buttonPanel.add(verticalStrut_7, BorderLayout.NORTH);
        Component verticalStrut_9 = Box.createVerticalStrut(10);
        buttonPanel.add(verticalStrut_9, BorderLayout.SOUTH);
        Component horizontalStrut_7 = Box.createHorizontalStrut(5);
        buttonPanel.add(horizontalStrut_7, BorderLayout.EAST);
    }
    
    // Gets a new scramble and updates the label
    private void displayNewScramble() {
        scrambleSeq.setText(String.format("%s", scramble.getScramble(numTurns)));
    }
    
    // Cycles avgLabel 
    private void updateLabel() {
        switch(count) {
            case 1:
                if (scores.size() >= 5) {
                    System.out.println("entered");
                    double lowest = scores.get(scores.size() - 5);
                    double highest = scores.get(scores.size() - 5);
                    double total = 0;
                    for (int i = scores.size() - 5; i < scores.size(); i++) {
                        if (scores.get(i) < lowest) {
                            lowest = scores.get(i);
                        }
                        if (scores.get(i) > highest) {
                            highest = scores.get(i);
                        }
                        total += scores.get(i);
                    }
                    total = total - lowest - highest;
                    infoLabel.setText("Ao5: " + check60(total/3));
                } else {
                    infoLabel.setText("Ao5: --.--");
                }
                break;
            case 2:
                if (scores.size() >= 12) {
                    System.out.println("entered");
                    double lowest = scores.get(scores.size() - 12);
                    double highest = scores.get(scores.size() - 12);
                    double total = 0;
                    for (int i = scores.size() - 12; i < scores.size(); i++) {
                        if (scores.get(i) < lowest) {
                            lowest = scores.get(i);
                        }
                        if (scores.get(i) > highest) {
                            highest = scores.get(i);
                        }
                        total += scores.get(i);
                    }
                    total = total - lowest - highest;
                    infoLabel.setText("Ao12: " + check60(total/10));
                } else {
                    infoLabel.setText("Ao12: --.--");
                }
                break;
            case 3:
                // Displays the lowest time on the scoreboard
                if (scores.size() > 0) {
                    double lowest = scores.get(0);
                    for (int i = 1; i < scores.size(); i++) {
                        if (scores.get(i) < lowest) {
                            lowest = scores.get(i);
                        }
                    }
                    infoLabel.setText("Best: " + check60(lowest));
                } else {
                    infoLabel.setText("Best: --.--");
                }
                break;
            case 4:
                // Gives an encouraging message after each solve
                if (scores.size() > 0 && !bksp) {
                    infoLabel.setText(msg.getMSG());
                } else {
                    infoLabel.setText("Solve!");
                }
                bksp = false;
                break;
            default:
                if (scores.size() > 0) {
                    infoLabel.setText(avgLabel);
                } else {
                    infoLabel.setText("AVG: --.--");
                }
                count = 0;
                break;
        }
    }
    
    private String check60(double  time) {
        if  (time >= 60) {
            return timeFormat.toMinutes(time);
        } else  {
            return timeFormat.toSeconds(time);
        }
    }
    
    // Nested class that handles the event fired when keys are pressed
    // SPACE Starts/stops timer. R resets everything. BACKSPACE removes last time from the board
    private class KeyHandler implements KeyListener, ActionListener{

        public void actionPerformed(ActionEvent e){
            long time = (System.currentTimeMillis() - startTime) / 1000;
            timerLabel.setText("" + time);
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE){   //Timer only works with the space key
                if (press) {                            // Prevents bugs with holding down space
                    //stops timer if timer is running and displays the stopped time
                    if (running) {
                        scrambleSeq.setText(scramble.getScramble(numTurns));
                        timerLabel.setForeground(Color.BLACK);
                        timer.stop();
                        long endTime = e.getWhen();
                        double seconds = (endTime - startTime) / 1000.0;
                        seconds = Math.round(seconds * 100.0) / 100.0;      // Rounds to 2 decimals
                        
                        timerLabel.setText(check60(seconds));
                        textArea.append(timeFormat.toMinutes(seconds) + "\n");
                        
                        avg = 0;
                        scores.add(Double.parseDouble(timeFormat.toSeconds(seconds)));
                        for (int i = 0; i < scores.size(); i++) {avg += scores.get(i);} // totals the values in arraylist
                        avg /= scores.size();   // divides the totals by the number of elements
                        avgLabel = "AVG: " + check60(avg);
                        updateLabel();
                } else {
                    //turns the text red in preparation for beginning the timer if the timer is not running
                    timerLabel.setForeground(Color.RED);
                    timerLabel.setText("0");
                    running = false;
                }
                    press = false;
                }
            }
            // Optional reset if "R" is pressed
            if (e.getKeyCode() == KeyEvent.VK_R) {
                timerLabel.setForeground(Color.BLACK);
                if (running) {
                    timer.stop();
                }
                running = false;
                timerLabel.setText("0");
                // Clears the scores area
                textArea.setText("");
                scores.clear();
                
                updateLabel();
                displayNewScramble();
            }
            
            // Removes last score from list if backspace pressed
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && scores.size() > 0 && !running) {
                if (scores.size() > 0) {
                    avg = 0; 
                    scores.remove(scores.size() - 1);
                    // Recalculates the average and reconstructs the text area
                    textArea.setText("");
                    for (int i = 0; i < scores.size(); i++) {
                        avg += scores.get(i);
                        textArea.append(timeFormat.toMinutes(scores.get(i)) + "\n");
                    }
                    avg /= scores.size();   // divides the totals by the number of elements
                    avgLabel = "AVG: " + check60(avg);
                    bksp = true;
                    updateLabel();
                }
            }
        }

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (!running) {
                    timerLabel.setForeground(new Color(10,170,110)); //turns the numbers green when the timer is running
                    running = true;
                    startTime = e.getWhen();    //sets startTime as a value that can be compared with endTime to get total time elapsed.
                    
                    if (timer == null) {        //creates a new timer object if it is null
                        timer = new Timer(100, this);
                        timer.start();
                    } else {
                        timerLabel.setText("0");
                        timer.restart();
                    }
                } else {
                    running = false;
                }
                press = true;    // Allows a press only if the space has been released
            }
        }
        public void keyTyped(KeyEvent e) {}
    }
    // Nested class that handles the event fired when the spinner value changes.
    // Increases the scramble size
    private class SpinHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            numTurns = (int)spinner.getValue();
            displayNewScramble();
            requestFocus();
        }
    }
    // Nested class that handles the events fired during mouse use. Clicking on the scramble will
    // generate a new scramble
    private class MouseHandler implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == scrambleSeq) {
                displayNewScramble();
            } else if (e.getSource() == infoLabel) {
                count++;
                updateLabel();
            }
        }

        public void mouseEntered(MouseEvent e) {
            if (e.getSource() == scrambleSeq) {
                scrambleSeq.setForeground(Color.GRAY);
            } else if (e.getSource() == infoLabel) {
                infoLabel.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
            }
        }

        public void mouseExited(MouseEvent e) {
            if (e.getSource() == scrambleSeq) {
                scrambleSeq.setForeground(Color.BLACK);
            } else if (e.getSource() == infoLabel) {
                infoLabel.setFont(new Font(font.getFontName(), Font.PLAIN, font.getSize()));
            }
        }

        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
    }
    // Nested class that handles the event fired when the slider is moved
    // Changes the font size of the scramble from 5-50
    private class SlideHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            scrambleSeq.setFont(new Font("", Font.PLAIN, slider.getValue()));
        }
    }
    // Nested class that handles the event fired when the button is pressed
    // Gives info for the app
    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JOptionPane dialog = new JOptionPane();
            JOptionPane.showMessageDialog(dialog,
                    "Companion app for speed cubers.\n"
                            + "Press SPACE to start and stop the timer.\n"
                            + "The time is added to the scoreboard to the right\n"
                            + "Click the scramble to generate a new scramble.\n"
                            + "Click the text under the score area to cycle messages.\n"
                            + "Press BACKSPACE to remove the last entry.\n"
                            + "Press R to reset everything.",
                            "Application Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
