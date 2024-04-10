package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static gitlet.Utils.*;
import static java.lang.System.exit;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author break
 */
public class Repository {
    /** File structure
     *  - CWD
     *      - .gitlet
     *           - staging
     *              - stagingArea
     *              - removedStagingArea
     *           - commits
     *              - sha code xxx(commit)
     *              - commitTree
     *
     *           - blobs
     *              - sha1 code xxx(files)
     *
     * */
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File Staging_Area = join(GITLET_DIR, "staging");
    public static final File Staging_Area_File = join(Staging_Area, "stagingArea.ser");
    public static final File Removed_Staging_Area_File = join(Staging_Area, "removed.ser");
    public static final File Commit_DIR = join(GITLET_DIR, "commits");
    public static final File CommitTree_DIR = join(Commit_DIR, "commitTree");
    public static final File CommitTree_DIR_File = join(CommitTree_DIR, "commitTree.ser");
    public static final File Files_DIR = join(GITLET_DIR, "blobs");

//    public static final File Tracked_DIR = join(Staging_Area, "tracked");
//    public static final File Tracked_DIR_File = join(Tracked_DIR, "tracked.ser");

    /* TODO: fill in the rest of this class. */

    public static void setupFileFolder() {
        GITLET_DIR.mkdir();
        Staging_Area.mkdir();
        Commit_DIR.mkdir();
        Files_DIR.mkdir();
        CommitTree_DIR.mkdir();
    }

    /**
     *  initialize the gitlet repository and make an initial commit.<p>
     *  if folder '.gitlet' already exists, abort error messages and exit.
     * */
    public static void initialize() {
        if(GITLET_DIR.isDirectory()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            exit(0);
        }
        setupFileFolder();

        HashSet<Blob> stagingArea = new HashSet<>();
        CommitTree commitTree = new CommitTree();
        HashSet<Blob> removedStagingArea = new HashSet<>();

        // make first commit
        Commit commit = new Commit("initial commit");
        commitTree.add_Commit(commit);
        commit.save();

        writeObject(Staging_Area_File, stagingArea);
        writeObject(CommitTree_DIR_File, commitTree);
        writeObject(Removed_Staging_Area_File, removedStagingArea);

    }

    /** Adds a copy of the file as it currently exists to the staging area.<p>
     *  If the file already exists,overwrite it.
     * */
    public static void add(File file) {
        if(!file.isFile()) {
            System.out.println("File does not exist.");
            exit(0);
        }
        // 获取HEAD的Commit
        Blob blob = new Blob(file);
        add(blob);
    }
    /**
     * 将blob加入暂存区，并检查是否有改动。
     * @param blob 一个Blob对象
     *
     * */
    private static void add(Blob blob) {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);

        HashSet<String> filesCodehashSet = commitTree.getHeadCommit().filesCode;
        if(filesCodehashSet.contains(blob.getShaCode())) {
            stagingArea.remove(blob);
            return;
        }

        // 如果暂存区内存在同名文件 将该文件替换为blob
        for(Blob blob2 : stagingArea) {
            if(blob2.getFile().equals(blob.getFile())) {
                stagingArea.remove(blob2);
                break;
            }
        }

