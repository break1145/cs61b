package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


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
    public CommitTree() {
        this.head = null;
    }
    /**
     * add commit to the tree. only make change on CommitTree
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
            Utils.message("Date "+ node.val.getCurrentDate().toString());
            Utils.message(node.val.getMessage());
            Utils.message("");

            node = node.parents.get(0);
        }
    }

    public Commit getHeadCommit() {
        return this.head.val;
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
            this.parents = null;
            this.children = new LinkedList<>();
        }
    }
}
