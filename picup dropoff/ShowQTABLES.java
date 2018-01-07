import java.io.*;
import javax.swing.*; 
import java.awt.GridLayout; 
import java.util.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.Color;
 
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.net.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class ShowQTABLES extends JFrame
{ 

    static class MainFrame extends JPanel
    {
        MainFrame(BorderLayout b)
        {
            super(b);
        }

        public void paint(Graphics g)
        {
            super.paint( g );
            g.drawLine(0, 0, 130, 130);
            g.drawLine(130, 0, 0, 130);
            g.drawLine(0, 0, 0, 130);
            g.drawLine(0, 1, 0, 140);
            g.drawLine(130, 0, 130, 130);
            g.drawLine(0, 130, 130, 130);
            g.drawLine(0, 0, 130, 0);
        }
    }




	 int head;



    public static void showGUI() throws AWTException, IOException, InterruptedException
    {
    	int val;
   	 	int head;

    	for(int l=1;l<=8;l++){
    		if(l%2==0)
    			val=2;
    		else
    			val=1;
    		if(l<=2||l==5||l==6)
    			head = 3000;
    		else
    			head = 6000;
	        File file1 = new File(head+"Qtable"+val+"Visual"+l+".csv");
	        BufferedReader br = new BufferedReader(new FileReader(file1));
	        String line = "";
	
	        JFrame frame = new JFrame(head+"Qtable"+val+"Visual"+l);
	        MainFrame[][] world = new MainFrame[6][6];
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	        int i, j;
	        frame.getContentPane();
	        frame.setLayout(new GridLayout(5,5));
	        int count = 0;
	            while((line = br.readLine()) != null&&count++<25)
	            {
	                if(line.equals("N/A")) continue;
	                i = Integer.parseInt(line.split(",")[0]);
	                j = Integer.parseInt(line.split(",")[1]);
	
	                world[i][j] = new MainFrame(new BorderLayout());
	
	                JLabel labelSouth = new JLabel("S " + line.split(",")[5], SwingConstants.CENTER);
	                labelSouth.setFont(new Font("Serif", Font.PLAIN, 13));
	                if(line.split(",")[5].trim().equals("N/A"))
	                {
	                    labelSouth = new JLabel("NA", SwingConstants.CENTER);
	                    labelSouth.setBackground(Color.BLACK);
	                }
	
	                labelSouth.setOpaque(true);
	                world[i][j].add(labelSouth, BorderLayout.SOUTH);
	
	                JLabel labelNorth = new JLabel("N " + line.split(",")[4], SwingConstants.CENTER);
	                labelNorth.setFont(new Font("Serif", Font.PLAIN, 13));
	                if(line.split(",")[4].trim().equals("N/A"))
	                {
	                    labelNorth = new JLabel("NA", SwingConstants.CENTER);
	                    labelNorth.setBackground(Color.BLACK);
	                }
	
	                labelNorth.setOpaque(true);
	                world[i][j].add(labelNorth, BorderLayout.NORTH);
	
	                JLabel labelEast = new JLabel("<html>E<br>"+line.split(",")[2]+"</html>" , SwingConstants.LEFT);
	                labelEast.setFont(new Font("Serif", Font.PLAIN, 13));
	                if(line.split(",")[2].trim().equals("N/A"))
	                {
	                    labelEast = new JLabel("NA", SwingConstants.CENTER);
	                    labelEast.setBackground(Color.BLACK);
	                }
	                labelEast.setOpaque(true);
	                world[i][j].add(labelEast, BorderLayout.EAST);
	
	                JLabel labelWest = new JLabel("<html>W<br>"+line.split(",")[3]+"</html>", SwingConstants.RIGHT);
	                labelWest.setFont(new Font("Serif", Font.PLAIN, 13));
	                if(line.split(",")[3].trim().equals("N/A"))
	                {
	                    labelWest = new JLabel("NA", SwingConstants.CENTER);
	                    labelWest.setBackground(Color.BLACK);
	                }
	                labelWest.setOpaque(true);
	                world[i][j].add(labelWest, BorderLayout.WEST);
	
	                if((i == 1 && j == 1) || (i == 5 && j == 5) || (i == 3 && j == 3) || (i == 4 && j == 1) || (i == 5 && j == 1) || (i == 4 && j ==4))
	                {
	                    JLabel label;
	                    if( (i == 1 && j == 1) || (i == 5 && j == 5) || (i == 3 && j == 3) || (i == 4 && j == 1) )
	                	   label = new JLabel("Pickup", SwingConstants.CENTER);
	                    else
	                        label = new JLabel("Dropoff", SwingConstants.CENTER);
	                    label.setOpaque(true);
	                    label.setBackground(Color.GREEN);
	                	label.setLayout(new BorderLayout());
	                	label.setPreferredSize(new Dimension(20,20));
	                	JLabel label1 = new JLabel(line.split(",")[6], SwingConstants.CENTER);
	                	JLabel label2 = new JLabel(line.split(",")[7], SwingConstants.CENTER);
	
	                	label.add(label1, BorderLayout.NORTH);
	                	label.add(label2, BorderLayout.SOUTH);
	
	                	world[i][j].add(label, BorderLayout.CENTER);
	                }
	
	                world[i][j].setVisible(true);
	                world[i][j].setPreferredSize(new Dimension(130, 130));
	
	                frame.add(world[i][j]);
	
	            }
	
	        frame.setVisible(true);
	        frame.pack();
	
	        Thread.sleep(1000);
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage capture = new Robot().createScreenCapture(new Rectangle(0, 0, 665, 695));
			File file = new File("screeshot2false.png");
			ImageIO.write(capture, "png", file);
	    }
    }




	public static void showQtables() throws AWTException, IOException, InterruptedException
	{ 

		showGUI();


	} 
}