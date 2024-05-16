package gitlet;



import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static gitlet.Repository.Files_DIR;
import static gitlet.Utils.*;
public class Blob implements Serializable ,Dumpable{
    private String path;
    private byte[] content;
    private File file;

    private String shaCode;

    public Blob(File f) {
        this.file = f;
        this.path = f.getPath();
        this.content = readContents(f);
        this.shaCode = sha1(this.path + readContentsAsString(f));
    }
    public Blob() {
        this.file = null;
    }
    public String getPath () {
        return path;
    }

    public String getShaCode () {
        return shaCode;
    }

    public byte[] getContent () {
        return content;
    }

    public File getFile () {
        return file;
    }

    public void save() {
        File blobFile = join(Files_DIR, this.shaCode);
        writeObject(blobFile, this);
    }

    public void setFile(File f) {
        this.file = f;
        this.path = f.getPath();
    }
    public void setContent(byte[] content) {
        this.content = content;
        this.shaCode = sha1(this.path + new String(content, StandardCharsets.UTF_8));
    }



    @Override
    public int hashCode() {
        return shaCode.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // if ref to same one
        if (this == obj) {
            return true;
        }
        // check class
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Blob other = (Blob) obj;
        // compare
        return this.shaCode.equals(other.shaCode);
    }

    @Override
    public void dump() {
        System.out.println("File :" + this.getFile());
        System.out.println("SCode:" + this.getShaCode());
    }

}
