public interface CpuScheduler {
    void addScheduleProcess(Integer pid); // Enqueue a process by PID
    Integer selectNextProcess() throws InterruptedException; // Select the next process for execution and return its PID

    void deleteFinishedProcess (); // delete the finished process for right schedule
}
