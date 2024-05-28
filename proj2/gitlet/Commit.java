package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.Commit_DIR;
import static gitlet.Repository.Commit_Prefix_Map_File;
import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 * does at a high level.
 *
 * @author break
 */
public class Commit implements Serializable, Dumpable {
    /**
     * A Set to store its files
     */
    public HashSet<Blob> files;
    public HashSet<String> filesCode;
    /**
     * parent commit
     */
    public List<String> parentCodes;
    public boolean isMergedCommit;
    /**
     * The message of this Commit.
     */
    private String message;
    private Date currentDate;
    private String hashCode;

    public Commit (String message) {
        this.message = message;
        this.currentDate = new Date();
        this.hashCode = this.getHashCode();
        this.files = new HashSet<>();
        this.filesCode = new HashSet<>();
        this.parentCodes = new ArrayList<>();
    }

    public Commit (Commit parent , String message) {
        this.parentCodes = new ArrayList<>(Collections.singletonList(parent.hashCode));
        this.files = parent.files;
        this.filesCode = parent.filesCode;

        this.currentDate = new Date();
        this.message = message;
        this.hashCode = this.getHashCode();
        this.isMergedCommit = false;
    }

    public Commit (Commit c) {
        this.message = c.message;
        this.currentDate = c.currentDate;
        this.hashCode = c.hashCode;
        this.files = c.files;
        this.filesCode = c.filesCode;
        this.parentCodes = c.parentCodes;
        this.isMergedCommit = false;
    }

    /**
     * @return 返回当前分支的父提交
     */
    public Commit getParentCommit () {
        return readObject(join(Commit_DIR , this.parentCodes.get(0)) , Commit.class);
    }


    /**
     * 1. get its parent commit
     * 2. diff between all files in parent and now,add files in stagingArea to new commit
     * 3. clear stagingArea
     * 4. move head
     */

    public void save () {
        File file = join(Commit_DIR , this.hashCode);
        String prefix = this.hashCode.substring(0 , 5);
        HashMap<String, String> commitPrefixMap = readObject(Commit_Prefix_Map_File , HashMap.class);
        commitPrefixMap.put(prefix , this.hashCode);
        writeObject(file , this);
        if (this.files != null) {
            for (Blob b : this.files) {
                b.save();
            }
        }
    }

    public String getHashCode () {
        List<Object> ls = new LinkedList<>();
        ls.add(this.message);
        ls.add(this.currentDate.toString());
        Random random = new Random();
        Integer randomInt = random.nextInt();
        ls.add(randomInt.toString());
        return sha1(ls);
    }

    public String hashcode () {
        return this.hashCode;
    }

    public Date getCurrentDate () {
        return this.currentDate;
    }

    public String getMessage () {
        return message;
    }

    public void setIsMergedCommit (boolean flag) {
        this.isMergedCommit = flag;
    }

    public boolean isMergedCommit () {
        return this.isMergedCommit;
    }

    @Override
    public String toString () {
        return this.message + ' ' + this.currentDate.toString();
    }

    @Override
    public void dump () {
        printCommit(this);
        System.out.println("files");
        for (String x : this.filesCode) {
            System.out.println(x);
        }
    }

    @Override
    public boolean equals (Object obj) {
        if (obj instanceof Commit) {
            Commit c = (Commit) obj;
            return this.hashCode.equals(c.hashCode);
        } else {
            return false;
        }
    }

}