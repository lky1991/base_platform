#HBase 相关配置参数
##参数一：base.rpc.timeout
解释:rpc的超时时间，默认60s，不建议修改，避免影响正常的业务，在线上环境刚开始配置的是3秒，运行半天后发现了大量的timeout error，原因是有一个region出现了如下问题阻塞了写操作：“Blocking updates … memstore size 434.3m is >= than blocking 256.0m size”可见不能太低。
##参数二：base.rpc.timeout
解释:建立链接的超时时间，应该小于或者等于rpc的超时时间，默认为20s

##参数三：hbase.client.retries.number
解释:重试次数，默认为14，可配置为3
##参数四：hbase.client.pause
解释：重试的休眠时间，默认为1s，可减少，比如100ms
##参数五：hbase.regionserver.lease.period
解释:scan查询时每次与server交互的超时时间，默认为60s，可不调整。