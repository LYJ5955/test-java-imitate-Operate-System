import org.apache.log4j.Logger;
import java.util.Random;

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
    public static Logger logger = Logger.getLogger (IoFinishInterrupt.class);

    private int pid;
    public IoFinishInterrupt(int priority,int pid) {
        super(InterruptState.IoFinish, priority);
        this.pid = pid;
    }


    @Override
    public void handle() {
        logger.info ("IoFinishInterrupt: Handling I/O Finish Interrupt with priority: " + getPriority()+" for process: "+this.pid);
        // 实现I/O完成后的处理逻辑
        // 将其加入ready队列
        OS.getProcessMap ().get (this.pid).setState (ProcessState.READY);
        OS.blockedQueue.remove (this.pid);
        OS.readyQueue.offer (this.pid);
    }
}

class IoBeginInterrupt extends Interrupt {
    public static Logger logger = Logger.getLogger (IoBeginInterrupt.class);

    private int pid;
    public IoBeginInterrupt(int pid,int priority) {
        super(InterruptState.IoBegin, priority);
        this.pid = pid;
    }

    @Override
    public void handle() {
        logger.info ("IoBeginInterrupt: Handling I/O Begin Interrupt with priority: " + getPriority()+" for process: "+this.pid);
        // 实现I/O开始的处理逻辑
        // 将其加入block队列并将状态改为blocked
        OS.blockedQueue.offer (this.pid);
        OS.getProcessMap ().get (this.pid).setState (ProcessState.BLOCKED);
        // 需要重新运行调度器
        OS.interruptManager.setIsSchedule (true);
    }
}
class priorityAdjustInterrupt extends Interrupt {
    public static Logger logger = Logger.getLogger (priorityAdjustInterrupt.class);

    private int pid;
    public priorityAdjustInterrupt(int pid,int priority) {
        super(InterruptState.PriorityAdjust, priority);
        this.pid = pid;
    }

    @Override
    public void handle() {
        logger.info ("priorityAdjustInterrupt: Handling priority Adjust Interrupt with priority: " + getPriority()+" for process: "+this.pid);
        // 改变优先级
        Random random = new Random();
        int randomNumber = 1 + random.nextInt(OS.priorityMax);  // nextInt(max) 生成一个0到max-1的随机数
        OS.processMap.get (pid).setPid (randomNumber);
        System.out.println ("new priority of process with PID:"+pid+" is "+randomNumber);
    }
}
class CreateProcessInterrupt extends Interrupt {
    public static Logger logger = Logger.getLogger (CreateProcessInterrupt.class);

    public CreateProcessInterrupt(int priority) {
        super(InterruptState.CreateProcess, priority);
    }

    @Override
    public void handle() {
        logger.info ("CreateProcessInterrupt: Create new process use Interrupt");
        Random rand = new Random();
        int randomNum = 10 + rand.nextInt(91); // 91 = 100 - 10 + 1
        String processName = "newProcess" + randomNum;
        if(-1!=OS.createProcess (processName, 1, 1, "dfFile")){
            System.out.println ("new process name "+processName+" create successfully");
        }

    }
}
class CreateFileInterrupt extends Interrupt {
    public static Logger logger = Logger.getLogger (CreateFileInterrupt.class);

    public CreateFileInterrupt(int priority) {
        super(InterruptState.CreateFile, priority);
    }

    @Override
    public void handle() {
        Random rand = new Random();
        int randomNum = 10 + rand.nextInt(91); // 91 = 100 - 10 + 1
        String fileName = "newFile" + randomNum;
        logger.info ("CreateFileInterrupt: Create new File use Interrupt");
        OS.createFile (fileName,null);
        System.out.println ("create new file name:"+fileName);
        // 实现系统调用的处理逻辑
        // should create with automatically ,shouldn't use console
    }
}
class CreateDirtoryInterrupt extends Interrupt {
    public static Logger logger = Logger.getLogger (CreateDirtoryInterrupt.class);

    public CreateDirtoryInterrupt(int priority) {
        super(InterruptState.CreateDirectory, priority);
    }

    @Override
    public void handle() {
        Random rand = new Random();
        int randomNum = 10 + rand.nextInt(91); // 91 = 100 - 10 + 1
        logger.info ("CreateDirectoryInterrupt: Create new Directory use Interrupt");
        // 实现系统调用的处理逻辑
        // should create with automatically ,shouldn't use console
        String dirName = "newDir" + randomNum;
        OS.createDirectory ("newDir"+randomNum);
        System.out.println ("create new Dir name: "+dirName);
    }
}
class TimeSliceInterrupt extends Interrupt {
    public static Logger logger = Logger.getLogger (TimeSliceInterrupt.class);

    public TimeSliceInterrupt(int priority) {
        super(InterruptState.TimeSlice, priority);
    }

    @Override
    public void handle() {
        logger.info ("TimeSliceInterrupt: Handling Time Slice Interrupt with priority: " + getPriority());
        // 实现时间片到期的处理逻辑

    }
}
class ProcessTerminateInterrupt extends Interrupt{
    public static Logger logger = Logger.getLogger (ProcessTerminateInterrupt.class);

    private int pid;
    public ProcessTerminateInterrupt(int pid,int priority) {
        super(InterruptState.ProcessTerminate, priority);
        this.pid = pid;
    }

    @Override
    public void handle() {
        // 实现terminate终止开始的处理逻辑
        // 设置该进程的状态为terminate，并将其从其他队列移入terminate队列
        logger.info ("ProcessTerminateInterrupt: Handling ProcessTerminate Interrupt with priority: " + getPriority());

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
        logger.info ("ProcessTerminateInterrupt: process "+this.pid+" execute finally");
    }
}
class ElectronicBreakInterrupt extends Interrupt {
    public static Logger logger = Logger.getLogger (ElectronicBreakInterrupt.class);

    public ElectronicBreakInterrupt(int priority) {
        super(InterruptState.ElectronicBreak, priority);
    }

    @Override
    public void handle() {
        logger.info ("ElectronicBreakInterrupt: Handling Electronic Break Interrupt with priority: " + getPriority());
        logger.info ("ElectronicBreakInterrupt: This it the highest priority interrupt,Everything will terminate");

        System.out.println ("ElectronicBreakInterrupt: Handling Electronic Break Interrupt with priority: " + getPriority());
        System.out.println ("ElectronicBreakInterrupt: This it the highest priority interrupt,Everything will terminate");

        // 实现电子中断（紧急停止）的处理逻辑
        System.exit (0);
    }
}

class SystemCallInterrupt extends Interrupt {
    public static Logger logger = Logger.getLogger (SystemCallInterrupt.class);

    public SystemCallInterrupt(int priority) {
        super(InterruptState.SystemCall, priority);
    }

    @Override
    public void handle() {
        logger.info ("SystemCallInterrupt: Handling System Call Interrupt with priority: " + getPriority());
        System.out.println ("SystemCallInterrupt: Handling System Call Interrupt with priority: " + getPriority());
        // 实现系统调用的处理逻辑
    }
}


class OutputSysInformation extends Interrupt{
    public static Logger logger = Logger.getLogger (OutputSysInformation.class);

    public OutputSysInformation(int priority) {
        super(InterruptState.OutputSysInformation, priority);
    }
    @Override
    public void handle() {
        logger.info ("OutputSysInformation: 执行系统内容输出");
        System.out.println ("OutputSysInformation: 执行系统内容输出");
        OS.OutputOsInformation ();
        // 实现系统调用的处理逻辑
    }
}

