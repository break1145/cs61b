package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static gitlet.Repository.Commit_DIR;
import static gitlet.Utils.join;
import static gitlet.Utils.readObject;


/**
 * represent the structure of commit
 * each Commit Node has its value(Commit),parent(CTreeNode),head pointer(Commit)
 *
 * @author break
 */
public class CommitTree implements Serializable {
    public CTreeNode head;
    private CTreeNode root;
    private int size;
    private branch currentBranch;
    private String currentBranchName;

    public CommitTree () {
        this.head = null;
    }

    /**
     * get the node in the commitTree by commit
     *
     * @param node   root
     * @param commit value of the node to be found
     * @return a CTreeNode with value of commit in the tree. <p></p>
     * null if no node satisfy with that
     */
    private CTreeNode getNodebyCommit (CTreeNode node , Commit commit) {
        if (node == null) {
            return null;
        }
        if (node.val.hashcode().equals(commit.hashcode())) {
            return node;
        }
        if (node.children != null) {
            for (CTreeNode child : node.children) {
                CTreeNode result = getNodebyCommit(child , commit);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * read branch from file and update to commitTree
     * promise to get latest branch
     */
    public branch getCurrentBranch () {
        this.currentBranch = Utils.readObject(Utils.join(Repository.Branch_DIR , currentBranchName) , branch.class);
        return this.currentBranch;
    }

    /**
     * get bvranch from name
     */
    public void setCurrentBranch (String currentBranchName) {
        this.currentBranchName = currentBranchName;
        getCurrentBranch();
        List<String> commitList = this.currentBranch.commitList;
        this.head = getNodebyCommit(root , readObject(join(Commit_DIR , commitList.get(commitList.size() - 1)) , Commit.class));
        if (head == null) {
            throw new RuntimeException("No commit found in CommitTree");
        }

    }

    /**
     * save branch to file
     */
    public void saveCurrentBranch () {
        Utils.writeObject(Utils.join(Repository.Branch_DIR , currentBranchName) , this.currentBranch);
    }

    /**
     * add commit to the tree. only make change on CommitTree and current branch
     */
    public boolean add_Commit (Commit commit) {
        CTreeNode newNode = new CTreeNode(commit);
        // empty tree
        if (this.size == 0) {
            this.head = newNode;
            this.root = newNode;
            size += 1;
            return true;
        }
        if (head == null) {
            return false;
        }
        this.head.children.add(newNode);
        newNode.parents.add(head);
        head = newNode;
        size += 1;

        // add commit to current branch
        getCurrentBranch();
        this.currentBranch.commitList.add(head.val.hashcode());
        saveCurrentBranch();
        return true;
    }

    /**
     * print the structure of commit tree
     * can be used for log or debug
     */
    public void printTree () {
        printTreeRecursive(root , 0);
    }

    private void printTreeRecursive (CTreeNode node , int depth) {
        if (node == null) {
            return;
        }
        // print node if is not root

        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
        System.out.println(node.val);
        System.out.println("CHILDREN: " + node.children);
        System.out.println("PARENTS: " + node.parents);

        for (CTreeNode child : node.children) {
            printTreeRecursive(child , depth + 1);
        }
    }

    /**
     * make the given commit be the parent of 'origin'.
     *
     * @param origin The commit to which a parent node should be added
     * @param given  The commit  to be added to node 'origin'
     * @return true if and only if addition is successful;<p></p>
     * false otherwise
     */
    public boolean addParent (Commit origin , Commit given) {
        CTreeNode originNode = getNodebyCommit(root , origin);
        CTreeNode givenNode = getNodebyCommit(root , given);
        if (originNode != null && givenNode != null) {
            originNode.parents.add(givenNode);
            givenNode.children.add(originNode);
            return true;
        }
        return false;
    }

    /**
     * get current branch's head commit
     */
    public Commit getHeadCommit () {
        return this.currentBranch.getHeadCommit();
    }

    public int size () {
        return this.size;
    }

    /**
     * node of commit tree
     */
    private static class CTreeNode implements Serializable {
        public Commit val;
        //public CTreeNode parent;
        public List<CTreeNode> parents;
        public LinkedList<CTreeNode> children;

        public CTreeNode (Commit commit) {
            this.val = commit;
            this.parents = new ArrayList<>();
            this.children = new LinkedList<>();
        }
    }
}
