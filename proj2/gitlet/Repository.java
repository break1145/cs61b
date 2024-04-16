package gitlet;

import java.io.File;
import java.util.*;

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

    public static final File Addition_File = join(Staging_Area, "addition.ser");
    public static final File Branch_DIR = join(GITLET_DIR, "branches");
//    public static final File Tracked_DIR = join(Staging_Area, "tracked");
//    public static final File Tracked_DIR_File = join(Tracked_DIR, "tracked.ser");

    /* TODO: fill in the rest of this class. */

    public static void setupFileFolder() {
        GITLET_DIR.mkdir();
        Staging_Area.mkdir();
        Commit_DIR.mkdir();
        Files_DIR.mkdir();
        CommitTree_DIR.mkdir();
        Branch_DIR.mkdir();
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
        HashSet<Blob> additionArea = new HashSet<>();

        // make first commit
        Commit commit = new Commit("initial commit");
        commitTree.add_Commit(commit);
        commit.save();

        //create a default master
        branch master = new branch(commit.hashcode(), "master");
        File newBranchFile = join(Branch_DIR, master.getbranchName());
        writeObject(newBranchFile, master);

        commitTree.setCurrentBranch("master");

        writeObject(Staging_Area_File, stagingArea);
        writeObject(CommitTree_DIR_File, commitTree);
        writeObject(Removed_Staging_Area_File, removedStagingArea);
        writeObject(Addition_File, additionArea);

    }

    /** Adds a copy of the file as it currently exists to the staging area.<p>
     *  If the file already exists,overwrite it.
     * */
    public static void add(File file) {
        if(!file.isFile()) {
            System.out.println("File does not exist.");
            exit(0);
        }
        // get Head Commit
        Blob blob = new Blob(file);
        add(blob, false);
    }
    /**
     * add blob to staging area and check if any change occured
     * @param blob a blob
     *
     * */
    private static void add(Blob blob, boolean fromStartCheck) {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> additionArea = readObject(Addition_File, HashSet.class);

        HashSet<String> filesCodehashSet = commitTree.getHeadCommit().filesCode;
        if(filesCodehashSet.contains(blob.getShaCode())) {
            stagingArea.remove(blob);
            return;
        }

        // if a file in stagingArea and name is same as blob's,change it to blob
        for(Blob blob2 : stagingArea) {
            if(blob2.getFile().equals(blob.getFile())) {
                stagingArea.remove(blob2);
                break;
            }
        }

        stagingArea.add(blob);
        if(!fromStartCheck) {
            additionArea.add(blob);
        }
        writeObject(Staging_Area_File, stagingArea);
        writeObject(Addition_File, additionArea);
    }

    /**
     * when program starts,call each file in headCCommit with method add, and add change into stagingArea
     * */
    public static void startCheck() {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> blobs = commitTree.getHeadCommit().files;
        if(blobs != null) {
            for(Blob blob : blobs) {
                // read file in CWD as headCommit's file path
                Blob newBlob = new Blob(blob.getFile());
                add(newBlob, true);
            }
        }

    }

    public static void commit(String message) {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        HashSet<Blob> additionArea = readObject(Addition_File, HashSet.class);


        if(stagingArea.isEmpty() && removedStagingArea.isEmpty()) {
            // no file changed
            message("No changes added to the commit.");
            exit(0);
        }
        Commit latestCommit = commitTree.getHeadCommit();
        Commit newCommit = new Commit(latestCommit, message);

        // update and save commit
        newCommit = updateFile(newCommit);
        newCommit.save();

        // delete file in CWD
        for(Blob b : removedStagingArea) {
            Utils.restrictedDelete(b.getFile());
        }

        commitTree.add_Commit(newCommit);
        writeObject(CommitTree_DIR_File, commitTree);
        stagingArea.clear();
        writeObject(Staging_Area_File,stagingArea);
        removedStagingArea.clear();
        writeObject(Removed_Staging_Area_File, removedStagingArea);
        additionArea.clear();
        writeObject(Addition_File,additionArea);
    }
    /**
     * update files in stagingArea and remove from removeStagingArea
     * @return a HashSet contains files. if file differ from one in stagingArea,use it.Or use the old
     * */
    private static Commit updateFile(Commit commit) {
        // update commit in stagingArea
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);

        Commit updatedCommit = new Commit(commit);
        HashSet<Blob> updatedFiles;
        if(updatedCommit.files == null) {
            updatedFiles = new HashSet<>();
        } else {
            updatedFiles = new HashSet<>(updatedCommit.files);
        }
        // add files in stagingArea to updated file list
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
        // delete all files in removedStagingArea from commit
        for(Blob b : removedStagingArea) {
            for(Blob b2 : updatedFiles) {
                if(b.getPath().equals(b2.getPath())) {
                    updatedFiles.remove(b2);

                }
            }
        }
        // update file list and its SHA-1 code
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
     * usage Utils.plainFilenamesIn(),traverse all commit in folder commit
     * */
    public static void global_log() {
        List<String> commitList = Utils.plainFilenamesIn(Commit_DIR);
        for(String commitID : commitList) {
            Commit currentCommit = readObject(join(Commit_DIR, commitID), Commit.class);
            System.out.println("===");
            System.out.println("Commit " + currentCommit.getHashCode());
            Formatter formatter = new Formatter(Locale.ENGLISH);
            Date currentDate = new Date();
            String formattedDate = String.valueOf(formatter.format("Date: %ta %tb %td %tT %tY %tz", currentDate, currentDate, currentDate, currentDate, currentDate, currentDate));
            Utils.message(formattedDate);
            System.out.println(currentCommit.getMessage());
            System.out.println();
        }

    }

    /**
     * command find [message]
     *  traverse commit DIR ,find and print appropriate commit's ID
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
     * 1. branch list and current branch
     * 2. tracked files
     * 3. files in removedStagingArea
     * 4. TODO:changed but not commit files
     * 5. TODO:untracked files
     * */
    public static void status() {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        HashSet<Blob> additionArea = readObject(Addition_File, HashSet.class);

        Commit headCommit = commitTree.getHeadCommit();
        //branch
        message("=== Branches ===");
        List<String> branchList= plainFilenamesIn(Branch_DIR);
        branch currentBranch = commitTree.getCurrentBranch();
        for(String branch : Objects.requireNonNull(branchList)) {
            if (branch.equals(currentBranch.getbranchName())) {
                message("*", branch);
            } else {
                message(branch);
            }
        }
        message("");

        //staged(tracked)
        HashSet<Blob> staged = new HashSet<>(headCommit.files);
        staged.addAll(additionArea);
        message("=== Staged Files ===");
        for(Blob b : staged) {
            message(b.getFile().getName());
        }
        message("");

        //TODO: removed (haven't tested)
        message("=== Removed Files ===");
        for(Blob b : removedStagingArea) {
            message(b.getFile().getName());
        }
        message("");

        //modified but not commit
        message("=== Modifications Not Staged For Commit ===");
        //modified
        HashSet<Blob> modified = new HashSet<>(stagingArea);
        modified.removeAll(additionArea);
        for(Blob b : modified) {
            message(b.getFile().getName() + " (modified)");
        }
        //TODO: deleted

        message("");

        //Untracked
        message("=== Untracked Files ===");
        List<String> fileList = Utils.plainFilenamesIn(CWD);
        for(String file : fileList) {
            Blob b = new Blob(new File(file));
            if(!staged.contains(b)) {
                message(b.getFile().getName());
            }
        }
        message("");

    }

    /**
     * command checkout
     * 1. checkout -- [file name]
     *
     * 2. checkout [commit id] -- [file name]
     *
     * 3. checkout [branch name]
     * */
    public static void checkout(String[] args) {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        HashSet<Blob> additionArea = readObject(Addition_File, HashSet.class);
        // case 1

        if (args[1].equals("--")) {

            String filename = args[2];
            Commit headCommit = commitTree.getHeadCommit();

            if(!randw(filename, headCommit)) {
                message("File does not exist in that commit.");
            }
            exit(0);
        }

        // case 2
        if(args[2].equals("--")) {
            String commitID = args[1];
            String filename = args[3];
            List<String> commitIDList = plainFilenamesIn(Commit_DIR);
            if (commitIDList != null && commitIDList.contains(commitID)) {
                Commit forwardCommit = readObject(join(Commit_DIR, commitID), Commit.class);
                if(!randw(filename, forwardCommit)) {
                    message("File does not exist in that commit.");
                    exit(0);
                }
            } else {
                message("No commit with that id exists");
                exit(0);
            }
            exit(0);
        }

        //case 3
        String branchName = args[1];
        if (branchName.equals(commitTree.getCurrentBranch().getbranchName())) {
            message("No need to checkout the current branch.");
            exit(0);
        } else {
            List<String> branchList = plainFilenamesIn(Branch_DIR);
            if (branchList != null && branchList.contains(branchName)) {
                branch forwardBranch = readObject(join(Branch_DIR, branchName), branch.class);
                Commit headCommit = commitTree.getHeadCommit();

                HashSet<Blob> staged = new HashSet<>(headCommit.files);
                staged.addAll(additionArea);
                // case1 untracked
                List<String> fileList = Utils.plainFilenamesIn(CWD);
                for(String file : fileList) {
                    Blob b = new Blob(new File(file));
                    if(!staged.contains(b)) {
                        message("There is an untracked file in the way; delete it, or add and commit it first.");
                        exit(0);
                    }
                }
                // case2 change hasn't been commited
                HashSet<Blob> modified = new HashSet<>(stagingArea);
                modified.removeAll(additionArea);
                for(Blob b : modified) {
                    message("There is an untracked file in the way; delete it, or add and commit it first.");
                    exit(0);
                }
                // case3 all file is tracked and no change to commit,start changing branch

                commitTree.setCurrentBranch(branchName);
                headCommit = commitTree.getHeadCommit();
                // clear CWD
                List<String> filenames = plainFilenamesIn(CWD);
                for(String filename : filenames) {
                    restrictedDelete(new File(filename));
                }
                for(String fileCode : headCommit.filesCode) {
                    Blob newBlob = readObject(join(Files_DIR, fileCode), Blob.class);
                    writeContents(join(CWD, newBlob.getFile().getName()), newBlob.getContent());
                }
                stagingArea.clear();

                writeObject(Staging_Area_File, stagingArea);
                writeObject(CommitTree_DIR_File, commitTree);
                exit(0);
            } else {
                message("No such branch exists.");
                exit(0);
            }
        }



    }

    /**
     * get a file named filename in commit c from blobs and write to CWD
     * @return true if and only if file is found and write successfully,otherwise false
     * */
    public static boolean randw(String filename, Commit c) {
        for(Blob b : c.files) {
            if(b.getFile().getName().equals(filename)) {
                Blob newBlob = readObject(join(Files_DIR, b.getShaCode()), Blob.class);
                writeContents(join(CWD, newBlob.getFile().getName()), newBlob.getContent());
                return true;
            }
        }
        return false;
    }

    /*
     * create a branch which begin at current headCommit
     * */
    public static void branch(String branchName) {
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        branch newBranch = new branch(commitTree.getHeadCommit().getHashCode(), branchName);
        writeObject(join(Branch_DIR, branchName), newBranch);
    }

    /**
    */
    public static void removeBranch(String branchName) {
        restrictedDelete(join(Branch_DIR, branchName));
    }

}

