        stagingArea.add(blob);
        writeObject(Staging_Area_File, stagingArea);
    }

    /**
     * 程序启动时 对HEAD Commit的所有文件调用add(Blob b) 将更改加入暂存区
     * */
    public static void startCheck() {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> blobs = commitTree.getHeadCommit().files;
        for(Blob blob : blobs) {
            // 根据HEAD Commit的File路径在工作区内读取文件
            Blob newBlob = new Blob(blob.getFile());
            add(newBlob);
        }
    }

    public static void commit(String message) {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);

        if(stagingArea.isEmpty() && removedStagingArea.isEmpty()) {
            // no file changed
            message("No changes added to the commit.");
            exit(0);
        }
        Commit latestCommit = commitTree.getHeadCommit();
        Commit newCommit = new Commit(latestCommit, message);

        // 更新并保存commit
        newCommit = updateFile(newCommit);
        newCommit.save();

        // 从工作区删除文件
        for(Blob b : removedStagingArea) {
            Utils.restrictedDelete(b.getFile());
        }

        commitTree.add_Commit(newCommit);
        writeObject(CommitTree_DIR_File, commitTree);
        stagingArea.clear();
        writeObject(Staging_Area_File,stagingArea);
        removedStagingArea.clear();
        writeObject(Removed_Staging_Area_File, removedStagingArea);
    }
    /**
     * update files in stagingArea and remove from removeStagingArea
     * @return a HashSet contains files. if file differ from one in stagingArea,use it.Or use the old
     * */
    private static Commit updateFile(Commit commit) {
        // 根据暂存区更新 commit
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);

        Commit updatedCommit = new Commit(commit);
        HashSet<Blob> updatedFiles;
        if(updatedCommit.files == null) {
            updatedFiles = new HashSet<>();
        } else {
            updatedFiles = new HashSet<>(updatedCommit.files);
        }
        // 添加暂存区中的文件到更新后的文件列表
        for(Blob b : stagingArea) {
            boolean found = false;
            for(Blob b2 : updatedFiles) {
                if(b.getPath().equals(b2.getPath())) {
                    updatedFiles.remove(b2);
                    updatedFiles.add(b);
                    found = true;
                }
            }
            if(!found) {
                updatedFiles.add(b);
            }
        }
        // 将删除区的文件从commit中删除
        for(Blob b : removedStagingArea) {
            for(Blob b2 : updatedFiles) {
                if(b.getPath().equals(b2.getPath())) {
                    updatedFiles.remove(b2);

                }
            }
        }
        // 更新文件列表和文件 SHA-1 校验码
        updatedCommit.files = updatedFiles;
        updatedCommit.filesCode.clear();
        for (Blob file : updatedFiles) {
            updatedCommit.filesCode.add(file.getShaCode());
        }
        return updatedCommit;
    }



    /**
     * command 'rm'
     * remove files in staging area and delete files if in current commit<p>
     * In short, untrack a file
     * */
    public static void remove(File file) {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        Commit headCommit = commitTree.getHeadCommit();

        Blob blob = new Blob(file);
        if(!stagingArea.contains(blob) && !removedStagingArea.contains(blob)) {
            message("No reason to remove the file.");
            exit(0);
        }
        if(stagingArea.contains(blob)) {
            stagingArea.remove(blob);
        }
        if(headCommit.files.contains(blob)) {
            removedStagingArea.add(blob);
        }

        writeObject(Staging_Area_File, stagingArea);
        writeObject(Removed_Staging_Area_File, removedStagingArea);
    }

    /**
     * command log
     * 利用commitTree中的方法打印从head到初始commit的log
     * */
    public static void log() {
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        commitTree.printTreefromHead();
    }

    /**
     * command global_log
     * 使用Utils.plainFilenamesIn(),遍历commits文件夹内所有commit
     * */
    public static void global_log() {
        List<String> commitList = Utils.plainFilenamesIn(Commit_DIR);
        for(String commitID : commitList) {
            Commit currentCommit = readObject(join(Commit_DIR, commitID), Commit.class);
            System.out.println("===");
            System.out.println("Commit " + currentCommit.getHashCode());
            System.out.println("Date " + currentCommit.getCurrentDate());
            System.out.println(currentCommit.getMessage());
            System.out.println();
        }

    }

    /**
     * command find [message]
     *  遍历commits目录，查找符合条件的commit并输出id
     * */
    public static void find(String message) {
        List<String> commitList = Utils.plainFilenamesIn(Commit_DIR);
        Boolean founded = false;
        for(String commitID : commitList) {
            Commit currentCommit = readObject(join(Commit_DIR, commitID), Commit.class);
            if(currentCommit.getMessage().equals(message)) {
                message(currentCommit.getHashCode());
                founded = true;
            }
        }
        if(!founded) {
            message("Found no commit with that message.");
        }
    }
    /**
     * command status
     * 显示
     * 1. 分支列表及当前分支
     * 2. 追踪的文件
     * 3. 删除暂存区 预删除文件
     * 4. TODO: 修改但未提交的文件
     * 5. TODO: 未追踪的文件
     * */
    public static void status() {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);

        //TODO: branch
        //TODO: staged(tracked)
        //TODO: removed
        //TODO: modified but not commit
        //TODO: Untracked

    }


}

















