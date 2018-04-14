/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    SimpleKMeans.java
 *    Copyright (C) 2000-2012 University of Waikato, Hamilton, New Zealand
 *
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import weka.classifiers.rules.DecisionTableHashKey;
import weka.clusterers.NumberOfClustersRequestable;
import weka.clusterers.RandomizableClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

/**
 * <!-- globalinfo-start --> Proposes the number of clusters for
 *	multiple runs of SimpleKMeans clustering algorithm  
 *	with different seeds based on the voting of
 *	Clustering Quality Metrics from each seed.
 * 
 * <p/>
 * <!-- globalinfo-end -->
 * 	 
 * <!-- options-start --> Valid options are:
 * <p/>
 * 
 * <pre>
 * -N &lt;num&gt;
 *  Number of clusters.
 *  (default 5).
 * </pre>
 *  
 * <pre>
 * -S &lt;num&gt;
 *  Random number seed.
 *  (default 10)
 * </pre>
 * 
 * <!-- options-end -->
 */
public class VoteK extends RandomizableClusterer implements
NumberOfClustersRequestable 
{
	
  /** for serialization. */
  static final long serialVersionUID = -3235809600124455376L; 
  
  /** Holds the predicted number of clusters */
  protected String m_predictedNrClust = ""; 
  
  /** Holds the number of seeds that voted for the winner cluster */
  protected int m_winnerSeeds = 0;
  
  /**
   * maximum number of clusters to generate.
   */
  protected int m_NumClusters = 12;

  /**
   * the default constructor.
   */
  public VoteK() 
  {
    super();

    m_SeedDefault = 10;
    setSeed(m_SeedDefault);
  }  
  
  /**
   * Returns the tip text for this property.
   * 
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String numClustersTipText()
  {
    return "set the maximum number of clusters";
  }
  
  /**
   * Returns the tip text for this property
   * 
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String seedTipText() 
  {
    return "The maximum number of seeds.";
  }

  /**
   * Returns a string describing this clusterer.
   * 
   * @return a description of the evaluator suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() 
  {
    return "Cluster data using the VoteK algorithm. Uses "
      + "the Euclidean distance." ;      
  }

  /**
   * Returns default capabilities of the clusterer.
   * 
   * @return the capabilities of this clusterer
   */
  @Override
  public Capabilities getCapabilities() 
  {
    Capabilities result = super.getCapabilities();
    result.disableAll();
    result.enable(Capability.NO_CLASS);

    // attributes   
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);
    result.enable(Capability.NOMINAL_ATTRIBUTES); //work around
    return result;
  }

  /**
   * Proposes number of clusters.
   * 
   * @param data set of instances serving as training data
   * @throws Exception if the clusterer has not been generated successfully
   */
  @Override
  public void buildClusterer(Instances data2) throws Exception
  {
	  	Instances data = new Instances(data2);
	  	
		CQM cqm = new CQM();
		int[] assigments = null;
		Instance centroid = null;	
		Instances centroids = null;
		int[] voting;
		String[] seeds = new String[10];
		double[][] metrics = new double[8][m_NumClusters];		
		
		for (int i = 0; i < seeds.length; i++)
		{
			seeds[i] = "";
		}		
		
		for (int seed = 1; seed <= m_Seed; seed++)
		{	
			voting = new int[m_NumClusters];
			SimpleKMeans simpleKMeans = new SimpleKMeans();
		  	
			simpleKMeans.setNumClusters(1);
			simpleKMeans.setPreserveInstancesOrder(true);	
			simpleKMeans.setSeed(seed);		
			simpleKMeans.buildClusterer(data);
			centroid = simpleKMeans.getClusterCentroids().instance(0);			
			
			for (int numClust = 2; numClust <= m_NumClusters; numClust++)
			{
				int nC2 = numClust;
				SimpleKMeans debg = new SimpleKMeans();
				debg.setNumClusters(nC2);
				
				debg.setPreserveInstancesOrder(true);	
				debg.setSeed(seed);		
				debg.buildClusterer(data);
				
				assigments = debg.getAssignments();
				centroids = debg.getClusterCentroids();
				//numClust = simpleKMeans.getNumClusters();
				//System.out.println(numClust);
				
				//System.out.println(debg.getNumClusters() + " debg.getNumClusters");
				metrics[0][numClust - 2] = cqm.getDaviesBouldinIndex(centroids, debg.numberOfClusters(), data, assigments);
				//System.out.println("mai traieste... 0");
				metrics[1][numClust - 2] = cqm.getDunnIndex(centroids, debg.numberOfClusters(), data, assigments); //too slow for bigdata
							
				metrics[2][numClust - 2] = cqm.getXiBeniIndex(centroids, debg.numberOfClusters(), data, assigments);
				//System.out.println("mai traieste... 2");			
				metrics[3][numClust - 2] = cqm.getBanfeldRafteryIndex(centroids, debg.numberOfClusters(), data, assigments);
				//System.out.println("mai traieste... 3");
				metrics[4][numClust - 2] = cqm.getMcClainRaoIndex(debg.numberOfClusters(), data, assigments); //too slow for bigdata
							
				metrics[5][numClust - 2] = cqm.getRayTuriIndex(centroids, debg.numberOfClusters(), data, assigments);
				//System.out.println("mai traieste... 5");
				metrics[6][numClust - 2] = cqm.getCalinskiHarabaszIndex(centroid, centroids, debg.numberOfClusters(), data, assigments);
				//System.out.println("mai traieste... 6");
				metrics[7][numClust - 2] = cqm.getPBMindex(centroid, centroids, debg.numberOfClusters(), data, assigments);
				//System.out.println("mai traieste... 7");
				
				if (numClust == 2)
				{
					metrics[0][m_NumClusters - 1] = metrics[0][0];
					metrics[1][m_NumClusters - 1] = metrics[1][0];
					metrics[2][m_NumClusters - 1] = metrics[2][0];
					metrics[3][m_NumClusters - 1] = metrics[3][0];
					metrics[4][m_NumClusters - 1] = metrics[4][0];
					metrics[5][m_NumClusters - 1] = metrics[5][0];
					metrics[6][m_NumClusters - 1] = metrics[6][0];
					metrics[7][m_NumClusters - 1] = metrics[7][0];
				}
				else
				{
					//DB min
					if (metrics[0][m_NumClusters - 1] > metrics[0][numClust - 2])
					{
						metrics[0][m_NumClusters - 1] = metrics[0][numClust - 2];
					}
					
					//Dunn max
					if (metrics[1][m_NumClusters - 1] < metrics[1][numClust - 2])
					{
						metrics[1][m_NumClusters - 1] = metrics[1][numClust - 2];
					}
					
					//XB min
					if (metrics[2][m_NumClusters - 1] > metrics[2][numClust - 2])
					{
						metrics[2][m_NumClusters - 1] = metrics[2][numClust - 2];
					}
					
					//BR min
					if (metrics[3][m_NumClusters - 1] > metrics[3][numClust - 2])
					{
						metrics[3][m_NumClusters - 1] = metrics[3][numClust - 2];
					}
					
					//MR min
					if (metrics[4][m_NumClusters - 1] > metrics[4][numClust - 2])
					{
						metrics[4][m_NumClusters - 1] = metrics[4][numClust - 2];
					}
					
					//RT min
					if (metrics[5][m_NumClusters - 1] > metrics[5][numClust - 2])
					{
						metrics[5][m_NumClusters - 1] = metrics[5][numClust - 2];
					}
					
					//CH max
					if (metrics[6][m_NumClusters - 1] < metrics[6][numClust - 2])
					{
						metrics[6][m_NumClusters - 1] = metrics[6][numClust - 2];
					}
					
					//PBM min
					if (metrics[7][m_NumClusters - 1] > metrics[7][numClust - 2])
					{
						metrics[7][m_NumClusters - 1] = metrics[7][numClust - 2];
					}
				}
				debg = null;
			}
			
			//System.out.println("mai traieste... j");
			
			for (int i = 0; i < 8; i++)
			{
				for (int j = 0; j < m_NumClusters - 1; j++)
				{
					
					// too slow for bigdata
					/*
					if(i == 1 && i == 4)
					{
						continue;
					}*/
					
					if (metrics[i][j] == metrics[i][m_NumClusters - 1])
					{
						voting[j]++;
					}
				}
			}
			
			int max = 0;
			
			for (int i = 0; i < m_NumClusters - 1; i++)
			{
				if (max < voting[i])
				{
					max = voting[i];
				}
			}
			
			for (int i = 0; i < m_NumClusters - 1; i++)
			{
				if (max == voting[i])
				{
					seeds[seed - 1] += i+2;
				}
			}			
			simpleKMeans = null;
		}
		
		//System.out.println("mai traieste... ");
		
		voting = new int[m_NumClusters];
		
		for (int i = 0; i < m_NumClusters - 1; i++)
		{
			for (int j = 0; j < seeds.length; j++)
			{
				int clust = i + 2;
				
				if (seeds[j].contains(""+clust))
				{
					voting[i]++;
				}
			}
		}
		
		int max = 0;
		
		for (int i = 0; i < m_NumClusters - 1; i++)
		{
			if (max < voting[i])
			{
				max = voting[i];				
			}
		}
		
		for (int i = 0; i < m_NumClusters - 1; i++)
		{
			if (max == voting[i])
			{
				int clust = i + 2;
				m_predictedNrClust += clust + ",";
				//System.out.println(m_predictedNrClust);
			}
		}
		
		
		
		for (int i = 0; i < seeds.length; i++)
		{
			if (seeds[i].contains(""+m_predictedNrClust.charAt(0)))
			{
				m_winnerSeeds++;
			}
		}
		//System.out.println(m_predictedNrClust);
		//System.out.println(m_winnerSeeds + " seed");
		m_predictedNrClust = m_predictedNrClust.substring(0, m_predictedNrClust.length()-1);
  }  

 

  /**
   * Returns an enumeration describing the available options.
   * 
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() 
  {
	  
    Vector<Option> result = new Vector<Option>();   

    result.addAll(Collections.list(super.listOptions()));

    return result.elements();
  }  

  /**
   * Parses a given list of options.
   * <p/>
   * 
   * <!-- options-start --> Valid options are:
   * <p/>
   * <pre>
   * -N &lt;num&gt;
   *  Number of clusters.
   *  (default 5).
   * </pre>
   * <pre>
   * -S &lt;num&gt;
   *  Random number seed.
   *  (default 10)
   * </pre>  
   * <!-- options-end -->
   * 
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception 
  {
	  String optionString = Utils.getOption('N', options);

	  if (optionString.length() != 0) 
	  {
		 setNumClusters(Integer.parseInt(optionString));
	  }
	 
	  super.setOptions(options);

	  Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings of SimpleKMeans.
   * 
   * @return an array of strings suitable for passing to setOptions()
   */
  @Override
  public String[] getOptions()
  {
    Vector<String> result = new Vector<String>();    
    
    result.add("-N");
    result.add("" + getNumClusters());

    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[result.size()]);
  }

  /**
   * return a string describing this clusterer.
   * 
   * @return a description of the clusterer as a string
   */
  @Override
  public String toString() 
  {
	 
	  StringBuffer temp = new StringBuffer();
	  temp.append("\nVoteK\n======\n");
	  temp.append("Predicted number of clusters : " + m_predictedNrClust);
	  temp.append("\nConfidence : " + m_winnerSeeds);
	  temp.append(" voters from : " + m_Seed);
	  temp.append(" agreed with " + m_predictedNrClust + " clusters");
	  temp.append("\n\n");
	  return temp.toString();
  }  

  /**
   * Returns the revision string.
   * 
   * @return the revision
   */
  @Override
  public String getRevision() 
  {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for executing this class.
   * 
   * @param args use -h to list all parameters
   */
  public static void main(String[] args) 
  {
    runClusterer(new VoteK(), args);
  }  
 
  /**
   * Returns the number of clusters.
   * 
   * @return the number of clusters generated for a training dataset.
   * @throws Exception if number of clusters could not be returned successfully
   */
  @Override
  public int numberOfClusters() throws Exception
  {
	// TODO Auto-generated method stub
	int numClust = Integer.parseInt("" + m_predictedNrClust.charAt(0));
	
	return numClust;
  }
	
  /**
   * gets the maximum number of clusters to generate.
   * 
   * @return the maximum number of clusters to generate
   */
  public int getNumClusters() 
  {
    return m_NumClusters;
  }
  
  public int getPrediction()
  {
	  //System.out.println(m_predictedNrClust);
	  return Integer.parseInt("" + m_predictedNrClust.charAt(0));
  }
	  
  /**
   * set the maximum number of clusters to generate.
   * 
   * @param n the maximum number of clusters to generate
   * @throws Exception if number of clusters is negative
   */
  @Override
  public void setNumClusters(int n) throws Exception 
  {
    if (n <= 0) 
    {
      throw new Exception("Maximum number of clusters must be > 0");
    }
    
    m_NumClusters = n;
  }
}
