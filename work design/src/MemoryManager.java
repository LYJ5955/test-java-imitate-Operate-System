import org.apache.log4j.Logger;
public class MemoryManager {
    public static Logger logger = Logger.getLogger (MemoryManager.class);
    private MemoryBlock[] memoryBlocks; // Array of memory blocks representing the physical memory
    private int totalBlocks; // Total number of blocks in memory

    /**
     * Initializes the memory manager with a specified number of memory blocks.
     * @param totalBlocks The total number of memory blocks available in the system.
     */
    public MemoryManager(int totalBlocks) {
        this.totalBlocks = totalBlocks;
        this.memoryBlocks = new MemoryBlock[totalBlocks];
        for (int i = 0; i < totalBlocks; i++) {
            memoryBlocks[i] = new MemoryBlock(i); // Initialize each memory block
        }
    }

    /**
     * Attempts to allocate a specified number of memory blocks to a process.
     * @param processId The ID of the process requesting memory allocation.
     * @param numberOfBlocks The number of memory blocks requested by the process.
     * @return true if allocation is successful, false otherwise.
     */
    public int allocateMemory(int processId, int numberOfBlocks) {
        // Simple first-fit allocation strategy as an example
        // isAllocated false show not allocated to one process
        int beginIndex = -1;
        int contiguousBlocks = 0;
        for (int i = 0; i < totalBlocks; i++) {
            if (!memoryBlocks[i].isAllocated()) {
                contiguousBlocks++;
                if (contiguousBlocks == numberOfBlocks) {
                    // if continuous free block is enough
                    beginIndex = i - numberOfBlocks + 1;
                    for (int j = i - numberOfBlocks + 1; j <= i; j++) {
                        memoryBlocks[j].setAllocated(true);
                        memoryBlocks[j].setProcessId(processId);
                    }
                    logger.info("Memory Management: Allocated " + numberOfBlocks + " blocks starting at index " + beginIndex + " for PID " + processId + ".");

                    return beginIndex;
                }
            } else {
                contiguousBlocks = 0; // Reset if a used block is found
            }
        }
        return beginIndex; // Allocation failed
    }

    /**
     * Frees up memory blocks used by a specific process.
     * @param processId The ID of the process whose memory is to be freed.
     */
    public void freeMemory(int processId) {
        for (MemoryBlock block : memoryBlocks) {
            if (block.getProcessId() == processId) {
                block.setAllocated(false);
                block.setProcessId(-1);
            }
        }
        logger.info("Memory Management: Freed memory blocks allocated to PID: " + processId + ".");
    }

    // Getters
    public synchronized MemoryBlock[] getMemoryBlocks() {
        return memoryBlocks;
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }
}
