package cn.fancychuan.flink.cdc;

import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkCDC {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // flinkCDC将读取binlog的位置信息以状态的方式保存在checkpoint。如果想做到断点续传，需要从checkpoint或者savepoint启动程序
        env.enableCheckpointing(5000L);
        // 指定checkpoint的一致性语义
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
//        // 设置任务关闭的时候保留最后一次checkpoint数据
//        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        // 指定从checkpoint的重启策略
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 2000L));
        // 设置状态后端
        env.setStateBackend(new FsStateBackend("hdfs://hadoop101:8020/flinkCDC/statebackend"));

        System.setProperty("HADOOP_USER_NAME", "hdfs");
        DebeziumSourceFunction<String> mysqlSource = MySqlSource.<String>builder()
                .hostname("hphost").port(3307)
                .username("root").password("123456")
//                .serverTimeZone("Asia/Shanghai")  // 使用高版本的驱动比如8.0以上就可以不加这个
                .databaseList("forlearn")
                // 可选配置，不配置时默认为整库。注意格式是：db.table
                .tableList("forlearn.flinkcdc")
                // 反序列化器，其实 StringDebeziumDeserializationSchema 封装得不是特别好
                .deserializer(new StringDebeziumDeserializationSchema())
                .startupOptions(StartupOptions.initial())
                .build();

        DataStreamSource<String> mysqlCDC = env.addSource(mysqlSource);
        mysqlCDC.print();
        env.execute("flink-mysql-cdc");
    }
}
