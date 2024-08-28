# 本单元内容的知识点

1. 通过yaml文件配置多数据源，以及分库分表的规则。
2. 基于AbstractRoutingDataSource封装多数据源，并使用ThreadLocal控制切换数据源。
3. 通过FactoryBean，在mapper代理之上封装一个新的Proxy，提前做拦截。
4. Proxy里使用engine处理sql和参数，获取分片结果。
5. 参数如果非基本类型，需要根据mapping信息进行转换。
6. engine的实现里，需要使用sqlparser解析sql，获取逻辑表名和涉及的列名，以及列对应的值。（对于Insert和其他操作使用方法不同。）
7. 基于Groovy实现分片表达式计算，根据参数获取实际的ds和table。
8. 使用mybatis的拦截器，基于unsafe替换掉逻辑SQL为实际SQL。