##基础参数
###limit修改
set hive.limit.optimize.enable=true
###JOIN优化
###本地模式
###并行操作
set hive.exec.parallel.thread.number =8
set hive.exec.parallel=true
##其他参数调优
参数1.hive.cli.print.header
参数作用:让CLI打印出字段名称
参数设置:
set hive.cli.print.header=true
参数2：hive.map.aggr
参数作用:提高聚合性能
set hive.map.aggr=true;