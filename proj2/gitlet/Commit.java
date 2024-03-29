package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author break
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date currentDate;
    private String hashCode;
    public HashSet<File> files;
    private Commit parent;

    public Commit(String message) {
        this.message = message;
        this.currentDate = new Date();
    }
    public Commit(Commit parent, String message) {
        this.parent = parent;
        this.files = parent.files;
        this.currentDate = new Date();
        this.message = message;
    }


    /* TODO: fill in the rest of this class. */
    /**
     * 1. get its parent commit
     * 2. diff between all files in parent and now,add files in stagingArea to new commit
     * 3. clear stagingArea
     * 4. move head
     *
     * */

    public boolean saveCommit() {
        // save object commit
        File file = join(Commit_DIR, this.getHashCode());
        if(file.mkdir()) {
            message("error when save commit: file has already exist");
            return false;
        }
        writeObject(file, this);

        boolean flag = false;

        //save files if not exist before
        for(File f : this.files) {
            List<String> ls = new LinkedList<>();
            ls.add(readContentsAsString(f));
            String shaCode = sha1(ls);
            File newDIR = join(Files_DIR, shaCode);
            if(newDIR.mkdir()) {
                continue;
            }
            flag = true;
            writeContents(newDIR, readContents(f));
        }

        return true;
    }

    public String getHashCode() {
        List<String> ls = new LinkedList<>();
        ls.add(this.message);
        ls.add(this.currentDate.toString());
        return sha1(ls);
    }
    @Override
    public String toString() {
        return this.message + ' ' + this.currentDate.toString();
    }
}