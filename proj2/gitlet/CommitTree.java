package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;


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
        newNode.parent = head;
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
        if(node != root) {
            for (int i = 0; i < depth; i++) {
                System.out.print("  ");
            }
            System.out.println(node.val);
        }
        for (CTreeNode child : node.children) {
            printTreeRecursive(child, depth + 1);
        }
    }

    public int size(){return this.size;}

    /**
     * node of commit tree
     * */
    private class CTreeNode {
        public Commit val;
        public CTreeNode parent;
        public LinkedList<CTreeNode> children;
        public CTreeNode(Commit commit) {
            this.val = commit;
            this.parent = null;
            this.children = new LinkedList<>();
        }
    }
}
