package estimator;

import common.Energy;
import org.joda.time.Interval;
import usage.CpuData;
import usage.EnergyData;
import usage.EstimationData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class NodeInfoAnalyzer
{
	private final Properties energyFile;
	private final Interval timeInterval;

	public NodeInfoAnalyzer(Interval _timeInterval)
	{
		this.timeInterval = _timeInterval;
		energyFile = new Properties();

		try
		{
			energyFile.load(new FileInputStream(Energy.ENERGY_FILE));

		} catch (FileNotFoundException e)
		{
			Logger.getLogger(NodeInfoAnalyzer.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		} catch (IOException e)
		{
			Logger.getLogger(NodeInfoAnalyzer.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Analyze the consulted data.
	 * 
	 * @return
	 */
	public EnergyData analyze(EstimationData _eData)
	{
		if (isDataOk(_eData))
			if (Boolean.parseBoolean(energyFile.getProperty(Energy.IS_FACTORY_VALUES)))
			{

				return new EnergyData(
						_eData.getNodeUUID(),
						calculateCpu(
								_eData.getRawCpuData(),
								Double.parseDouble(energyFile.getProperty(Energy.MAX_CPU_FACTORY_TDP))),
						calculateDisk(
								_eData.getRawDiskData(),
								Double.parseDouble(energyFile.getProperty(Energy.AVG_DISK_FACTORY_PCP))),
						calculateMemory(
								_eData.getRawMemData(),
								Double.parseDouble(energyFile.getProperty(Energy.AVG_MEM_FACTORY_PCP))),
						Double.parseDouble(energyFile.getProperty(Energy.MAX_NET_FACTORY_PCP)),
						Double.parseDouble(energyFile.getProperty(Energy.MISC_PCP)));
			} else
			{
				return new EnergyData(
						_eData.getNodeUUID(),
						calculateCpu(
								_eData.getRawCpuData(),
								Double.parseDouble(energyFile.getProperty(Energy.MAX_CPU_MEASURED_TDP))),
						calculateDisk(
								_eData.getRawDiskData(),
								Double.parseDouble(energyFile.getProperty(Energy.AVG_DISK_MEASURED_PCP))),
						calculateMemory(
								_eData.getRawMemData(),
								Double.parseDouble(energyFile.getProperty(Energy.AVG_MEM_MEASURED_PCP))),
						Double.parseDouble(energyFile.getProperty(Energy.MAX_NET_MEASURED_PCP)),
						Double.parseDouble(energyFile.getProperty(Energy.MISC_PCP)));
			}
		return null;
	}

	private boolean isDataOk(EstimationData _eData)
	{
		if (_eData == null)
		{
			Logger.getLogger(NodeInfoAnalyzer.class.getName()).log(
					Level.WARNING, "Estimation data object null. Stoping...");
			return false;
		} else if (_eData.getRawCpuData().size() == 0)
		{
			Logger.getLogger(NodeInfoAnalyzer.class.getName()).log(
					Level.WARNING,
					String.format(
							"No CPU data found for node %s in the given interval. Stoping...",
							_eData.getNodeUUID()));
			return false;
		} else if (_eData.getRawDiskData().size() == 0)
		{
			Logger.getLogger(NodeInfoAnalyzer.class.getName()).log(
					Level.WARNING,
					String.format(
							"No Disk data found for node %s in the  given interval. Stoping...",
							_eData.getNodeUUID()));
			return false;
		} else if (_eData.getRawMemData().size() == 0)
		{
			Logger.getLogger(NodeInfoAnalyzer.class.getName()).log(
					Level.WARNING,
					String.format(
							"No Memory data found for node %s in the  given interval. Stoping...",
							_eData.getNodeUUID()));
			return false;
		} else
			return true;
	}

	private Double calculateMemory(ArrayList<Integer> rawData, double watts)
	{
		// Dividing by 1M to get the values in Gb
		return (Double.valueOf(rawData.get(0)) / 1000000) * watts;
	}

	private Double calculateDisk(ArrayList<Integer> rawData, double watts)
	{
		// Tx1 = millisecondsSpentInIO, t1
		Integer start = rawData.get(0);
		// Tx2 = millisecondsSpentInIO, t2
		Integer end = rawData.get(rawData.size() - 1);
		// Delta t = t2 - t1
		long deltaT =
				this.timeInterval.getEndMillis() - this.timeInterval.getStartMillis();
		// TxUsage = (Tx2 - Tx1) / deltaT
		Double txUsage = (((double) (end - start)) / deltaT);
		return txUsage * (deltaT / 1000) * watts;
	}

	private Double calculateCpu(ArrayList<CpuData> rawData, double watts)
	{
		CpuData start = rawData.get(0);
		CpuData end = rawData.get(rawData.size() - 1);

		Double diffIdle, diffTotal;
		// DiffIdle = idle - prevIdle
		diffIdle = (double) (end.getIdle() - start.getIdle());
		// DiffTotal = total - prevTotal
		diffTotal = (double) (end.getTotalTimes() - start.getTotalTimes());
		// DiffUsage = (1000*(DiffTotal - DiffIdle)/DiffTotal + 5) / 10
		Double diffUsage = (1000 * (diffTotal - diffIdle) / diffTotal + 5) / 10;
		return (diffUsage * 100) / watts;
	}
}
