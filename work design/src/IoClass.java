import org.apache.log4j.Logger;

public class IoClass extends Thread {
    public static Logger logger = Logger.getLogger (IoClass.class);
    public void run() {
        //System.out.println("System Io device begin running ~");
        logger.info ("System Io device begin running ~");

        while (true){
            if(OS.getBlockQueue ().size ()!=0){
                executeIo (OS.getBlockQueue ().poll ());
            }
        }
    }
    public void executeIo(int tPid){

        ProcessStruct tProcess = OS.getProcessMap ().get (tPid);
        // 执行IO就不需要去页表调用等等，直接从物理块中执行
        /*
        * 不需要调用到页表中，直接和物理内存交互
        *
         */
        while (tProcess.getPcPage ()<tProcess.getMemorySize ()){
            // 获取当前内存块
            MemoryBlock tpBlock=OS.getMemoryManager ().getMemoryBlocks()
                    [tProcess.getPcPage ()+tProcess.getBeginMemoryIndex ()];
            // 循环执行当前物理块
            while(tProcess.getPc ()<MemoryBlock.BlockSize
                    &&tpBlock.getInstruction (tProcess.getPc ()).getState ()==InstructionState.IO){
                // 执行Io指令
//                System.out.print ("Execute the IO instruction in ");
//                System.out.print (tProcess.getPcPage () + tProcess.getBeginMemoryIndex ());
//                System.out.print (" Physical page in ");
//                System.out.print (tProcess.getPc ());
//                System.out.println (" Instruction for process "+tProcess.getPid ());
                logger.info ("Execute the IO instruction in " +
                        (tProcess.getPcPage() + tProcess.getBeginMemoryIndex()) +
                        " Physical page in " +
                        tProcess.getPc() +
                        " Instruction for process " + tProcess.getPid());

                tProcess.addPc ();
            }
            // 判断是否一个物理块被完全执行完
            if(tProcess.getPc ()==MemoryBlock.BlockSize){
                tProcess.setPc (0);
                tProcess.addPcPage ();
                if(tProcess.getPcPage ()==tProcess.getMemorySize ()){
                    // 即 经过IO执行之后，该进程结束，移入到FINISH队列
                    OS.addInterrupt (new ProcessTerminateInterrupt (tProcess.getPid (),1));
                    break;
                }
            }
            if(tProcess.getPc ()<MemoryBlock.BlockSize){
                MemoryBlock nowBlock=OS.getMemoryManager ().getMemoryBlocks()
                        [tProcess.getPcPage ()+tProcess.getBeginMemoryIndex ()];
                if(nowBlock.getInstruction (tProcess.getPc ()).getState ()!=InstructionState.IO){
                    // 触发中断，将进程从block队列移动到ready队列
                    OS.addInterrupt (new IoFinishInterrupt (1,tProcess.getPid ()));
                    break;
                }
            }
        }

//        if(tProcess.getPcPage ()==tProcess.getMemorySize ()){
//            // 即 经过IO执行之后，该进程结束，移入到FINISH队列
//            OS.addInterrupt (new ProcessTerminateInterrupt (tProcess.getPid (),1));
//        }
    }
}