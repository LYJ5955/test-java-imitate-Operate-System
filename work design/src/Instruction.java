public class Instruction {
    private int index; // Index of the instruction
    private InstructionState state; // State of the instruction

    // Enum to represent the instruction state
    public Instruction(){
    }

    public Instruction(int index, InstructionState state) {
        this.index = index;
        this.state = state;
    }

    // Getters and setters
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public InstructionState getState() {
        return state;
    }

    public void setState(InstructionState state) {
        this.state = state;
    }
}
