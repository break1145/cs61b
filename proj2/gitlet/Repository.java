package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static gitlet.Utils.*;

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
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File Staging_Area = join(GITLET_DIR, "staging");
    public static final File Commit_DIR = join(GITLET_DIR, "commits");
    public static final File Files_DIR = join(GITLET_DIR, "blobs");


    /* TODO: fill in the rest of this class. */

    public static void setupFileFolder() {
        GITLET_DIR.mkdir();
        Staging_Area.mkdir();
        Commit_DIR.mkdir();
        Files_DIR.mkdir();
    }

    /**
     *  initialize the gitlet repository and make an initial commit.<p>
     *  if folder '.gitlet' already exists, abort error messages and exit.
     * */
    public static void initialize() {
        if(GITLET_DIR.isDirectory()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
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
            System.exit(0);
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
        // TODO: deal with case if 'rm'
    }
    public static void commit() {

    }
}
