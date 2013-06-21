-- SQL script generated by FireStorm/DAO 4.0.1
-- Visit http://www.codefutures.com/firestormdao for more information

CREATE TABLE Sensor(
ID INTEGER NOT NULL auto_increment,
SENSOR_ID INTEGER NOT NULL,
primary key (ID, SENSOR_ID)
);

CREATE TABLE Time(
ID INTEGER NOT NULL,
SECOND INTEGER NOT NULL,
MINUTE INTEGER NOT NULL,
HOUR INTEGER NOT NULL,
M_DAY INTEGER ,
MONTH INTEGER NOT NULL,
YEAR INTEGER NOT NULL,
W_DAY INTEGER ,
Y_DAY INTEGER ,
IS_DST INTEGER ,
primary key (ID, SECOND, MINUTE, HOUR, MONTH, YEAR)
);

CREATE TABLE Node(
ID INTEGER NOT NULL auto_increment,
NODE_UUID INTEGER NOT NULL,
primary key (ID, NODE_UUID)
);

CREATE TABLE UnityType(
ID INTEGER NOT NULL auto_increment,
UNITY TEXT NOT NULL,
primary key (ID)
);

CREATE TABLE CpuUsage(
ID INTEGER NOT NULL auto_increment,
NODE_UUID INTEGER NOT NULL,
TIME_ID INTEGER NOT NULL,
CORE_ID INTEGER NOT NULL,
USER INTEGER ,
NICE INTEGER ,
SYSMODE INTEGER ,
IDLE INTEGER ,
IOWAIT INTEGER ,
IRQ INTEGER ,
SOFTIRQ INTEGER ,
STEAL INTEGER ,
GUEST INTEGER ,
primary key (ID, NODE_UUID, TIME_ID, CORE_ID)
);

CREATE TABLE DiskUsage(
ID INTEGER NOT NULL auto_increment,
NODE_UUID INTEGER NOT NULL,
TIME_ID INTEGER NOT NULL,
PARTITION_NAME INTEGER NOT NULL,
READS_COMPLETED INTEGER ,
READS_MERGED INTEGER ,
WRITES_MERGED INTEGER ,
SECTORS_READ INTEGER ,
MILLISECONDS_READING INTEGER ,
WRITES_COMPLETED INTEGER ,
SECTORS_WRITTEN INTEGER ,
MILLISECONDS_WRITING INTEGER ,
IO_IN_PROGRESS INTEGER ,
MILLISECONDS_SPENT_IN_IO INTEGER ,
WEIGHTED_MILLISECONDS_DOING_IO INTEGER ,
primary key (ID, NODE_UUID, TIME_ID, PARTITION_NAME)
);

CREATE TABLE MemoryUsage(
ID INTEGER NOT NULL auto_increment,
NODE_UUID INTEGER NOT NULL,
TIME_ID INTEGER NOT NULL,
SIZE INTEGER ,
RESIDENT INTEGER ,
SHARE INTEGER ,
TEXT INTEGER ,
DATA INTEGER ,
VIRTUALSIZE INTEGER ,
RSS INTEGER ,
RSSLIM INTEGER ,
MEM_TOTAL INTEGER ,
MEM_USED INTEGER ,
MEM_FREE INTEGER ,
MEM_BUFFERS INTEGER ,
MEM_CACHED INTEGER ,
primary key (ID, NODE_UUID, TIME_ID)
);

CREATE TABLE NetworkUsage(
ID INTEGER NOT NULL auto_increment,
NODE_UUID INTEGER NOT NULL,
TIME_ID INTEGER NOT NULL,
INTERFACE TEXT ,
R_BYTES INTEGER ,
R_PACKETS INTEGER ,
R_ERRORS INTEGER ,
R_DROP INTEGER ,
R_FIFO INTEGER ,
R_FRAME INTEGER ,
R_COMPRESSED INTEGER ,
R_MULTICAST INTEGER ,
T_BYTES INTEGER ,
T_PACKETS INTEGER ,
T_ERRORS INTEGER ,
T_DROP INTEGER ,
T_FIFO INTEGER ,
T_COLLS INTEGER ,
T_CARRIER INTEGER ,
T_COMPRESSED INTEGER ,
primary key (ID, NODE_UUID, TIME_ID)
);

CREATE TABLE SensorUsage(
ID INTEGER NOT NULL auto_increment,
NODE_UUID INTEGER NOT NULL,
TIME_ID INTEGER NOT NULL,
SENSOR_ID INTEGER NOT NULL,
CHANNEL_NUMBER INTEGER ,
UNITY_TYPE_ID INTEGER ,
VALUE NUMERIC(0,0) ,
primary key (ID, NODE_UUID, TIME_ID, SENSOR_ID)
);

CREATE TABLE EnergyConsumption(
ID INTEGER NOT NULL auto_increment,
NODE_UUID INTEGER NOT NULL,
TIME_ID INTEGER NOT NULL,
TOTAL REAL ,
CPU REAL ,
DISK REAL ,
MEM REAL ,
NET REAL ,
MISC REAL ,
primary key (ID, NODE_UUID, TIME_ID)
);

ALTER TABLE CpuUsage ADD FOREIGN KEY (NODE_UUID) REFERENCES Node (ID);

ALTER TABLE CpuUsage ADD FOREIGN KEY (TIME_ID) REFERENCES Time (ID);

ALTER TABLE DiskUsage ADD FOREIGN KEY (NODE_UUID) REFERENCES Node (ID);

ALTER TABLE DiskUsage ADD FOREIGN KEY (TIME_ID) REFERENCES Time (ID);

ALTER TABLE MemoryUsage ADD FOREIGN KEY (NODE_UUID) REFERENCES Node (ID);

ALTER TABLE MemoryUsage ADD FOREIGN KEY (TIME_ID) REFERENCES Time (ID);

ALTER TABLE NetworkUsage ADD FOREIGN KEY (NODE_UUID) REFERENCES Node (ID);

ALTER TABLE NetworkUsage ADD FOREIGN KEY (TIME_ID) REFERENCES Time (ID);

ALTER TABLE SensorUsage ADD FOREIGN KEY (NODE_UUID) REFERENCES Node (ID);

ALTER TABLE SensorUsage ADD FOREIGN KEY (TIME_ID) REFERENCES Time (ID);

ALTER TABLE SensorUsage ADD FOREIGN KEY (SENSOR_ID) REFERENCES Sensor (ID);

ALTER TABLE EnergyConsumption ADD FOREIGN KEY (NODE_UUID) REFERENCES Node (ID);

ALTER TABLE EnergyConsumption ADD FOREIGN KEY (TIME_ID) REFERENCES Time (ID);

