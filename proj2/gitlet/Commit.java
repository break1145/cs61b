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
     */

    /** The message of this Commit. */
    private String message;
    private Date currentDate;
    private String hashCode;
    /** A Set to store its files */
    public HashSet<Blob> files;
    public HashSet<String> filesCode;
    /** parent commit */
    private Commit parent;

    public Commit(String message) {
        this.message = message;
        this.currentDate = new Date();
        this.hashCode = this.getHashCode();
        this.filesCode = new HashSet<>();
    }
    public Commit(Commit parent, String message) {
        this.parent = parent;
        this.files = parent.files;
        this.filesCode = parent.filesCode;
        this.currentDate = new Date();
        this.message = message;
        this.hashCode = this.getHashCode();
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
        File file = join(Commit_DIR, this.hashCode);
        Boolean isSaved = false;

//        if(file.mkdir()) {
//            message("error when save commit: file has already exist");
//            return false;
//        }
        writeObject(file, this);
        for(Blob b : this.files) {
            File file1 = join(Files_DIR, b.getShaCode());
            if(file1.isDirectory()) {
                // file already exists
                isSaved = true;
                continue;
            }
            file1.mkdir();
            file1 = join(file1, b.getFile().getName());
            writeObject(file1, b.getFile());
            this.filesCode.add(b.getShaCode());

        }
        return isSaved;
    }

    public String getHashCode() {
        List<Object> ls = new LinkedList<>();
        ls.add(this.message);
        ls.add(this.currentDate.toString());
        return sha1(ls);
    }
    public String hashcode() {
        return this.hashCode;
    }
    public Date getCurrentDate() {
        return this.currentDate;
    }

    public String getMessage () {
        return message;
    }

    @Override
    public String toString() {
        return this.message + ' ' + this.currentDate.toString();
    }
}