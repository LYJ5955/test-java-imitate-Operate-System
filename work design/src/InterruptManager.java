import org.apache.log4j.Logger;

import java.util.*;

public class InterruptManager {
    private Queue<Interrupt> interruptqueue;
    private boolean isSchedule;
    public static Logger logger = Logger.getLogger (InterruptManager.class);


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
            logger.info ("Interrupt Management: Execute the interrupt with "+interrupt.getState ()+" state.");
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
