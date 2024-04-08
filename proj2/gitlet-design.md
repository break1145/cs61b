# Gitlet Design Document

**Name**:break<p>
**Partner**:ChatGPT(3.5)

## 类和数据结构
**项目结构**
- `/.gitlet`
  - `/blobs`
  - `/commits`
    - `/commitTree`
      - commitTree.ser
    - `/commits`(hashcode)
  - `/staging`
    - stagingArea.ser
    - removed.ser
    - tracked.ser
***
**类设计**
1. `Commit`<p>
```java
//成员
private String message;
private Date currentDate;
private String hashCode;

public transient HashSet<Blob> files;
public HashSet<String> filesCode;
private List<String> parentCodes;
//方法
public Commit(String message);
public Commit(Commit parent, String message);
public boolean saveCommit();
public String getHashCode();
public Date getCurrentDate();
public String getMessage ();
```
2. `Blob`
````java
// 成员
private String path;
private byte[] content;
private File file;
private String shaCode;
public Blob(File f);
// 方法
public String getPath ();
public String getShaCode ();
public void save();
public boolean equals();
    

````
***

## 命令设计
1. init
    创建`/.gitlet`文件夹并补全文件结构。同时向`commitTree`添加初始commit
    - 失败情况：已经存在`/.gitlet`
2. add<p>
    `add [file name]`将当前文件的副本加入暂存区。添加已经暂存的文件时会用新内容覆盖暂存区内文件<p>当文件与`HEAD`指向的`Commit`相同时，不添加并从暂存区内删除(如果存在)
    - 失败情况：文件不存在
3. commit<p>`commit [meaasge]`
创建`newCommit`，更新`parent`,`message`.初始`blobs`为`parent`的`blobs`.根据`stagingArea`和`removedStagingArea`更新blobs,并重新构造`filesCode`  
保存commit：保存`newCommit`中所有文件，保存该`commit`，向`commitTree`加入该`commit` ，更新`HEAD`，清空缓存区  
   - 失败情况：缓存区为空 或 message为空
4. rm

5. log

6. global-log


## Algorithms
1. 更新：程序启动时队所有跟踪的文件执行`add`操作，让`add`分辨哪些文件有更新
2. 
## Persistence

