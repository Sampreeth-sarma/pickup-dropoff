import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;

public class VisualiseMovement extends JFrame implements MouseListener
{
    static JPanel [][] squares;
    public VisualiseMovement() 
    {
        Container c = getContentPane();
        c.setLayout(new GridLayout(5,5, 1 , 1)); 
        squares = new JPanel[5][5];
        //int presentstate=0;
        for(int i=0; i<5; i++)
        {
            for(int j=0; j<5; j++)
            {
                squares[i][j] = new JPanel();
                if((i==0&&j==0)||(i==2&&j==2)||(i==4&&j==4)||(i==3&&j==0))
                {
                  JLabel l = new JLabel("pickup");
                   squares[i][j].setBackground(Color.GREEN);
                   squares[i][j].add(l);
                } 
                    else if((i==3&&j==3)||(i==4&&j==0))
                    {
                    	JLabel l = new JLabel("dropoff");
                        squares[i][j].setBackground(Color.RED);
                        squares[i][j].add(l);
                    }
                    else
                    squares[i][j].setBackground(Color.white);
                squares[i][j].addMouseListener(this);
                c.add(squares[i][j]);
            }           
        }
        
    }
    
    /*public void addPiece(String piece_name,int i, int j)
    {
        Icon piece_icon = new ImageIcon(getClass().getResource(piece_name));
        squares[i][j].add((JComponent)piece_icon);
    }*/
    
    public void mouseClicked(MouseEvent e){ } 
    public void mouseEntered(MouseEvent e){ } 
    public void mouseExited(MouseEvent e) { } 
    public void mousePressed(MouseEvent e) { } 
    public void mouseReleased(MouseEvent e) { } 
    public static void move(int turns) throws AWTException, IOException, InterruptedException
    {
    	File file1 = new File("presenstate"+turns+".csv");
    	String line;
    	long time_press=System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(file1));
        while((line = br.readLine()) != null)
        {
//        	if(System.currentTimeMillis()<time_press+1)
//        	{
        	int presentstate=Integer.parseInt(line);
        	presentstate -=1;
        	squares[presentstate/5][presentstate%5].setBackground(Color.BLACK);
    	Thread.sleep(1);
    	if((presentstate/5==0&&presentstate%5==0)||(presentstate/5==2&&presentstate%5==2)||(presentstate/5==4&&presentstate%5==4)||(presentstate/5==3&&presentstate%5==0))
        	squares[presentstate/5][presentstate%5].setBackground(Color.GREEN);
        	else if((presentstate/5==3&&presentstate%5==3)||(presentstate/5==4&&presentstate%5==0))
        		squares[presentstate/5][presentstate%5].setBackground(Color.RED);
        	else
        		squares[presentstate/5][presentstate%5].setBackground(Color.WHITE);
//    	time_press+=1;
//        }
        }
    }
    public static void visualise(int turns) throws AWTException, IOException, InterruptedException
    {
    	VisualiseMovement test = new VisualiseMovement();
        //test.addPiece("king.bmp",0,0);
        test.setSize(300,300);
        test.setResizable(false);
        //test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        test.setVisible(true);
        move(turns);
    }
    
}