-- Table: UnityType
CREATE TABLE IF NOT EXISTS UnityType ( 
    ID    INTEGER,
    UNITY TEXT,
    CONSTRAINT PK_UNITY_TYPE PRIMARY KEY ( ID ) 
);




-- Table: Sensor
CREATE TABLE IF NOT EXISTS Sensor ( 
    ID          INTEGER,
    SENSOR_UUID INTEGER UNIQUE,
    CONSTRAINT 'PK_Sensor' PRIMARY KEY ( ID ) 
);




-- Table: Node
CREATE TABLE IF NOT EXISTS Node ( 
    ID        INTEGER,
    NODE_UUID INTEGER UNIQUE,
    CONSTRAINT 'PK_Node' PRIMARY KEY ( ID ) 
);




-- Table: CpuUsage
CREATE TABLE IF NOT EXISTS CpuUsage ( 
    ID      INTEGER,
    NODE_ID INTEGER  CONSTRAINT 'FK_CpuUsage' REFERENCES Node ( ID ) ON DELETE CASCADE
                                            ON UPDATE CASCADE,
    TIME    DATETIME,
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
    CONSTRAINT 'PK_CpuUsage' PRIMARY KEY ( ID ) 
);




-- Table: DiskUsage
CREATE TABLE IF NOT EXISTS DiskUsage ( 
    ID                             INTEGER,
    NODE_ID                        INTEGER,
    TIME                           DATETIME,
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
    CONSTRAINT 'FK_DiskUsage' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                 ON UPDATE CASCADE 
);




-- Table: EnergyConsumption
CREATE TABLE IF NOT EXISTS EnergyConsumption ( 
    ID      INTEGER,
    NODE_ID INTEGER,
    TIME    DATETIME,
    TOTAL   REAL,
    CPU     REAL,
    DISK    REAL,
    MEM     REAL,
    NET     REAL,
    MISC    REAL,
    PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_EnergyConsumption' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                         ON UPDATE CASCADE 
);




-- Table: MemoryUsage
CREATE TABLE IF NOT EXISTS MemoryUsage ( 
    ID          INTEGER,
    NODE_ID     INTEGER,
    TIME        DATETIME,
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
    CONSTRAINT 'FK_MemoryUsage' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                   ON UPDATE CASCADE 
);




-- Table: NetworkUsage
CREATE TABLE IF NOT EXISTS NetworkUsage ( 
    ID           INTEGER,
    NODE_ID      INTEGER,
    TIME         DATETIME,
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
    CONSTRAINT 'FK_NetworkUsage' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                    ON UPDATE CASCADE 
);




-- Table: SensorUsage
CREATE TABLE IF NOT EXISTS SensorUsage ( 
    ID             INTEGER,
    NODE_ID        INTEGER,
    TIME           INTEGER,
    SENSOR_ID      INTEGER,
    CHANNEL_NUMBER INTEGER,
    UNITY_TYPE_ID  INTEGER,
    VALUE          NUMERIC,
    CONSTRAINT 'PK_Sensor' PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_SensorUsage' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                   ON UPDATE CASCADE 
);




-- Index: idx_CpuUsage
CREATE UNIQUE INDEX IF NOT EXISTS idx_CpuUsage ON CpuUsage ( 
    TIME,
    NODE_ID,
    CORE_ID 
);


-- Index: idx_DiskUsage
CREATE UNIQUE INDEX IF NOT EXISTS idx_DiskUsage ON DiskUsage ( 
    PARTITION_NAME,
    NODE_ID,
    TIME 
);


-- Index: idx_EnergyConsumption
CREATE UNIQUE INDEX IF NOT EXISTS idx_EnergyConsumption ON EnergyConsumption ( 
    TIME,
    NODE_ID 
);


-- Index: idx_MemoryUsage
CREATE UNIQUE INDEX IF NOT EXISTS idx_MemoryUsage ON MemoryUsage ( 
    NODE_ID,
    TIME 
);


-- Index: idx_NetworkUsage
CREATE UNIQUE INDEX IF NOT EXISTS idx_NetworkUsage ON NetworkUsage ( 
    NODE_ID,
    TIME,
    INTERFACE 
);



