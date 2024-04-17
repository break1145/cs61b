package gitlet;

import java.io.Serializable;

public class branch implements Serializable {
    public String startCommitID;
    public String headCommitID;
    private String branchName;
    public branch(String startCommit, String branchName) {
        this.startCommitID = startCommit;
        this.headCommitID = startCommit;
        this.branchName = branchName;
    }
    public String getbranchName() {
        return branchName;
    }
    public void setHeadCommitID(String commitID) {
        this.headCommitID = commitID;
    }
}
