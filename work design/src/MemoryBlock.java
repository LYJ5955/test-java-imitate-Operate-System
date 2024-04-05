import java.util.*;
public class MemoryBlock {
    private int blockId;
    private boolean isAllocated;
    private int processId; // The ID of the process using this block, -1 if it's free
    private List<Instruction> instructionList;
    public static int BlockSize = 20;
    public Instruction getInstruction(int index){
        try{
            return instructionList.get (index);
        }catch (Exception e){
            System.out.println ("Illegal index for getInstruction,please terminate and examine");
            return new Instruction ();
        }
    }

    public MemoryBlock(int blockId) {
        this.blockId = blockId;
        this.isAllocated = false;
        this.processId = -1; // -1 indicates the block is free
        this.instructionList = new ArrayList<>(); // Initialize with default capacity

        // set instructions Kinds
        Random rand = new Random();
        int randomValue = rand.nextInt(BlockSize) + 1;
        if(randomValue % 2 == 0) {
            // containing io
            randomValue = randomValue % BlockSize;
            for (int i = 0; i < randomValue; i++) {
                Instruction instruction = new Instruction (i, InstructionState.CALCULATION);
                this.instructionList.add(instruction);
            }
            for (int i = randomValue; i < BlockSize; i++) {
                Instruction instruction = new Instruction (i, InstructionState.IO);
                this.instructionList.add(instruction);
            }
        } else {
            // just only calculations
            for (int i = 0; i < BlockSize; i++) {
                Instruction instruction = new Instruction (i, InstructionState.CALCULATION);
                this.instructionList.add(instruction);
            }
        }
    }


    // Getters
    public int getBlockId() {
        return blockId;
    }

    public boolean isAllocated() {
        return isAllocated;
    }

    public int getProcessId() {
        return processId;
    }

    // Setters
    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public void setAllocated(boolean isAllocated) {
        this.isAllocated = isAllocated;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }
}
