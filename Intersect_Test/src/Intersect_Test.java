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
        Graphics2D outpanel = (Graphics2D) g;
        BufferedImage bImg = new BufferedImage(this.getWidth(), this.getWidth(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension d = getSize();
        l = new ArrayList<RadialDistance>(5);
        int size = (int) (Math.random()*12)+3;
        for(int i = 0; i < size; ++i)
        {
        	l.add(new RadialDistance(Math.random()*1024, Math.random()*1024, 128, (Math.random()+1)*100));
        }
        
        
        Color fg3D = Color.lightGray;
 
        g2.setPaint(fg3D);
        g2.draw3DRect(0, 0, d.width - 1, d.height - 1, true);
        g2.draw3DRect(3, 3, d.width - 7, d.height - 7, false);
        g2.setPaint(fg);
 
        for(int j = 0; j < l.size(); ++j)
        {
        	g2.drawOval((int)l.get(j).GetX()-(int)l.get(j).GetDistance()/2, (int)l.get(j).GetY()-(int)l.get(j).GetDistance()/2, (int)l.get(j).GetDistance(), (int)l.get(j).GetDistance());
        	g2.drawOval((int)l.get(j).GetX()+1, (int)l.get(j).GetY()+1, 2, 2);
        }
        i = new Intersect(l);
        g2.setColor(red);
        g2.drawOval((int)i.GetX()+1, (int)i.GetY()+1, 2, 2);
        save(bImg, "RandomText.png");
        
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
        f.setSize(new Dimension(1024,1024));
        f.setVisible(true);
    }
}
