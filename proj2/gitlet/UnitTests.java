package gitlet;


import org.junit.Test;

import java.io.File;
import java.util.HashSet;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
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
        Repository.startCheck();
    }

    @org.junit.Test
    public void testCommit() {
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);

        Blob b = new Blob(new File("gitlet/test.md"));
        stagingArea.add(b);

        writeObject(CommitTree_DIR_File, commitTree);
        writeObject(Removed_Staging_Area_File, removedStagingArea);
        writeObject(Staging_Area_File, stagingArea);
        commit("add blob b");
    }
    @Test
    public void testCommit_withCh4nge() {

        startCheck();
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        HashSet<Blob> stagingArea = readObject(Staging_Area_File, HashSet.class);
        HashSet<Blob> removedStagingArea = readObject(Removed_Staging_Area_File, HashSet.class);

        Repository.commit("change file");
    }

    @Test
    public void testLog() {
        Repository.log();

    }

    @Test
    public void testReadFile() {
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        Commit head = commitTree.getHeadCommit();
        HashSet<String> code = head.filesCode;

        System.out.println(head.filesCode);

//        HashSet<String> code = head.filesCode;

        for(String s : code) {
            Blob b = readObject(join(Files_DIR, s), Blob.class);
            writeContents(join(CWD, "testReadFile.md"), b.getContent());
        }
    }
    @Test
    public void testRM() {
        // TODO:测试搁置 父目录包含gitlet
        Repository.remove(new File("gitlet/test.md"));
        Repository.commit("delete file");
        Repository.log();
    }
    @Test
    public void testFind() {
        Repository.find("change file");
    }
    @Test
    public void testStatus() {
        Repository.startCheck();
        Repository.status();
    }

    @Test
    public void testIteminHashSet() {
        HashSet<Blob> newHashset = new HashSet<>();
        Blob b1 = new Blob(new File("gitlet/test.md"));
        Blob b2 = new Blob(new File("gitlet/test.md"));
        newHashset.add(b1);
        System.out.println(newHashset.contains(b2));
        System.out.println(b1.getFile().getName());
//        System.out.println(b2.getShaCode());
    }


}





//
















