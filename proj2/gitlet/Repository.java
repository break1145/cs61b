package gitlet;


import java.io.File;
import java.util.*;

import static gitlet.Utils.*;
import static java.lang.System.exit;


/**
 * Represents a gitlet repository.
 * <p>
 * does at a high level.
 *
 * @author break
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
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD , ".gitlet");
    public static final File Staging_Area = join(GITLET_DIR , "staging");
    public static final File Staging_Area_File = join(Staging_Area , "stagingArea.ser");
    public static final File Removed_Staging_Area_File = join(Staging_Area , "removed.ser");
    public static final File Addition_File = join(Staging_Area , "addition.ser");
    public static final File Commit_Prefix_Map_File = join(Staging_Area , "commitPrefixMap.ser");
    public static final File Modified_Staging_Area_File = join(Staging_Area , "modifiedStagingArea.ser");
    public static final File Commit_DIR = join(GITLET_DIR , "commits");
    public static final File CommitTree_DIR = join(Commit_DIR , "commitTree");
    public static final File CommitTree_DIR_File = join(CommitTree_DIR , "commitTree.ser");
    public static final File Files_DIR = join(GITLET_DIR , "blobs");
    public static final File Branch_DIR = join(GITLET_DIR , "branches");
//    public static final File Tracked_DIR = join(Staging_Area, "tracked");
//    public static final File Tracked_DIR_File = join(Tracked_DIR, "tracked.ser");

    public static void setupFileFolder () {
        GITLET_DIR.mkdir();
        Staging_Area.mkdir();
        Commit_DIR.mkdir();
        Files_DIR.mkdir();
        CommitTree_DIR.mkdir();
        Branch_DIR.mkdir();
    }

    /**
     * initialize the gitlet repository and make an initial commit.<p>
     * if folder '.gitlet' already exists, abort error messages and exit.
     */
    public static void initialize () {
        if (GITLET_DIR.isDirectory()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            exit(0);
        }
        setupFileFolder();

        HashSet<Blob> stagingArea = new HashSet<>();
        CommitTree commitTree = new CommitTree();
        HashSet<Blob> removedStagingArea = new HashSet<>();
        HashSet<Blob> additionArea = new HashSet<>();
        HashMap<String, String> commitPrefixMap = new HashMap<>();
        writeObject(Commit_Prefix_Map_File , commitPrefixMap);

        // make first commit
        Commit commit = new Commit("initial commit");
        commitTree.add_Commit(commit);
        commit.save();

        //create a default master
        List<String> defaultList = new ArrayList<>();
        defaultList.add(commit.hashcode());
        branch master = new branch("master" , defaultList);
        File newBranchFile = join(Branch_DIR , master.getBranchName());
        writeObject(newBranchFile , master);

        commitTree.setCurrentBranch("master");

        writeObject(Staging_Area_File , stagingArea);
        writeObject(CommitTree_DIR_File , commitTree);
        writeObject(Removed_Staging_Area_File , removedStagingArea);
        writeObject(Addition_File , additionArea);
        writeObject(Commit_Prefix_Map_File , commitPrefixMap);
        writeObject(Modified_Staging_Area_File , new HashSet<>());

    }

    /**
     * Adds a copy of the file as it currently exists to the staging area.<p>
     * If the file already exists,overwrite it.
     */
    public static void add (File file) {
        if (!file.isFile()) {
            System.out.println("File does not exist.");
            exit(0);
        }
        // get Head Commit
        Blob blob = new Blob(file);
        add(blob);
    }

    /**
     * add blob to staging area and check if any change occured
     *
     * @param blob a blob
     */
    private static void add (Blob blob) {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File , HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File , CommitTree.class);
        HashSet<Blob> additionArea = readObject(Addition_File , HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File , HashSet.class);
        HashSet<String> filesCodehashSet = commitTree.getHeadCommit().filesCode;

        if (removedStagingArea.contains(blob)) {
            removedStagingArea.remove(blob);
            writeObject(Removed_Staging_Area_File , removedStagingArea);
            return;
        }
        // if a file in stagingArea and name is same as blob's,change it to blob
        for (Blob blob2 : stagingArea) {
            if (blob2.getFile().equals(blob.getFile())) {
                stagingArea.remove(blob2);
                break;
            }
        }

        stagingArea.add(blob);
        boolean flag = true;
        for (Blob b : commitTree.getHeadCommit().files) {
            if (b.getFile().equals(blob.getFile())) {
                flag = false;
            }
        }
        if (flag) {
            additionArea.add(blob);
        }
        writeObject(Staging_Area_File , stagingArea);
        writeObject(Addition_File , additionArea);
        writeObject(Removed_Staging_Area_File , removedStagingArea);
    }

    /**
     * when program starts,add modified files into "modified area" and removed files into "removed area"
     */
    public static void startCheck () {
        CommitTree commitTree = readObject(CommitTree_DIR_File , CommitTree.class);
        HashSet<Blob> blobs = commitTree.getHeadCommit().files;
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File , HashSet.class);
        HashSet<File> modifiedStagingArea = readObject(Modified_Staging_Area_File , HashSet.class);
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
//        if (blobs != null) {
//            for (Blob blob : blobs) {
//                // read file in CWD as headCommit's file path
//                if (!removedStagingArea.contains(blob)) {
//                    Blob newBlob = new Blob(blob.getFile());
//                    if (!newBlob.getContent().equals(blob.getContent())) {
//                        modifiedStagingArea.add(newBlob.getFile());
//                    }
//                }
//            }
//        }
//        writeObject(Modified_Staging_Area_File, modifiedStagingArea);
        writeObject(Removed_Staging_Area_File , removedStagingArea);
    }

    public static void commit (String message) {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File , HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File , CommitTree.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File , HashSet.class);
        HashSet<Blob> additionArea = readObject(Addition_File , HashSet.class);


        if (stagingArea.isEmpty() && removedStagingArea.isEmpty()) {
            // no file changed
            message("No changes added to the commit.");
            exit(0);
        }
        Commit latestCommit = commitTree.getHeadCommit();
        Commit newCommit = new Commit(latestCommit , message);

        // update and save commit
        newCommit = updateFile(newCommit);
        newCommit.save();

        // delete file in CWD
        for (Blob b : removedStagingArea) {
            Utils.restrictedDelete(b.getFile() , false);
        }


        commitTree.add_Commit(newCommit);
        writeObject(CommitTree_DIR_File , commitTree);
        stagingArea.clear();
        writeObject(Staging_Area_File , stagingArea);
        removedStagingArea.clear();
        writeObject(Removed_Staging_Area_File , removedStagingArea);
        additionArea.clear();
        writeObject(Addition_File , additionArea);
    }

    /**
     * update files in stagingArea and remove from removeStagingArea
     *
     * @return a HashSet contains files. if file differ from one in stagingArea,use it.Or use the old
     */
    private static Commit updateFile (Commit commit) {
        // update commit in stagingArea
        HashSet<Blob> stagingArea = readObject(Staging_Area_File , HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File , HashSet.class);

        Commit updatedCommit = new Commit(commit);
        HashSet<Blob> updatedFiles;
        if (updatedCommit.files == null) {
            updatedFiles = new HashSet<>();
        } else {
            updatedFiles = new HashSet<>(updatedCommit.files);
        }
        // add files in stagingArea to updated file list
        for (Blob b : stagingArea) {
            boolean found = false;
            Iterator<Blob> iterator = updatedFiles.iterator();
            while (iterator.hasNext()) {
                Blob b2 = iterator.next();
                if (b.getPath().equals(b2.getPath())) {
                    iterator.remove();
                    found = true;
                }
            }

            updatedFiles.add(b);

        }
        // delete all files in removedStagingArea from commit
        for (Blob b : removedStagingArea) {
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
     */
    public static void remove (File file) {

        HashSet<Blob> stagingArea = readObject(Staging_Area_File , HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File , CommitTree.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File , HashSet.class);
        HashSet<Blob> additionArea = readObject(Addition_File , HashSet.class);
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
                writeObject(Removed_Staging_Area_File , removedStagingArea);
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
            restrictedDelete(file , false);
        }
        if (additionArea.contains(blob)) {
            additionArea.remove(blob);
        }

        writeObject(Staging_Area_File , stagingArea);
        writeObject(Removed_Staging_Area_File , removedStagingArea);
        writeObject(Addition_File , additionArea);
    }


    /**
     * command log
     * print current branch's commit history
     */
    public static void log () {
        CommitTree commitTree = readObject(CommitTree_DIR_File , CommitTree.class);
        branch currentBranch = commitTree.getCurrentBranch();
        List<String> commitList = new ArrayList<>(currentBranch.commitList);
        Collections.reverse(commitList);
        for (String commitID : commitList) {
            Commit commit = readObject(join(Commit_DIR , commitID) , Commit.class);
            if (commit != null) {
                printCommit(commit);
            }
        }
    }

    /**
     * command global_log
     * use method: Utils.plainFilenamesIn(),traverse all commit in folder commit
     */
    public static void global_log () {
        List<String> commitList = Utils.plainFilenamesIn(Commit_DIR);
        for (String commitID : commitList) {
            Commit commit = readObject(join(Commit_DIR , commitID) , Commit.class);
            printCommit(commit);
        }
    }

    /**
     * command find [message]
     * traverse commit DIR ,find and print appropriate commit's ID
     */
    public static void find (String message) {
        List<String> commitList = Utils.plainFilenamesIn(Commit_DIR);
        boolean founded = false;
        for (String commitID : commitList) {
            Commit currentCommit = readObject(join(Commit_DIR , commitID) , Commit.class);
            if (currentCommit.getMessage().equals(message)) {
                message(currentCommit.hashcode());
                founded = true;
            }
        }
        if (!founded) {
            message("Found no commit with that message.");
        }
    }

    /**
     * command status
     * 显示
     * 1. branch list and current branch
     * 2. tracked files
     * 3. files in removedStagingArea
     */
    public static void status () {
        HashSet<Blob> stagingArea = readObject(Staging_Area_File , HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File , CommitTree.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File , HashSet.class);
        HashSet<Blob> additionArea = readObject(Addition_File , HashSet.class);
        HashSet<File> modified = readObject(Modified_Staging_Area_File , HashSet.class);

        Commit headCommit = commitTree.getHeadCommit();
        //branch
        System.out.println("=== Branches ===");
        List<String> branchList = plainFilenamesIn(Branch_DIR);
        branch currentBranch = commitTree.getCurrentBranch();
        for (String branch : Objects.requireNonNull(branchList)) {
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
        for (Blob b : staged) {
            System.out.println(b.getFile().getName());
        }
        System.out.println("");

        System.out.println("=== Removed Files ===");
        for (Blob b : removedStagingArea) {
            System.out.println(b.getFile().getName());
        }
        System.out.println("");

        //modified but not commit
        System.out.println("=== Modifications Not Staged For Commit ===");
        //modified
//        for (File b : modified) {
//            System.out.println(b.getName() + "(modified)");
//        }
        List<String> fileList = Utils.plainFilenamesIn(CWD);
//        for(Blob blob : commitTree.getHeadCommit().files) {
//            if (!fileList.contains(blob.getFile().getName()) && !removedStagingArea.contains(blob)) {
//                System.out.println(blob.getFile().getName() + "(deleted)");
//            }
//        }

        System.out.println("");

        //Untracked
        System.out.println("=== Untracked Files ===");
//        // build file set:
//        HashSet<String> stagedFiles = new HashSet<>();
//        for(Blob blob : headCommit.files) {
//            stagedFiles.add(blob.getFile().getName());
//        }
//        for (Blob blob : stagingArea) {
//            stagedFiles.add(blob.getFile().getName());
//        }
//        for (Blob blob : removedStagingArea) {
//            stagedFiles.add(blob.getFile().getName());
//        }
//        for (Blob blob : additionArea) {
//            stagedFiles.add(blob.getFile().getName());
//        }
//        for(String file : fileList) {
//            if(!stagedFiles.contains(file)) {
//                System.out.println(file);
//            }
//        }
        System.out.println("");

    }

    public static boolean checkout (String[] args) {
        if (args == null || args.length < 2) {
            message("Incorrect operands.");
            return false;
        }

        HashSet<Blob> stagingArea = readObject(Staging_Area_File , HashSet.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File , CommitTree.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File , HashSet.class);
        HashSet<Blob> additionArea = readObject(Addition_File , HashSet.class);

        // case 1: checkout -- [file name]
        if (args.length == 3 && args[1].equals("--")) {
            String filename = args[2];
            Commit headCommit = commitTree.getHeadCommit();

            if (!randw(filename , headCommit)) {
                message("File does not exist in that commit.");
                return false;
            } else {
                randw(filename , headCommit);
            }
            return true;
        }

        // case 2: checkout [commit id] -- [file name]
        if (args.length == 4 && args[2].equals("--")) {
            List<String> commitIDList = plainFilenamesIn(Commit_DIR);
            String commitID = args[1];
            if (commitID.length() != 40) {
                for (String id : commitIDList) {
                    if (id.startsWith(commitID)) {
                        commitID = id;
                        break;
                    }
                }
            }
            String filename = args[3];
            /*check untracked*/

            Commit headCommit = commitTree.getHeadCommit();

            HashSet<Blob> staged = new HashSet<>(headCommit.files);
            staged.addAll(additionArea);
            staged.addAll(stagingArea);
//            Map<File, Blob> tracked = staged.stream()
//                    .collect(Collectors.toMap(Blob::getFile, blob -> blob));
            List<File> tracked = new ArrayList<>();
            for (Blob b : staged) {
                tracked.add(b.getFile());
            }
            // case1 untracked
            List<String> fileList = Utils.plainFilenamesIn(CWD);

            for (String file : fileList) {
                File f = new File(file);
                if (!tracked.contains(f)) {
                    message("There is an untracked file in the way; delete it, or add and commit it first.");
                    return false;
                }
            }
            if (commitIDList != null && commitIDList.contains(commitID)) {
                Commit forwardCommit = readObject(join(Commit_DIR , commitID) , Commit.class);
                if (!randw(filename , forwardCommit)) {
                    message("File does not exist in that commit.");
                    return false;
                } else {
                    add(new File(filename));
                }
//                writeObject(Staging_Area_File, new HashSet<>());
                writeObject(Addition_File , new HashSet<>());
            } else {
                message("No commit with that id exists.");
                return false;
            }
            return true;
        }

        // case 3: checkout [branch name]
        if (args.length == 2) {
            String branchName = args[1];
            if (branchName.equals(commitTree.getCurrentBranch().getBranchName())) {
                message("No need to checkout the current branch.");
                return false;
            } else {
                List<String> branchList = plainFilenamesIn(Branch_DIR);
                if (branchList != null && branchList.contains(branchName)) {
                    branch forwardBranch = readObject(join(Branch_DIR , branchName) , branch.class);
                    Commit headCommit = commitTree.getHeadCommit();

                    HashSet<Blob> staged = new HashSet<>(headCommit.files);
                    staged.addAll(additionArea);
                    staged.addAll(stagingArea);
                    List<File> tracked = new ArrayList<>();
                    for (Blob b : staged) {
                        tracked.add(b.getFile());
                    }

                    // case1 untracked
                    List<String> fileList = Utils.plainFilenamesIn(CWD);

                    for (String file : fileList) {
                        File f = new File(file);
                        if (!tracked.contains(f)) {
                            message("There is an untracked file in the way; delete it, or add and commit it first.");
                            return false;
                        }
                    }

                    // case2 change hasn't been commited
//                    HashSet<Blob> modified = new HashSet<>(stagingArea);
//                    modified.removeAll(additionArea);
//                    for(Blob b : modified) {
//                        message("There is an untracked file in the way; delete it, or add and commit it first.");
//                        return false;
//                    }
                    // case3 all file is tracked and no change to commit,start changing branch

                    commitTree.setCurrentBranch(branchName);
                    headCommit = commitTree.getHeadCommit();
                    // clear CWD
                    List<String> filenames = plainFilenamesIn(CWD);
                    for (String filename : filenames) {
                        restrictedDelete(new File(filename) , false);
                    }
                    for (String fileCode : headCommit.filesCode) {
                        Blob newBlob = readObject(join(Files_DIR , fileCode) , Blob.class);
                        writeContents(join(CWD , newBlob.getFile().getName()) , newBlob.getContent());
                    }
                    stagingArea.clear();

                    writeObject(Staging_Area_File , stagingArea);
                    writeObject(CommitTree_DIR_File , commitTree);
                    return true;
                } else {
                    message("No such branch exists.");
                    return false;
                }
            }
        }

        // 如果参数格式不符合任何已知的 checkout 形式，则输出错误信息
        message("Incorrect operands.");
        return false;
    }


    /**
     * get a file named filename in commit c from blobs and write to CWD
     *
     * @return true if and only if file is found and write successfully,otherwise false
     */
    public static boolean randw (String filename , Commit c) {
        for (Blob b : c.files) {
            if (b.getFile().getName().equals(filename)) {
                Blob newBlob = readObject(join(Files_DIR , b.getShaCode()) , Blob.class);
                writeContents(join(CWD , newBlob.getFile().getName()) , newBlob.getContent());
                return true;
            }
        }
        return false;
    }

    /*
     * create a branch which begin at current headCommit
     * */
    public static void branch (String branchName) {
        CommitTree commitTree = readObject(CommitTree_DIR_File , CommitTree.class);
        List<String> fileList = plainFilenamesIn(Branch_DIR);
        if (fileList != null && fileList.contains(branchName)) {
            message("A branch with that name already exists.");
        } else {
            branch newBranch = new branch(branchName , commitTree.getCurrentBranch().getBranchName());
            writeObject(join(Branch_DIR , branchName) , newBranch);
        }
    }

    /**
     *
     */
    public static void removeBranch (String branchName) {
        CommitTree commitTree = readObject(CommitTree_DIR_File , CommitTree.class);
        List<String> fileList = plainFilenamesIn(Branch_DIR);
        if (fileList != null && fileList.contains(branchName)) {
            if (commitTree.getCurrentBranch().getBranchName().equals(branchName)) {
                message("Cannot remove the current branch.");
            } else {
                restrictedDelete(join(Branch_DIR , branchName) , true);
            }
        } else {
            message("A branch with that name does not exist.");
        }


    }

    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch’s head to that commit node
     */
    public static void reset (String commitID) {
        List<String> fileList = plainFilenamesIn(Commit_DIR);
        if (!fileList.contains(commitID)) {
            message("No commit with that id exists.");
            return;
        }
        Commit commit = readObject(join(Commit_DIR , commitID) , Commit.class);
        CommitTree commitTree = readObject(CommitTree_DIR_File , CommitTree.class);

        // clear CWD
        List<String> filenames = plainFilenamesIn(CWD);
//        for (Blob blob : commit.files) {
//            String filename = blob.getFile().getName();
//            if (!filenames.contains(filename)) {
//                restrictedDelete(new File(filename), false);
//            }
//        }

        List<String> commitFiles = new ArrayList<>();
        for (Blob blob : commit.files) {
            commitFiles.add(blob.getFile().getName());
        }
        for (String filename : filenames) {
            if (!commitFiles.contains(filename)) {
                restrictedDelete(new File(filename) , false);
            }
        }


        if (commit.files != null) {
            for (Blob b : commit.files) {
                if (!checkout(new String[]{"" , commitID , "--" , b.getFile().getName()})) {
                    return;
                }
            }
        }

        branch currentBranch = commitTree.getCurrentBranch();
        currentBranch.setHeadCommit(commit);
        commitTree.setCurrentBranch(currentBranch.getBranchName());
        writeObject(CommitTree_DIR_File , commitTree);
        writeObject(join(Branch_DIR , currentBranch.getBranchName()) , currentBranch);
        // clear stagingArea
        writeObject(Staging_Area_File , new HashSet<Blob>());
        writeObject(Removed_Staging_Area_File , new HashSet<Blob>());
        writeObject(Addition_File , new HashSet<Blob>());
    }

    /**
     * Command Merge
     * details are shown in /gitlet-design.md
     *
     * @param givenBranch branch to be merged to current branch
     */
    public static void merge (branch givenBranch) throws Exception {
        // failure case2: branch not exist
        List<String> branchList = plainFilenamesIn(Branch_DIR);
        if (!branchList.contains(givenBranch.getBranchName())) {
            message("A branch with that name does not exist.");
            return;
        }

        // check before merge
        CommitTree commitTree = readObject(CommitTree_DIR_File , CommitTree.class);
        branch currentBranch = commitTree.getCurrentBranch();
        Commit splitPoint = getSplitPoint(currentBranch.getBranchName() , givenBranch.getBranchName());
        if (currentBranch.getBranchName().equals(givenBranch.getBranchName())) {
            message("Cannot merge a branch with itself.");
            return;
        }
//        if (splitPoint.equals(currentBranch.getHeadCommit()) && splitPoint.equals(givenBranch.getHeadCommit())) {
//            // two same branches
//            message("Given branch is an ancestor of the current branch.");
//            return;
//        }
        if (currentBranch.commitList.containsAll(givenBranch.commitList)) {
            message("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitPoint.equals(currentBranch.getHeadCommit())) {
            // given branch fast-forward
            checkout(new String[]{"" , givenBranch.getBranchName()});
            message("Current branch fast-forwarded.");
            return;
        }

        // failure case1: uncommited change
        HashSet<Blob> staging_Area = readObject(Staging_Area_File , HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File , HashSet.class);
        if (!staging_Area.isEmpty() || !removedStagingArea.isEmpty()) {
            message("You have uncommitted changes");
            return;
        }


        // build Map<File, Blob> for compare file content in three commits:split,current and given
        Commit currentHead = commitTree.getHeadCommit();
        Commit givenHead = givenBranch.getHeadCommit();
        Map<File, Blob> curnt_map = buildMapformCommit(currentHead);
        Map<File, Blob> given_map = buildMapformCommit(givenHead);
        Map<File, Blob> split_map = buildMapformCommit(splitPoint);
        List<String> fileList = plainFilenamesIn(CWD);
        // check failure case 5:
        for (String filename : fileList) {
            if (!curnt_map.containsKey(new File(filename))) {
                message("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        // merge
        /*
         * curnt_map[a]:blob in current branch
         * given_map[b]:blob in given branch
         * split_map[s]:blob in split branch
         * */
        Set<Blob> result = new HashSet<>();
        Set<Blob> result_Conflict = new HashSet<>();
        Set<File> deleted = new HashSet<>();

        Map<File, Blob> uniteMap = new HashMap<>();
        uniteMap.putAll(curnt_map);
        uniteMap.putAll(given_map);
        uniteMap.putAll(split_map);

        boolean conflictExist = false;

        for (File item : uniteMap.keySet()) {
            Blob a = curnt_map.get(item);
            Blob b = given_map.get(item);
            Blob s = split_map.get(item);
            if (curnt_map.containsKey(item) && given_map.containsKey(item) && split_map.containsKey(item)) {
                // exist in all the commits:
                if (!a.equals(b) && (a.equals(s) || b.equals(s))) {
                    if (a.equals(s)) {
                        // case 1:
                        result.add(b);
                    } else if (b.equals(s)) {
                        // case 2:
                        result.add(a);
                    }
                } else if (!a.equals(s) && !b.equals(s) && !a.equals(b)) {
                    // case 8.1 : file changed by different ways:
                    result_Conflict.add(mergeConflict(a , b));
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
                    result_Conflict.add(mergeConflict(a , b));
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
                } else if ((a != null && !a.equals(s)) || (b != null && !b.equals(s))) {
                    // changed in one and deleted in the other
                    // a or b != null and != s
                    result_Conflict.add(mergeConflict(a , b));
                    conflictExist = true;
                } else {
                    // both deleted in two branches
                    // item in s not in a or b
                    deleted.add(item);
                }
            }
        }
        /**
         * for blob in result:
         *     checkout blob
         * for file in deleted:
         *     if file in workspace:
         *     delete file from workspace
         *
         * */
        for (Blob blob : result) {
            writeContents(blob.getFile() , blob.getContent());
        }
        for (File delete : deleted) {
            if (fileList.contains(delete.getName())) {
                restrictedDelete(delete , false);
            }
        }
        /**
         * clear all stagingAreas
         * make new commit to current branch's head
         * AND if there is a conflict: build a new file with conflict files, put off commit!
         * */
        if (conflictExist) {
            message("Encountered a merge conflict");
            for (Blob blob : result_Conflict) {
                writeContents(blob.getFile() , blob.getContent());
            }
        }
        // if there's no conflict: commit changes
        String commitMessage = String.format("Merged %s into %s."
                , givenBranch.getBranchName()
                , currentBranch.getBranchName());
        // build a new commit
        Commit commit = new Commit(commitMessage);
        commit.parentCodes.add(currentHead.hashcode());
        commit.parentCodes.add(givenHead.hashcode());
        commit.setIsMergedCommit(true);

        for (Blob blob : result) {
            commit.files.add(blob);
            commit.filesCode.add(blob.getShaCode());
        }
        commit.save();

        // update commitTree
        commitTree.add_Commit(commit);
        commitTree.addParent(commit , givenHead);
        writeObject(CommitTree_DIR_File , commitTree);
        // clear all stagingAreas
        writeObject(Staging_Area_File , new HashSet<>());
        writeObject(Removed_Staging_Area_File , new HashSet<>());
        writeObject(Addition_File , new HashSet<>());


    }


}

















