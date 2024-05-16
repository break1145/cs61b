package gitlet;


import org.checkerframework.checker.units.qual.C;
import org.junit.Test;

import java.io.File;
import java.util.*;

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
//        Repository.log();
        Repository.add(new File("README.md"));
        status();
    }

    @Test
    public void testReadFile() {
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        Commit head = commitTree.getHeadCommit();
        HashSet<String> code = head.filesCode;

        System.out.println(head.filesCode);
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
    @Test
    public void testBranch() {
        startCheck();
//        add(new File("gitlet/testmore"));
//        commit("new file testmore");
        branch currentBranch = readObject(join(Branch_DIR, "master"), branch.class);
        List<String> commitList = currentBranch.commitList;
        for(String commitID : commitList) {
            Commit commit1 = readObject(join(Commit_DIR, commitID), Commit.class);
            System.out.printf(commit1.getHashCode() + ' ');
            System.out.println(commit1.getMessage());
        }

    }


    /**
     * algorithm is ok
     * */
    @Test
    public void testLCA() {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();

        a.add("1");
        a.add("2");
        a.add("3");
        b = new ArrayList<>(a);
        a.add("4");
        a.add("5");
        b.add("6");
        b.add("7");
        Collections.reverse(a);
        Collections.reverse(b);
        Map<String, Integer> ba_Map = new HashMap<>();
        for(int i = 0;i < a.size();i++) {
            ba_Map.put(a.get(i), i);
        }
        System.out.println(ba_Map);
        for(String itemB : b) {
            System.out.println(itemB);
            if(ba_Map.containsKey(itemB)) {
                System.out.println(itemB);
                break;
            }
        }
        /**
         * 1 -> 2 -> 3 -> 4 -> 5    b1
         *            \
         *              6 -> 7      b2
         * */
    }
    @Test
    public void testMergeFile() {
        Blob blob1 = new Blob(join(CWD, "testFile1.md"));
        Blob blob2 = new Blob(join(CWD, "testFile2.md"));
        byte[] content = mergeFilewithConflict(blob1.getContent(), blob2.getContent());
        writeContents(join(CWD, "testFile1.md"),content);
    }
    @Test
    public void testWriteFiletoBottom() {
        Blob blob1 = new Blob(join(CWD, "testFile1.md"));
        writeContents(join(CWD, "testFile1.md"),writeLinetoBottom(blob1.getContent(), "edit!"));
    }

    @Test
    public void testMerge_commonCase() {
        initialize();

    }

    @Test
    public void testDumpobj(){
        String fileName = ".gitlet/commits/3c9bfc102dc642d8c3213654202063a155d480b9";
        Dumpable obj = Utils.readObject(new File(fileName),
                Dumpable.class);
        obj.dump();
    }
    @Test
    public void testStatus_test12() {
        Repository.initialize();
        Repository.add(new File("f.txt"));
        Repository.add(new File("g.txt"));
        Repository.status();

    }

    @Test
    public void testRM_test13() {
        initialize();
        add(new File("f.txt"));
        add(new File("g.txt"));
        commit("two new files");
        remove(new File("f.txt"));
        status();
    }
}





//
















