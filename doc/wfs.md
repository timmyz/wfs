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

```java
package com.icbc.wfs.service;

import java.io.InputStream;
import java.util.List;

public interface WfsIO {
	
	/**
	 * 存放文件
	 * @param path 虚拟路径
	 * @param in 文件流
	 * @return 成功失败
	 */
	boolean put(String path, InputStream in);

	/**
	 * 获取文件
	 * @param path 文件流
	 * @return 文件流
	 */
	InputStream get(String path);
	
	/**
	 * 重命名文件
	 * @param newPath
	 * @param oldPath
	 * @return 成功失败
	 */
	boolean ren(String newPath, String oldPath);
	
	/**
	 * 删除文件
	 * @param path 虚拟路径
	 * @return 成功失败
	 */
	boolean del(String path);
	
	/**
	 * 获取目录
	 * @param path 虚拟路径
	 * @return 文件列表
	 */
	List<String> list(String path);
}
```

#### PUT
###### 输入

| 参数类型   | 参数名  | 参数说明 | 示例值                                      |
| ------ | ---- | ---- | ---------------------------------------- |
| String | path | 虚拟路径 | "/remoteDir/remoteFile.jpg"              |
| String | in   | 文件流  | new FileInputStream("/localDir/localFile.jpg") |
###### 输出

​	boolean 成功或失败

###### 异常
如果 path  虚拟路径 为空，则抛出运行时异常"虚拟路径为空"
如果 in  文件流 为空，则抛出运行时异常"文件流为空"

###### 代码示例
```java
/** 存放文件 */
public void put() {
  // 虚拟路径
  String remotePath = "/remoteDir/remoteFile.jpg";
  // 本地文件
  FileInputStream in = new FileInputStream("/localDir/localFile.jpg");
  boolean isSucc = wfsIO.put(remotePath, in);
}
```

#### GET
###### 输入

| 参数类型   | 参数名  | 参数说明 | 示例值                         |
| ------ | ---- | ---- | --------------------------- |
| String | path | 虚拟路径 | "/remoteDir/remoteFile.jpg" |
###### 输出

​	InputStream 文件流

###### 异常
如果 path  虚拟路径 为空，则抛出运行时异常"虚拟路径为空"

###### 代码示例
```java
/** 获取文件 */
public void get() {
  // 虚拟路径
  String remotePath = "/remoteDir/remoteFile.jpg";
  InputStream stream = wfsIO.get(remotePath);
}
```

#### REN
###### 输入

| 参数类型   | 参数名     | 参数说明  | 示例值                         |
| ------ | ------- | ----- | --------------------------- |
| String | newPath | 新虚拟路径 | "/remoteDir/remoteFile.jpg" |
| String | oldPath | 原虚拟路径 | "/newDir/remoteFile.jpg"    |

###### 输出
​	boolean 成功或失败

###### 异常
如果 newPath  新虚拟路径 为空，则抛出运行时异常"新虚拟路径为空"
如果 oldPath  新虚拟路径 为空，则抛出运行时异常"原虚拟路径为空"
如果 newPath  新虚拟路径 == oldPath  新虚拟路径，则抛出运行时异常"新虚拟路径不能等于原虚拟路径"

###### 代码示例
```java
/** 重命名文件 */
public void rename() {
  // 原虚拟路径
  String prePath = "/remoteDir/remoteFile.jpg";
  // 新虚拟路径
  String newPath = "/newDir/remoteFile.jpg";
  boolean isSucc = wfsIO.ren(newPath, prePath);
}
```
#### DEL
###### 输入

| 参数类型   | 参数名  | 参数说明 | 示例值                         |
| ------ | ---- | ---- | --------------------------- |
| String | path | 虚拟路径 | "/remoteDir/remoteFile.jpg" |
###### 输出
​	boolean 成功或失败

###### 异常
如果 path  虚拟路径 为空，则抛出运行时异常"虚拟路径为空"

###### 代码示例
```java
/** 删除文件 */
public void delete() {
  // 虚拟路径
  String remotePath = "/remoteDir/remoteFile.jpg";
  boolean isSucc = wfsIO.del(remotePath);
}
```

#### LIST
###### 输入

| 参数类型   | 参数名  | 参数说明 | 示例值           |
| ------ | ---- | ---- | ------------- |
| String | path | 虚拟路径 | "/remoteDir/" |

###### 输出
​	List<String>为文件列表，不包含下级目录，如
​		remoteFile1.jpg
​		remoteFile2.jpg
​		remoteDir/

###### 异常
如果 path  虚拟路径 为空，则抛出运行时异常"虚拟路径为空"

###### 代码示例
```java
/** 获取目录 */
public void list() {
  // 虚拟路径
  String remoteDir = "/remoteDir/";
  List<String> fileList = wfsIO.list(remoteDir);
}
```

