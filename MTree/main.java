import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import weka.core.SelectedTag;
import javax.imageio.ImageIO;

import weka.core.converters.*;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.instance.Randomize;
//import weka.clusterers.MTree;

public class mtreee {
 	public static void main(String[] args) throws Exception{

 		double globalSSE = 999999999;
 		
 		for(int p = 4; p <= 8; p ++)	
 		for(int k = 0; k <= 100; k ++)
 		{
	 		BufferedImage inputImage = ImageIO.read(new File("sample512.png"));
	 		
	 		// facem de mana ca bajetii
	 		
	 		FileWriter file = new FileWriter("sample.arff");
	 		BufferedWriter buffer = new BufferedWriter(file);
	 		
	 		int inputWidth = inputImage.getWidth();
	 		int inputHeight = inputImage.getHeight();
	 		int[] coord = new int[inputWidth * inputHeight];
	 		int nr = 0;
	 		
	 		buffer.write("@RELATION pixels\n@ATTRIBUTE x NUMERIC\n@ATTRIBUTE y NUMERIC\n@DATA\n");
	 		for(int i = 0; i < inputWidth; i ++)
	 		{
	 			for(int j = 0; j < inputHeight; j ++)
	 			{
	 				if(inputImage.getRGB(i, j) == -1)
	 				{
	 					buffer.write(i + "," + j + "\n");
		 			}
	 			}
	 		}
	 		buffer.close();
	 
	 		ArffLoader loader = new ArffLoader();
	 		loader.setFile(new File("sample.arff"));
	 		Instances structure = loader.getDataSet();
	 		
	 		int[] globalAssignments = new int[inputHeight * inputWidth];
	 		
	 		MTree myMTree = new MTree();
	 		
	 		
	 		myMTree.setNumClusters(p);
	 		myMTree.setSeed(k);
	 		myMTree.buildClusterer(structure);
	 		globalAssignments = myMTree.getAssignments();
	 		System.out.println(myMTree.toString() + "");
	 		
	 			
		 		
	 		FileWriter file2 = new FileWriter("assignements.txt");
	 		BufferedWriter buffer2 = new BufferedWriter(file2);
	 		
	 		for (int i = 0; i < globalAssignments.length; i++) 
	 		{
	 			  coord[ nr ++] = (int)structure.instance(i).value(0) * inputHeight + (int)structure.instance(i).value(1);
	 			  buffer2.write("Instance" + i + "-> Cluster " + globalAssignments[i] + "\n");
	 		}
	 		
	 		buffer2.close();
	 		Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.LIGHT_GRAY, Color.cyan, Color.WHITE, Color.DARK_GRAY, Color.GRAY};
	 		BufferedImage bi = new BufferedImage(inputWidth, inputHeight, BufferedImage.TYPE_INT_ARGB);     
	        int index = 0; 
	        for(int clusterNum : globalAssignments) 
	        {
	        	bi.setRGB(coord[index] / inputWidth, coord[index] % inputHeight,  colors[clusterNum].getRGB() );
	 		    index ++;
	 		}
	        
	        ImageIO.write(bi, "PNG", new File("cobweb/c" + p + "/" + k + ".png"));	 		
 		
		    loader.reset();
 		}
	 }
}
