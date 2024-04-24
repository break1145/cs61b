package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static gitlet.Utils.*;

public class branch implements Serializable {
    public List<String> commitList;
    private String branchName;
//    private String startCommitID;
//    public branch(String branchName, String startCommit) {
//        this.branchName = branchName;
//        commitList = new ArrayList<String>();
//        startCommitID = startCommit;
//        commitList.add(startCommit);
//    }
    public branch(String branchName, String parentBranchName) {
        this.branchName = branchName;
        if (parentBranchName == null) {
            this.commitList = new ArrayList<>();
        } else {
            branch parentBranch = readObject(join(Repository.Branch_DIR, parentBranchName), branch.class);
            this.commitList = new ArrayList<>(parentBranch.commitList);
        }
    }
    public String getBranchName() {
        return branchName;
    }
//    public String getStartCommitID() {
//        return startCommitID;
//    }
    public List<String> getCommitList() {
        return commitList;
    }

    public void setBranchName (String branchName) {
        this.branchName = branchName;
    }
//    public void setStartCommitID(String startCommitID) {
//        this.startCommitID = startCommitID;
//    }

    public Commit getHeadCommit() {
        return Utils.readObject(join(Repository.Commit_DIR, this.commitList.get(this.commitList.size() - 1)), Commit.class);
    }
}
