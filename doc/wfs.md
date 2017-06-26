# 分布式文件存储服务

### 功能概述

本服务提供服务化分布式文件系统功能

### 场景介绍

提供文件存储接口，支持存放文件、获取文件、获取目录、删除文件、重命名文件等服务化接口

### 特别说明

根据 dubbo 文档的描述，dubbo 协议并不适合文件传输场景，下引用自 dubbo 文档：

- 适用范围：传入传出参数数据包较小（建议小于100K），消费者比提供者个数多，单一消费者无法压满提供者，尽量不要用dubbo协议传输大文件或超大字符串。

因此根据 dubbo 官方文档的建议，本项目采用 hessian 作为服务化协议。

又由于研发支持的 DSF 框架将 hessian 协议作了精简，经过与主办方沟通，我们基于含有 Hessian 协议的官方 dubbo 版本，建议使用 dubbo-2.5.3.jar。

### 服务配置

消费者方在dubbo.xml中配置引用服务信息

```xml
<dubbo:reference id="wfsIO" interface="com.icbc.wfs.service.WfsIO" version="0.0.1" check="false" />
```

在使用服务的地方注入服务

```java
@Resource
private WfsIO wfsIO;
```

###接口方法

| 方法名  | 方法说明  |
| ---- | ----- |
| put  | 存放文件  |
| get  | 获取文件  |
| ren  | 重命名文件 |
| del  | 删除文件  |
| list | 获取目录  |

#### PUT

| 参数类型   | 参数名  | 参数说明 | 示例值                                      |
| ------ | ---- | ---- | ---------------------------------------- |
| String | path | 虚拟路径 | "/remoteDir/remoteFile.jpg"              |
| String | in   | 文件流  | new FileInputStream("/localDir/localFile.jpg") |
```java
/** 存放文件 */
public void put() throws Exception {
  // 虚拟路径
  String remotePath = "/remoteDir/remoteFile.jpg";
  // 本地文件
  FileInputStream in = new FileInputStream("/localDir/localFile.jpg");
  boolean isSucc = wfsIO.put(remotePath, in);
}
```

#### GET

| 参数类型   | 参数名  | 参数说明 | 示例值                         |
| ------ | ---- | ---- | --------------------------- |
| String | path | 虚拟路径 | "/remoteDir/remoteFile.jpg" |

```java
/** 获取文件 */
public void get() throws Exception {
  // 虚拟路径
  String remotePath = "/remoteDir/remoteFile.jpg";
  InputStream stream = wfsIO.get(remotePath);
}
```

#### REN

| 参数类型   | 参数名     | 参数说明  | 示例值                         |
| ------ | ------- | ----- | --------------------------- |
| String | newPath | 新虚拟路径 | "/remoteDir/remoteFile.jpg" |
| String | oldPath | 原虚拟路径 | "/newDir/remoteFile.jpg"    |

```java
/** 重命名文件 */
public void rename() throws Exception {
  // 原虚拟路径
  String prePath = "/remoteDir/remoteFile.jpg";
  // 新虚拟路径
  String newPath = "/newDir/remoteFile.jpg";
  boolean isSucc = wfsIO.ren(newPath, prePath);
}
```
#### DEL

| 参数类型   | 参数名  | 参数说明 | 示例值                         |
| ------ | ---- | ---- | --------------------------- |
| String | path | 虚拟路径 | "/remoteDir/remoteFile.jpg" |
```java
/** 删除文件 */
public void delete() throws Exception {
  // 虚拟路径
  String remotePath = "/remoteDir/remoteFile.jpg";
  boolean isSucc = wfsIO.del(remotePath);
}
```

#### LIST

| 参数类型   | 参数名  | 参数说明 | 示例值           |
| ------ | ---- | ---- | ------------- |
| String | path | 虚拟路径 | "/remoteDir/" |
```java
/** 获取目录 */
public void list() throws Exception {
  // 虚拟路径
  String remoteDir = "/remoteDir/";
  List<String> fileList = wfsIO.list(remoteDir);
}
```

