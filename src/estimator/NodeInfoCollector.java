package estimator;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import storage.HistoricalDatabase;
import storage.SqlScripts;
import usage.CpuData;
import usage.EstimationData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class NodeInfoCollector {
    private final Interval interval;
    private final HistoricalDatabase hdb;

    public NodeInfoCollector(Interval _interval, String _hdbFile) {
        this.interval = _interval;
        hdb = new HistoricalDatabase(_hdbFile);

    }

    public EstimationData collect(String _uuid, String _core, String _partition) {
        return new EstimationData(_uuid,
                queryCpu(_uuid, _core), queryDisk(_uuid, _partition),
                queryMemory(_uuid));
    }

    private ArrayList<Integer> queryMemory(String _uuid) {
        String query = String.format(SqlScripts.SELECT_MEM_DATA, _uuid,
                getTime(this.interval.getStart()), getTime(this.interval.getEnd()));

        ResultSet tempResult = this.hdb.readFromDatabase(query);

        ArrayList<Integer> result = new ArrayList<Integer>();
        try {
            while (tempResult.next()) {
                result.add(tempResult.getInt(1));
            }
        } catch (SQLException e) {
            Logger.getLogger(NodeInfoCollector.class.getName()).log(
                    Level.SEVERE, e.getMessage(), e);
        }

        return result;
    }

    private ArrayList<Integer> queryDisk(String _uuid, String _partition) {
        String query = String.format(SqlScripts.SELECT_DISK_DATA, _uuid,
                _partition, getTime(this.interval.getStart()), getTime(this.interval.getEnd()));

        ResultSet tempResult = this.hdb.readFromDatabase(query);

        ArrayList<Integer> result = new ArrayList<Integer>();
        try {
            while (tempResult.next()) {
                result.add(tempResult.getInt(1));
            }
        } catch (SQLException e) {
            Logger.getLogger(NodeInfoCollector.class.getName()).log(
                    Level.SEVERE, e.getMessage(), e);
        }

        return result;
    }

    private ArrayList<CpuData> queryCpu(String _uuid, String _core) {
        String query = String.format(SqlScripts.SELECT_CPU_DATA, _uuid, _core,
                getTime(this.interval.getStart()), getTime(this.interval.getEnd()));

        ResultSet tempResult = this.hdb.readFromDatabase(query);

        ArrayList<CpuData> result = new ArrayList<CpuData>();
        try {
            while (tempResult.next()) {
                result.add(new CpuData(-1, tempResult.getInt(1),
                        tempResult.getInt(2), tempResult.getInt(3),
                        tempResult.getInt(4), tempResult.getInt(5),
                        tempResult.getInt(6), tempResult.getInt(7),
                        tempResult.getInt(8), tempResult.getInt(9)));
            }
        } catch (SQLException e) {
            Logger.getLogger(NodeInfoCollector.class.getName()).log(
                    Level.SEVERE, e.getMessage(), e);
        }

        return result;
    }

    private String getTime(DateTime date) {
    	return String.format("%s-%s-%s %s:%s:%s", date.getYear(),
                date.getMonthOfYear(), date.getDayOfMonth(),
                date.getHourOfDay(), date.getMinuteOfHour(),
                date.getSecondOfMinute());
            }

}
