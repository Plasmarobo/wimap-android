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
    
    protected static BufferedImage bImg;
    protected static Graphics2D g2;
 
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
        bImg = new BufferedImage(this.getWidth(), this.getWidth(), BufferedImage.TYPE_INT_RGB);
        g2 = bImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
    protected void drawTest(List<RadialDistance> rl, double max)
    {
    	for(int j = 0; j < l.size(); ++j)
    	{
    		g2.setColor(new Color((float) ((float)l.get(j).GetDistance()/max), (float).5 ,(float).5));
    		g2.drawOval((int)l.get(j).GetX()-(int)l.get(j).GetDistance()/2, (int)l.get(j).GetY()-(int)l.get(j).GetDistance()/2, (int)l.get(j).GetDistance(), (int)l.get(j).GetDistance());
    		g2.setColor(fg);
    		g2.drawOval((int)l.get(j).GetX()-1, (int)l.get(j).GetY()-1, 2, 2);
    	}
    }
    
    protected void finishTest(String name, Intersect i, Graphics g)
    {
    	g2.setColor(Color.green);
    	g2.fillOval((int)i.x-5, (int)i.y-5, 10, 10);
    	System.out.println("X: " + i.x + " Y: " + i.x);
    	save(bImg, name + ".png");
    	g.drawImage(bImg, 0, 0, null);
    }
    protected void newTest(List<RadialDistance> l)
    {
    	g2.setPaint (bg);
    	g2.fillRect ( 0, 0, bImg.getWidth(), bImg.getHeight() );
    	l.clear();
    }
    
 
    public void paint(Graphics g) {
        
        l = new ArrayList<RadialDistance>(5);
        newTest(l);
        l.add(new RadialDistance(475060,1096300,4670,5940.893));
        l.add(new RadialDistance(481500,1094900,4694,2420.883));
        l.add(new RadialDistance(482230,1088430,4831,5087.666));
        l.add(new RadialDistance(478050,1087810,4775,5545.271));
        l.add(new RadialDistance(471430,1088580,4752,9643.044));
        l.add(new RadialDistance(468720,1091240,4803, 11417.270));
        l.add(new RadialDistance(467400,1093980,4705, 12638.110));
        l.add(new RadialDistance(468730,1097340,4747, 12077.030));
        drawTest(l, 13000);
        i = new Intersect(l,200, 200, 128);
        if(Math.abs(i.x-48000) < 100)
        	System.out.println("X within tolerance");
        if(Math.abs(i.y-1093000) < 100)
        	System.out.println("Y within tolerance");
        if(Math.abs(i.z-4535) < 100)
        	System.out.println("Z within tolerance");
        finishTest("datatest", i, g);
        
        newTest(l);
        l.add(new RadialDistance(0,0,128,720));
        l.add(new RadialDistance(512,0,128,720));
        l.add(new RadialDistance(0,512,128,720));
        l.add(new RadialDistance(512,512,128,720));
        drawTest(l, 750);
        i = new Intersect(l,200, 200, 128);
        finishTest("cornertest", i,g);
        
        
        
        for(int v = 0; v < 2; ++v)
        {
        	newTest(l);
        	int size = (int) (Math.random()*12)+3;
        	for(int i = 0; i < size; ++i)
        	{
        		l.add(new RadialDistance(Math.random()*512, Math.random()*512, 128, 10+(Math.random())*246));
        	}
        
        	drawTest(l, 256);
        	
        	i = new Intersect(l, 256, 256, 256);
        	finishTest("randomtest" + v, i, g);
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
