import org.apache.log4j.Logger;

import java.util.Iterator;

public class PriorityScheduler extends AbstructCpuScheduler{
    public static Logger logger = Logger.getLogger (PriorityScheduler.class);

    @Override
    public Integer selectNextProcess() throws InterruptedException {
        if(OS.readyQueue.size ()==0){
            Thread.sleep (5000);
            logger.info ("Os is waiting new process~");
          // System.out.println ("Os is waiting new process~");
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
        logger.info ("Priority Schedule : now the ready queue and priority are this");
        StringBuilder sb = new StringBuilder();
        for (Integer item : OS.readyQueue) {
            ProcessStruct process = OS.processMap.get(item);
            if (process != null) {
                sb.append("PID: ").append(process.getPid())
                        .append(", Priority: ").append(process.getPriority())
                        .append("\n");
            } else {
                sb.append("No process found for PID: ").append(item).append("\n");
            }
        }
        logger.info(sb.toString());
        logger.info ("The process with pid: "+maxPid+" scheduled to be executed");

        return maxPid;
    }
}
