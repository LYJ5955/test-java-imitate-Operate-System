import java.util.*;
public class File {
    private String name;
    private List<Integer> processIds; // Assuming processes are identified by an Integer ID

    public File(String name) {
        this.name = name;
        this.processIds = new ArrayList<>();
    }

    // Add a process to this file
    public void addProcess(Integer processId) {
        processIds.add(processId);
    }

    // judge a process whether contained in a File
    public boolean isContained(Integer processId){
        if(this.processIds.contains (processId)){
            return true;
        }else {
            return false;
        }
    }

    // Remove a process from this file
    public boolean removeProcess(Integer processId) {
        return processIds.remove(processId);
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public List<Integer> getProcessIds() {
        return processIds;
    }

    public void setName(String name) {
        this.name = name;
    }
}
