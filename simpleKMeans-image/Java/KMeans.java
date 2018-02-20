import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.clusterers.SimpleKMeans;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import javax.imageio.ImageIO;

public class KMeans{

 	public static void main(String[] args) throws Exception{
  
 		BufferedImage inputImage = ImageIO.read(new File("sample.png"));
 		FileWriter file = new FileWriter("sample.arff");
 		BufferedWriter buffer = new BufferedWriter(file);
 		
 		int inputWidth = inputImage.getWidth();
 		int inputHeight = inputImage.getHeight();
 		int[] coord = new int[inputWidth * inputHeight];
 		int nr = 0;
 		
 		// make .arff file
 		buffer.write("@RELATION pixels\n@ATTRIBUTE x NUMERIC\n@ATTRIBUTE y NUMERIC\n@DATA\n");
 		
 		for(int i = 0; i < inputHeight; i ++)
 		{
 			for(int j = 0; j < inputWidth; j ++)
 			{
 				if(inputImage.getRGB(i, j) == -1) // is White
 				{
	 				buffer.write(i + "," + j + "\n");
	 				coord[nr ++] = inputHeight * i + j; // save pixel coordinates
	 			}
 			}
 		}
 		buffer.close();
 		
 		//read arff file
 		ArffLoader loader = new ArffLoader();
 		loader.setFile(new File("sample.arff"));
 		Instances structure = loader.getDataSet();
 		
 		
 		int[] globalAssignments = new int[inputHeight * inputWidth];
 		double globalSSE = 999999999;
 		
 		//simpleKMeans
 		for(int t = 0; t <= 20; t ++)
 		{
	 		SimpleKMeans SKMeans = new SimpleKMeans();
	 		SKMeans.setPreserveInstancesOrder(true);
	 		SKMeans.setMaxIterations(500);
	 		SKMeans.setNumClusters(8); // set the number of clusters here
	 		SKMeans.setSeed(t); //more seeds for better result
	 		SKMeans.buildClusterer(structure);
	 		
	 		if(SKMeans.getSquaredError() < globalSSE)
	 		{
	 			globalSSE = SKMeans.getSquaredError();
	 			globalAssignments = SKMeans.getAssignments();
	 		}
	 		SKMeans = null;
 		}	
 		
 		//make assignment file (optional)
 		FileWriter file2 = new FileWriter("assignements.txt");
 		BufferedWriter buffer2 = new BufferedWriter(file2);
 		
 		int index1=0;
 		for(int clusterNum : globalAssignments) 
 		{
 			buffer2.write("Instance " + index1 + " -> Cluster " + clusterNum + "\n");
 		  index1++;
 		}
 		buffer2.close();
 		
 		
 		//photo coloring
 		Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.LIGHT_GRAY, Color.cyan};
 		BufferedImage bi = new BufferedImage(inputWidth, inputHeight, BufferedImage.TYPE_INT_ARGB);
        
 		int index = 0;
		for(int clusterNum : globalAssignments) 
		{
		  bi.setRGB(coord[index] / inputHeight, coord[index] % inputWidth,  colors[clusterNum].getRGB() );
		  index ++;
		} 
        	ImageIO.write(bi, "PNG", new File("result.png"));
 	}
 	 
}
