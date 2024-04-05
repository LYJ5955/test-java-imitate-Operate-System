import java.util.*;

public class Directory {
    private String directoryName;
    private List<String> fileNames; // List to store the names of the files

    public Directory(String directoryName) {
        this.directoryName = directoryName;
        this.fileNames = new ArrayList<>();
    }

    // Method to add a file name to the directory
    public void addFile(String fileName) {
        fileNames.add(fileName);
    }

    // Method to remove a file name from the directory
    public boolean removeFile(String fileName) {
        return fileNames.remove(fileName);
    }

    // Method to check if a file name exists in the directory
    public boolean IsContainsFile(String fileName) {
        return fileNames.contains(fileName);
    }

    // Method to get the list of file names in the directory
    public List<String> getFileNames() {
        return fileNames;
    }

    // Getters and setters
    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
}