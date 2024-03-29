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
    public static final File Commit_DIR = join(GITLET_DIR, "commits");
    public static final File CommitTree_DIR = join(Commit_DIR, "commitTree");
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

        HashSet<String> stagingArea= new HashSet<>();
        writeObject(Staging_Area, stagingArea);

        // TODO: fill in the init command after 'commit' implement
        // make first commit with no content
    }

    /** Adds a copy of the file as it currently exists to the staging area.<p>
     *  If the file already exists,overwrite it.
     * */
    public static void add(File file) {
        if(!file.isFile()) {
            System.out.println("File does not exist.");
            exit(0);
        }

        HashSet<File> stagingArea = readObject(Staging_Area, HashSet.class);
        //TODO: check if the file is same as the latest commit's
        // get file from commit
        //File latestFile =
        try {
            // deal with if file is same
            compareFiles(file, file);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // overwrite if exist
        if(stagingArea.contains(file)) {
            stagingArea.remove(file);
        }
        stagingArea.add(file);
        writeObject(Staging_Area, stagingArea);
        // TODO: deal with case if 'rm'
    }
    public static void commit(String meassage) {
        HashSet<File> stagingArea = readObject(Staging_Area, HashSet.class);
        if (stagingArea.size() == 0) {
            message("No changes added to the commit.");
            exit(0);
        }
        CommitTree commitTree = readObject(CommitTree_DIR, CommitTree.class);
        Commit latestCommit = commitTree.getHeadCommit();

        // get latest commit and initialize new commit with that
        Commit newCommit = new Commit(latestCommit, meassage);
        try {
            newCommit.files = updateFile(newCommit, stagingArea);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        commitTree.add_Commit(newCommit);
        //save commit
        newCommit.saveCommit();
        stagingArea.clear();
        writeObject(Staging_Area,stagingArea);

        // TODO: deal with case if 'rm'
    }
    /**
     * @return a HashSet contains files. if file differ from one in stagingArea,use it.Or use the old
     * */
    public static HashSet updateFile(Commit c, HashSet<File> stagingArea) throws IOException {
        HashSet<File> result = new HashSet<>();
        for(File f : c.files) {
            String name = f.getName();
            for(File f2 : stagingArea) {
                if(f2.getName().equals(name)) {
                    if(!compareFiles(f, f2)) {
                        result.add(f2);
                    } else {
                        result.add(f);
                    }
                }
            }
        }
        return result;
    }
}

















