import java.util.*;
public class ProcessStruct {
    private int pid; // Process ID
    private String processName; // Process Name
    private ProcessState state; // Process state
    private int priority; // Process priority
    private int memorySize; // Memory size allocated to the process
    // 被分配给内存块的数量
    private int pc; //程序计数器
    private int pcPage; //目前执行到的页面标号
    private int beginMemoryIndex; //采用连续内存分配，记录第一个内存块的索引
    private List<Integer> pageTableIndex; //被分配的页表项索引

    public void setPc(int pc){
        this.pc = pc;
    }
    public void addPc(){
        pc++;
    }
    public void addPcPage(){
        pcPage++;
    }

    public int getPc () {
        return pc;
    }
    public int getPcPage(){
        return pcPage;
    }
    public int getBeginMemoryIndex(){
        return this.beginMemoryIndex;
    }
    public void setBeginMemoryIndex(int beginMemoryIndex){
        this.beginMemoryIndex = beginMemoryIndex;
    }
    public void setPageTableIndex(int index){
        this.pageTableIndex.add (index);
    }
    public List<Integer> getPageTableIndex () {
        return pageTableIndex;
    }
    // FIFO页面置换算法，选择在页表项中留存时间最长的页表项
    public List<Integer> selectPtEntryIndex(){
        // FIFO页面置换
        // 选择在页表项中留存时间最长的页表项
        // 每次都将新的放在队列首，其他的老的，自然向后移动

        List<Integer> tpIndex = new ArrayList<> ();
        tpIndex.add (pageTableIndex.get (pageTableIndex.size ()-1));
        for (int i = 0; i < pageTableIndex.size ()-1; i++) {
            tpIndex.add (pageTableIndex.get (i));
        }
        pageTableIndex = tpIndex;
        return pageTableIndex;
    }

    public ProcessStruct(int beginMemoryIndex, int pid, String processName, ProcessState state, int priority,
                         int memorySize) {
        this.pid = pid;
        this.processName = processName;
        this.state = state;
        this.priority = priority;
        this.memorySize = memorySize;
        this.pc = 0;
        this.pcPage = 0;
        this.beginMemoryIndex = beginMemoryIndex;
        pageTableIndex = new ArrayList<> ();

    }

    // Getters
    public int getPid() {
        return pid;
    }

    public String getProcessName() {
        return processName;
    }

    public ProcessState getState() {
        return state;
    }

    public int getPriority() {
        return priority;
    }

    public int getMemorySize() {
        return memorySize;
    }

    // Setters
    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }
}
