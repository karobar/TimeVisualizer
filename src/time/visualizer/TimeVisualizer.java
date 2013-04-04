
package time.visualizer;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFrame;

public class TimeVisualizer extends JFrame{
    static int height = 0;
    static int width = 0;
    
    static Container myPane;
    
    static int SAND   = 0xFFE5DA94;
    static int RED    = 0xFFAA0000;
    static int GREEN  = 0xFF00AA00;
    static int BLACK  = 0xFF000000;
    
    TimeVisualizer(int requestedWidth, int requestedHeight) {
        super("Time Visualizer");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);	
        setIgnoreRepaint(true);
        pack();
        Insets insets = getInsets();
        
        width = requestedWidth - (insets.left + insets.right);
        height = requestedHeight - (insets.top + insets.bottom);

        setSize(width + insets.left + insets.right, 
                height + insets.top  + insets.bottom); 
        setVisible(true);
        

        //create graphics on the main pane
        myPane = getContentPane();
        myPane.setLayout(null);
    }
    
    public static void main(String[] args) {
        System.out.print("Enter window size: ");
 
	String input = "";
        
        try {
	    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    input = bufferRead.readLine();
	} catch(IOException e) {
            e.printStackTrace();
	}
        
        String[] inputarray = input.split("x");
        
        int requestedWidth = Integer.parseInt(inputarray[0]);
        int requestedHeight = Integer.parseInt(inputarray[1]);
        
        
        
        //====
        System.out.print("Enter number of seconds: ");
        
        try {
	    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    input = bufferRead.readLine();
	} catch(IOException e) {
            e.printStackTrace();
	}
        
        int numberOfSeconds = Integer.parseInt(input);
        //====
        
        TimeVisualizer mainVis = new TimeVisualizer(requestedWidth, requestedHeight);
        
        
        
        Graphics contentGraphics = myPane.getGraphics();
        
        int[] pixelPos = {width,height-1};
        
        int totalNumberOfPixels = width * height;
        
        double pixelsPerSecond = ((double)totalNumberOfPixels) / ((double)numberOfSeconds);
        System.out.println("Pixels per second:" + pixelsPerSecond);
        double secondsPerRow = ((double)width)/pixelsPerSecond;
        System.out.println("Seconds per row:" + secondsPerRow + "(" + secondsPerRow/60 + " minutes)");
        
        double pixelsToRemove = 0;
        long loopStartTime;
        long elapsedTime;
        double elapsedSec;
        double remainderPixels;
        while(true) {
            loopStartTime = System.nanoTime();
            mainVis.render(contentGraphics, pixelPos);
            elapsedTime = System.nanoTime() - loopStartTime;
            elapsedSec = ((double)elapsedTime/1e9);
            //System.out.println("-" + elapsedTime + " seconds: " + elapsedSec);
            pixelsToRemove = pixelsToRemove + (pixelsPerSecond * elapsedSec);
            //System.out.println(pixelsToRemove);
            
            if(pixelsToRemove >= 1.0) {      
                pixelPos = decrement(pixelPos, width, (int)pixelsToRemove);
            }
            pixelsToRemove = pixelsToRemove - (int) pixelsToRemove;
        }
    }
    
    void render(Graphics g, int[] pixelpos){
        BufferedImage currFrame = new BufferedImage(myPane.getWidth(), myPane.getWidth(), BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < width ; i++) {
            for(int j = 0; j < height ; j++) {					  
                if(j > pixelpos[1] || (j == pixelpos[1] && i > pixelpos[0])){
                    currFrame.setRGB(i, j, BLACK);
                }
                else
                    currFrame.setRGB(i, j, SAND);
            }
        }
        
        g.drawImage(currFrame, 0, 0, myPane);
    }

    private static int[] decrement(int[] pixelPos, int rowMax,int pixelsToRemove) {
        int[] returnedPixel = new int[2];
        
        
        if(pixelPos[0] - pixelsToRemove >= 0){
            returnedPixel[0] = pixelPos[0] - pixelsToRemove;
            returnedPixel[1] = pixelPos[1];
        }
        else if(pixelPos[0] - pixelsToRemove < 0){
            //pixelsToRemove-pixelPos[0] refers to the number of pixels remaining
            //after removing the pixels on the current row
            returnedPixel[0] = width - (pixelsToRemove - pixelPos[0])%width;
            returnedPixel[1] = pixelPos[1] - (1 + (pixelsToRemove - pixelPos[0])/width);
        }
        else 
            System.out.println("wat.");
        
        return returnedPixel;
    }
}