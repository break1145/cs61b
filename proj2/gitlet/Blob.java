package gitlet;

import net.sf.saxon.trans.SymbolicName;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;
public class Blob implements Serializable {
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

    public boolean equals(Object obj) {
        // check if ref to same one
        if (this == obj) {
            return true;
        }
        // check class
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Blob other = (Blob) obj;
        // 比较 SHA-1 哈希码
        return this.shaCode.equals(other.shaCode);
    }

}
