package weka.clusterers;

import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import weka.core.SelectedTag;
import javax.imageio.ImageIO;


import weka.core.converters.*;
import weka.clusterers.EM;
import weka.clusterers.FarthestFirst;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.*;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.instance.Randomize;
//import weka.clusterers.MTree;

public class main {

	public static void main(String[] args) throws Exception{

 		
 		Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.LIGHT_GRAY, Color.cyan, Color.WHITE, Color.DARK_GRAY, Color.GRAY};
 		int[] coord2 = new int[100000];
 		int nr2 = 0;
 		int[] globalAssignments2 = new int[200000];
 		List<long[]> globalList = new ArrayList<long[]>();
 		Instances structure2 = null;
 		ClusterEvaluation eval = new ClusterEvaluation();
 		double globalSSE = 999999999;
 				
 		//for(int p = 0; p <= 10; p ++)
 		//{
 			//double globalSSE = 999999999;
 		
 		for(int k = 0; k <= 100; k ++)
 		{
	 		//2
 			
	 		ArffLoader loader = new ArffLoader();
	 		loader.setFile(new File("iris_data_scaled.csv.arff")); // data_banknote_authentication_normalized    wine_normalized
	 		Instances structure = loader.getDataSet();
	 		structure2 = structure;
	 		
	 		int[] globalAssignments = new int[200000];
	 		int[] coord = new int[200000];
	 		int nr = 0;
	 		
	 		MTree myMTree = new MTree();

	 		myMTree.setSplitPolicy(new SelectedTag(MTree.CANOPY, MTree.TAGS_SELECTION));
	 		myMTree.setDistanceFunction(new SelectedTag(MTree.EuclideanDistance, MTree.DISTANCE_SELECTION));
	 		myMTree.setNumClusters(-1);
	 		myMTree.setSeed(k);
	 		myMTree.buildClusterer(structure);
	 		globalAssignments = myMTree.getAssignments();

	 		
	 		if(myMTree.getSumOfSquaredErrors(myMTree.mTree) < globalSSE)
	 		{
	 			globalSSE = myMTree.getSumOfSquaredErrors(myMTree.mTree);
	 			globalAssignments2 = myMTree.getAssignments();
	 		}
	 			
		 	
	 		///3
	 		
 			System.out.println(globalSSE);
 		}
 		
 			
 		FileWriter file3 = new FileWriter("iris_cKMs.txt");
 		BufferedWriter buffer3 = new BufferedWriter(file3);
 		
 		buffer3.write("Predictions");
 		buffer3.newLine();
 		for (int i = 0; i < globalAssignments2.length; i++) 
 		{
 			  buffer3.write(globalAssignments2[i] + "");
 			  buffer3.newLine();
 		}
 		
 		buffer3.close();
 		
	}
	
}

//2
/*BufferedImage inputImage = ImageIO.read(new File("sample.png"));
	
	
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
	buffer.close();*/






///3
/*
	Canopy kmeans = new Canopy();
	kmeans.setNumClusters(8);
	//kmeans.setMaxIterations(10000);
	//.setPreserveInstancesOrder(true);
	kmeans.buildClusterer(structure);
	
	ArrayList<long[]> m_clusterCanopies = new ArrayList<long[]>();
	Instances m_canopies = kmeans.getCanopies();
	
	for (int i = 0; i < m_canopies.size(); i++) 
	{
		Instance inst = m_canopies.instance(i);
		try 
		{
			globalAssignments2 = kmeans.assignCanopies(inst);
			m_clusterCanopies.add(globalAssignments2);
		} catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}
	
	
	System.out.println(m_clusterCanopies);*/
	
	//FileWriter file2 = new FileWriter("CanopyAssignementsUnbalanced.txt");
	//BufferedWriter buffer2 = new BufferedWriter(file2);
	
	/*for (int i = 0; i < globalAssignments.length; i++) 
	{
		  coord[ nr ++] = (int)structure.instance(i).value(0) * 10000 + (int)structure.instance(i).value(1);
		  buffer2.write("Instance" + i + "-> Cluster " + globalAssignments[i] + "\n");
	}*/
	
	//buffer2.close();
	
	/*BufferedImage bi = new BufferedImage(200000, 200000, BufferedImage.TYPE_INT_ARGB);     
int index = 0; 
for(int clusterNum : globalAssignments) 
{
	bi.setRGB(coord[index] / 200000, coord[index] % 200000,  colors[clusterNum].getRGB() );
	    index ++;
	}

ImageIO.write(bi, "PNG", new File("cascade/c" + p + "/" + k + "_K" + p + "_" + myMTree.getSumOfSquaredErrors(myMTree.mTree) + ".png"));	 		

loader.reset();*/
//}
	/*BufferedImage bi2 = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);     
int index = 0; 

for (int i = 0; i < globalAssignments2.length; i++) 
	{
		  coord2[ nr2 ++] = (int)structure2.instance(i).value(0) * 256 + (int)structure2.instance(i).value(1);
	}

for(int clusterNum : globalAssignments2) 
{
	bi2.setRGB(coord2[index] / 256, coord2[index] % 256,  colors[clusterNum].getRGB() );
	    index ++;
	}

ImageIO.write(bi2, "PNG", new File("cascade/" + "SSE_K" + p + "_" + globalSSE + ".png"));*/
