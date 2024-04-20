# Gitlet Design Document

**Name**:break<p>
**Partner**:ChatGPT

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
  - `/branches`
    - master
    - xxx
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
HashSet<Blob> stagingArea;
HashSet<Blob> removedStagingArea;
HashSet<Blob> addition;
```
暂存区。当调用`add(File f)`或`add(Blob b)`时向其中添加。
程序启动时，尝试对所有已追踪的文件调用`add()`，以达到检测文件更新的目的。也就是说，当调用`commit()`时，暂存区内会包含`add()`的文件和有更改的文件  
[已追踪] `当前HEAD指向的Commit包含的文件`∪`暂存区内文件`
`addition`代表已添加但未提交的文件，仅用于 `git status`
***
3. 'branches'
分支是对`commit`的引用。 `initialize()`会创建默认分支 `master`，从`initial commit`开始。  
每个`branch`指向一个`commit`，通过遍历`commitTree`获得`branch`关系。


## 命令设计
1. init
    创建`/.gitlet`文件夹并补全文件结构。同时向`commitTree`添加初始commit,创建`master`分支并设置为当前分支
    
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
    1. checkout -- [file name]
    获取文件在头提交中存在的版本，并将其放入工作目录，
    覆盖已经存在的文件版本（如果存在的话）。新文件版本不会被暂存。
    ```
    从头提交获取对应文件并写入工作区
    ```
    2. checkout [commit id] -- [file name]
    获取文件在具有给定ID的提交中存在的版本，并将其放入工作目录，
    覆盖已经存在的文件版本（如果存在的话）。新文件版本不会被暂存。
    ```
    根据id查找commit，获取文件、写入工作区
    ```
    3. checkout [branch name]
    获取给定分支头部提交中的所有文件，并将它们放入工作目录，
    覆盖已经存在的文件版本（如果存在的话）。此命令结束后，给定分支将被视为当前分支（HEAD）。
    任何在当前分支中被跟踪但在被检出分支中不存在的文件将被删除。暂存区被清空，除非被检出分支是当前分支。
    ```
    实现 通过commitID的前6位 来checkout：额外维护一个哈希表`HsahMap<String, String>`
    ```
   HashMap<String, String>
    a0da1e->
    a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
   ```
   

   - 失败情况

     1. 文件不存在，输出`File does not exist in that commit.`
     2. 给出的id不存在，输出`No commit with that id exists.`
     3. - 给出的分支名不存在，输出`No such branch exists.`
        - 分支为当前分支 输出`No need to checkout the current branch.`
        - 工作区有文件未跟踪，输出`There is an untracked file in the way; delete it, or add and commit it first.`

     以上情况均不修改工作区
   
9. branch  

    *Usage*: `java gitlet.Main branch [branch name]`

    ```java
    private Commit startCommit;
    private Commit headCommit;
    private String branchName;
    getter();
    setter();
    ```
    每个分支包含起始commit、headCommit和分支名。
    `branch [branch name]`以当前headCommit为起点创建一个分支。  
    将当前分支储存在`commitTree`中，调用`addCommit`方法后修改该分支的`headCommit`

10. rm-branch

    *Usage*: `java gitlet.Main rm-branch [branch name]`

    删除对应分支

11. reset
    将给定commit所有文件检出，清空暂存区，将当前分支的headCommmit移到该commit
    思路：清空工作区、暂存区，对给定Commit的所有文件调用checkout

12. merge

      *Usage*: `java gitlet.Main merge [branch name]`

      			@param branch name:给定分支

      描述：将给定分支的文件合并到当前分支

      *分割点((Split point)*：两个分支 `headCommit`的最近公共祖先

      进行合并操作前，先进行检查：

      ```mathematica
            A --- B --- C  (master)
                 \
                  D --- E  (feature)
      ```

     两个Commit的最近公共祖先为分割点

     1. 如果分割点是与给定分支相同的提交（例如提交 E），则说明给定分支已经包含了当前分支的所有内容，合并已经完成，无需执行任何操作。
     2. 

     

      否则，执行以下步骤

      1. 如果某个文件在给定分支被修改过，但在当前分支未被修改，则将给定分支的`headCommit`中`checkout`该文件，并自动暂存[检查文件是否被修改可比较其SHA-1 code]

      2. 在当前分支被修改过的文件，如果不在给定分支中，保持不变(还放在当前分支)

      3. 任何在当前分支和给定分支中以相同方式修改的文件（即，现在两个文件具有相同的内容或都已被删除）在合并时保持不变。

         如果一个文件被两个分支都删除，但工作区内有一个同名文件，保持其未跟踪状态

      4. 不在分割点，只在当前分支的文件保持不变

      5. 不在分割点，只在给定分支的文件应该被`checkout`并加入暂存区

      6. 在分割点，当前分支未修改过，但在给定分支中被删除的文件应该被删除并取消追踪

      7. 在分割点，给定分支未修改过，但在当前分支被删除的文件应该保持不变(merge后无此文件)

      8. 在当前分支和给定分支 以不同方式修改的文件被认为是*冲突(conflict)*，有以下几种情况

         - 都被修改，但内容不同
         - 在一个分支修改，另一个分支删除
         - 在分割点不存在，但在当前分支和给定分支中有不同内容

         这种情况下，将冲突部分替换为

          ```
         <<<<<<< HEAD
         contents of file in current branch
         =======
         contents of file in given branch
         >>>>>>>
          ```

      所有文件都被更新后，分割点不是两个分支的头，merge操作会自动提交commit`Merged [given branch name] into [current branch name].`到log 如果merge遇到了冲突，输出信息`Encountered a merge conflict.` 到终端。

      合并提交与其他提交不同：它们将当前分支的头（称为第一个父级）和命令行上给出的要合并的分支的头记录为父级。

      *失败情况*： PASS

     

     实现思路

     1. `Utils.java` 实现 `public Commit LCA(Commit a, Commit b);` 返回`CommitTree`上两个`Commit`的最近公共祖先
        - LCA：
          1. 倍增 O(logn)
          2. dfs O(n) 由于文档要求复杂度为O(NlogN),使用此方法
        - 

      ​	

13. 



## Algorithms
1. 更新：程序启动时队所有跟踪的文件执行`add`操作，让`add`分辨哪些文件有更新
2. 
## Persistence

