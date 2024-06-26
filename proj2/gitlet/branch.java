package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Utils.join;
import static gitlet.Utils.readObject;

public class branch implements Serializable {
    public List<String> commitList;
    private String branchName;

    public branch (String branchName , String parentBranchName) {
        this.branchName = branchName;
        if (parentBranchName == null) {
            this.commitList = new ArrayList<>();
        } else {
            branch parentBranch = readObject(join(Repository.Branch_DIR , parentBranchName) , branch.class);
            this.commitList = new ArrayList<>(parentBranch.commitList);
        }
    }

    public branch (String branchName , List<String> commitList) {
        this.branchName = branchName;
        this.commitList = commitList;
    }

    public String getBranchName () {
        return branchName;
    }

    public void setBranchName (String branchName) {
        this.branchName = branchName;
    }

    //    public String getStartCommitID() {
//        return startCommitID;
//    }
    public List<String> getCommitList () {
        return commitList;
    }
//    public void setStartCommitID(String startCommitID) {
//        this.startCommitID = startCommitID;
//    }

    public Commit getHeadCommit () {
        return Utils.readObject(join(Repository.Commit_DIR , this.commitList.get(this.commitList.size() - 1)) , Commit.class);
    }

    /*
     * only can be used in reset
     * */
    public void setHeadCommit (Commit commit) {
        int index = this.commitList.indexOf(commit.hashcode());
        if (index != -1) {
            this.commitList = new ArrayList<>(this.commitList.subList(0 , index + 1));
        } else {
            this.commitList.add(commit.hashcode());
        }
    }
}
