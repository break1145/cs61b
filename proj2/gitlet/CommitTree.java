package gitlet;

import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;


/**
 * represent the structure of commit
 * each Commit Node has its value(Commit),parent(CTreeNode),head pointer(Commit)
 *
 * @author break
 * */
public class CommitTree implements Serializable {
    public CTreeNode head;
    private CTreeNode root;
    private int size;
    private branch currentBranch;
    private String currentBranchName;
    public CommitTree() {
        this.head = null;
    }
    /**
     * get bvranch from name
     * */
    public void setCurrentBranch(String currentBranchName) {
        this.currentBranchName = currentBranchName;
        getCurrentBranch();
        //TODO: bug: after changeing branch,head node has not changed yet
        List<String> commitList = this.currentBranch.commitList;
        this.head = getNodebyCommit(root, readObject(join(Commit_DIR, commitList.get(commitList.size() -1)),Commit.class));
    }
    private CTreeNode getNodebyCommit(CTreeNode node,Commit commit) {
        if (node.val.hashcode().equals(commit.hashcode())) {
            return node;
        }
        if (node == null || node.children == null) {
            return null;
        }
        for(CTreeNode child : node.children) {
            return getNodebyCommit(child, commit);
        }
        return null;
    }
    /**
     * read branch from file and save to this
     * promise to get latest branch
     * */
    public branch getCurrentBranch() {
        this.currentBranch = Utils.readObject(Utils.join(Repository.Branch_DIR, currentBranchName), branch.class);
        return this.currentBranch;
    }
    /**
     * save branch to file
     * */
    public void saveCurrentBranch() {
        Utils.writeObject(Utils.join(Repository.Branch_DIR, currentBranchName),this.currentBranch);
    }
    /**
     * add commit to the tree. only make change on CommitTree and current branch
     * */
    public boolean add_Commit(Commit commit) {
        CTreeNode newNode = new CTreeNode(commit);
        // empty tree
        if(this.size == 0) {
            this.head = newNode;
            this.root = newNode;
            size += 1;
            return true;
        }
        if(head == null) {
            return false;
        }
        this.head.children.add(newNode);
        newNode.parents.add(head);
        head = newNode;
        size += 1;

        getCurrentBranch();
        this.currentBranch.commitList.add(head.val.hashcode());
        saveCurrentBranch();

        return true;
    }


    /**
     * print the structure of commit tree
     * can be used for log or debug
     * */
    public void printTree() {
        printTreeRecursive(root, 0);
    }
    private void printTreeRecursive(CTreeNode node, int depth) {
        if (node == null) {
            return;
        }
        // print node if is not root

        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
        System.out.println(node.val);

        for (CTreeNode child : node.children) {
            printTreeRecursive(child, depth + 1);
        }
    }
    public void printTreefromHead() {
        CTreeNode node = this.head;
        while(node != null) {
            Utils.message("===");
            Utils.message("commit "+ node.val.hashcode());
            // 使用formatter输出标准日期
            Formatter formatter = new Formatter(Locale.ENGLISH);
            Date currentDate = new Date();
            String formattedDate = String.valueOf(formatter.format("Date: %ta %tb %td %tT %tY %tz", currentDate, currentDate, currentDate, currentDate, currentDate, currentDate));
            Utils.message(formattedDate);
            Utils.message(node.val.getMessage());
            Utils.message("");
            if(node.parents.isEmpty()) {
                break;
            }
            node = node.parents.get(0);
        }
    }



    /**
     * 获取当前分支的headCommit
     * */
    public Commit getHeadCommit() {
        return this.currentBranch.getHeadCommit();
    }
    public int size(){return this.size;}

    /**
     * node of commit tree
     * */
    private static class CTreeNode implements Serializable{
        public Commit val;
        //public CTreeNode parent;
        public List<CTreeNode> parents;
        public LinkedList<CTreeNode> children;
        public CTreeNode(Commit commit) {
            this.val = commit;
            this.parents = new ArrayList<>();
            this.children = new LinkedList<>();
        }
    }
}
