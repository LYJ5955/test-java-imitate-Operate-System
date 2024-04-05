import java.util.Iterator;

public class PriorityScheduler extends AbstructCpuScheduler{
    @Override
    public Integer selectNextProcess() throws InterruptedException {
        if(OS.readyQueue.size ()==0){
            Thread.sleep (5000);
            System.out.println ("Os is waiting new process~");
            return -1;
        }

        // return queue.poll();
        // queue中都是Pid，从ready队列中选择出优先级最大的Pid
        Iterator<Integer> iterator = OS.readyQueue.iterator();
        int maxPid = -1;
        int maxPriority = -1;
        while (iterator.hasNext()) {
            Integer element = iterator.next ();
            int tpPriority = OS.processMap.get (element).getPriority ();
            if(tpPriority>maxPriority){
                maxPriority = tpPriority;
                maxPid = element;
            }
        }
        System.out.println ("The process with pid: "+maxPid+" scheduled to be executed");
        return maxPid;
    }
}
