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

        Repository.startCheck();

        if (args.length == 0) {
            // TODO: what if args is empty?
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                initialize();
                break;
            case "add":
                File file = join(GITLET_DIR, args[1]);
                add(file);
                break;
            // TODO: FILL THE REST IN
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
            default:
                // TODO: Output messages for error commands


        }
    }
}
