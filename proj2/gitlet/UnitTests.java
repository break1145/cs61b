package gitlet;


import net.sf.saxon.trans.SymbolicName;
import org.checkerframework.checker.units.qual.C;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        Repository.add(new File("h.txt"));
        Repository.status();

    }

    @Test
    public void testRM_test15_p1() throws IOException {
        initialize();
        add(new File("f.txt"));
        add(new File("g.txt"));
        commit("two new files");
    }
    @Test
    public void testRM_test15_p2() throws IOException {
        startCheck();
        remove(new File("f.txt"));
        File f = new File("f.txt");
        f.createNewFile();
        startCheck();
        add(f);
        startCheck();
        status();
    }
    @Test
    public void testStatus_test18() {
        initialize();
        add(new File("f.txt"));
        add(new File("g.txt"));
        commit("two new files");
        add(new File("f.txt"));
        status();
        //TODO: 进度：
        /**
         * test01-init:
         * OK
         * test02-basic-checkout:
         * OK
         * test03-basic-log:
         * OK
         * test04-prev-checkout:
         * OK
         * test11-basic-status:
         * OK
         * test12-add-status-debug:
         * OK
         * test12-add-status:
         * OK
         * test13-remove-status:
         * OK
         * test14-add-remove-status:
         * OK
         * test15-remove-add-status:
         * OK
         * test16-empty-commit-err:
         * OK
         * test17-empty-commit-message-err:
         * OK
         * test18-nop-add:
         * OK
         * test19-add-missing-err:
         * OK
         * test20-status-after-commit:
         * OK
         * test21-nop-remove-err:
         * OK
         * test22-remove-deleted-file:
         * ERROR (java gitlet.Main exited with code 1)
         * test23-global-log:
         * ERROR (java gitlet.Main exited with code 1)
         * test24-global-log-prev:
         * ERROR (java gitlet.Main exited with code 1)
         * test25-successful-find:
         * OK
         * test26-successful-find-orphan:
         * ERROR (java gitlet.Main exited with code 1)
         * test27-unsuccessful-find-err:
         * OK
         * test28-checkout-detail:
         * ERROR (incorrect output)
         * test29-bad-checkouts-err:
         * ERROR (incorrect output)
         * test30-branches:
         * ERROR (java gitlet.Main exited with code 1)
         * test30-rm-branch:
         * ERROR (java gitlet.Main exited with code 1)
         * test31-duplicate-branch-err:
         * ERROR (incorrect output)
         * test31-rm-branch-err:
         * ERROR (java gitlet.Main exited with code 1)
         * test32-file-overwrite-err:
         * ERROR (java gitlet.Main exited with code 1)
         * test33-merge-no-conflicts:
         * ERROR (java gitlet.Main exited with code 1)
         * test34-merge-conflicts:
         * ERROR (java gitlet.Main exited with code 1)
         * test35-merge-rm-conflicts:
         * ERROR (java gitlet.Main exited with code 1)
         * test36-merge-err:
         * ERROR (java gitlet.Main exited with code 1)
         * test36-merge-parent2:
         * ERROR (java gitlet.Main exited with code 1)
         * test37-reset1:
         * ERROR (java gitlet.Main exited with code 1)
         * test38-bad-resets-err:
         * ERROR (java gitlet.Main exited with code 1)
         * test39-short-uid:
         * ERROR (incorrect output)
         * test40-special-merge-cases:
         * ERROR (java gitlet.Main exited with code 1)
         * test41-no-command-err:
         * ERROR (incorrect output)
         * test42-other-err:
         * ERROR (java gitlet.Main exited with code 1)
         * test43-criss-cross-merge-b:
         * ERROR (java gitlet.Main exited with code 1)
         * test43-criss-cross-merge:
         * ERROR (java gitlet.Main exited with code 1)
         * test44-bai-merge:
         * ERROR (java gitlet.Main exited with code 1)
         * test45-normal-status:
         * OK
         * */
    }
    @Test
    public void testRemove_test22() throws IOException {
        initialize();
        File file = new File("f.txt");
        add(file);
        commit("new file");
        restrictedDelete(file, false);
        startCheck();
        remove(file);
        status();
        /**
         * ec-test01-untracked:
         * ERROR (incorrect output)
         * ec-test10-diff-head-working:
         * FAILED (file text1.txt could not be copied to f.txt)
         * ec-test10-remote-fetch-push:
         * ERROR (java gitlet.Main exited with code 1)
         * ec-test11-diff-branch-working:
         * FAILED (file text1.txt could not be copied to f.txt)
         * ec-test11-remote-fetch-pull:
         * ERROR (java gitlet.Main exited with code 1)
         * ec-test12-bad-remotes-err:
         * ERROR (incorrect output)
         * ec-test12-diff-two-branches:
         * FAILED (file text1.txt could not be copied to f.txt)
         * test01-init:
         * OK
         * test02-basic-checkout:
         * OK
         * test03-basic-log:
         * OK
         * test04-prev-checkout:
         * OK
         * test11-basic-status:
         * OK
         * test12-add-status-debug:
         * OK
         * test12-add-status:
         * OK
         * test13-remove-status:
         * OK
         * test14-add-remove-status:
         * OK
         * test15-remove-add-status:
         * OK
         * test16-empty-commit-err:
         * OK
         * test17-empty-commit-message-err:
         * OK
         * test18-nop-add:
         * OK
         * test19-add-missing-err:
         * OK
         * test20-status-after-commit:
         * OK
         * test21-nop-remove-err:
         * OK
         * test22-remove-deleted-file:
         * OK
         * test23-global-log:
         * ERROR (java gitlet.Main exited with code 1)
         * test24-global-log-prev:
         * ERROR (java gitlet.Main exited with code 1)
         * test25-successful-find:
         * OK
         * test26-successful-find-orphan:
         * OK
         * test27-unsuccessful-find-err:
         * OK
         * // TODO
         * test28-checkout-detail:
         * ERROR (incorrect output)
         * test29-bad-checkouts-err:
         * ERROR (incorrect output)
         * test30-branches:
         * ERROR (java gitlet.Main exited with code 1)
         * test30-rm-branch:
         * ERROR (java gitlet.Main exited with code 1)
         * test31-duplicate-branch-err:
         * ERROR (incorrect output)
         * test31-rm-branch-err:
         * ERROR (java gitlet.Main exited with code 1)
         * test32-file-overwrite-err:
         * ERROR (java gitlet.Main exited with code 1)
         * test33-merge-no-conflicts:
         * ERROR (java gitlet.Main exited with code 1)
         * test34-merge-conflicts:
         * ERROR (java gitlet.Main exited with code 1)
         * test35-merge-rm-conflicts:
         * ERROR (java gitlet.Main exited with code 1)
         * test36-merge-err:
         * ERROR (java gitlet.Main exited with code 1)
         * test36-merge-parent2:
         * ERROR (java gitlet.Main exited with code 1)
         * test37-reset1:
         * ERROR (java gitlet.Main exited with code 1)
         * test38-bad-resets-err:
         * ERROR (java gitlet.Main exited with code 1)
         * test39-short-uid:
         * ERROR (incorrect output)
         * test40-special-merge-cases:
         * ERROR (java gitlet.Main exited with code 1)
         * test41-no-command-err:
         * ERROR (incorrect output)
         * test42-other-err:
         * ERROR (java gitlet.Main exited with code 1)
         * test43-criss-cross-merge-b:
         * ERROR (java gitlet.Main exited with code 1)
         * test43-criss-cross-merge:
         * ERROR (java gitlet.Main exited with code 1)
         * test44-bai-merge:
         * ERROR (java gitlet.Main exited with code 1)
         * test45-normal-status:
         * OK
         *
         * Ran 51 tests. 21 passed.*/
    }

    @Test
    public void testCommit_test23() {
//        initialize();
//        add(new File("f.txt"));
//        add(new File("g.txt"));
//        commit("two new files");
//        add(new File("h.txt"));
//        commit("add h");
        log();
    }

    @Test
    public void testCheckout_case1_test28() {
        initialize();
        File file = new File("f.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) { // false表示覆盖模式
            writer.write("version1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        add(file);
        commit("version 1");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) { // false表示覆盖模式
            writer.write("version2");
        } catch (IOException e) {
            e.printStackTrace();
        }
        add(new File("f.txt"));
        commit("version 2");
        status();
    }

    @Test
    public void testCheckout_case1_test28_p2() {
        String[] args = new String[5];
        args[0] = "checkout";
        args[1] = "9531dc3342b5f795edeb0e0900a823231c8f1dfc";
        args[2] = "--";
        args[3] = "f.txt";
        checkout(args);
        startCheck();
        status();
    }

    @Test
    public void testNewStatus() {
//        log();
        startCheck();
        status();
    }

    @Test
    public void test29_badCheckout() {

    }
    @Test
    public void test30_branch() {
        initialize();
        branch("other");
        File f = new File("f.txt");
        File g = new File("g.txt");
        write(f.getName(), "wug.txt");
        write(g.getName(), "notwug.txt");
        add(f);
        add(g);
        commit("main two files");
//        status();
        checkout(new String[]{"checkout", "other"});
        write(f.getName(), "notwug.txt");
//        write(g.getName(), "notwug2.txt");
        add(f);
        commit("Alternative file");
        System.out.println("OTHER");
        System.out.println(readContentsAsString(f));
        if (g.exists()) {
            System.out.println(readContentsAsString(g));
        }
//        status();

        checkout(new String[]{"checkout", "master"});
        System.out.println("MASTER");
        System.out.println(readContentsAsString(f));
        System.out.println(readContentsAsString(g));

        checkout(new String[]{"checkout", "other"});
        System.out.println("OTHER");
        System.out.println(readContentsAsString(f));
        if (g.exists()) {
            System.out.println(readContentsAsString(g));
        }
    }
    public void write(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) { // true表示追加模式
            writer.write(content);
//            writer.newLine(); // 添加新行
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testP() {
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        commitTree.printTree();

    }

    @Test
    public void test33_merge_no_conflict() {
        initialize();
        File f = new File("f.txt");
        File g = new File("g.txt");
        write("f.txt", "wug.txt");
        write("g.txt", "notwug.txt");
        add(f);
        add(g);
        commit("Two files");
        branch("other");
        File h = new File("h.txt");
        write("h.txt", "wug2.txt");
        add(h);
        remove(g);
        commit("Add h.txt and remove g.txt");
        checkout(new String[]{"checkout", "other"});
        remove(f);
        File k = new File("k.txt");
        write("k.txt", "wug3.txt");
        add(k);
        commit("Add k.txt and remove f.txt");
        checkout(new String[]{"checkout", "master"});
        try {
            merge(readObject(join(Branch_DIR, "other"), branch.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<String> fileList = plainFilenamesIn(CWD);
        System.out.println(fileList);
        System.out.println("f.txt exists: "+ f.exists());
        if (f.exists()) {
            System.out.println("k contents: "+ readContentsAsString(f));
        }
        System.out.println("g.txt exists: "+ g.exists());
        if (g.exists()) {
            System.out.println("k contents: "+ readContentsAsString(g));
        }
        System.out.println("h exists: "+ h.exists());
        if (h.exists()) {
            System.out.println("h contents: "+ readContentsAsString(h));
        }
        System.out.println("k exists: "+ k.exists());
        if (k.exists()) {
            System.out.println("k contents: "+ readContentsAsString(k));
        }
        log();


    }
    @Test
    public void test_part_status() {
        status();
        global_log();
    }

    @Test
    public void test34_merge_with_conflict() {
        initialize();
        File f = new File("f.txt");
        File g = new File("g.txt");
        File h = new File("h.txt");
        File k = new File("k.txt");
        /**/
        restrictedDelete(f.getName());
        restrictedDelete(g.getName());
        restrictedDelete(h.getName());
        restrictedDelete(k.getName());
        /**/
        write("f.txt", "wug.txt");
        write("g.txt", "notwug.txt");
        add(f);
        add(g);
        commit("Two files");
        branch("other");

        write("h.txt", "wug2.txt");
        add(h);
        remove(g);
        write("f.txt", "wug2.txt");
        add(f);
        commit("Add h.txt and remove g.txt, and change f.txt");

        checkout(new String[]{"checkout", "other"});
        write("f.txt", "notwug.txt");
        add(f);
        write("k.txt", "wug3.txt");
        add(k);
        commit("Add k.txt and modify f.txt");
        checkout(new String[]{"checkout", "master"});

        log();
        CommitTree commitTree = readObject(CommitTree_DIR_File, CommitTree.class);
        Commit master_head = commitTree.getHeadCommit();
        try {
            merge(readObject(join(Branch_DIR, "other"), branch.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Commit current_head = commitTree.getHeadCommit();

        log();
        status();
        /*debug*/
        System.out.println("f.txt exists: "+ f.exists());
        if (f.exists()) {
            System.out.println("f contents: \n"+ readContentsAsString(f));
        }
        System.out.println("g.txt exists: "+ g.exists());
        if (g.exists()) {
            System.out.println("g contents: \n"+ readContentsAsString(g));
        }
        System.out.println("h exists: "+ h.exists());
        if (h.exists()) {
            System.out.println("h contents: \n"+ readContentsAsString(h));
        }
        System.out.println("k exists: "+ k.exists());
        if (k.exists()) {
            System.out.println("k contents: \n"+ readContentsAsString(k));
        }

        System.out.println("current head = master head:" + master_head.hashcode().equals(current_head.hashcode()));
        /*debug*/
        /*
         * branch master: add h remove g change f
         * branch other : add k modify f
         * after merge: add h add k remove g ; f conflict
         * file f:
         *  in branch master [wug2.txt]
         *  in branch other  [notwug.txt]
         * file g:
         *  deleted
         * file h:
         *  wug2.txt
         * file k:
         *  wug3.txt
         * */
    }
    @Test
    public void testGetSplitPoint() {
        initialize();
        File f = new File("f.txt");
        File g = new File("g.txt");
        File h = new File("h.txt");
        File k = new File("k.txt");
        /*reset files*/
        restrictedDelete(f.getName());
        restrictedDelete(g.getName());
        restrictedDelete(h.getName());
        restrictedDelete(k.getName());
        /**/
        write("f.txt", "wug.txt");
        write("g.txt", "notwug.txt");
        add(f);
        add(g);
        commit("Two files");
        branch("other");
        write("h.txt", "wug2.txt");
        add(h);
        remove(g);
        commit("Add h.txt and remove g.txt");
        checkout(new String[]{"checkout", "other"});
        write("f.txt", "notwug.txt");
        add(f);
        write("k.txt", "wug3.txt");
        add(k);
        commit("Add k.txt and modify f.txt");
        checkout(new String[]{"checkout", "master"});
        Commit splitPoint = getSplitPoint("master", "other");
        message(splitPoint.getHashCode());
        global_log();
        for (Blob blob : splitPoint.files) {
            message(blob.getFile().getName());
            message(new String(blob.getContent()));
        }
    }
    @Test
    public void test35_merge_with_rm_conflict() {

    }
    @Test
    public void test36_merge_parent2() {
        initialize();
        branch("B1");
        branch("B2");
        File f = new File("f.txt");
        File g = new File("g.txt");
        File h = new File("h.txt");
        /**/
        restrictedDelete(f.getName());
        restrictedDelete(g.getName());
        restrictedDelete(h.getName());

        checkout(new String[]{"checkout", "B1"});
        write("h.txt", "This is a wug.\n");
        add(h);
        commit("Add h.txt");

        checkout(new String[]{"checkout", "B2"});
        write("f.txt", "This is a wug.\n");
        add(f);
        commit("f.txt added");

        branch("C1");
        write("g.txt", "This is not a wug.\n");
        add(g);
        remove(f);
        commit("Add g.txt, remove f.txt");
        assertEquals("This is not a wug.\n", readContentsAsString(g));
        assertFalse(f.exists());
        assertFalse(h.exists());

        checkout(new String[]{"checkout", "B1"});
        // B1: add h
        assertEquals("This is a wug.\n", readContentsAsString(h));
        assertFalse(f.exists());
        assertFalse(g.exists());
        try {
            // merge C1
        // C1: add f
        // B2: add f, add g, rm f
            merge(readObject(join(Branch_DIR, "C1"), branch.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals("This is a wug.\n", readContentsAsString(h));
        assertEquals("This is a wug.\n", readContentsAsString(f));
        assertFalse(g.exists());

        try {
            // merge B2
        // B2(head): g, h, rm(f)
        // B1(head): (no)g, h, f
            merge(readObject(join(Branch_DIR, "B2"), branch.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertFalse(f.exists());
        assertEquals("This is not a wug.\n", readContentsAsString(g));
        assertEquals("This is a wug.\n", readContentsAsString(h));
        /*
        *
        *
        * B2--B2-
        *   \    \
        *    C1   \
        *      \   \
        * B1---B1---B1
        *
        *
        * */
    }

}





//
















