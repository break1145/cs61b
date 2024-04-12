package gitlet;

import java.io.Serializable;

public class branch implements Serializable {
    public String startCommitID;
    public String headCommitID;
    private String branchName;
    public branch(String startCommit, String branchName) {
        this.startCommitID = startCommit;
        this.branchName = branchName;
    }
    public String getbranchName() {
        return branchName;
    }
}
