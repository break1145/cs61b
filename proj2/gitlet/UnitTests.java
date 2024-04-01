package gitlet;


import org.junit.Test;

import java.io.File;
import java.util.HashSet;

import static gitlet.Utils.*;
import static gitlet.Repository.*;
public class UnitTests {
    @org.junit.Test
    public void testAddCommit() {
        CommitTree CT = new CommitTree();
        CT.add_Commit(new Commit("0   commit"));
        CT.add_Commit(new Commit("1st commit"));
        CT.add_Commit(new Commit("2nd commit"));
        CT.add_Commit(new Commit("3rd commit"));
        CT.printTree();
    }
    @org.junit.Test
    public void testFileGet() {
        File CWD = new File(System.getProperty("user.dir"));
        File f = join(CWD, "gitlet-design.md");

    }

    @Test
    public void testInitialize() {
        Repository.initialize();
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        commitTree.printTree();
    }

    @org.junit.Test
    public void testCommit() {

        CommitTree commitTree = new CommitTree();
        HashSet<Blob> stagingArea = new HashSet<>();
        HashSet<Blob> removedStagingArea = new HashSet<>();
        writeObject(CommitTree_DIR_File, commitTree);
        writeObject(Removed_Staging_Area_File, removedStagingArea);
        writeObject(Staging_Area_File, stagingArea);

        commitTree.printTree();

//        commit("initial commit");
        Blob b = new Blob();
        stagingArea.add(b);
//        commit("add blob b");


    }
}






















