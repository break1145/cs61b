package gitlet;


import java.io.File;


import static gitlet.Repository.*;
import static gitlet.Utils.*;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author break
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {

        if(GITLET_DIR.isDirectory()) {
            Repository.startCheck();
        }
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                initialize();
                break;
            case "add":
                File file = new File(args[1]);
                add(file);
                break;
            case "commit":
                if(args.length <= 1) {
                    message("Please enter a commit message.");
                }
                commit(args[1]);
                break;
            case "rm":
                if(args.length <= 1) {
                    message("Please enter a file path.");
                }
                remove(new File(args[1]));
                break;
            case "log":
                Repository.log();
                break;
            case "checkout":
                if(args.length <= 1) {
                    message("Please enter a file path.");
                } else {
                    Repository.checkout(args);
                }
                break;
            case "reset":
                if(args.length <= 1) {
                    message("Please enter a file path.");
                } else {
                    Repository.reset(args[1]);
                }
                break;
            case "merge":
                branch given = readObject(join(Branch_DIR, args[1]), branch.class);
                Repository.merge(given);
                break;
            case "status":
                Repository.status();
                break;
            case "global-log":
                Repository.global_log();
                break;
            case "find":
                Repository.find(args[1]);
                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                Repository.removeBranch(args[1]);
                break;

            default:
                // TODO: Output messages for error commands


        }
    }
}
