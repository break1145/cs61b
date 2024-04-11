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
3. `Staging_Area`
```java
HashSet<Blob> stagingArea = new HashSet<>();
HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
```
暂存区。当调用`add(File f)`或`add(Blob b)`时向其中添加。
程序启动时，尝试对所有已追踪的文件调用`add()`，以达到检测文件更新的目的。也就是说，当调用`commit()`时，暂存区内会包含`add()`的文件和有更改的文件  
[已追踪] `当前HEAD指向的Commit包含的文件`∪`暂存区内文件`
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
4. rm<p> `rm [file]`</p>
检查file，如果
   - 在`StagingArea`<p>
        从暂存区删除，取消跟踪
   - 在`HEAD Commit`<p>
        加入`removedStagingArea`标记为待删除。下次提交时候从工作区删除
   - 不在`StagingArea`，不在`HEAD Commit`<p>
        输出 No reason to remove the file.
5. log
6. global-log
7. status
   1. 分支列表及当前分支 
        TODO
   2. 追踪的文件
        最新`commit`∪`StagingArea`
   3. 删除暂存区 预删除文件
        `removedStagingArea`
   4. 修改但未提交的文件
        `startcheck()`会将修改的文件加入stagingArea。也就是说，`stagingArea`∩`LatestCommit`的结果就是修改但未提交的文件
   5. 未追踪的文件
        遍历工作区，与`追踪文件`的差
8. checkout
   ```
   java gitlet.Main checkout -- [file name]
   获取文件在头提交中存在的版本，并将其放入工作目录，
   覆盖已经存在的文件版本（如果存在的话）。新文件版本不会被暂存。
   ```
   从头提交获取对应文件并写入工作区
   ```
   java gitlet.Main checkout [commit id] -- [file name]
   获取文件在具有给定ID的提交中存在的版本，并将其放入工作目录，
   覆盖已经存在的文件版本（如果存在的话）。新文件版本不会被暂存。
   ```
   根据id查找commit，获取文件、写入工作区
   ```
   java gitlet.Main checkout [branch name]
   获取给定分支头部提交中的所有文件，并将它们放入工作目录，
   覆盖已经存在的文件版本（如果存在的话）。此命令结束后，给定分支将被视为当前分支（HEAD）。
   任何在当前分支中被跟踪但在被检出分支中不存在的文件将被删除。暂存区被清空，除非被检出分支是当前分支。
   ```
   

   - 失败情况

     1. 文件不存在，输出`File does not exist in that commit.`
     2. 给出的id不存在，输出`No commit with that id exists.`
     3. - 给出的分支名不存在，输出`No such branch exists.`
        - 分支为当前分支 输出`No need to checkout the current branch.`
        - 工作区有文件未跟踪，输出`There is an untracked file in the way; delete it, or add and commit it first.`

     以上情况均不修改工作区

## Algorithms
1. 更新：程序启动时队所有跟踪的文件执行`add`操作，让`add`分辨哪些文件有更新
2. 
## Persistence

