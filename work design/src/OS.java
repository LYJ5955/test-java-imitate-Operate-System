import java.util.*;

import org.apache.log4j.Logger;

public class OS {
    public static Logger logger = Logger.getLogger (OS.class);
    // 进程管理部分数据结构
    public static Map<Integer, ProcessStruct> processMap;
    public static Queue<Integer> readyQueue;
    public static Queue<Integer> blockedQueue;
    public static Queue<Integer> runningQueue;
    public static Queue<Integer> terminateQueue;
    public static int processId;

    //文件管理数据结构
    public static Map<String, File> fileMap;
    public static Map<String, Directory> directoryMap;

    // 内存管理数据结构
    public static PageTable pageTable;
    public static MemoryManager memoryManager;
    public static int pageTableSize = 20;
    public static int totalBlock = 100;


    // 创建调度器，在开始执行时初始化
    public static AbstructCpuScheduler scheduler;

    // 模拟时间片轮转时钟
    public static int timer;
    public static int timerSize;

    // 进程优先级上限
    public static int priorityMax;

    // 中断及其处理

    public static ScheduleRecord scheduleRecord;
    public static InterruptManager interruptManager;

    public static synchronized void addInterrupt(Interrupt tpInterrupt){
        logger.info ("Interrupt Management: Add a new interrupt.");
        interruptManager.addInterrupt (tpInterrupt);
    }

    public static synchronized Queue<Integer> getBlockQueue(){
        return blockedQueue;
    }
    public static synchronized Map<Integer, ProcessStruct> getProcessMap(){
        return processMap;
    }
    public static synchronized MemoryManager getMemoryManager(){
        return memoryManager;
    }

    public OS () {
//        logger.info ("this is a debug message");
//        logger.error ("this is a error");
        /*
         * 1. initiate the processMap and four process queues
         * 2. initiate the fileMap and pageTable and memoryManager
         * 3. initiate the CpuScheduler
         */
        // 1. Initiate the processMap and four process queues
        processMap = new HashMap<> ();
        readyQueue = new LinkedList<> ();
        blockedQueue = new LinkedList<> ();
        runningQueue = new LinkedList<> ();
        terminateQueue = new LinkedList<> ();
        processId = 0;
        timer = 0;
        priorityMax = 10;
        timerSize = 15;
        scheduleRecord = ScheduleRecord.None;

        // 2. Initiate the fileMap, pageTable, and memoryManager
        fileMap = new HashMap<> (); // Adjust according to actual FileMap class structure
        pageTable = new PageTable (pageTableSize); // Assuming a constructor exists for PageTable
        memoryManager = new MemoryManager (totalBlock); // Assuming a constructor exists for MemoryManager
        directoryMap = new HashMap<> ();
        interruptManager = new InterruptManager ();

        // 3. Initiate the CpuScheduler
        Scanner scanner = new Scanner (System.in);
        int choice = 0;

        while (true) {
            System.out.println ("Select the CPU scheduling algorithm:");
            System.out.println ("1: FCFS (First-Come, First-Served)");
            System.out.println ("2: Priority Preemptive");
            System.out.println ("3: Time Slice (Round Robin)");
            System.out.println ("Please input:");
            if (scanner.hasNextInt ()) {
                choice = scanner.nextInt ();
                if (choice >= 1 && choice <= 3) {
                    break; // Break the loop if a valid choice is made
                }
            } else {
                scanner.next (); // Consume the invalid input
            }

            System.out.println ("Invalid selection. Please enter 1, 2, or 3.");
        }

        // Placeholder for scheduler initialization
        switch (choice) {
            case 1:
                System.out.println ("You selected FCFS (First-Come, First-Served).");
                logger.info ("Select FCFS schedule");
                scheduler = new FCFSScheduler ();
                scheduleRecord = ScheduleRecord.FCFS;
                break;
            case 2:
                System.out.println ("You selected Priority Preemptive.");
                logger.info ("Select Priority Preemptive schedule");
                scheduler = new PriorityScheduler ();
                scheduleRecord = ScheduleRecord.PRIORITY;
                break;
            case 3:
                System.out.println ("You selected Time Slice (Round Robin).");
                logger.info ("Select Time Slice (Round Robin) schedule");
                scheduler = new TImeSliceScheduler ();
                scheduleRecord = ScheduleRecord.TIME;
                break;
            default:
                // This case should never be reached due to the validation above
                break;
        }
    }

