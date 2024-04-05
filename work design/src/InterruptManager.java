import java.util.*;

public class InterruptManager {
    private Queue<Interrupt> interruptqueue;
    private boolean isSchedule;


    public InterruptManager () {
        interruptqueue = new LinkedList<> ();
        isSchedule = false;
    }

    public void addInterrupt (Interrupt interrupt) {
        interruptqueue.offer (interrupt);
    }

    public void executeInterrupt () {
        while (false == interruptqueue.isEmpty ()) {
            System.out.println ("Execute Interrupt");
            Interrupt interrupt = interruptqueue.poll ();

            interrupt.handle ();
        }
    }
    public boolean getIsSchedule(){
        return this.isSchedule;
    }
    public void setIsSchedule(boolean isSchedule){
        this.isSchedule=isSchedule;
    }

    public int getInterruptNum () {
        return interruptqueue.size ();
    }
}
