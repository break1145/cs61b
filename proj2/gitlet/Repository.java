package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
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
        HashSet<File> trackedFiles = new HashSet<>();

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

        //TODO: check if the file is same as the latest commit's
        // get file from commit
        //File latestFile =
        Blob blob = new Blob(file);
        HashSet<Blob> blobHashSet = commitTree.getHeadCommit().files;
        if(blobHashSet.contains(blob)) {
            /*remove if blob in stagingArea is same as latest commit*/
            stagingArea.remove(blob);
            exit(0);
        }

        // overwrite if exist
        stagingArea.remove(blob);
        stagingArea.add(blob);
        writeObject(Staging_Area_File, stagingArea);
        // TODO: deal with case if 'rm'
    }
    public static void commit(String message) {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);

        Commit latestCommit = commitTree.getHeadCommit();
        // get latest commit and initialize new commit with that
        Commit newCommit = new Commit(latestCommit, message);
        newCommit.files = updateFile(newCommit);
        //save commit
        Boolean saved = newCommit.saveCommit();


        // remove files from working directory
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        for(Blob b : removedStagingArea) {
            restrictedDelete(b.getFile());
        }
        removedStagingArea.clear();
        writeObject(Removed_Staging_Area_File, removedStagingArea);

        if(saved) {
            // no file changed
            message("No changes added to the commit.");
            exit(0);
        }
        commitTree.add_Commit(newCommit);
        writeObject(CommitTree_DIR_File, commitTree);
        stagingArea.clear();
        writeObject(Staging_Area_File,stagingArea);
    }
    /**
     * update files in stagingArea and remove from removeStagingArea
     * @return a HashSet contains files. if file differ from one in stagingArea,use it.Or use the old
     * */
    private static HashSet<Blob> updateFile(Commit c) {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        HashSet<Blob> result;
        // compare with latest commit
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        Commit latestCommit = commitTree.getHeadCommit();

        if (c.files != null) {
            result = new HashSet<>(c.files);
            for (Blob b2 : stagingArea) {
                boolean found = false;
                for (Blob b1 : c.files) {
                    if (b1.getPath().equals(b2.getPath())) {
                        found = true;
                        if (!b1.equals(b2)) {
                            result.remove(b1);
                            result.add(b2);
                        }
                        break;
                    }
                }
                if (!found) { // new file in stagingArea
                    result.add(b2);
                }
            }
            // remove files in removeStagingArea
            result = Utils.remove(result, removedStagingArea);
        } else {
            result = Utils.remove(new HashSet<>(stagingArea), removedStagingArea);
        }
        //TODO 遍历result 和latest commit 比较File内容 有变化就取新的
//        for (Blob b : result) {
//
//        }
        return result;
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

















