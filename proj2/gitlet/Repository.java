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
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    /** File structure
     *  - CWD
     *      - .gitlet
     *           - staging
     *              - stagingArea
     *              - removedStagingArea
     *           - commits
     *              - sha code xxx(commit)
     *              - commitTree
     *           - blobs
     *              - sha1 code xxx(files)
     *
     * */
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

        // make first commit
        HashSet<Blob> stagingArea = new HashSet<>();
        writeObject(Staging_Area_File, stagingArea);
        CommitTree commitTree = new CommitTree();
        writeObject(CommitTree_DIR_File, commitTree);
        HashSet<Blob> removedStagingArea = new HashSet<>();
        writeObject(Removed_Staging_Area_File, removedStagingArea);


        Repository.commit("initial commit");

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
        if(stagingArea.contains(blob)) {
            stagingArea.remove(blob);
        }
        stagingArea.add(blob);

        writeObject(Staging_Area_File, stagingArea);
        // TODO: deal with case if 'rm'
    }
    public static void commit(String meassage) {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        if (stagingArea.isEmpty()) {
            message("No changes added to the commit.");
            exit(0);
        }

        Commit latestCommit = commitTree.getHeadCommit();

        // get latest commit and initialize new commit with that
        Commit newCommit = new Commit(latestCommit, meassage);
        newCommit.files = updateFile(newCommit, stagingArea);

        commitTree.add_Commit(newCommit);
        writeObject(CommitTree_DIR_File, commitTree);

        //save commit
        newCommit.saveCommit();
        stagingArea.clear();
        writeObject(Staging_Area,stagingArea);

        // remove files from working directory
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        for(Blob b : removedStagingArea) {
            Utils.restrictedDelete(b.getFile());
        }
        removedStagingArea.clear();
        writeObject(Removed_Staging_Area_File, removedStagingArea);

    }
    /**
     * update files in stagingArea and remove from removeStagingArea
     * @return a HashSet contains files. if file differ from one in stagingArea,use it.Or use the old
     * */
    private static HashSet<Blob> updateFile(Commit c, HashSet<Blob> stagingArea) {
        HashSet<Blob> result = new HashSet<>(c.files);
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
        // remove files in removedStagingArea
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        result = Utils.remove(result, removedStagingArea);
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




}

















