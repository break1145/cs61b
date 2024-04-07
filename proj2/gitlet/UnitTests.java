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

        // Repository.initialize();
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        commitTree.printTree();

        Blob b = new Blob(new File("gitlet/test.md"));
        stagingArea.add(b);

        writeObject(CommitTree_DIR_File, commitTree);
        writeObject(Removed_Staging_Area_File, removedStagingArea);
        writeObject(Staging_Area_File, stagingArea);
        commit("add blob b");
    }
    @Test
    public void testCommit2() {
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);
        Repository.log();

        commit("add blob b");

    }

    @Test
    public void testLog() {

        Repository.log();
    }
}






















