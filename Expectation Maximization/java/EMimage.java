import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import weka.clusterers.SimpleKMeans;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class EMimage 
{
 	public static void main(String[] args) throws Exception{
 		  
 		BufferedImage inputImage = ImageIO.read(new File("sample.png"));
 		FileWriter file = new FileWriter("sample.arff");
 		BufferedWriter buffer = new BufferedWriter(file);
 		
 		int inputWidth = inputImage.getWidth();
 		int inputHeight = inputImage.getHeight();
 		
 		buffer.write("@RELATION pixels\n@ATTRIBUTE x NUMERIC\n@ATTRIBUTE y NUMERIC\n@DATA\n");
 		
 		int[] coord = new int[inputWidth * inputHeight];
 		int nr = 0;
 		
 		for(int i = 0; i < inputHeight; i ++)
 		{
 			for(int j = 0; j < inputWidth; j ++)
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
 		
 		ClusterEvaluation eval = new ClusterEvaluation();
 		EM myEM = new EM();
 		myEM.setMaxIterations(500);
 		myEM.setNumClusters(8);
 		myEM.setSeed(7);
 		myEM.buildClusterer(structure);
 		
 		eval.setClusterer(myEM);
 		eval.evaluateClusterer(structure);
 		
 		
 		double[] globalAssignments = eval.getClusterAssignments();
 		
 		FileWriter file2 = new FileWriter("assignements.txt");
 		BufferedWriter buffer2 = new BufferedWriter(file2);
 		
 		
 		for (int i = 0; i < globalAssignments.length; i++) 
 		{
 			  coord[ nr ++] = (int)structure.instance(i).value(0) * inputHeight + (int)structure.instance(i).value(1);
 			  buffer2.write("Instance" + i + "-> Cluster " + globalAssignments[i] + "\n");
 		}

		buffer2.write(myEM.toString());
		buffer2.write("");	
 		buffer2.close();
 		
 		Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.LIGHT_GRAY, Color.cyan};
 		
 		BufferedImage bi = new BufferedImage(inputWidth, inputHeight, BufferedImage.TYPE_INT_ARGB);
       
        int index = 0;         
        for(double clusterNum : globalAssignments) 
        {
        	bi.setRGB(coord[index] / inputHeight, coord[index] % inputWidth,  colors[(int)clusterNum].getRGB() );
 		    index ++;
 		}
        
        ImageIO.write(bi, "PNG", new File("result.png"));
 		}
}
