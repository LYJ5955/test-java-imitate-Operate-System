import java.util.Random;
import java.util.Scanner;

public abstract class Interrupt {
    private InterruptState state; // 中断的状态
    private int priority; // 中断的优先级

    // 构造函数
    public Interrupt(InterruptState state, int priority) {
        this.state = state;
        this.priority = priority;
    }

    // Getter和Setter
    public InterruptState getState() {
        return state;
    }

    public void setState(InterruptState state) {
        this.state = state;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    // 抽象方法，用于处理中断
    public abstract void handle();
}
class IoFinishInterrupt extends Interrupt {
    private int pid;
    public IoFinishInterrupt(int priority,int pid) {
        super(InterruptState.IoFinish, priority);
        this.pid = pid;
    }


    @Override
    public void handle() {
        System.out.println("Handling I/O Finish Interrupt with priority: " + getPriority()+" for process: "+this.pid);
        // 实现I/O完成后的处理逻辑
        // 将其加入ready队列
        OS.getProcessMap ().get (this.pid).setState (ProcessState.READY);
        OS.blockedQueue.remove (this.pid);
        OS.readyQueue.offer (this.pid);
    }
}

class IoBeginInterrupt extends Interrupt {
    private int pid;
    public IoBeginInterrupt(int pid,int priority) {
        super(InterruptState.IoBegin, priority);
        this.pid = pid;
    }

    @Override
    public void handle() {
        System.out.println("Handling I/O Begin Interrupt with priority: " + getPriority()+" for process: "+this.pid);
        // 实现I/O开始的处理逻辑
        // 将其加入block队列并将状态改为blocked
        OS.blockedQueue.offer (this.pid);
        OS.getProcessMap ().get (this.pid).setState (ProcessState.BLOCKED);
        // 需要重新运行调度器
        OS.interruptManager.setIsSchedule (true);
    }
}
class priorityAdjustInterrupt extends Interrupt {
    private int pid;
    public priorityAdjustInterrupt(int pid,int priority) {
        super(InterruptState.PriorityAdjust, priority);
        this.pid = pid;
    }

    @Override
    public void handle() {
        System.out.println("Handling priority Adjust Interrupt with priority: " + getPriority()+" for process: "+this.pid);
        // 改变优先级
        Random random = new Random();
        int randomNumber = 1 + random.nextInt(OS.priorityMax);  // nextInt(max) 生成一个0到max-1的随机数
        OS.processMap.get (pid).setPid (randomNumber);
    }
}
class CreateProcessInterrupt extends Interrupt {
    public CreateProcessInterrupt(int priority) {
        super(InterruptState.CreateProcess, priority);
    }

    @Override
    public void handle() {
        System.out.println("Create new process use Interrupt");
        // 实现系统调用的处理逻辑
        Scanner scanner=new Scanner (System.in);
        OS.createProcessWorkflow (scanner);
    }
}

class TimeSliceInterrupt extends Interrupt {
    public TimeSliceInterrupt(int priority) {
        super(InterruptState.TimeSlice, priority);
    }

    @Override
    public void handle() {
        System.out.println("Handling Time Slice Interrupt with priority: " + getPriority());
        // 实现时间片到期的处理逻辑

    }
}
class ProcessTerminateInterrupt extends Interrupt{
    private int pid;
    public ProcessTerminateInterrupt(int pid,int priority) {
        super(InterruptState.ProcessTerminate, priority);
        this.pid = pid;
    }

    @Override
    public void handle() {
        // 实现terminate终止开始的处理逻辑
        // 设置该进程的状态为terminate，并将其从其他队列移入terminate队列
        System.out.println("Handling ProcessTerminate Interrupt with priority: " + getPriority());
        ProcessStruct tProcess = OS.processMap.get (this.pid);
        if(OS.readyQueue.contains (this.pid)){
            OS.readyQueue.remove (this.pid);
        }
        if(OS.runningQueue.contains (this.pid)){
            OS.runningQueue.remove (this.pid);
        }
        if(OS.blockedQueue.contains (this.pid)){
            OS.blockedQueue.remove (this.pid);
        }
        tProcess.setState (ProcessState.TERMINATED);
        OS.terminateQueue.offer (this.pid);
        System.out.println ("process "+this.pid+" execute finally");
    }
}
class ElectronicBreakInterrupt extends Interrupt {
    public ElectronicBreakInterrupt(int priority) {
        super(InterruptState.ElectronicBreak, priority);
    }

    @Override
    public void handle() {
        System.out.println("Handling Electronic Break Interrupt with priority: " + getPriority());
        System.out.println ("This it the highest priority interrupt,Everything will terminate");
        // 实现电子中断（紧急停止）的处理逻辑
        System.exit (0);
    }
}

class SystemCallInterrupt extends Interrupt {
    public SystemCallInterrupt(int priority) {
        super(InterruptState.SystemCall, priority);
    }

    @Override
    public void handle() {
        System.out.println("Handling System Call Interrupt with priority: " + getPriority());
        // 实现系统调用的处理逻辑
    }
}
class OutputSysInformation extends Interrupt{
    public OutputSysInformation(int priority) {
        super(InterruptState.SystemCall, priority);
    }
    @Override
    public void handle() {
        System.out.println("执行系统内容输出");
        // 实现系统调用的处理逻辑
    }
}

