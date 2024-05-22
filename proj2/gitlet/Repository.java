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
    public static final File Commit_Prefix_Map_File = join(Staging_Area, "commitPrefixMap.ser");
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
        HashMap<String, String> commitPrefixMap = new HashMap<>();
        writeObject(Commit_Prefix_Map_File, commitPrefixMap);

        // make first commit
        Commit commit = new Commit("initial commit");
        commitTree.add_Commit(commit);
        commit.save();

        //create a default master
        List<String> defaultList = new ArrayList<>();
        defaultList.add(commit.hashcode());
        branch master = new branch("master", defaultList);
        File newBranchFile = join(Branch_DIR, master.getBranchName());
        writeObject(newBranchFile, master);

        commitTree.setCurrentBranch("master");

        writeObject(Staging_Area_File, stagingArea);
        writeObject(CommitTree_DIR_File, commitTree);
        writeObject(Removed_Staging_Area_File, removedStagingArea);
        writeObject(Addition_File, additionArea);
        writeObject(Commit_Prefix_Map_File, commitPrefixMap);

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
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        HashSet<String> filesCodehashSet = commitTree.getHeadCommit().filesCode;

        if (removedStagingArea.contains(blob)) {
            removedStagingArea.remove(blob);
            writeObject(Removed_Staging_Area_File, removedStagingArea);
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
        if(!fromStartCheck && !commitTree.getHeadCommit().files.contains(blob)) {
            additionArea.add(blob);
        }
        writeObject(Staging_Area_File, stagingArea);
        writeObject(Addition_File, additionArea);
        writeObject(Removed_Staging_Area_File, removedStagingArea);
    }

    /**
     * when program starts,call each file in headCCommit with method add, and add change into stagingArea
     * */
    public static void startCheck() {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> blobs = commitTree.getHeadCommit().files;
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        List<String> fileList = Utils.plainFilenamesIn(CWD);
        Commit headCommit = commitTree.getHeadCommit();
        if (fileList != null) {
            // unstage file by system command delete rather than remove:
            for (Blob blob1 : headCommit.files) {
                if (!fileList.contains(blob1.getFile().getName())) {
                    // file is deleted but not from gitlet:
                    removedStagingArea.add(blob1);
                    break;
                }
            }
        }
        if (blobs != null) {
            for (Blob blob : blobs) {
                // read file in CWD as headCommit's file path
                if (!removedStagingArea.contains(blob)) {
                    Blob newBlob = new Blob(blob.getFile());
                    add(newBlob, true);
                }
            }
        }
        writeObject(Removed_Staging_Area_File, removedStagingArea);
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
            Iterator<Blob> iterator = updatedFiles.iterator();
            while(iterator.hasNext()) {
                Blob b2 = iterator.next();
                if(b.getPath().equals(b2.getPath())) {
                    iterator.remove();  // 使用迭代器的remove方法
                    found = true;
                }
            }
            if(!found) {
                updatedFiles.add(b);
            }
        }
        // delete all files in removedStagingArea from commit
        for(Blob b : removedStagingArea) {
            updatedFiles.removeIf(b2 -> b.getPath().equals(b2.getPath()));
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
        //TODO: a bit ugly :(
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        HashSet<Blob> additionArea = readObject(Addition_File, HashSet.class);
        Commit headCommit = commitTree.getHeadCommit();
        List<String> fileList = Utils.plainFilenamesIn(CWD);

        if (!file.exists()) {
            if (fileList != null) {
                // unstage file by system command delete rather than remove:
                for (Blob blob1 : headCommit.files) {
                    if (!fileList.contains(blob1.getFile())) {
                        // file is deleted but not from gitlet:
                        removedStagingArea.add(blob1);
                        break;
                    }
                }
                writeObject(Removed_Staging_Area_File, removedStagingArea);
            } else {
                System.out.println("No reason to remove the file.");
            }
            System.exit(0);
        }

        Blob blob = new Blob(file);
        boolean isStaged = stagingArea.contains(blob);
        boolean isTracked = headCommit.files.contains(blob);

        if (!isStaged && !isTracked) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (isStaged) {
            stagingArea.remove(blob);
        }
        if (isTracked) {
            removedStagingArea.add(blob);
            restrictedDelete(file);
        }
        if (additionArea.contains(blob)) {
            additionArea.remove(blob);
        }

        writeObject(Staging_Area_File, stagingArea);
        writeObject(Removed_Staging_Area_File, removedStagingArea);
        writeObject(Addition_File, additionArea);
    }


    /**
     * command log
     * print current branch's commit history
     * */
    public static void log() {
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        branch currentBranch = commitTree.getCurrentBranch();
        List<String> commitList = new ArrayList<>(currentBranch.commitList);
        Collections.reverse(commitList);
        for(String commitID : commitList) {
            Commit commit = readObject(join(Commit_DIR, commitID), Commit.class);
            if(commit != null) {
                printCommit(commit);
            }
        }
    }

    /**
     * command global_log
     * use method: Utils.plainFilenamesIn(),traverse all commit in folder commit
     * */
    public static void global_log() {
        List<String> commitList = Utils.plainFilenamesIn(Commit_DIR);
        for(String commitID : commitList) {
            Commit commit = readObject(join(Commit_DIR, commitID), Commit.class);
            printCommit(commit);
        }
    }

    /**
     * command find [message]
     *  traverse commit DIR ,find and print appropriate commit's ID
     * */
    public static void find(String message) {
        List<String> commitList = Utils.plainFilenamesIn(Commit_DIR);
        boolean founded = false;
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
        System.out.println("=== Branches ===");
        List<String> branchList= plainFilenamesIn(Branch_DIR);
        branch currentBranch = commitTree.getCurrentBranch();
        for(String branch : Objects.requireNonNull(branchList)) {
            if (branch.equals(currentBranch.getBranchName())) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println("");

        //staged(tracked)
        HashSet<Blob> staged = new HashSet<>();
        staged.addAll(additionArea);
        System.out.println("=== Staged Files ===");
        for(Blob b : staged) {
            System.out.println(b.getFile().getName());
        }
        System.out.println("");

        System.out.println("=== Removed Files ===");
        for(Blob b : removedStagingArea) {
            System.out.println(b.getFile().getName());
        }
        System.out.println("");

        //modified but not commit
        System.out.println("=== Modifications Not Staged For Commit ===");
        //modified

        System.out.println("");

        //Untracked
        System.out.println("=== Untracked Files ===");
        List<String> fileList = Utils.plainFilenamesIn(CWD);
        for(String file : fileList) {
            Blob b = new Blob(new File(file));
            staged.addAll(headCommit.files);
            if(!staged.contains(b)) {
                System.out.println(b.getFile().getName());
            }
        }
        System.out.println("");

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
            if(commitID.length() == 6) {
                HashMap<String, String> commitPrefixMap = readObject(Commit_Prefix_Map_File, HashMap.class);
                commitID = commitPrefixMap.get(commitID);
            }
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
        if (branchName.equals(commitTree.getCurrentBranch().getBranchName())) {
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
        branch newBranch = new branch(branchName, commitTree.getCurrentBranch().getBranchName());
        writeObject(join(Branch_DIR, branchName), newBranch);
    }

    /**
    */
    public static void removeBranch(String branchName) {
        restrictedDelete(join(Branch_DIR, branchName));
    }

    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch’s head to that commit node
     * */
    public static void reset(String commitID) {
        // clear CWD
        List<String> filenames = plainFilenamesIn(CWD);
        for(String filename : filenames) {
            restrictedDelete(new File(filename));
        }
        // clear stagingArea
        writeObject(Staging_Area_File, new HashSet<Blob>());
        writeObject(Removed_Staging_Area_File, new HashSet<Blob>());

        Commit commit = readObject(join(Commit_DIR, commitID), Commit.class);
        if(commit.files != null) {
            for(Blob b : commit.files) {
                checkout(new String[]{"", commitID, "--", b.getFile().getName()});
            }
        }
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        branch currentBranch = commitTree.getCurrentBranch();
        // remove other commits
        List<String> commitList = currentBranch.commitList;
        for(int index = commitList.size() -1; index >=0; index--) {
            if(commitList.get(index).equals(commitID)) {
                break;
            } else {
                commitList.remove(index);
            }
        }
        writeObject(join(Branch_DIR, currentBranch.getBranchName()), currentBranch);

    }

    /**
     * Command Merge
     * details are shown in /gitlet-design.md
     * @param givenBranch branch to be merged to current branch
     * */
    public static void merge(branch givenBranch) {
        //TODO: failure case 4/5

        // failure case2: branch not exist
        List<String> branchList = plainFilenamesIn(Branch_DIR);
        if (!branchList.contains(givenBranch.getBranchName())) {
            message("A branch with that name does not exist.");
            return;
        }
        // check before merge
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        branch currentBranch = commitTree.getCurrentBranch();
        Commit splitPoint = getSplitPoint(currentBranch.getBranchName(), givenBranch.getBranchName());
        if (splitPoint.equals(currentBranch.getHeadCommit()) && splitPoint.equals(givenBranch.getHeadCommit())) {
            // two same branches
            message("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitPoint.equals(currentBranch.getHeadCommit())) {
            // given branch fast-forward
            checkout(new String[]{"", givenBranch.getBranchName()});
            message("Current branch fast-forwarded.");
            return;
        }
        if (currentBranch.getBranchName().equals(givenBranch.getBranchName())) {
            message("Cannot merge a branch with itself.");
            return;
        }
        //
        // TODO: check failure cases
        // failure case1: uncommited change
        HashSet<Blob> staging_Area = readObject(Staging_Area_File, HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        if (!staging_Area.isEmpty() || !removedStagingArea.isEmpty()) {
            message("You have uncommitted changes");
            return;
        }


        // merge
        // build Map<File, Blob> for compare file content in three commits:split,current and given
        Commit currentHead = commitTree.getHeadCommit();
        Commit givenHead = givenBranch.getHeadCommit();
        Map<File, Blob> curnt_map = buildMapformCommit(currentHead);
        Map<File, Blob> given_map = buildMapformCommit(givenHead);
        Map<File, Blob> split_map = buildMapformCommit(splitPoint);
        Set<Blob> result = new HashSet<>();
        Set<Blob> result_Conflict = new HashSet<>();
        Set<File> deleted = new HashSet<>();

        Map<File, Blob> uniteMap = new HashMap<>();
        uniteMap.putAll(curnt_map);
        uniteMap.putAll(given_map);
        uniteMap.putAll(split_map);

        boolean conflictExist = false;

        for(File item : uniteMap.keySet()) {
            Blob a = curnt_map.get(item);
            Blob b = given_map.get(item);
            Blob s = split_map.get(item);
            if (curnt_map.containsKey(item) && given_map.containsKey(item) && split_map.containsKey(item)) {
                // exist in all the commits:
                if (!a.equals(b)) {
                    if (a.equals(s)) {
                        // case 1:
                        result.add(b);
                    } else if (b.equals(s)) {
                        // case 2:
                        result.add(a);
                    }
                } else if (!a.equals(s) && !b.equals(s) && !a.equals(b)) {
                    // case 8.1 : file changed by different ways:
                    result_Conflict.add(mergeConflict(a, b));
                    conflictExist = true;
                }
            } else if (!split_map.containsKey(item)) {
                // not exist in split point
                if (b == null) {
                    // case 4
                    result.add(a);
                } else if (a == null) {
                    // case 5
                    result.add(b);
                } else if (a != null && b != null && !a.equals(b)) {
                    // case 8.3 : not in split point but have different content
                    result_Conflict.add(mergeConflict(a, b));
                    conflictExist = true;
                } else {
                    // case 3
                    result.add(a);
                }
            } else if (split_map.containsKey(item) && (a == null || b == null)) {
                if (a != null && a.equals(s)) {
                    deleted.add(item);
                } else if (b != null && b.equals(s)) {
                    deleted.add(item);
                } else {
                    // changed in one and deleted in the other
                    result_Conflict.add(mergeConflict(a, b));
                    conflictExist = true;
                }
            } else {
                // both deleted in two branches

            }
        }
        //TODO: modifiy file in workspace and delete file
        /**
         * for blob in result:
         *     checkout blob
         * for file in deleted:
         *     if file in workspace:
         *     delete file from workspace
         *
         * */
        List<String> fileList = plainFilenamesIn(CWD);
        for (Blob blob: result) {
            writeContents(blob.getFile(), blob.getContent());
        }
        for (File delete : deleted) {
            if (fileList.contains(delete.getName())) {
                restrictedDelete(delete);
            }
        }
        /**
         * clear all stagingAreas
         * make new commit to current branch's head
         * AND if there is a conflict: build a new file with conflict files, put off commit!
         * */
        if (!conflictExist) {
            // if there's no conflict: commit changes
            String commitMessage = String.format("Merged [%s] into [%s]."
                    , givenBranch.getBranchName()
                    , currentBranch.getBranchName());
            // build a new commit
            Commit commit = new Commit(commitMessage);
            commit.parentCodes.add(currentHead.hashcode());
            commit.parentCodes.add(givenHead.hashcode());

            for (Blob blob : result) {
                commit.files.add(blob);
                commit.filesCode.add(blob.getShaCode());
            }
            commit.save();

            // update commitTree
            commitTree.add_Commit(commit);
            commitTree.addParent(commit, givenHead);
            writeObject(CommitTree_DIR_File, commitTree);
            // clear all stagingAreas
            writeObject(Staging_Area_File,new HashSet<>());
            writeObject(Removed_Staging_Area_File, new HashSet<>());
            writeObject(Addition_File,new HashSet<>());
        } else {
            // wait until conflict is handled
            // TODO: fill in rest code
            message("Encountered a merge conflict");
            for (Blob blob : result_Conflict) {
                writeContents(blob.getFile(), blob.getContent());
            }
            // stage conflict file
            writeObject(Staging_Area_File, new HashSet<>(result_Conflict));

        }
    }



}

















