package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
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
        commitTree.add_Commit(new Commit("initial commit"));

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

        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        // 获取HEAD的Commit
        Blob blob = new Blob(file);
        HashSet<String> filesCodehashSet = commitTree.getHeadCommit().filesCode;
        if(filesCodehashSet.contains(blob.getShaCode())) {
            /*remove if blob in stagingArea is same as latest commit*/
            stagingArea.remove(blob);
            exit(0);
        }

        // 如果缓存区内存在同名文件 重写
        for(Blob blob2 : stagingArea) {
            if(blob2.getFile().equals(blob.getFile())) {
                stagingArea.remove(blob2);
                break;
            }
        }
        stagingArea.add(blob);

        writeObject(Staging_Area_File, stagingArea);
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

        //TODO: 从工作区删除对应文件
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
        Blob blob = new Blob(file);
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        Commit latestCommit = commitTree.getHeadCommit();

        if (latestCommit.files.contains(blob)) {
            removedStagingArea.add(blob);
        } else if (stagingArea.contains(blob)) {
            stagingArea.remove(blob);
        } else {
            message("No reason to remove the file.");
            exit(0);
        }


        writeObject(Staging_Area_File, stagingArea);
        writeObject(Removed_Staging_Area_File, removedStagingArea);
    }

    public static void log() {
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        commitTree.printTreefromHead();
    }

    /**
     * when program start,check files and add to staging area if changed
     *
     * */
    public static void startCheck() {

    }


}

















