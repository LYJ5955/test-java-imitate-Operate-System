import java.util.*;
public class PageTable {

    // A pageTable is a set of PageTableEntry

    // A list of page table entries representing the virtual to physical address mappings for a process.
    //private List<PageTableEntry> entries;
    private PageTableEntry[] entries;

    // Constructor initializes the list of entries.
    // indicate the size of pageTable
    public PageTable(int tableSize) {
        this.entries = new PageTableEntry[tableSize];
        for (int i = 0; i < tableSize; i++) {
            this.entries[i]=new PageTableEntry (-1,false,
                    false,AccessPermissions.ALL,-1);
        }

    }

    /**
     * Retrieves the page table entry for a given virtual page number.
     * This method is used to look up the physical frame number and other attributes
     * associated with a virtual page.
     *
     * @param virtualPageNumber The virtual page number to look up.
     * @return The PageTableEntry corresponding to the virtual page number, or null if not found.
     */
    public PageTableEntry getEntry(int virtualPageNumber) {
        if (virtualPageNumber >= 0 && virtualPageNumber < entries.length) {
            return entries[virtualPageNumber];
        }
        return null; // Return null if the virtual page number is out of bounds
    }

    // Provides access to the list of page table entries. Useful for inspection or debugging.
    public PageTableEntry[] getEntries() {
        return entries;
    }

    // free the one process's pageTable Entry
    public void freeProcessPageEntry(int pid){
        System.out.println ("释放进程："+pid+" 空间");
        for (int i = 0; i < entries.length; i++) {
            if(entries[i].getAllocatedPid ()==pid){
                entries[i] = new PageTableEntry (-1,
                        false, false, AccessPermissions.ALL, -1);
            }
        }
    }

    // whether this pid have the pageTable entry
    public boolean isAllocatedEntries(int pid){
        for(PageTableEntry e:entries){
            if(e.getAllocatedPid ()==pid&&e.isUsed ()==true){
                return true;
            }
        }
        return false;
    }

    // calculate not allocated entries number
    public int getNotAllocatedEntries(){
        int number = 0;
        for(PageTableEntry e:entries){
            if(e.isUsed ()==false){
                number++;
            }
        }
        return number;
    }

    // allocated the particular entries to one process
    public void setOneEntries(int index,int pid,int physicalIndex){
        System.out.println ("allocate the " + index + "th pageTable entries to the process pid:" + pid);
        entries[index] = new PageTableEntry (physicalIndex, true, false,
                AccessPermissions.ALL, pid);
    }

    // allocate the entries for the process
    public void allocateEntries(ProcessStruct tpProcess){
        System.out.println ("allocate the entries for the process pid:"+tpProcess.getPid ());
        /*
        * allocatedEntries for the process
        * two condition
        *   1. remainEntries is 0 number
        *   2. remainEntries is not 0 number
         */
        int needEntries = tpProcess.getMemorySize ();
        int remainEntries = getNotAllocatedEntries ();
        if(remainEntries==0){
            // only preempt entries from other process
            preemptEntries (tpProcess);
        }

        int allocatedEntries = 0;
        if(needEntries<remainEntries){
            allocatedEntries = (int)(needEntries * tpProcess.getPriority ()/OS.priorityMax);
        }else{
            allocatedEntries = (int) (remainEntries * tpProcess.getPriority ()/OS.priorityMax);
        }
        // prevent have only one remainEntries
        if(allocatedEntries==0){
            allocatedEntries++;
        }


        // base on this rule,confirm the number of entries allocated to this process
        int tpNum = 0;
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].isUsed ()==false){
                setOneEntries (i,tpProcess.getPid (),tpProcess.getBeginMemoryIndex ()+tpNum);
                tpProcess.getPageTableIndex ().add (i);
                tpNum++;
            }
            if(tpNum==allocatedEntries){
                break;
            }
        }
    }
    // 判断进程的页表中是否有需要的物理块
    public boolean isContainPhysicalPage(ProcessStruct thProcess){
        for (int i = 0; i < thProcess.getPageTableIndex ().size (); i++) {
            int pageIndex = thProcess.getPageTableIndex ().get (i);
            if(entries[pageIndex].getPhysicalFrameNumber ()==thProcess.getPcPage ()+thProcess.getBeginMemoryIndex ()){
                return true;
            }
        }
        return false;
    }
    // 处理 缺页错误
    public void pageTableFault(ProcessStruct tProcess){
        System.out.println ("process pid:"+tProcess.getPid ()+" 发生缺页错误");
        int needPhysical = tProcess.getPcPage ()+tProcess.getBeginMemoryIndex ();
        try{
            int ptIndex = tProcess.getPageTableIndex ().get (0);
            entries[ptIndex] = new PageTableEntry (needPhysical,
                    true, false, AccessPermissions.ALL, tProcess.getPid ());
        }catch (Exception e){
            System.out.println ("该进程无页表项,需要被分配页表");
            allocateEntries (tProcess);
        }
    }
    public void preemptEntries(ProcessStruct tpProcess){
        int needEntries = tpProcess.getMemorySize ();

        int allocatedNum = (int)(needEntries / (OS.priorityMax * 2));
        if(allocatedNum==0){
            allocatedNum++;
        }

        // which entry should be allocated?
        // the random select entries is better than select from the high number entries process
        Random rand = new Random();
        ArrayList<Integer> randomNumbers = new ArrayList<>();
        for (int i = 0; i < allocatedNum; i++) {
            randomNumbers.add( rand.nextInt(entries.length - 1));
        }
        int tpIndex = -1;
        try {
            for (int i = 0; i < randomNumbers.size(); i++) {
                tpIndex = randomNumbers.get(i);

                // 确保tpIndex有效
                if (tpIndex < entries.length && tpIndex >= 0) {
                    // 从旧的进程页表索引中移除
                    OS.processMap.get(entries[tpIndex].getAllocatedPid())
                            .getPageTableIndex().remove((Integer) tpIndex); // 显式地移除对象

                    // 配置新的页表条目
                    entries[i] = new PageTableEntry(tpProcess.getBeginMemoryIndex() + i, true, false, AccessPermissions.ALL,
                            tpProcess.getPid());

                    // 添加到新进程的页表索引中
                    tpProcess.getPageTableIndex().add(tpIndex);
                } else {
                    // 处理无效tpIndex
                    System.out.println("Invalid tpIndex: " + tpIndex);
                }
            }
        } catch (Exception e) {
            // 打印出异常的相关信息来帮助诊断问题
            System.out.println("Exception caught: " + e.getMessage());
            System.out.println("Entries length: " + entries.length);
            // 这里需要检查tpIndex是否在有效范围内来避免在异常处理中引发另一个异常
            if(tpIndex >= 0 && tpIndex < entries.length) {
                System.out.println("Process pageTableIndex size: " + OS.processMap.get(entries[tpIndex].getAllocatedPid())
                        .getPageTableIndex().size());
            }
        }

    }
}
