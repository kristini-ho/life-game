import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Scanner;
import java.io.*;

public class LifeGUI extends Life implements MouseListener, MouseMotionListener{
    JFrame window;
    JSlider slider;
    JButton clear, run;
    JRadioButton red, blue, green, custom, singleStep, continuous;   
    ButtonGroup colors;
    ButtonGroup mode;
    //arrays for drawing, opening diff size images, & saving opened img with drawing on top
    Color [][] array, otherArray, toSave;        
    Color color; 
    DrawPanel panel;
    JMenuBar menuBar;
    JMenu file;
    JMenu edit;
    JMenuItem clear2; 
    JMenuItem open;
    JMenuItem save;
    JFileChooser fileChooser;
    Scanner scanner;
    FileWriter writer;
    int square, cellSize, generation; //square is width of one color in pixels
    int myRow, myCol; //number of colors vertically and horizontally
    javax.swing.Timer timer;
    
    public void openWindow(){ 
        //Initialize some starting variables
        generation = 0;
        cellSize = square = 25;       
        color = Color.RED;
        myRow = myCol = 20; 
        array = new Color[21][21]; 
        otherArray = array.clone();   
        
        //Make the drawing grid, clear button, run button, slider
        panel = new DrawPanel();        
        clear = new JButton("Clear");  
        panel.setBounds(2, 30, 501, 501);
        clear.setBounds(200, 580, 100, 25);  
        run = new JButton("Run");
        run.setBounds(310, 580, 100, 25);
        slider = new JSlider(100, 850, 600);
        slider.setBounds(30, 580, 150, 30);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(25);
        
        //Adding the color selection options, mode options
        singleStep = new JRadioButton("single-step", true);
        continuous = new JRadioButton("continuous");
        mode = new ButtonGroup();
        mode.add(singleStep);
        mode.add(continuous);
        singleStep.setBounds(150, 620, 100, 25);
        continuous.setBounds(250, 620, 100, 25);
        
        red = new JRadioButton("red", true);
        blue = new JRadioButton("blue");
        green = new JRadioButton("green");
        custom = new JRadioButton("custom");
        colors = new ButtonGroup();                        
        colors.add(red);
        colors.add(green);
        colors.add(blue);
        colors.add(custom);
        red.setBounds(70, 550, 60, 15);
        green.setBounds(160, 550, 60, 15);
        blue.setBounds(250, 550, 60, 15);
        custom.setBounds(340, 550, 90, 15);                
        
        //Setting up the menu bar
        menuBar = new JMenuBar();
        file = new JMenu("File");
        open = new JMenuItem("Open");
        save = new JMenuItem("Save");
        edit = new JMenu("Edit");
        clear2 = new JMenuItem("Clear");
        file.add(open);
        file.add(save);
        edit.add(clear2);
        menuBar.add(file);
        menuBar.add(edit);                   
        menuBar.setBounds(0, 0, 650, 25);  
        
        //Adding everything to the JFrame
        window = new JFrame("Generation 0");   
        window.setBounds(200, 30, 510, 680);
        window.add(menuBar);
        window.add(panel);
        window.add(custom);
        window.add(red);
        window.add(green);
        window.add(blue);
        window.add(clear);
        window.add(run);
        window.add(singleStep);
        window.add(continuous);
        window.add(slider);
        
        //Basic properties of JFrame
        window.setLayout(null);
        window.setResizable(false);
        window.setVisible(true); 
        
        //Registering GUI things for listeners
        open.addMouseListener(this);
        save.addMouseListener(this);
        red.addMouseListener(this);
        green.addMouseListener(this);
        blue.addMouseListener(this);
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);
        clear2.addMouseListener(this);
        clear.addMouseListener(this);
        custom.addMouseListener(this);
        run.addMouseListener(this);
        slider.addChangeListener(new Speed());
        
        //Making the initial background white
        for(int r = 1; r < 21; r++){
                for(int c = 1; c < 21; c++){
                    array[r][c] = Color.WHITE;                
                }                                    
        } 
        
