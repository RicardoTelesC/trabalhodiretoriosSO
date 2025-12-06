import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Directory {
    private String name;
    private Date createdDate;
    private Date modifiedDate;
    private Directory parent;
    private List<File> files;
    private List<Directory> subdirectories;

    public Directory(String name, Directory parent) {
        this.name = name;
        this.createdDate = new Date();
        this.modifiedDate = new Date();
        this.parent = parent;
        this.files = new ArrayList<>();
        this.subdirectories = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateModifiedDate();
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

    public List<File> getFiles() {
        return files;
    }

    public List<Directory> getSubdirectories() {
        return subdirectories;
    }

    public void addFile(File file) {
        files.add(file);
        updateModifiedDate();
    }

    public boolean removeFile(File file) {
        boolean removed = files.remove(file);
        if (removed) {
            updateModifiedDate();
        }
        return removed;
    }

    public void addSubdirectory(Directory directory) {
        subdirectories.add(directory);
        updateModifiedDate();
    }

    public boolean removeSubdirectory(Directory directory) {
        boolean removed = subdirectories.remove(directory);
        if (removed) {
            updateModifiedDate();
        }
        return removed;
    }

    public File findFile(String fileName) {
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public Directory findSubdirectory(String dirName) {
        for (Directory dir : subdirectories) {
            if (dir.getName().equals(dirName)) {
                return dir;
            }
        }
        return null;
    }

    public String getFullPath() {
        if (parent == null) {
            return "/" + name;
        }
        if (name.equals("/")) {
            return "/";
        }
        return parent.getFullPath() + "/" + name;
    }

    public String listContents() {
        StringBuilder sb = new StringBuilder();
        sb.append("Contents of ").append(getFullPath()).append(":\n");
        sb.append("Directories:\n");
        for (Directory dir : subdirectories) {
            sb.append("  [DIR] ").append(dir.getName()).append("\n");
        }
        sb.append("Files:\n");
        for (File file : files) {
            sb.append("  ").append(file.getName()).append("\n");
        }
        return sb.toString();
    }

    private void updateModifiedDate() {
        this.modifiedDate = new Date();
        if (parent != null) {
            parent.updateModifiedDate();
        }
    }

    @Override
    public String toString() {
        return "Directory: " + name + " | Files: " + files.size() + " | Subdirectories: " + subdirectories.size();
    }
}

