## TODO


- 非官方Flink-sink的实现细节，比如RedisSink是bahir提供的，实例化的时候需要传入redis配置以及实现一个mapper
- Flink的TaskManager中，slot这种架构模式的有点是什么？为什么flink要采用固定资源大小的slot编程模式呢？是否还有其他的编程模式
- Flink的几种图是什么设计，为什么flink要采取这种方式（执行图、数据流图）
- DataFlow和StreamGraph是什么关系？是否为不同视角，DataFlow是逻辑上的概念，对代码的可视化，更侧重数据流的关系；而StreamGraph虽然也是逻辑上的概念，但是更多的是从执行视角看的程序的拓扑结构。
- Flink的StreamGraph、JobGraph、ExecutionGraph的数据结构是什么样子的
- Flink的Sink通过JDBC来自定义时，如何使用连接池来提高性能？避免一个流就创建一次连接