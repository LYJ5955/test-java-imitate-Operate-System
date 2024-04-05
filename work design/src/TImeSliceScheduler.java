public class TImeSliceScheduler extends AbstructCpuScheduler{

    @Override
    public Integer selectNextProcess() throws InterruptedException {
        if(OS.readyQueue.size ()==0){
            Thread.sleep (5000);
            System.out.println ("Os is waiting new process~");
            return -1;
        }
        // Check if the queue is not empty
        if (!OS.readyQueue.isEmpty()) {
            // Take the next process from the front of the queue
            Integer pid = OS.readyQueue.poll();

            // Return this process to the end of the queue for its next turn (if needed)
            // Note: This step may be adjusted based on your handling of process completion.
            // If a process completes its execution and does not need to be scheduled again,
            // it should not be added back to the queue here.

            // OS.readyQueue.offer(pid);
            // 不确定这里是否需要Offer，因为如果进程不被执行了，会处理该进程的！

            // Return the process ID to be scheduled next
            System.out.println ("The process with pid: "+pid+" scheduled to be executed");
            return pid;
        }
        // Return null if the queue is empty indicating no process is currently scheduled.
        return null;
    }
}
