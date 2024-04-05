public class PageTableEntry {
    // Represents the physical frame number that this entry maps to.
    private int physicalFrameNumber;

    // Indicates if this page table entry is valid
    // true if the page is allocated to one valid process.
    private boolean isUsed;

    // Indicates if the page has been modified since it was brought into memory.
    // just not same with the physical memory
    private boolean isDirty;

    // Specifies the access permissions for this page (e.g., READ, WRITE).
    private AccessPermissions permissions;
    private int allocatedPid;

    // Constructor to initialize a page table entry.
    public PageTableEntry(int physicalFrameNumber, boolean isUsed, boolean isDirty, AccessPermissions permissions,
    int allocatedPid) {
        this.physicalFrameNumber = physicalFrameNumber;
        this.isUsed = isUsed;
        this.isDirty = isDirty;
        this.permissions = permissions;
        this.allocatedPid = allocatedPid;
    }

//    public PageTableEntry (int pageTableSize) {
//    }

    // Getter and Setter methods.
    public int getAllocatedPid(){
        return this.allocatedPid;
    }
    public void setAllocatedPid(int pid){
        this.allocatedPid = pid;
    }
    public int getPhysicalFrameNumber() {
        return physicalFrameNumber;
    }
    public void setPhysicalFrameNumber(int physicalFrameNumber) {
        this.physicalFrameNumber = physicalFrameNumber;
    }
    public boolean isUsed () {
        return isUsed;
    }
    public void setUsed (boolean used) {
        isUsed = used;
    }
    public boolean isDirty() {
        return isDirty;
    }
    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }
    public AccessPermissions getPermissions() {
        return permissions;
    }
    public void setPermissions(AccessPermissions permissions) {
        this.permissions = permissions;
    }
}
