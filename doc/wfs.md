# 分布式文件存储服务

### 功能概述

本服务提供分布式文件存储的功能

### 场景介绍

提供文件存储接口，支持存放文件、获取文件、获取目录、删除文件、重命名文件等服务化接口

### 服务配置

消费者方在dubbo.xml中配置引用服务信息，因需要使用Hessian协议的dubbo版本，建议使用dubbo-2.5.3.jar

```xml
<dubbo:reference id="wfsIO" interface="com.icbc.wfs.service.WfsIO" version="0.0.1" check="false" />
```

在使用服务的地方注入服务

```java
@Resource
private WfsIO wfsIO;
```

### 程序示例

```java
/** 存放文件 */
public void put() throws Exception {
  // 虚拟路径
  String remotePath = "/remoteDir/remoteFile.jpg";
  // 本地文件
  FileInputStream in = new FileInputStream("/localDir/localFile.jpg");
  boolean isSucc = wfsIO.put(remotePath, in);
}

/** 获取文件 */
public void get() throws Exception {
  // 虚拟路径
  String remotePath = "/remoteDir/remoteFile.jpg";
  InputStream stream = wfsIO.get(remotePath);
}

/** 重命名文件 */
public void rename() throws Exception {
  // 原虚拟路径
  String prePath = "/remoteDir/remoteFile.jpg";
  // 新虚拟路径
  String newPath = "/newDir/remoteFile.jpg";
  boolean isSucc = wfsIO.ren(newPath, prePath);
}

/** 删除文件 */
public void delete() throws Exception {
  // 虚拟路径
  String remotePath = "/remoteDir/remoteFile.jpg";
  boolean isSucc = wfsIO.del(remotePath);
}

/** 获取目录 */
public void list() throws Exception {
  // 虚拟路径
  String remoteDir = "/remoteDir/";
  List<String> fileList = wfsIO.list(remoteDir);
}
```

