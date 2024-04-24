package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.Commit_DIR;
import static gitlet.Repository.Commit_Prefix_Map_File;
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
    private List<String> parentCodes;

    public Commit(String message) {
        this.message = message;
        this.currentDate = new Date();
        this.hashCode = this.getHashCode();
        this.filesCode = new HashSet<>();
    }
    public Commit(Commit parent, String message) {
        this.parentCodes = new ArrayList<>(Collections.singletonList(parent.hashCode));
        this.files = parent.files;
        this.filesCode = parent.filesCode;

        this.currentDate = new Date();
        this.message = message;
        this.hashCode = this.getHashCode();
    }
    public Commit(Commit c) {
        this.message = c.message;
        this.currentDate = c.currentDate;
        this.hashCode = c.hashCode;
        this.files = c.files;
        this.filesCode = c.filesCode;
        this.parentCodes = c.parentCodes;
    }
    /**
     * @return 返回当前分支的父提交
     * */
    public Commit getParentCommit() {
        return readObject(join(Commit_DIR, this.parentCodes.get(0)), Commit.class);
    }


    /* TODO: fill in the rest of this class. */
    /**
     * 1. get its parent commit
     * 2. diff between all files in parent and now,add files in stagingArea to new commit
     * 3. clear stagingArea
     * 4. move head
     *
     * */

    public void save() {
        File file = join(Commit_DIR, this.hashCode);
        String prefix = this.hashCode.substring(0, 5);
        HashMap<String, String> commitPrefixMap = readObject(Commit_Prefix_Map_File, HashMap.class);
        commitPrefixMap.put(prefix, this.hashCode);
        writeObject(file, this);
        if(this.files != null) {
            for(Blob b : this.files) {
                b.save();
            }
        }
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