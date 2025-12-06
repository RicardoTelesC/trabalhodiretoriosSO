import java.util.Date;

public class File {
    private String name;
    private String content;
    private Date createdDate;
    private Date modifiedDate;
    private Directory parent;

    public File(String name, Directory parent) {
        this.name = name;
        this.content = "";
        this.createdDate = new Date();
        this.modifiedDate = new Date();
        this.parent = parent;
    }

    public File(String name, String content, Directory parent) {
        this.name = name;
        this.content = content;
        this.createdDate = new Date();
        this.modifiedDate = new Date();
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.modifiedDate = new Date();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.modifiedDate = new Date();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public String getFullPath() {
        if (parent == null) {
            return "/" + name;
        }
        return parent.getFullPath() + "/" + name;
    }

    public File copy(Directory newParent) {
        return new File(this.name, this.content, newParent);
    }

    @Override
    public String toString() {
        return "File: " + name + " | Size: " + content.length() + " bytes | Modified: " + modifiedDate;
    }
}

