package gitlet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Repository.Branch_DIR;
import static gitlet.Repository.Commit_DIR;


/**
 * Assorted utilities.
 * <p>
 * Give this file a good read as it provides several useful utility functions
 * to save you some time.
 *
 * @author P. N. Hilfinger
 */
class Utils {

    /**
     * The length of a complete SHA-1 UID as a hexadecimal numeral.
     */
    static final int UID_LENGTH = 40;

    /* SHA-1 HASH VALUES. */
    /**
     * Filter out all but plain files.
     */
    private static final FilenameFilter PLAIN_FILES =
            new FilenameFilter() {
                // if need implement status-untracked:
//            @Override
//            public boolean accept(File dir, String name) {
//                return new File(dir, name).isFile() && !isIgnored(name);
//            }
                @Override
                public boolean accept (File dir , String name) {
                    return new File(dir , name).isFile();
                }


            };

    /**
     * Returns the SHA-1 hash of the concatenation of VALS, which may
     * be any mixture of byte arrays and Strings.
     */
    static String sha1 (Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x" , b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /* FILE DELETION */

    /**
     * Returns the SHA-1 hash of the concatenation of the strings in
     * VALS.
     */
    static String sha1 (List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /**
     * Deletes FILE if it exists and is not a directory.  Returns true
     * if FILE was deleted, and false otherwise.  Refuses to delete FILE
     * and throws IllegalArgumentException unless the directory designated by
     * FILE also contains a directory named .gitlet.
     */
    static boolean restrictedDelete (File file , boolean force_delete) {
        if (!(new File(file.getParentFile() , ".gitlet")).isDirectory() && !force_delete) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /* READING AND WRITING FILE CONTENTS */

    /**
     * Deletes the file named FILE if it exists and is not a directory.
     * Returns true if FILE was deleted, and false otherwise.  Refuses
     * to delete FILE and throws IllegalArgumentException unless the
     * directory designated by FILE also contains a directory named .gitlet.
     */
    static boolean restrictedDelete (String file) {
        return restrictedDelete(new File(file) , false);
    }

    /**
     * Return the entire contents of FILE as a byte array.  FILE must
     * be a normal file.  Throws IllegalArgumentException
     * in case of problems.
     */
    static byte[] readContents (File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Return the entire contents of FILE as a String.  FILE must
     * be a normal file.  Throws IllegalArgumentException
     * in case of problems.
     */
    static String readContentsAsString (File file) {
        return new String(readContents(file) , StandardCharsets.UTF_8);
    }

    /**
     * Write the result of concatenating the bytes in CONTENTS to FILE,
     * creating or overwriting it as needed.  Each object in CONTENTS may be
     * either a String or a byte array.  Throws IllegalArgumentException
     * in case of problems.
     */
    static void writeContents (File file , Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                        new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                    new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     * Throws IllegalArgumentException in case of problems.
     */
    static <T extends Serializable> T readObject (File file ,
                                                  Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /* DIRECTORIES */

    /**
     * Write OBJ to FILE.
     */
    static void writeObject (File file , Serializable obj) {
        writeContents(file , serialize(obj));
    }

    /**
     * Returns a list of the names of all plain files in the directory DIR, in
     * lexicographic order as Java Strings.  Returns null if DIR does
     * not denote a directory.
     */
    static List<String> plainFilenamesIn (File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /**
     * Returns a list of the names of all plain files in the directory DIR, in
     * lexicographic order as Java Strings.  Returns null if DIR does
     * not denote a directory.
     */
    static List<String> plainFilenamesIn (String dir) {
        return plainFilenamesIn(new File(dir));
    }

    /* OTHER FILE UTILITIES */
    // Check if the file name is in the ignore list
    private static boolean isIgnored (String fileName) {
        try {
            File ignoreFile = new File("ignore");
            if (ignoreFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(ignoreFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (fileName.equals(line.trim())) {
                        reader.close();
                        return true;
                    }
                }
                reader.close();
                // If fileName is not found in ignore list, return false
                return false;
            } else {
                // If ignore doesn't exist, don't ignore any files
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Return the concatentation of FIRST and OTHERS into a File designator,
     * analogous to the {@link java.nio.file.Paths#get(String , String[])}
     * method.
     */
    static File join (String first , String... others) {
        return Paths.get(first , others).toFile();
    }

    /**
     * Return the concatentation of FIRST and OTHERS into a File designator,
     * analogous to the {@link java.nio.file.Paths#get(String , String[])}
     * method.
     */
    static File join (File first , String... others) {
        return Paths.get(first.getPath() , others).toFile();
    }


    /* SERIALIZATION UTILITIES */

    /**
     * Returns a byte array containing the serialized contents of OBJ.
     */
    static byte[] serialize (Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw error("Internal error serializing commit.");
        }
    }



    /* MESSAGES AND ERROR REPORTING */

    /**
     * Return a GitletException whose message is composed from MSG and ARGS as
     * for the String.format method.
     */
    static GitletException error (String msg , Object... args) {
        return new GitletException(String.format(msg , args));
    }

    /**
     * Print a message composed from MSG and ARGS as for the String.format
     * method, followed by a newline.
     */
    static void message (String msg , Object... args) {
        System.out.printf(msg , args);
        System.out.println();
    }

    /**
     * compare two files and return true if files' content is same
     */
    public static boolean compareFiles (File file1 , File file2) throws IOException {
        if (file1.length() != file2.length()) {
            return false;
        }
        try (InputStream is1 = new FileInputStream(file1);
             InputStream is2 = new FileInputStream(file2)) {
            int byte1;
            int byte2;
            do {
                byte1 = is1.read();
                byte2 = is2.read();
                if (byte1 != byte2) {
                    return false;
                }
            } while (byte1 != -1 && byte2 != -1);
        }
        return true;
    }

    public static void printCommit (Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.hashcode());
//        System.out.println("commit " + "123456 ");

        Formatter formatter = new Formatter(Locale.ENGLISH);
        Date currentDate = commit.getCurrentDate();
        String formattedDate = String.valueOf(formatter.format("Date: %ta %tb %td %tT %tY %tz" , currentDate , currentDate , currentDate , currentDate , currentDate , currentDate));
        System.out.println(formattedDate);
        System.out.println(commit.getMessage());
        System.out.println("");


    }

    /**
     *
     * @return a LCA Commit
     * */
//    public static Commit getSplitPoint(String branchA, String branchB) {
//        branch bA = readObject(join(Branch_DIR, branchA), branch.class);
//        branch bB = readObject(join(Branch_DIR, branchB), branch.class);
//        // use new to avoid changing original list
//        List<String> bA_Commits = new ArrayList<>(bA.commitList);
//        List<String> bB_Commits = new ArrayList<>(bB.commitList);
//        Collections.reverse(bA_Commits);
//        Collections.reverse(bB_Commits);
//
//        // use a map to avoid nested loop
//        Map<String, Integer> ba_Map = new HashMap<>();
//        for(int i = 0;i < bA_Commits.size();i++) {
//            ba_Map.put(bA_Commits.get(i), i);
//        }
//        for(String itemB : bB_Commits) {
//            if(ba_Map.containsKey(itemB)) {
////                System.out.println(itemB);
//                return readObject(join(Commit_DIR, itemB), Commit.class);
//            }
//        }
//        // not found
//        return null;
//    }

    /**
     * Add a commit's all parents, and its parents' parents recursively.
     *
     * @param commit A commit, which may be a merged commit or not.
     * @return A List containing all ancestors of the given commit.
     * If the commit is not a merged commit, returns a List containing only itself.
     */
    public static List<Commit> addParentCommits (Commit commit) {
        List<Commit> ancestors = new ArrayList<>();
        addAncestorsRecursively(commit , ancestors);
        return ancestors;
    }

    private static void addAncestorsRecursively (Commit commit , List<Commit> ancestors) {
        if (commit == null || ancestors.contains(commit)) {
            return; // Avoid null commits and prevent infinite loops in case of cycles
        }

        ancestors.add(commit);

        if (commit.isMergedCommit) {
            for (String parentID : commit.parentCodes) {
                Commit parentCommit = readObject(join(Commit_DIR , parentID) , Commit.class);
                addAncestorsRecursively(parentCommit , ancestors); // Recursive call
            }
        }
    }


    public static Commit getSplitPoint (String branchA , String branchB) {
        branch bA = readObject(join(Branch_DIR , branchA) , branch.class);
        branch bB = readObject(join(Branch_DIR , branchB) , branch.class);
        List<Commit> aOrb = new ArrayList<>();
        List<Commit> aAndb = new ArrayList<>();

        List<Commit> aCommits = bA.commitList.stream()
                .map(commitID -> readObject(join(Commit_DIR , commitID) , Commit.class))
                .collect(Collectors.toList());
        List<Commit> bCommits = bB.commitList.stream()
                .map(commitID -> readObject(join(Commit_DIR , commitID) , Commit.class))
                .collect(Collectors.toList());

        aCommits.forEach(commit -> aOrb.addAll(addParentCommits(commit)));
        bCommits.forEach(commit -> aOrb.addAll(addParentCommits(commit)));

        aCommits = aCommits.stream()
                .flatMap(commit -> addParentCommits(commit).stream())
                .collect(Collectors.toList());
        bCommits = bCommits.stream()
                .flatMap(commit -> addParentCommits(commit).stream())
                .collect(Collectors.toList());


        for (Commit commit : aOrb) {
            if (aCommits.contains(commit) && bCommits.contains(commit)) {
                aAndb.add(commit);
            }
        }

        aAndb.sort(Comparator.comparing(Commit::getCurrentDate).reversed());

        return aAndb.isEmpty() ? null : aAndb.get(0);
    }


    /**
     * build a new blob with conflicted content
     */
    public static Blob mergeConflict (Blob current , Blob given) throws Exception {
        if (current == null && given == null) {
            throw new NullPointerException("current and given blobs cannot be null at the same time");
        }
        Blob merged = new Blob();

        byte[] currentContent = current != null ? current.getContent() : new byte[0];
        byte[] givenContent = given != null ? given.getContent() : new byte[0];

        merged.setFile(current != null ? current.getFile() : given.getFile());
        merged.setContent(mergeFilewithConflict(currentContent , givenContent));
        return merged;
    }


    public static byte[] mergeFilewithConflict (byte[] content1 , byte[] content2) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.writeBytes("<<<<<<< HEAD".getBytes());
        baos.writeBytes(System.lineSeparator().getBytes());
        baos.writeBytes(content1);
        baos.writeBytes("=======".getBytes());
        baos.writeBytes(System.lineSeparator().getBytes());
        baos.writeBytes(content2);
        baos.writeBytes(">>>>>>>".getBytes());
        baos.writeBytes("\n".getBytes());
        return baos.toByteArray();
    }

    /**
     * write a String to content's bottom
     *
     * @return a byte array after editing
     */
    public static byte[] writeLinetoBottom (byte[] content , String line) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.writeBytes(content);
        // write string and '\n'
        baos.writeBytes(line.getBytes());
        baos.writeBytes(System.lineSeparator().getBytes());
        return baos.toByteArray();
    }

    /**
     * merge utils
     * - buildMapformCommit
     * -
     * -
     */
    public static Map<File, Blob> buildMapformCommit (Commit commit) {
        Map<File, Blob> result_map = new HashMap<>();
        for (Blob b : commit.files) {
            result_map.put(b.getFile() , b);
        }
        return result_map;
    }

    public static class pair<A, B> {
        public final A first;
        public final B second;

        public pair (A first , B second) {
            this.first = first;
            this.second = second;
        }
    }

}

