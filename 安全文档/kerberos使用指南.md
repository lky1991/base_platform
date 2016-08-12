# Kerberos部署实施指南
## Kerberos简介
## Kerberos在Hadoop中的应用
## Kerberos部署
## Kerberos HA部署
在生产环境里，单节点的Kerberos一旦发生故障，服务将在短时间内不可用，数据也有丢失的风险，因此需要部署多副本的KDC，保证服务可用性与数据安全。Kerberos可以通过安装多个slave KDC，master kdc每隔一段时间便将数据分发到各个节点的slave kdc，保证数据同步。同时，slave kdc同样可以接受第三方的kerberos认证请求，并返回相应的tgt，但是slave kdc不具备管理功能。
#### 安装并配置master KDC
当你使用`yum install krb5-server krb5-libs krb5-workstation`安装好MIT-Kerberos的组件后，需要进行一系列的配置。下面我们先约定一下命名：
> master.domain  - master KDC域名   
  slave.domain   - slave KDC域名   
  admin/admin    - admin principal   
  HADOOP         - realm name

通常来说，你需要在/etc/krb.conf 和{KDC_HOME}/kdc.conf进行一系列的配置，按照上面的约定，krb.conf指定了realm相关配置，例如默认realm、realm的组件分布，应该是这样的：
```
[libdefaults]
    default_realm = HADOOP

[realms]
    HADOOP = {
        kdc = master.domain
        kdc = slave.domain
        admin_server = master.domain
    }
[logging]
    kdc = FILE:/var/log/krb5kdc.log
    admin_server = FILE:/var/log/kadmin.log
    default = FILE:/var/log/krb5lib.log
```
而kdc.conf制订了一些具体的配置，端口、tgt最大生命周期等，是这样的：
```
[kdcdefaults]
    kdc_ports = 88,750

[realms]
    HADOOP = {
        kadmind_port = 749
        max_life = 12h 0m 0s
        max_renewable_life = 7d 0h 0m 0s
        master_key_type = aes256-cts
        supported_enctypes = aes256-cts:normal aes128-cts:normal
    }
```

以上配置多数都有默认值，kdc.conf即使不配置，也不会影响使用。通常来说，kdc.conf是krb.conf的一个补充，krb.conf既作用于KDC，也作用于客户端(kdc位置信息，kadmin位置信息等)，而kdc.conf则只包含KDC相关配置，在kdc运行过程中，会将二者合并到一个文件。   
接下来，你需要配置{KDC_HOME}/kadm5.acl:
```
*/admin@hadoop *
```
接下来创建数据库，执行`kdb5_util create -r HADOOP -s`
使用kadmin.local 创建admin：
```
kadmin.local

addprinc admin/admin@HADOOP
```
接下来分别启动kdc和kadmin：
```
/etc/init.d/krb5dc start
/etc/init.d/kadmin start
```
#### 安装slave KDC
经过上面的配置，master KDC已经配置完毕并启动。你可能注意到，目前为止，与单机版的KDC相比较，唯一多出的一项就是在krb.conf中指定了2个KDC。   
现在，首先需要做的就是在master.domainmaster.domain上为每个KDC所在的host添加principal，只有持有验证为这些principal才可以参与KDC之间的数据同步。
```
kadmin.local
addprinc -randkey host/master.domain
addprinc -randkey host/slave.domain
```
上面已经提到，keytab是由各个KDC持有的，因此你需要手动把每个principal导出成keytab，并将keytab分发到各自的节点上，例如slave.domain。登录kadmin.local导出keytab到/etc/krb5.keytab
```
kadmin.local
ktadd host/slave.domain
```
将生成的krb5.keytab传输到slave.domain的/etc/目录下。
上述步骤完成后，便可以配置slave KDC了，将master.domain的krb.conf、kdc.conf、kadm5.acl、.k5.HADOOP复制到slave.domain的镜像位置。接着，需要使用上面创建的各个host principal，将这些principal写进每个kdc的kpropd.acl文件(在kdc.conf一级，默认没有，自己创建)，表示允许这些principal的kdc向向这个节点推送数据(其实是允许master推送，写上所有的是方便master切换)。kpropd.acl大概是这样的:
```
host/master.domain
host/slave.domain
```
接着，启动每个slave kdc的kpropd
```
kpropd -S
```
至此，所有slave kdc虽然没有启动，但是各个kpropd已经准备好接受master kdc的数据推送了，由于kerberos做的不完善，你需要手动写脚本，并使用crontab定期推送数据，大概是这样的：
```shell
#!/bin/sh

kdclist = "slave.domain"

kdb5_util dump /usr/local/var/krb5kdc/slave_datatrans

for kdc in $kdclist
do
    kprop -f /usr/local/var/krb5kdc/slave_datatrans $kdc
done
```
在第一次推送后，你就可以去启动所有slave 的kdc了，因为到这个时候，kdc才真正有数据(之前切记不要在slave kdc上执行`kdb5_util create -r HADOOP -s`)
#### 切换master KDC

  其实master kdc与slave kdc最大的区别就在于：msater节点kadmin是开启的，并且有同步脚本在定时执行，所以切换master步骤如下：
  old master：
  * 关闭kadmin，防止principal变动
  * 关掉你的定时任务脚本
  * 手动在执行以下同步脚本，保证各数据一致
  new master:
  * 开启kadmin
  * 开启定时脚本
  * 修改各个节点的krb.conf，修改kadmin信息，并重启


























123