    // output information in OS system
    public static void OutputOsInformation (){
        // queue
        // process state
        System.out.println("Ready Queue:");
        logger.info("Ready Queue:");
        printQueue(readyQueue);

        System.out.println("Blocked Queue:");
        logger.info("Blocked Queue:");
        printQueue(blockedQueue);

//        System.out.println("Running Queue:");
//        logger.info("Running Queue:");
//        printQueue(runningQueue);

        System.out.println("Terminate Queue:");
        logger.info("Terminate Queue:");
        printQueue(terminateQueue);

        logger.info("Process Information");
        System.out.println ("Process Information");
        for (Map.Entry<Integer, ProcessStruct> entry : processMap.entrySet()) {
            printProcess(entry.getKey(), entry.getValue());
        }
    }
    public static void printQueue(Queue<Integer> queue) {
        if (queue.isEmpty()) {
            logger.info("  [Queue is empty]");
            System.out.println("  [Queue is empty]");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Integer item : queue) {
                sb.append(item).append(" ");
            }
            logger.info(sb.toString());
            System.out.println(sb.toString()); // Prints all elements
        }
    }

    private static void printProcess(Integer key, ProcessStruct process) {
        StringBuilder sb = new StringBuilder();
        sb.append("  PID: ").append(process.getPid())
                .append("\n  Process Name: ").append(process.getProcessName())
                .append("\n  State: ").append(process.getState())
                .append("\n  Priority: ").append(process.getPriority())
                .append("\n  Memory Size: ").append(process.getMemorySize())
                .append("\n  Begin Memory Index: ").append(process.getBeginMemoryIndex())
                .append("\n  PC: ").append(process.getPc())
                .append("\n  PC Page: ").append(process.getPcPage())
                .append("\n  Page Table Indexes: ").append(process.getPageTableIndex())
                .append("\n");

        logger.info(sb.toString());
        System.out.println(sb.toString());
    }

    // For example, a method to create a new process:
    public static Integer createProcess (String processName, int priority, int memorySize,String fileName) {

        /*
         * implementation of process creation
         * 1. judge whether memory have enough space
         *   if not return false
         * 2. create creation (not running in this step,so not consider pageTable)
         * 3. joined into processMap and readyQueue
         * 3.1 join this process to file
         * 4. return pid or -1 nevertheless
         */
        int tpPid = processId + 1;
        int beginIndex = memoryManager.allocateMemory (tpPid, memorySize);
        if (beginIndex!=-1){
            processId++;
            ProcessStruct tpProcess = new ProcessStruct (beginIndex,
                    tpPid, processName, ProcessState.READY, priority, memorySize);
            processMap.put (tpPid, tpProcess);
            readyQueue.add (tpPid);
            logger.info("Process Management: MemoryBeginIndex: " + beginIndex +
                    ", PID: " + tpPid +
                    ", Name: '" + processName + '\'' +
                    ", State: " + tpProcess.getState () +
                    ", Priority: " + priority +
                    ", MemorySize: " + memorySize );
            fileMap.get(fileName).addProcess(tpPid);
        }else{
            logger.error ("Memory Management: Memory not enough, process create fail\n");
            System.out.println ("Memory not enough,process create fail\n");
            return -1;
        }
        return tpPid;
    }
    // a method to delete process
    public boolean deleteProcess(int pid){
        // Implementation of process delete
        /*
        * judge whether processMap contains this pid
        * 1. just make the state into terminate
        * 1.1 remove from the other queue
        *   but not make like this, if we use judge whether terminate before in queue
        * 2. delete from the file
        *   we don't do that so, we use process if it's really not terminate
        * 3. delete from the physical memory and pageTable
        * */
        // Check if the process exists and is not already terminated
        if (processMap.containsKey(pid) && processMap.get(pid).getState() != ProcessState.TERMINATED) {
            ProcessStruct process = processMap.get(pid);
            process.setState(ProcessState.TERMINATED); // Mark the process as terminated
            logger.info("Process Management: PID: " + pid + " - Process state changed to TERMINATED.");

            memoryManager.freeMemory(pid); // Free memory allocated to the process
            logger.info("Process Management: PID: " + pid + " - Process memory has been freed.");

            pageTable.freeProcessPageEntry(pid); // Free page table entries associated with the process
            logger.info("PageTable Management: Freed page table entries for PID: " + pid + ".");

            System.out.println("PID: " + pid + " process has been successfully deleted.");
            logger.info("File Management: PID: " + pid + " - Process successfully deleted.");
            return true;

        } else {
            System.out.println("The PID is illegal or the process is already terminated, unable to delete.");

            return false;
        }
    }
    public static String createDirectory(String directoryName){
        /*
        * 判断是否存在重名文件夹 - 同样，不需要这个逻辑！
        * 但是要将文件夹加入到文件夹Map中去
         */
        Directory tpDirectory = new Directory (directoryName);
        directoryMap.put (directoryName, tpDirectory);
        //System.out.println ("The directory "+directoryName+" create successfully");
        return directoryName;

    }
    public boolean deleteDirectory(String directoryName){
        /*
        * judge whether this directory contained
        *  if not return false
        * then traversal all file and all process delete it
        * then remove this directory from the directory map
         */
        if(directoryMap.containsKey (directoryName)){
            Directory tpDirectory = directoryMap.get (directoryName);
            List<String> tpnameList = new ArrayList<> (tpDirectory.getFileNames ());
            for(String fileName:tpnameList){
                if(fileMap.containsKey (fileName)){
                    deleteFile (fileName);
                }
            }
            directoryMap.remove (directoryName);
            logger.info ("File Management: delete the Directory name: "+directoryName+"\n");
            return true;
        }else {
            System.out.println ("Not have "+directoryName+" directory in system");
            return false;
        }
    }
    public boolean deleteFile(String fileName){
        /*
         * judge whether this file contained
         *  if not return false
         * then traversal all  process delete it
         * then remove this file from the fileMap
         * 还有一个问题，删去File之后，directory中的filename也要被删除
         */
        if(fileMap.containsKey (fileName)){
            if(fileMap.containsKey (fileName)){
                File tpFile = fileMap.get (fileName);
                for(int processId:tpFile.getProcessIds ()){
                    if(processMap.containsKey (processId)){
                        deleteProcess (processId);
                    }
                }
                fileMap.remove (fileName);
                // Iterate through all directoryMap entries
                for (Map.Entry<String, Directory> entry : directoryMap.entrySet()) {
                    Directory directory = entry.getValue();
                    // Remove the file from the directory if it exists
                    if (directory.removeFile(fileName)) {
                        System.out.println("Removed file '" + fileName + "' from directory '" + entry.getKey() + "'.");
                    }
                }
            }
            logger.info ("File Management: delete the File name: "+fileName+"\n");
            return true;
        }else {
            System.out.println ("Not have "+fileName+" file in system");
            return false;
        }
    }
    public static String createFile(String fileName,String dirName){
        /*
         * 判断是否存在重名文件 -
         * 不需要这个逻辑，在调用函数之前已经确定不会出现这个问题
         * 但是在函数中要讲文件加入到文件夹中
         * 但是也并不是文件一定有所属文件夹啊！
         */
        // Create the file and add it to the global file map regardless of directory
        File tpFile = new File(fileName);
        fileMap.put(fileName, tpFile);

        if (dirName != null && directoryMap.containsKey(dirName)) {
            // The file belongs to a directory
            Directory directory = directoryMap.get(dirName);
            // Add the file to the directory
            directory.addFile(fileName);
            System.out.println("The file '" + fileName + "' was added to directory '" + dirName + "' successfully.");
        } else if (dirName == null) {
            // The file does not belong to any directory
            System.out.println("Creating a file without a directory.");
        } else {
            System.out.println("Invalid directory name. File will be created without a directory.");
        }

        System.out.println("The file '" + fileName + "' was created successfully.");
        logger.info ("File Management: The file '" + fileName + "' was created successfully.");
        return fileName;

    }
    public void start () {
        // reminder user to create directory\file\process
        Scanner scanner = new Scanner(System.in);

        System.out.println("Operating System Simulation Starting...");
        System.out.println("Please follow the prompts to create directories, files, and processes.");


        // 目前使用代码自动生成测试用例
        // createWorkflow (scanner); //创建工作
        testExampleCreate ();
        createDirectory ("dfDir");
        createFile ("dfFile",null);
        inspectWorkflow (scanner); //展示工作
        deleteWorkflow (scanner); //删除工作

        System.out.println ("\n Pre-configuration End\n");
    }
    public void testExampleCreate() {
//        createDirectory ("Dir1");
//        createFile ("file1", "Dir1");
//        for (int i = 1; i <= 3; i++) {
//            String processName = "Process" + i;
//            int priority = (int) (Math.random() * 10 + 1); // Priority between 1 and 10
//            int memorySize = (int) (Math.random() * 10 + 1); // Memory size between 1 and 10
//            String fileName = "file1";
//
//            createProcess(processName, priority, memorySize, fileName);
//            System.out.println("Created process: " + processName + " with priority: " + priority +
//                    ", memory size: " + memorySize + " and associated with file: " + fileName);
//        }

        // Create directories - Ensuring Dir3 remains empty
        String[] directories = {"Dir1", "Dir2", "Dir3"};
        for (String dirName : directories) {
            createDirectory(dirName);
            System.out.println("Created directory: " + dirName);
        }

        // Create files with the first four associated with Dir1 and Dir2
        Map<String, String[]> filesInDirectories = new HashMap<>();
        filesInDirectories.put("Dir1", new String[]{"File1", "File2"});
        filesInDirectories.put("Dir2", new String[]{"File3", "File4"});

        // Files not belonging to any directory - File7 will be left empty
        String[] standaloneFiles = {"File5", "File6", "File7"};

        // Create files within directories
        for (Map.Entry<String, String[]> entry : filesInDirectories.entrySet()) {
            String dirName = entry.getKey();
            for (String fileName : entry.getValue()) {
                createFile(fileName, dirName);
                System.out.println("Created file: " + fileName + " in directory: " + dirName);
            }
        }

        // Create standalone files
        for (String fileName : standaloneFiles) {
            createFile(fileName, null); // null indicates no directory association
            System.out.println("Created standalone file: " + fileName);
        }

        // Associate processes with files, except for File7
        String[] associatedFiles = {"File1", "File2", "File3", "File4", "File5", "File6"};
        for (int i = 1; i <= 15; i++) {
            String processName = "Process" + i;
            int priority = (int) (Math.random() * 10 + 1); // Priority between 1 and 10
            int memorySize = (int) (Math.random() * 10 + 1); // Memory size between 1 and 10
            String fileName = associatedFiles[(int) (Math.random() * associatedFiles.length)]; // Random file association, excluding File7

            if(-1!=createProcess(processName, priority, memorySize, fileName)){
                System.out.println("Created process: " + processName + " with priority: " + priority +
                        ", memory size: " + memorySize + " and associated with file: " + fileName);
                ProcessStruct tProcess = OS.getProcessMap ().get (i);
                System.out.println ("begin index is :"+tProcess.getBeginMemoryIndex ());
                for (int j = 0; j < memorySize; j++) {
                    MemoryBlock tpBlock = memoryManager.getMemoryBlocks ()[tProcess.getBeginMemoryIndex ()+j];
                    System.out.println ("block :"+(j+tProcess.getBeginMemoryIndex ()));
                    for (int k = 0; k < MemoryBlock.BlockSize; k++) {
                        System.out.print(tpBlock.getInstruction (k).getState ());
                    }
                    System.out.println ();
                }
            }else {
                System.out.println ("error IN PID "+i);
            }
        }
    }

    public void deleteWorkflow(Scanner scanner) {
        while (true) {
            System.out.println("\nWhat would you like to delete?");
            System.out.println("1. Directory");
            System.out.println("2. File");
            System.out.println("3. Process");
            System.out.println("4. Exit Deletion Workflow");
            System.out.print("Enter your choice (1/2/3/4): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": // Delete Directory
                    System.out.print("Enter the name of the directory to delete: ");
                    String directoryName = scanner.nextLine();
                    if (deleteDirectory(directoryName)) {
                        System.out.println("Directory '" + directoryName + "' has been deleted.");
                        logger.info ("File Management: Directory '" + directoryName + "' has been deleted.");
                    } else {
                        System.out.println("Directory deletion failed.");
                    }
                    break;

                case "2": // Delete File
                    System.out.print("Enter the name of the file to delete: ");
                    String fileName = scanner.nextLine();
                    if (deleteFile(fileName)) {
                        System.out.println("File '" + fileName + "' has been deleted.");
                    } else {
                        System.out.println("File deletion failed.");
                    }
                    break;

                case "3": // Delete Process
                    System.out.print("Enter the PID of the process to delete: ");
                    int pid = -1;
                    try {
                        pid = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid PID. Please enter a valid integer.");
                        break;
                    }
                    if (deleteProcess(pid)) {
                        System.out.println("Process with PID '" + pid + "' has been deleted.");
                    } else {
                        System.out.println("Process deletion failed.");
                    }
                    break;

                case "4": // Exit Deletion Workflow
                    System.out.println("Exiting deletion workflow...");
                    return;

                default:
                    System.out.println("Invalid choice. Please enter a valid option (1, 2, 3, or 4).");
                    break;
            }

            // Perform an inspection after the deletion
            inspectWorkflow(scanner);
        }
    }

    public void inspectWorkflow(Scanner scanner) {
        System.out.println("System Inspection Workflow");

        // Display directories and their files
        System.out.println("\n--- Directories and Files ---");
        for (Map.Entry<String, Directory> directoryEntry : directoryMap.entrySet()) {
            String dirName = directoryEntry.getKey();
            Directory directory = directoryEntry.getValue();
            System.out.println("Directory: " + dirName);

            List<String> fileNames = directory.getFileNames();
            if (fileNames.isEmpty()) {
                System.out.println("  [No files]");
            } else {
                for (String fileName : fileNames) {
                    System.out.println("  File: " + fileName);
                }
            }
        }

        // Display files and their processes
        System.out.println("\n--- Files and Processes ---");
        for (Map.Entry<String, File> fileEntry : fileMap.entrySet()) {
            String fileName = fileEntry.getKey();
            File file = fileEntry.getValue();
            System.out.println("File: " + fileName);

            List<Integer> processIds = file.getProcessIds();
            if (processIds.isEmpty()) {
                System.out.println("  [No processes]");
            } else {
                for (Integer pid : processIds) {
                    if (processMap.containsKey(pid) && processMap.get (pid).getState ()!=ProcessState.TERMINATED) {
                        ProcessStruct process = processMap.get(pid);
                        System.out.println("  Process ID: " + process.getPid() + ", Name: " + process.getProcessName());
                    }
                }
            }
        }

        // Display processes
        System.out.println("\n--- Processes ---");
        for (Map.Entry<Integer, ProcessStruct> processEntry : processMap.entrySet()) {
            ProcessStruct process = processEntry.getValue();
            System.out.println("Process PID: " + process.getPid() + ", Name: " + process.getProcessName() + ", State:" +
                    " " + process.getState());
        }
    }

    public static void createWorkflow(Scanner scanner){

        String input;
        while (true) {
            System.out.println("\nWould you like to [1] Create Directory, [2] Create File, [3] Create Process, or [4] Exit Setup?");
            System.out.print("Enter your choice (1/2/3/4): ");
            input = scanner.nextLine();

            switch (input) {
                case "1":
                    createDirectoryWorkflow(scanner);
                    break;

                case "2":
                    createFileWorkflow(scanner);
                    break;

                case "3":
                    createProcessWorkflow(scanner);
                    break;

                case "4":
                    System.out.println("Exiting setup...");
                    return;

                default:
                    System.out.println("Invalid choice. Please enter a valid option (1, 2, 3, or 4).");
                    break;
            }
        }
    }

    public static void createDirectoryWorkflow(Scanner scanner) {
        /*
        * 1. judge whether existed this name Directory
        *   if not , go create directory and add it to directoryMap and reminder success, return true
        *   whether , reminder already existed this directory, create failness, return false
         */
        System.out.print("Enter Directory Name: ");
        String directoryName = scanner.nextLine();

        if (directoryMap.containsKey(directoryName)) {
            System.out.println("Directory creation failed: A directory with the name '" + directoryName + "' already exists.");
            logger.error ("File Manage: Directory creation failed: A directory with the name '" + directoryName + "' " +
                    "already exists.");
        } else {
            createDirectory (directoryName);
            System.out.println("Directory '" + directoryName + "' created successfully.");
            logger.info ("File Manage: Directory '" + directoryName + "' created successfully.");
        }

    }

    public static void createFileWorkflow(Scanner scanner) {
        System.out.print("Enter Directory Name to add File (or type 'none' if no directory): ");
        String dirName = scanner.nextLine();

        System.out.print("Enter File Name: ");
        String fileName = scanner.nextLine();

        if(fileMap.containsKey (fileName)){
            // 这个意味着系统中不能有重名的两个文件
            System.out.println ("already exist this file in system");
            logger.error ("File Management: already exist this file "+fileName+" in system");
            return;
        }

        if (!"none".equalsIgnoreCase(dirName)) {
            // Check if the directory exists
            if (!directoryMap.containsKey(dirName)) {
                System.out.println("Directory does not exist. Please create the directory first.");
                return;
            }

            // Retrieve the directory object
            Directory directory = directoryMap.get(dirName);

            // Check if the file already exists in the directory
            if (directory.getFileNames().contains(fileName)) {
                System.out.println("A file with the name '" + fileName + "' already exists in the directory '" + dirName + "'.");
                return; // Exit the method if file already exists
            }

            // Add the file to the directory and the global file map
            createFile(fileName, dirName);
        } else {
            // Create a file without a directory
            createFile(fileName, null);
        }
    }

    public static void createProcessWorkflow(Scanner scanner) {
        System.out.print("Enter File Name to add Process: ");
        String fileName = scanner.nextLine();
        if (!fileMap.containsKey(fileName)) {
            System.out.println("File does not exist. Please create the file first.");
            return;
        }
        System.out.print("Enter Process Name: ");
        String processName = scanner.nextLine();
        System.out.print("Enter Priority (integer):[1~10] ");
        int priority = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Memory Size (integer): ");
        int memorySize = Integer.parseInt(scanner.nextLine());
        // You may need to modify this to suit how you're actually creating processes
        int pid = createProcess(processName, priority, memorySize,fileName); // This method needs to be defined or updated
        if (pid != -1) {
            System.out.println ("The process create successfully and pid is: " + pid);
            logger.info ("File Management: The process create successfully and pid is: " + pid);
        }else{
            System.out.println ("create process failed");
        }
    }


    // Methods to simulate the system running, scheduling, etc.
    public void run () throws Exception {



        System.out.println ("Our OS Win99 Plus is Running!\n");
      //  logger.info ("ttttttttttttttttttttttttttttttt");
//        logger.info ("Begin Running!!!");
        // Implementation of the main simulation loop
        /*
        * CPU调度
        * 进程状态改变-等待io、死亡、中断
        * 内存分配
        * 页表分配+缺页错误
        * io 新开线程
         */
        while(true){
            if(!readyQueue.isEmpty ()||!runningQueue.isEmpty ()){
                // 调度选择某个进程进入CPU执行
                int thisPid = scheduler.selectNextProcess ();
                timer = 0;
                if(thisPid!=-1&&processMap.containsKey (thisPid)){
                    logger.info ("Process Management: Select PID: "+thisPid+" process to executed.");
                    /*
                     * 将进程状态改为running
                     * 从ready队列中移出，移入到running队列中
                     */
                    ProcessStruct thProcess = processMap.get (thisPid);
                    thProcess.setState (ProcessState.RUNNING);
                    OS.readyQueue.remove (thisPid);
                    OS.runningQueue.offer (thisPid);
                    thProcess.setState (ProcessState.RUNNING);
                    logger.info("Process Management: PID: " + thisPid + " state changed, and the owning queue was modified.");


                    /*
                     * 执行running队列中的进程
                     * 1. 该进程开始执行，
                     *       判断页表中是否有属于自己的页表项，若否 向页表中申请空间
                     * 2. 申请到空间之后一条一条执行指令
                     *       并且每执行一条指令，判断是否有中断发生，若有，则去处理
                     * 3. 根据指令内容进程执行，若有IO指令，则去执行IO
                     *       并且考虑进程的结束
                     * 4. 在合适的时机停止本次循环 选择下一个要执行的进程
                     *       将该进程状态改变，存储PC
                     */

                    if(thProcess.getPageTableIndex ().size ()==0){
                        // never allocated the tablePage entries
                        pageTable.allocateEntries (thProcess);
                    }
                    try{
                        // 这里需要将 页表管理、进程、物理内存都联系起来！
                        // 进程索引 thisPid
                        if(thProcess.getState ()!=ProcessState.TERMINATED){
                            if(thProcess.getPcPage()<thProcess.getMemorySize ()){
                                while (true) {
                                    Thread.sleep (300);
                                    // 去页表寻找是否有该物理页
                                    if (false == pageTable.isContainPhysicalPage (thProcess)) {
                                        // 页表项中无物理页，进行缺页错误
                                        pageTable.pageTableFault (thProcess);
                                    }
                                    // 逐个指令执行
                                    MemoryBlock tpBlock =
                                            memoryManager.getMemoryBlocks()[thProcess.getBeginMemoryIndex ()+thProcess.getPcPage ()];

                                    // while 循环执行一页中的内容，如果该页内容执行完毕，则跳出此while循环
                                    while (thProcess.getPc ()<MemoryBlock.BlockSize){

                                        Instruction tpInstruction = tpBlock.getInstruction (thProcess.getPc ());
                                        timer++;
                                        if(tpInstruction.getState ()==InstructionState.IO){
                                            logger.info("IO Management: "+" Execute the IO instruction in " +
                                                    (thProcess.getPcPage() + thProcess.getBeginMemoryIndex()) +
                                                    " Physical page in " +
                                                    (thProcess.getPc() - 1) +
                                                    " Instruction for process " + thProcess.getPid());


                                            // System.out.println (" -  - 404 wait finish - - ");

                                            OS.addInterrupt (new IoBeginInterrupt (thProcess.getPid (),1));
                                            // 这里应该抛出一个中断,在该指令执行后的if里面进行判断
                                            // thProcess.addPc ();// 指令执行完成
                                        }else {
                                            // 是计算类型的指令，继续执行

                                            logger.info("CPU Management: "+" Execute the calculate instruction in " +
                                                    (thProcess.getPcPage() + thProcess.getBeginMemoryIndex()) +
                                                    " Physical page in " +
                                                    thProcess.getPc() +
                                                    " Instruction for process " + thProcess.getPid());

                                            thProcess.addPc ();// 指令执行完成
                                        }
                                        //这里第一个if没问题，executeInterrupt之内有个循环
                                        if(OS.interruptManager.getInterruptNum ()!=0) {
                                            OS.interruptManager.executeInterrupt ();
                                            if(OS.interruptManager.getIsSchedule ()){
                                                break;
                                            }
                                        }
//                                  // 一种类型的中断是，处理中断后继续执行该进程
//                                  // 还有一种可能是，IO中断，这是进程需要重新调度
                                        if(timer>=timerSize&&scheduleRecord==ScheduleRecord.TIME){
                                            break;
                                        }
                                    }

                                    // if("需要重新调度进程，则应该在此break")
                                    // 目前来看
                                    // 在进程去执行Io时需要重新运行调度器
                                    if(OS.interruptManager.getIsSchedule ()){
                                        interruptManager.setIsSchedule (false);
                                        break;
                                    }
                                    // 一页执行完毕
                                    if(thProcess.getPc ()==MemoryBlock.BlockSize){
                                        thProcess.setPc (0);
                                        thProcess.addPcPage ();
                                        logger.info("Memory Management: Completed execution of current memory page. " +
                                                "PC reset to 0, moved to next page. Current PcPage: " + thProcess.getPcPage());
                                    }
                                    // 判断进程是否执行完毕
                                    if(thProcess.getPcPage ()>=thProcess.getMemorySize ()){
                                        OS.addInterrupt (new ProcessTerminateInterrupt (thProcess.getPid (),1));
//                                    OS.interruptManager.addInterrupt ();
//                                    // process execute finally
//                                    System.out.println ("process "+thProcess.getPid ()+" execute finally");
//                                    OS.runningQueue.remove (thisPid);
//                                    thProcess.setState (ProcessState.TERMINATED);
//                                    OS.terminateQueue.offer (thisPid);
                                        break;
                                        // break之后再次进入大循环，会有调度器进行分配新进程
                                    }

                                    if(timer>=timerSize&&scheduleRecord==ScheduleRecord.TIME){
//                                        System.out.println ("timer slice 时间片轮转");
                                        logger.info ("Process Management: Time slice rotates, rescheduling");
                                        OS.runningQueue.remove (thisPid);
                                        OS.readyQueue.offer (thisPid);
                                        thProcess.setState (ProcessState.READY);
                                        break;
                                    }

                                }
                            }
                        }
                    }catch (Exception e){
                        if(thProcess.getPageTableIndex ().size ()==0){
                            System.out.println (" 进程无页表项，需要重新分配页表");
                            logger.error ("Process Management: The process has no page table entries and requires " +
                                    "reallocation of the page table.");
                        }
                    }

                }
            }
            if(OS.interruptManager.getInterruptNum ()!=0) {

                OS.interruptManager.executeInterrupt ();
            }
        }
    }
}
