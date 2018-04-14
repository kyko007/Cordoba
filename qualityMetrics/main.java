package quality2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

import javax.imageio.ImageIO;

import weka.core.converters.*;
//import weka.clusterers.CQM;
import weka.clusterers.*;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.instance.Randomize;



public class Quality2 {
	
	public static void main(String args[]) throws Exception{
		
		for(int k = 2; k <= 8; k ++)
		{
			FileWriter file2 = new FileWriter("SKM-seeds/student-por/" + k + "/all2.txt");
	 		BufferedWriter buffer2 = new BufferedWriter(file2);
	 		
	 		int nr = 0;
	 		double[] DaviesBouldinIndex = new double[100];
	 		double[] XiBeniIndex = new double[100];
	 		double[] BanfeldRafteryIndex = new double[100];
	 		double[] RayTuriIndex = new double[100];
	 		double[] CalinskiHarabaszIndex = new double[100];
	 		double[] PBMindex = new double[100];
	 		
			
			for(int j = 0; j <= 10; j ++)
	 		{
				ArffLoader loader = new ArffLoader();
		 		loader.setFile(new File("db-student-por-formated.arff"));
		 		Instances structure = loader.getDataSet();
		 		//Instances structure2 = structure; 	//loader.getDataSet();
	 		
	 		
	 			int numClust = k;
	 	 		Instances centroids = null;
	 	 		Instance centroid = null;
	 	 		int[] assigments = null;
	 	 		
	 	 		//Instances structure3 = null;
	 	 		//structure2.randomize(new Random(j));
	 	 		//structure3 = structure2;
	 	 		
	 			SimpleKMeans y = new SimpleKMeans();
	 	 		y.setNumClusters(1);
	 			y.setPreserveInstancesOrder(true);	
	 			y.setSeed(j);		
	 			y.buildClusterer(structure);
	 			centroid = y.getClusterCentroids().instance(0);
	 			
	 	 		
	 	 		SimpleKMeans x = new SimpleKMeans();
	 	 		ClusterEvaluation eval = new ClusterEvaluation();
	 	 		
		 		x.setNumClusters(numClust);
				x.setPreserveInstancesOrder(true);	
				x.setSeed(j);		
				x.buildClusterer(structure);
				//centroid = x.getClusterCentroids().instance(0);
				eval.setClusterer(x);
				eval.evaluateClusterer(structure);
				centroids = x.getClusterCentroids();
				assigments = x.getAssignments();
				
	 	 		/*
		 		MTree mtr = new MTree();
		 		//mtr.setPreserveInstancesOrder(true);
		 		mtr.setNumClusters(numClust);
		 		mtr.setSeed(10);
		 		mtr.buildClusterer(structure);
		 		assigments = mtr.getAssignments();
		 		centroid = mtr.getClusterCentroids().instance(0);
		 		centroids = mtr.getClusterCentroids();
		 		
				centroids = mtr.getClusterCentroids();
				*/
		 		CQM cqm = new CQM();
		 		
		 		DaviesBouldinIndex[nr] = cqm.getDaviesBouldinIndex(centroids, numClust, structure, assigments);
				//double DunnIndex = cqm.getDunnIndex(centroids, numClust, structure, assigments); //too slow for bigdata
				XiBeniIndex[nr] = cqm.getXiBeniIndex(centroids, numClust, structure, assigments);
				BanfeldRafteryIndex[nr] = cqm.getBanfeldRafteryIndex(centroids, numClust, structure, assigments);
				//double McClainRaoIndex = cqm.getMcClainRaoIndex(numClust, structure, assigments); //too slow for bigdata
				RayTuriIndex[nr] = cqm.getRayTuriIndex(centroids, numClust, structure, assigments);
				CalinskiHarabaszIndex[nr] = cqm.getCalinskiHarabaszIndex(centroid, centroids, numClust, structure, assigments);
				PBMindex[nr] = cqm.getPBMindex(centroid, centroids, numClust, structure, assigments);
				
				nr ++;
				
				buffer2.write("seed" + j);
		 		buffer2.newLine();
				/*buffer2.write("DaviesBouldinIntdex: " + DaviesBouldinIndex + '\n');
				buffer2.newLine();
				//System.out.println("DunnIndex: " + DunnIndex);
				buffer2.write("XiBeniIndex: " + XiBeniIndex + '\n');
				buffer2.newLine();
				buffer2.write("BanfeldRafteryIndex: " + BanfeldRafteryIndex + '\n');
				buffer2.newLine();
				//System.out.println("McClainRaoIndex: " + McClainRaoIndex);
				buffer2.write("RayTuriIndex: " + RayTuriIndex + '\n');
				buffer2.newLine();
				buffer2.write("CalinskiHarabaszIndex: " + CalinskiHarabaszIndex + '\n');
				buffer2.newLine();
				buffer2.write("PBMindex: " + PBMindex + '\n');
				buffer2.newLine();
				buffer2.newLine();
				//System.out.println(mtr.getSquaredError() + "");
				*/
		 		
		 		
				//mtr = null;
		 		x = null;
				y = null;
				cqm = null;
				
				loader.reset();
	 		}
			buffer2.write("DaviesBouldinIndex");
	 		buffer2.newLine();
	 		for(int i = 0; i < nr; i ++)
	 		{
	 			buffer2.write(DaviesBouldinIndex[i] + "");
	 			buffer2.newLine();
	 		}
	 		buffer2.write("XiBeniIndex");
	 		buffer2.newLine();
	 		for(int i = 0; i < nr; i ++)
	 		{
	 			buffer2.write(XiBeniIndex[i] + "");
	 			buffer2.newLine();
	 		}
	 		buffer2.write("BanfeldRafteryIndex");
	 		buffer2.newLine();
	 		for(int i = 0; i < nr; i ++)
	 		{
	 			buffer2.write(BanfeldRafteryIndex[i] + "");
	 			buffer2.newLine();
	 		}
	 		buffer2.write("RayTuriIndex");
	 		buffer2.newLine();
	 		for(int i = 0; i < nr; i ++)
	 		{
	 			buffer2.write(DaviesBouldinIndex[i] + "");
	 			buffer2.newLine();
	 		}
	 		buffer2.write("CalinskiHarabaszIndex");
	 		buffer2.newLine();
	 		for(int i = 0; i < nr; i ++)
	 		{
	 			buffer2.write(CalinskiHarabaszIndex[i] + "");
	 			buffer2.newLine();
	 		}
	 		buffer2.write("PBMindex");
	 		buffer2.newLine();
	 		for(int i = 0; i < nr; i ++)
	 		{
	 			buffer2.write(PBMindex[i] + "");
	 			buffer2.newLine();
	 		}
	 			
			buffer2.close();
		}
	}
}
