-- Table: Sensor
CREATE TABLE Sensor ( 
    ID        INTEGER,
    SENSOR_ID INTEGER,
    CONSTRAINT PK_Sensor PRIMARY KEY ( ID ) 
);




-- Table: Time
CREATE TABLE Time ( 
    ID     INTEGER,
    SECOND INTEGER,
    MINUTE INTEGER,
    HOUR   INTEGER,
    M_DAY  INTEGER,
    MONTH  INTEGER,
    YEAR   INTEGER,
    W_DAY  INTEGER,
    Y_DAY  INTEGER,
    IS_DST INTEGER,
    CONSTRAINT PK_Time PRIMARY KEY ( ID ) 
);




-- Table: Node
CREATE TABLE Node ( 
    ID      INTEGER,
    NODE_UUID INTEGER,
    CONSTRAINT PK_Node PRIMARY KEY ( ID ) 
);




-- Table: UnityType
CREATE TABLE UnityType ( 
    ID    INTEGER,
    UNITY TEXT,
    CONSTRAINT PK_UNITY_TYPE PRIMARY KEY ( ID ) 
);




-- Table: CpuUsage
CREATE TABLE CpuUsage ( 
    ID      INTEGER,
    NODE_UUID INTEGER,
    TIME_ID INTEGER,
    CORE_ID INTEGER,
    USER    INTEGER,
    NICE    INTEGER,
    SYSMODE INTEGER,
    IDLE    INTEGER,
    IOWAIT  INTEGER,
    IRQ     INTEGER,
    SOFTIRQ INTEGER,
    STEAL   INTEGER,
    GUEST   INTEGER,
    CONSTRAINT 'PK_CpuUsage' PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_CpuUsage_Node' FOREIGN KEY ( NODE_UUID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                     ON UPDATE CASCADE 
);




-- Table: DiskUsage
CREATE TABLE DiskUsage ( 
    ID                             INTEGER,
    NODE_UUID                        INTEGER,
    TIME_ID                        INTEGER,
    PARTITION_NAME                 INTEGER,
    READS_COMPLETED                INTEGER,
    READS_MERGED                   INTEGER,
    WRITES_MERGED                  INTEGER,
    SECTORS_READ                   INTEGER,
    MILLISECONDS_READING           INTEGER,
    WRITES_COMPLETED               INTEGER,
    SECTORS_WRITTEN                INTEGER,
    MILLISECONDS_WRITING           INTEGER,
    IO_IN_PROGRESS                 INTEGER,
    MILLISECONDS_SPENT_IN_IO       INTEGER,
    WEIGHTED_MILLISECONDS_DOING_IO INTEGER,
    CONSTRAINT 'PK_DiskUsage' PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_DiskUsage_Node' FOREIGN KEY ( NODE_UUID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                      ON UPDATE CASCADE 
);




-- Table: MemoryUsage
CREATE TABLE MemoryUsage ( 
    ID          INTEGER,
    NODE_UUID     INTEGER,
    TIME_ID     INTEGER,
    SIZE        INTEGER,
    RESIDENT    INTEGER,
    SHARE       INTEGER,
    TEXT        INTEGER,
    DATA        INTEGER,
    VIRTUALSIZE INTEGER,
    RSS         INTEGER,
    RSSLIM      INTEGER,
    MEM_TOTAL   INTEGER,
    MEM_USED    INTEGER,
    MEM_FREE    INTEGER,
    MEM_BUFFERS INTEGER,
    MEM_CACHED  INTEGER,
    CONSTRAINT 'PK_MemoryUsage' PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_MemoryUsage_Node' FOREIGN KEY ( NODE_UUID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                        ON UPDATE CASCADE 
);




-- Table: NetworkUsage
CREATE TABLE NetworkUsage ( 
    ID           INTEGER,
    NODE_UUID      INTEGER,
    TIME_ID      INTEGER,
    INTERFACE    TEXT,
    R_BYTES      INTEGER,
    R_PACKETS    INTEGER,
    R_ERRORS     INTEGER,
    R_DROP       INTEGER,
    R_FIFO       INTEGER,
    R_FRAME      INTEGER,
    R_COMPRESSED INTEGER,
    R_MULTICAST  INTEGER,
    T_BYTES      INTEGER,
    T_PACKETS    INTEGER,
    T_ERRORS     INTEGER,
    T_DROP       INTEGER,
    T_FIFO       INTEGER,
    T_COLLS      INTEGER,
    T_CARRIER    INTEGER,
    T_COMPRESSED INTEGER,
    CONSTRAINT 'PK_NetworkUsage' PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_NetworkUsage_Node' FOREIGN KEY ( NODE_UUID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                         ON UPDATE CASCADE 
);




-- Table: SensorUsage
CREATE TABLE SensorUsage ( 
    ID             INTEGER,
    NODE_UUID        INTEGER,
    TIME_ID        INTEGER,
    SENSOR_ID      INTEGER,
    CHANNEL_NUMBER INTEGER,
    UNITY_TYPE_ID  INTEGER,
    VALUE          NUMERIC,
    CONSTRAINT 'PK_Sensor' PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_Sensor_Node' FOREIGN KEY ( NODE_UUID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                   ON UPDATE CASCADE 
);




-- Table: EnergyConsumption
CREATE TABLE EnergyConsumption ( 
    ID      INTEGER PRIMARY KEY AUTOINCREMENT,
    NODE_UUID INTEGER     REFERENCES Node ( ID ) ON DELETE CASCADE
                                           ON UPDATE CASCADE,
    TIME_ID INTEGER,
    TOTAL   REAL,
    CPU     REAL,
    DISK    REAL,
    MEM     REAL,
    NET     REAL,
    MISC    REAL 
);




-- Index: idx_CpuUsage
CREATE UNIQUE INDEX idx_CpuUsage ON CpuUsage ( 
    TIME_ID,
    NODE_UUID,
    CORE_ID 
);


-- Index: idx_DiskUsage
CREATE UNIQUE INDEX idx_DiskUsage ON DiskUsage ( 
    PARTITION_NAME,
    NODE_UUID,
    TIME_ID 
);


-- Index: idx_MemoryUsage
CREATE UNIQUE INDEX idx_MemoryUsage ON MemoryUsage ( 
    NODE_UUID,
    TIME_ID 
);


-- Index: idx_NetworkUsage
CREATE UNIQUE INDEX idx_NetworkUsage ON NetworkUsage ( 
    NODE_UUID,
    TIME_ID,
    INTERFACE 
);


-- Index: idx_SensorUsage
CREATE UNIQUE INDEX idx_SensorUsage ON SensorUsage ( 
    CHANNEL_NUMBER,
    TIME_ID,
    NODE_UUID,
    SENSOR_ID 
);


-- Index: idx_EnergyConsumption
CREATE UNIQUE INDEX idx_EnergyConsumption ON EnergyConsumption ( 
    TIME_ID,
    ID,
    NODE_UUID 
);


-- Index: idx_Time
CREATE UNIQUE INDEX idx_Time ON Time ( 
    YEAR,
    W_DAY,
    Y_DAY,
    HOUR,
    M_DAY,
    MINUTE,
    SECOND,
    MONTH 
);