        //Painting it up since the white array is ready
        panel.repaint();
        
        //Make a timer
        timer = new javax.swing.Timer(300, new CheckTime());
    }   
    public void mouseDragged(MouseEvent e){       
        if(e.getY() < 500 && e.getX() < 500 && e.getY() > 0 && e.getX() > 0){
           int row = (e.getY()+cellSize) / cellSize;  
           int col = (e.getX()+cellSize) / cellSize; 
           if((e.getModifiersEx() & e.BUTTON3_DOWN_MASK) == e.BUTTON3_DOWN_MASK){
                array[row][col] = Color.WHITE;
           }
           else array[row][col] = color;      
           panel.repaint(); 
        }                     
    }  
    public void mouseMoved(MouseEvent e){        
    }   
    public void mouseClicked(MouseEvent e){    
        if(e.getSource() == run){
            run();
        }
    }
    public void mouseEntered(MouseEvent e){        
    }
    public void mouseExited(MouseEvent e){        
    }
    public void mousePressed(MouseEvent e){  
        if(e.getSource() == panel && e.getY() < 500 && e.getX() < 500 && e.getY() > 0 && e.getX() > 0){
           int row = (e.getY()+cellSize) / cellSize;  
           int col = (e.getX()+cellSize) / cellSize; 
           if((e.getModifiersEx() & e.BUTTON3_DOWN_MASK) == e.BUTTON3_DOWN_MASK){
                array[row][col] = Color.WHITE;
           }
           else array[row][col] = color;      
           panel.repaint(); 
        }     
        if(e.getSource() == custom){
            color = JColorChooser.showDialog(null, "Color chooser", Color.WHITE); 
            if(color == Color.WHITE) color = new Color(255, 255, 254); 
            //I use WHITE as empty spot. Change value slightly when user chooses custom color white.
        }
        if(e.getSource() == open){
            fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(null);
            boolean none = false;
            try{               
                scanner = new Scanner(fileChooser.getSelectedFile());  
            }catch(IOException o){               
                System.out.println(o.getMessage());
            }
            catch(NullPointerException b){  
                none = true;
            }
            try{
                if(none == false && scanner.next().equals("P3")){                   
                    while(scanner.hasNext("#")) scanner.nextLine(); 
                    myCol = scanner.nextInt();                                
                    myRow = scanner.nextInt();
                    otherArray = new Color[myRow+1][myCol+1];
                    if(myRow > myCol) square = 500 / myRow;  //the size of an individual color-unit in pixels
                    else square = 500 / myCol;
                    while(scanner.hasNext("#")) scanner.nextLine();                     
                    if(scanner.nextInt() == 255){
                        while(scanner.hasNext("#")) scanner.nextLine();                         
                        for(int r = 1; r <= myRow; r++){
                             for(int c = 1; c <= myCol; c++){
                                    otherArray[r][c] = new Color(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());                             
                             }  
                             while(scanner.hasNext("#")) scanner.nextLine(); 
                        }                    
                    }
                    //make sure opening a file means drawn stuff is gone 
                    for(int r = 1; r <= 20; r++){                   
                        for(int c = 1; c <= 20; c++)
                            array[r][c] = Color.WHITE;                                                                                   
                    } 
                    panel.repaint(); 
                }    
            }catch(NullPointerException o){               
                System.out.println(o.getMessage());
            }                                         
        }
        if(e.getSource() == save){
            //copying image into toSave 2D array
            toSave = new Color[otherArray.length][otherArray[0].length];
            for(int r = 0; r < otherArray.length; r++){ 
                for(int c = 0; c < otherArray[0].length; c++){
                    toSave[r][c] = otherArray[r][c];
                }                
            }
            //replace parts of the image with whatever the person drew
            for(int row = 1; row <= 20; row++){
                    for(int col = 1; col <= 20; col++){                        
                        if(array[row][col] != Color.WHITE){                            
                            int picCol = col*square - square + 1;
                            int picRow = row*square - square + 1;
                            int r, c;
                            for(r = picRow; r < (picRow + square); r++){
                                for(c = picCol; c < (picCol + square); c++)
                                    toSave[r][c] = array[row][col];                                
                            } 
                        }
                    }                                    
             }             
             try{
                    writer = new FileWriter("artwork.ppm");
                    writer.write("P3\n", 0, 3);                                                          
                    String d = myCol + " " + myRow + "\n";                                         
                    writer.write(d, 0, d.length());
                    writer.write("255\n", 0, 4);                                        
                    writer.flush();
                    for(int row = 1; row <= myRow; row++){
                        for(int col = 1; col <= myCol; col++){
                            String b = "";
                            b += toSave[row][col].getRed() + " " + toSave[row][col].getGreen() + 
                                  " " + toSave[row][col].getBlue() + " ";   //saving RGB values                        
                            writer.write(b, 0, b.length());
                            writer.flush();
                        }    
                        if(row < myRow) writer.write("\n", 0, 1); 
                    }                        
                    writer.flush();   
            }catch(IOException f){
                System.out.println(f.getMessage());
            }
        }                    
        
        if(e.getSource() == red)color = Color.RED; 
        if(e.getSource() == green)color = Color.GREEN;
        if(e.getSource() == blue)color = Color.BLUE;  
        if(e.getSource() == clear || e.getSource() == clear2){
            stop();
            for(int r = 1; r <= myRow; r++){
                for(int c = 1; c <= myCol; c++){
                    otherArray[r][c] = Color.WHITE;                
                }                                    
            } 
            for(int r = 1; r <= 20; r++){
                for(int c = 1; c <= 20; c++){
                    array[r][c] = Color.WHITE;                
                }                                    
            } 
            panel.repaint();            
        }          
    }
    public void mouseReleased(MouseEvent e){          
    }
    public void run(){
        if(singleStep.isSelected()){
            generation++;
            window.setTitle("Generation " + generation);
            runLife(array, color);
            panel.repaint();
        }
        else timer.start();          
    }
    public void stop(){
        timer.stop();
        generation = 0;
        window.setTitle("Generation " + generation);
    }
    private class Speed implements ChangeListener{
        public void stateChanged(ChangeEvent e){
            timer.setDelay(900-slider.getValue());                                   
        }        
    }
    private class CheckTime implements ActionListener{
        public void actionPerformed(ActionEvent e){
            generation++;
            window.setTitle("Generation " + generation);
            runLife(array, color); 
            panel.repaint();
            if(singleStep.isSelected()) timer.stop();
        }        
    }
    private class DrawPanel extends JPanel{
        public void paintComponent (Graphics g){
               //draw colors of some image you opened              
               for(int row = 1; row <= myRow; row++){
                    for(int col = 1; col <= myCol; col++){
                        g.setColor( otherArray[row][col] );
                        g.fillRect(col * square - square+1, row * square - square+1, square, square);                                        
                    }                                    
               } 
               //draw colors of stuff you drew
               for(int row = 1; row <= 20; row++){
                    for(int col = 1; col <= 20; col++){
                        g.setColor( array[row][col] );
                        if(array[row][col] != Color.WHITE) 
                            g.fillRect(col * cellSize - (cellSize - 1), row * cellSize - (cellSize - 1), cellSize, cellSize);                              
                    }                                    
               }                     
                                          
                //draw gridlines            
                for(int row = 1; row < 21; row++){
                    for(int col = 1; col < 21; col++){
                        g.setColor( Color.BLACK );
                        g.drawRect(col * cellSize - cellSize, row * cellSize - cellSize, cellSize, cellSize);                                     
                    }                                    
                }               
                
         }
    }
    public static void main  (String[]args){
        LifeGUI b = new LifeGUI();
        b.openWindow();
    }        
}