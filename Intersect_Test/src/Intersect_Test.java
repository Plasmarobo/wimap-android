import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;

import com.witech.wimap.Intersect;
import com.witech.wimap.RadialDistance;


public class Intersect_Test extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 345430684452430485L;
	private static List<RadialDistance> l;
	private static Intersect i;
	final static int maxCharHeight = 15;
    final static int minFontSize = 6;
 
    final static Color bg = Color.white;
    final static Color fg = Color.black;
    final static Color red = Color.red;
    final static Color white = Color.white;
 
    final static BasicStroke stroke = new BasicStroke(2.0f);
    final static BasicStroke wideStroke = new BasicStroke(8.0f);
 
    final static float dash1[] = {10.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f, 
                                                      BasicStroke.CAP_BUTT, 
                                                      BasicStroke.JOIN_MITER, 
                                                      10.0f, dash1, 0.0f);
    Dimension totalSize;
    FontMetrics fontMetrics;
 
    public void init() {
        //Initialize drawing colors
        setBackground(bg);
        setForeground(fg);
    }
 
    FontMetrics pickFont(Graphics2D g2,
                         String longString,
                         int xSpace) {
        boolean fontFits = false;
        Font font = g2.getFont();
        FontMetrics fontMetrics = g2.getFontMetrics();
        int size = font.getSize();
        String name = font.getName();
        int style = font.getStyle();
 
        while ( !fontFits ) {
            if ( (fontMetrics.getHeight() <= maxCharHeight)
                 && (fontMetrics.stringWidth(longString) <= xSpace) ) {
                fontFits = true;
            }
            else {
                if ( size <= minFontSize ) {
                    fontFits = true;
                }
                else {
                    g2.setFont(font = new Font(name,
                                               style,
                                               --size));
                    fontMetrics = g2.getFontMetrics();
                }
            }
        }
 
        return fontMetrics;
    }
 
    public void paint(Graphics g) {
        //Graphics2D outpanel = (Graphics2D) g;
        BufferedImage bImg = new BufferedImage(this.getWidth(), this.getWidth(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bImg.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension d = getSize();
        l = new ArrayList<RadialDistance>(5);
        for(int v = 0; v < 2; ++v)
        {
        	g2.setPaint (bg);
            g2.fillRect ( 0, 0, bImg.getWidth(), bImg.getHeight() );
        	int size = (int) (Math.random()*12)+3;
        	l.clear();
        	for(int i = 0; i < size; ++i)
        	{
        		l.add(new RadialDistance(Math.random()*512, Math.random()*512, 128, 10+(Math.random())*246));
        	}
        
        
        
        	double avg_x = 0.0;
        	double avg_y = 0.0;
        	double avg_z = 0.0;
        	for(int j = 0; j < l.size(); ++j)
        	{
        		g2.setColor(new Color((float)l.get(j).GetDistance()/256, (float).5 ,(float).5));
        		g2.drawOval((int)l.get(j).GetX()-(int)l.get(j).GetDistance()/2, (int)l.get(j).GetY()-(int)l.get(j).GetDistance()/2, (int)l.get(j).GetDistance(), (int)l.get(j).GetDistance());
        		g2.setColor(fg);
        		g2.drawOval((int)l.get(j).GetX()-1, (int)l.get(j).GetY()-1, 2, 2);
        		avg_x += l.get(j).GetX();
        		avg_y += l.get(j).GetY();
        		avg_z += l.get(j).GetZ();
        	}
        	avg_x/=l.size();
        	avg_y/=l.size();
        	avg_z/=l.size();
        	i = new Intersect(l, avg_x, avg_y, avg_z);
        	g2.setColor(Color.green);
        	g2.fillOval((int)i.GetX()-5, (int)i.GetY()-5, 10, 10);
        	System.out.println("X: " + i.GetX() + " Y: " + i.GetY());
        	save(bImg, "RandomTest" + v + ".png");
        	g.drawImage(bImg, 0, 0, null);
        	
        	try {
        		Thread.sleep(500);
        	} catch (InterruptedException e) {
			// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        }
        l.clear();
        l.add(new RadialDistance(0,0,128,550));
        l.add(new RadialDistance(512,0,128,550));
        l.add(new RadialDistance(0,512,128,550));
        l.add(new RadialDistance(512,512,128,550));
        g2.setPaint (bg);
        g2.fillRect ( 0, 0, bImg.getWidth(), bImg.getHeight() );
       
        for(int j = 0; j < l.size(); ++j)
        {
        	g2.setColor(new Color((float)l.get(j).GetDistance()/550, (float).5 ,(float)0.5));
        	g2.drawOval((int)l.get(j).GetX()-(int)l.get(j).GetDistance()/2, (int)l.get(j).GetY()-(int)l.get(j).GetDistance()/2, (int)l.get(j).GetDistance(), (int)l.get(j).GetDistance());
        	g2.setColor(fg);
        	g2.drawOval((int)l.get(j).GetX()-1, (int)l.get(j).GetY()-1, 2, 2);
        	
        }
        
        i = new Intersect(l,200, 200, 128);
        g2.setColor(Color.green);
        g2.fillOval((int)i.GetX()-5, (int)i.GetY()-5, 10, 10);
        System.out.println("X: " + i.GetX() + " Y: " + i.GetY());
        save(bImg, "FixedTest.png");
        g.drawImage(bImg, 0, 0, null);
        try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        l.clear();
        l.add(new RadialDistance(475060,1096300,4670,5940.893));
        l.add(new RadialDistance(481500,1094900,4694,2420.883));
        l.add(new RadialDistance(482230,1088430,4831,5087.666));
        l.add(new RadialDistance(478050,1087810,4775,5545.271));
        l.add(new RadialDistance(471430,1088580,4752,9643.044));
        l.add(new RadialDistance(468720,1091240,4803, 11417.270));
        l.add(new RadialDistance(467400,1093980,4705, 12638.110));
        l.add(new RadialDistance(468730,1097340,4747, 12077.030));
        g2.setPaint (bg);
        g2.fillRect ( 0, 0, bImg.getWidth(), bImg.getHeight() );
       
        for(int j = 0; j < l.size(); ++j)
        {
        	g2.setColor(new Color((float)l.get(j).GetDistance()/13000, (float).5 ,(float)0.5));
        	g2.drawOval((int)l.get(j).GetX()-(int)l.get(j).GetDistance()/2, (int)l.get(j).GetY()-(int)l.get(j).GetDistance()/2, (int)l.get(j).GetDistance(), (int)l.get(j).GetDistance());
        	g2.setColor(fg);
        	g2.drawOval((int)l.get(j).GetX()-1, (int)l.get(j).GetY()-1, 2, 2);
        	
        }
        
        i = new Intersect(l,200, 200, 128);
        g2.setColor(Color.green);
        g2.fillOval((int)i.GetX()-5, (int)i.GetY()-5, 10, 10);
        System.out.println("X: " + i.GetX() + " Y: " + i.GetY());
        save(bImg, "FixedTest.png");
        g.drawImage(bImg, 0, 0, null);
        try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.exit(0);
    }
    public void save(BufferedImage bImg, String file)
    {
        try {
                if (ImageIO.write(bImg, "png", new File(file)))
                {
                    System.out.println("-- saved");
                }
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
    }
 
    public static void main(String s[]) {
        JFrame f = new JFrame("Plot Intersect");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        JApplet applet = new Intersect_Test();
        f.getContentPane().add("Center", applet);
        applet.init();
        f.pack();
        f.setSize(new Dimension(512,512));
        f.setVisible(true);
    }
}
