package estimator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.Interval;

import storage.HistoricalDatabase;
import usage.EnergyData;

public class EstimationMaster implements Runnable
{
	private final NodeInfoCollector collector;
	private final NodeInfoAnalyzer analyzer;

	private final String nodeUUID;
	private final String cpuCore;
	private final String diskPartition;
	private final HistoricalDatabase hdb;

	public EstimationMaster(String _nodeUUID, String _cpuCore,
			String _diskPartition, Interval _timeInterval, String _hdbFile)
	{
		this.nodeUUID = _nodeUUID;
		this.cpuCore = _cpuCore;
		this.diskPartition = _diskPartition;
		this.hdb = new HistoricalDatabase(_hdbFile);

		this.collector = new NodeInfoCollector(_timeInterval, _hdbFile);
		this.analyzer = new NodeInfoAnalyzer(_timeInterval);
	}

	public void run()
	{
		hdb.saveOrUpdate(this.analyzer.analyze(this.collector.collect(
				this.nodeUUID, this.cpuCore, this.diskPartition)));
	}

	public static void showResults(String _hdbFile)
	{
		HistoricalDatabase staticHdb = new HistoricalDatabase(_hdbFile);
		ArrayList<EnergyData> eData = staticHdb.getEstimationResults();
		System.out.println("ENERGY ESTIMATION RESULTS");
		System.out.println(String.format(
				"%s nodes analyzed, showing detailed results (values in Watts):",
				eData.size()));
		System.out.println("");
		System.out.println("Node UUID	|	Total	|	CPU	|	DISK	|	MEMORY	|	NETWORK	|	MISC\n");

		try
		{
			FileWriter fstream = new FileWriter("estimation.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			Collections.sort(eData);
			
			for (EnergyData e : eData)
			{
				System.out.println(e.toString());
				out.write(e.toString()+"\n");
			}
			
			out.close();
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}

	}
	
}

