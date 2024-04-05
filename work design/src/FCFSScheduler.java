public class FCFSScheduler extends AbstructCpuScheduler {
    @Override
    public Integer selectNextProcess() throws InterruptedException {
        if(OS.readyQueue.size ()==0){
            Thread.sleep (5000);
            System.out.println ("Os is waiting new process~");
            return -1;
        }
        int nextPid = OS.readyQueue.poll ();
        System.out.println ("The process with pid: "+nextPid+" scheduled to be executed");
        return nextPid;
    }
}
