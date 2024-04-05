import java.util.*;
public abstract class AbstructCpuScheduler implements CpuScheduler {
    // 调度队列
    // protected Queue<Integer> queue = new LinkedList<>();

    @Override
    public void addScheduleProcess(Integer pid) {
        OS.readyQueue.offer(pid);
    }

    // delete already terminated process from queue
    public void deleteFinishedProcess(){
        Iterator<Integer> it = OS.readyQueue.iterator ();
        while (it.hasNext ()){
            int pid = it.next ();
            if(OS.processMap.get (pid).getState ()==ProcessState.TERMINATED){
                OS.readyQueue.remove (it);
                OS.terminateQueue.offer (OS.processMap.get (pid).getPid ());
            }
        }
    }

    // selectNextProcess is specific to the scheduling algorithm and must be implemented by subclasses.
    @Override
    public abstract Integer selectNextProcess() throws InterruptedException;


}
