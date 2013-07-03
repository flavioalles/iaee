package storage;

import common.Utils;
import org.joda.time.DateTime;
import usage.EnergyData;
import usage.MonitoredData;
import usage.SensorChannel;
import usage.SensorData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//import java.lang.Thread;

public class HistoricalDatabase
{

	private Connection conn;
	private Statement stm;
	private DateTime date;
	private Map<Integer, Integer> nodes; // UUID | ID
	//private Map<Integer, DateTime, String, Integer> nodes; // ID | TIME | NAME | UUID
	private Map<Integer, Integer> sensors;
	private Map<String, Integer> unities;
	
	public static String getUserName() {
		String ex = new String("exception");
    	try {			
	    	Runtime run = Runtime.getRuntime();
			Process p;
			p = run.exec("whoami");
			p.waitFor();
			
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = new String(ex);
			String auxLine = new String();
			while ((auxLine = b.readLine()) != null) {
			  line = auxLine;
			}
			return line;
		}
		catch (IOException e) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
			return ex;
		}
		catch (InterruptedException e) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
			return ex;
		}
	}

	/**
	 * O construtor cria uma nova conexão com o banco de dados sqlite contido no
	 * arquivo passado como parâmetro. A conexão é possibilitada pelo driver
	 * JDBC, fornecido por SQLiteJDBC.
	 */
	public HistoricalDatabase(String file)
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			String ex = new String("exception");
			String USER_NAME = new String(getUserName());
			while (USER_NAME == ex)
				USER_NAME = getUserName();
			//System.out.println("jdbc:sqlite:/home/" + USER_NAME + "/iaee/" + file);//debug
			//System.exit(0);//debug
			conn = DriverManager.getConnection("jdbc:sqlite:/home/" + USER_NAME + "/iaee/" + file);
			initDB();
		} catch (final SQLException e)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		} catch (final ClassNotFoundException e)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * @return The ID of the last row inserted in the database
	 */
	private int getLastInsertRowId()
	{
		int rowID = 0;
		ResultSet rs;
		try
		{
			rs = stm.getGeneratedKeys();
			while (rs.next())
				rowID = rs.getInt(1);
		} catch (final SQLException e)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}
		return rowID;
	}

	/**
	 * @return date and time in this format: '2009-11-13 00:00:00' / YYYY-MM-DD
	 *         HH:mm:ss
	 */
	private String getTime()
	{
		date = new DateTime();
		String time =
				String.format("%s-%s-%s %s:%s:%s", date.getYear(),
						date.getMonthOfYear(), date.getDayOfMonth(),
						date.getHourOfDay(), date.getMinuteOfHour(),
						date.getSecondOfMinute());

		date = null;
		return time;
	}

	/**
	 * Cria uma nova tabela de recordes, apagando tudo que houvesse na base
	 * anteriormente.
	 */
	private void initDB()
	{
		try
		{
			date = new DateTime();

			stm = conn.createStatement();
			String[] tables = SqlScripts.CREATE_TABLES.split(";");
			String[] indexes = SqlScripts.CREATE_INDEXES.split(";");

			for (final String query : tables)
				stm.executeUpdate(query);
			for (final String query : indexes)
				stm.executeUpdate(query);

			nodes = new HashMap<Integer, Integer>();
			loadNodesFromDB();
			sensors = new HashMap<Integer, Integer>();
			loadSensorsFromDB();
			unities = new HashMap<String, Integer>();
			loadUnityFromDB();

			if (unities.size() < 1)
			{
				String[] unityType = SqlScripts.INSERT_UNITY_TYPE.split(";");
				for (final String query : unityType)
					stm.executeUpdate(query);
			}

		} catch (final SQLException e)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}
	}

	private void loadNodesFromDB()
	{
		Statement select;
		try
		{
			select = conn.createStatement();

			ResultSet result = select.executeQuery(SqlScripts.SELECT_NODES); // SELECT * FROM Node

			/* NOTICE THE ORDER OF THE PARAMETERS! */
			/*
			 * The key in the map have to be the node UUID, not the ID from the
			 * DB
			 */
			while (result.next())
			{ // process results one row at a time
				if (!this.nodes.containsKey(result.getInt(4)))
					this.nodes.put(result.getInt(4), result.getInt(1));
			}
		} catch (SQLException e)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}
	}

	private void loadSensorsFromDB() throws SQLException
	{
		Statement select = conn.createStatement();
		ResultSet result = select.executeQuery(SqlScripts.SELECT_SENSORS);

		/* NOTICE THE ORDE OF THE PARAMETERS! */
		/*
		 * The key in the map have to be the sensor UUID, not the ID from the DB
		 */
		while (result.next())
		{ // process results one row at a time
			if (!this.sensors.containsKey(result.getInt(1)))
				this.sensors.put(result.getInt(2), result.getInt(1));
		}
	}

	private void loadUnityFromDB() throws SQLException
	{
		Statement select = conn.createStatement();
		ResultSet result = select.executeQuery(SqlScripts.SELECT_UNITY);

		/* NOTICE THE ORDE OF THE PARAMETERS! */
		while (result.next())
		{ // process results one row at a time
			if (!this.unities.containsKey(result.getString(2)))
			{
				this.unities.put(result.getString(2), result.getInt(1));
			}
		}
	}

	/**
	 * Adiciona uma nova linha na tabela de recordes.
	 */
	public void saveOrUpdate(MonitoredData mData)
	{

		final String timeID = getTime();
		final int nodeID;
		final int sensorID;
		try
		{
			nodeID = getNodeDbId(mData.getNodeUUID(), Utils.getMachineName());
			sensorID = getSensorDbId(mData.getSensor());

			saveOrUpdateToDatabase(String.format(
					SqlScripts.INSERT_MEMORY_USAGE, nodeID, timeID,
					mData.getMem().getSize(), mData.getMem().getResident(),
					mData.getMem().getShare(), mData.getMem().getText(),
					mData.getMem().getData(), mData.getMem().getVsize(),
					mData.getMem().getRss(), mData.getMem().getRsslim(),
					mData.getMem().getMemTotal(), mData.getMem().getMemUsed(),
					mData.getMem().getMemFree(),
					mData.getMem().getMemBuffers(),
					mData.getMem().getMemCached()));
			for (int core = 0; core < mData.getCpu().keySet().size(); core++)
				saveOrUpdateToDatabase(String.format(
						SqlScripts.INSERT_CPU_USAGE, nodeID, timeID,
						mData.getCpu().get(core).getCoreId(),
						mData.getCpu().get(core).getUser(),
						mData.getCpu().get(core).getNice(),
						mData.getCpu().get(core).getSysmode(),
						mData.getCpu().get(core).getIdle(),
						mData.getCpu().get(core).getIowait(),
						mData.getCpu().get(core).getIrq(),
						mData.getCpu().get(core).getSoftirq(),
						mData.getCpu().get(core).getSteal(),
						mData.getCpu().get(core).getGuest()));
			for (final String o : mData.getDisk().keySet())
				saveOrUpdateToDatabase(String.format(
						SqlScripts.INSERT_DISK_USAGE, nodeID, timeID,
						mData.getDisk().get(o).getName(),
						mData.getDisk().get(o).getReadsCompleted(),
						mData.getDisk().get(o).getReadsMerged(),
						mData.getDisk().get(o).getWritesMerged(),
						mData.getDisk().get(o).getSectorsRead(),
						mData.getDisk().get(o).getMillisecondsReading(),
						mData.getDisk().get(o).getWritesCompleted(),
						mData.getDisk().get(o).getSectorsWritten(),
						mData.getDisk().get(o).getMillisecondsWriting(),
						mData.getDisk().get(o).getIosInProgress(),
						mData.getDisk().get(o).getMillisecondsSpentInIO(),
						mData.getDisk().get(o).getWeightedMillisecondsDoingIO()));
			for (final String o : mData.getNet().keySet())
				saveOrUpdateToDatabase(String.format(
						SqlScripts.INSERT_NETWORK_USAGE,
						nodeID,
						timeID,
						mData.getNet().get(o).getInterfaceName(),
						mData.getNet().get(o).getReceive().getRX_Bytes(),
						mData.getNet().get(o).getReceive().getRX_Packets(),
						mData.getNet().get(o).getReceive().getRX_Erros(),
						mData.getNet().get(o).getReceive().getRX_Dropped(),
						mData.getNet().get(o).getReceive().getRX_Fifo(),
						mData.getNet().get(o).getReceive().getRX_Frame(),
						mData.getNet().get(o).getReceive().getRX_Compressed(),
						mData.getNet().get(o).getReceive().getRX_Multicast(),
						mData.getNet().get(o).getTransmit().getTX_Bytes(),
						mData.getNet().get(o).getTransmit().getTX_Packets(),
						mData.getNet().get(o).getTransmit().getTX_Erros(),
						mData.getNet().get(o).getTransmit().getTX_Dropped(),
						mData.getNet().get(o).getTransmit().getTX_Fifo(),
						mData.getNet().get(o).getTransmit().getTX_Collisions(),
						mData.getNet().get(o).getTransmit().getTX_CarrierErrors(),
						mData.getNet().get(o).getTransmit().getTX_Compressed()));

			if (mData.getSensor() != null)
				for (SensorChannel o : mData.getSensor().getChannels())
				{
					saveOrUpdateToDatabase(String.format(
							SqlScripts.INSERT_SENSOR_USAGE,
							nodeID,
							timeID,
							sensorID,
							o.getNummer(),
							this.unities.get(Utils.convertUnityToString(o.getUnity())),
							o.getValue()));
				}

		} catch (SQLException e)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		} catch (Exception e)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}

	}

	public void saveOrUpdate(EnergyData eData)
	{
		if (eData == null)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE,
					"The parameter EnergyData in saveOrUpdate method is null, probably due a wrong estimation interval");
		} else
		{
			final String timeID = getTime();
			final int nodeID = getNodeDbId(eData.getNodeUUID(), Utils.getMachineName());
			saveOrUpdateToDatabase(String.format(
					SqlScripts.INSERT_ENERGY_CONSUMPTION, nodeID, timeID,
					eData.getTotal(), eData.getCpu(), eData.getDisk(),
					eData.getMem(), eData.getNet(), eData.getMisc()));
		}
	}

	/**
	 * @param _sensorData
	 * @return the database ID of the sensor.
	 * @throws SQLException
	 */
	private int getSensorDbId(SensorData _sensorData) throws SQLException
	{
		if (_sensorData != null)
		{
			if (!this.sensors.containsKey(_sensorData.getSensorUUID()))
			{
				saveOrUpdateToDatabase(SqlScripts.INSERT_SENSOR.replace("?",
						String.valueOf(_sensorData.getSensorUUID())));
				loadSensorsFromDB();
			}
			return this.sensors.get(_sensorData.getSensorUUID());
		} else
			return Integer.MIN_VALUE;
	}

	/**
	 * @return the database ID of the monitored node.
	 * @throws SQLException
	 */
	private int getNodeDbId(int _dataNodeUUID, String nodeName)
	{
		final String timeID = getTime();
		//final String nodeName = Utils.getMachineName();
		
		if (!this.nodes.containsKey(_dataNodeUUID)) {
			saveOrUpdateToDatabase(String.format(
					SqlScripts.INSERT_NODE, timeID, nodeName,
					String.valueOf(_dataNodeUUID)));
			loadNodesFromDB();
		}
		
		return this.nodes.get(_dataNodeUUID); 
	}

	/**
	 * Adiciona uma nova linha na tabela e retorna o ID da linha inserida
	 * 
	 * @param insert
	 *            : the insert statement
	 * @return o ID da linha inserida
	 */
	private synchronized int saveOrUpdateToDatabase(String insert)
	{
		Boolean isLocked = true;

		// while (isLocked)
		try
		{
			stm = conn.createStatement();
			stm.executeUpdate(insert);
			isLocked = false;

		} catch (final SQLException e)
		{
			if (e.getMessage().equals("database locked"))
				isLocked = true;
		}

		return getLastInsertRowId();
	}

	public synchronized ResultSet readFromDatabase(String query)
	{
		Statement select;
		ResultSet result = null;
		Boolean isLocked = true;

		while (isLocked)
			try
			{
				select = conn.createStatement();
				result = select.executeQuery(query);
				isLocked = false;
			} catch (final SQLException e)
			{
				Logger.getLogger(HistoricalDatabase.class.getName()).log(
						Level.SEVERE, e.getMessage(), e);
			}

		return result;
	}

	public ArrayList<String> getNodeListFromDB()
	{
		ResultSet result = readFromDatabase(SqlScripts.SELECT_NODES_UUID);

		ArrayList<String> tempResult = new ArrayList<String>();

		try
		{
			while (result.next())
			{
				tempResult.add(result.getString(1));
			}
		} catch (SQLException e)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}

		return tempResult;
	}

	public ArrayList<EnergyData> getEstimationResults()
	{
		ResultSet result =
				readFromDatabase(SqlScripts.SELECT_ESTIMATION_RESULTS);

		ArrayList<EnergyData> tempResult = new ArrayList<EnergyData>();

		try
		{
			while (result.next())
			{
				tempResult.add(new EnergyData(result.getInt(1),
						result.getDouble(2), result.getDouble(3),
						result.getDouble(4), result.getDouble(5),
						result.getDouble(6)));

			}
		} catch (SQLException e)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}

		return tempResult;
	}

}
