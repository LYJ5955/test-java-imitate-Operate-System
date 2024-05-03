import org.apache.log4j.Logger;

public class FCFSScheduler extends AbstructCpuScheduler {
    public static Logger logger = Logger.getLogger (FCFSScheduler.class);
    @Override
    public Integer selectNextProcess() throws InterruptedException {
        if(OS.readyQueue.size ()==0){
            Thread.sleep (5000);
            logger.info ("Schedule: Os is waiting new process~");
            return -1;
        }
        StringBuilder sb = new StringBuilder();
        for (Integer item : OS.readyQueue) {
            sb.append(item).append(" ");
        }
        logger.info ("FCFS : now the ready queue is this");
        logger.info("Ready Queue: "+sb.toString());
        int nextPid = OS.readyQueue.poll ();
        logger.info ("Schedule: The process with pid: "+nextPid+" scheduled to be executed");
        return nextPid;
    }
}
