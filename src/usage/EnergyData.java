/**
 *
 */
package usage;

/**
 * @author pedro
 */
public final class EnergyData implements Comparable<EnergyData>
{
	private final Integer nodeUUID;
	private final Double total;
	private final Double cpu;
	private final Double disk;
	private final Double mem;
	private final Double net;
	private final Double misc;

	/**
	 * @param nodeUUID
	 * @param cpu
	 * @param disk
	 * @param mem
	 * @param net
	 * @param misc
	 */
	public EnergyData(Integer nodeUUID, Double cpu, Double disk, Double mem,
			Double net, Double misc)
	{
		this.nodeUUID = nodeUUID;
		this.total = cpu + disk + mem + net + misc;
		this.cpu = cpu;
		this.disk = disk;
		this.mem = mem;
		this.net = net;
		this.misc = misc;
	}

	/**
	 * @return the nodeUUID
	 */
	public final Integer getNodeUUID()
	{
		return nodeUUID;
	}

	/**
	 * @return the total
	 */
	public final Double getTotal()
	{
		return total;
	}

	/**
	 * @return the cpu
	 */
	public final Double getCpu()
	{
		return cpu;
	}

	/**
	 * @return the disk
	 */
	public final Double getDisk()
	{
		return disk;
	}

	/**
	 * @return the mem
	 */
	public final Double getMem()
	{
		return mem;
	}

	/**
	 * @return the net
	 */
	public final Double getNet()
	{
		return net;
	}

	/**
	 * @return the misc
	 */
	public final Double getMisc()
	{
		return misc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.format("%d	|	%.2f	|	%.2f	|	%.2f	|	%.2f	|	%.2f	|	%.2f",
				nodeUUID, total, cpu, disk, mem, net, misc);

	}

	public int compareTo(EnergyData o)
	{
		if (!(o instanceof EnergyData))
			throw new ClassCastException();

		EnergyData e = (EnergyData) o;

		return nodeUUID.compareTo(e.getNodeUUID());
	}
}
