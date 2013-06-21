package usage;

import java.util.ArrayList;

public class EstimationData {
    private final int nodeUUID;
    private final ArrayList<CpuData> rawCpuData;
    private final ArrayList<Integer> rawDiskData;
    private final ArrayList<Integer> rawMemData;

    public EstimationData(String _nodeUUID, ArrayList<CpuData> _cpuRawData,
                          ArrayList<Integer> _diskRawData, ArrayList<Integer> _memRawData) {
        this.nodeUUID = Integer.parseInt(_nodeUUID);
        this.rawCpuData = _cpuRawData;
        this.rawDiskData = _diskRawData;
        this.rawMemData = _memRawData;

    }

    public Integer getNodeUUID() {
        return this.nodeUUID;
    }

    /**
     * @return the rawCpuData
     */
    public ArrayList<CpuData> getRawCpuData() {
        return rawCpuData;
    }

    /**
     * @return the rawDiskData
     */
    public ArrayList<Integer> getRawDiskData() {
        return rawDiskData;
    }

    /**
     * @return the rawMemData
     */
    public ArrayList<Integer> getRawMemData() {
        return rawMemData;
    }

}
