import java.util.Scanner;

public class UseOs extends Thread {
    public void run() {
        System.out.println ("OK, welcome to use win99 Os");
        Scanner scanner = new Scanner (System.in);
        while (true) {
            System.out.println("Select the OS operation:");
            System.out.println("1: Create Directory");
            System.out.println("2: Create File");
            System.out.println("3: Create Process");
            System.out.println("4: Shutdown System");
            System.out.println("5: Adjust Process Priority");
            System.out.println("6: Output System Information");
            System.out.println("Please input:");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        // 1: Create Directory
                        // should create new interrupt
                        OS.addInterrupt (new CreateDirtoryInterrupt (1));
                        break;
                    case 2:
                        // 2: Create File
                        OS.addInterrupt (new CreateFileInterrupt (1));
                        break;
                    case 3:
                        // 3: Create Process
                        OS.addInterrupt (new CreateProcessInterrupt (1));
                        break;
                    case 4:
                        // 4: Shutdown System
                        OS.addInterrupt (new ElectronicBreakInterrupt (5));
                        break;
                    case 5:
                        // 5: Adjust Process Priority
                        System.out.println ("please input process Pid to adjust priority");
                        OS.addInterrupt (new priorityAdjustInterrupt (scanner.nextInt (),1));
                        break;
                    case 6:
                        // 6: Output System Information
                        OS.addInterrupt (new OutputSysInformation (1));
                        break;
                    case 0:
                        //System.out.println("Exiting...");
                        return; // Exit the program
                    default:
                        System.out.println("Invalid input. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume the invalid input
            }
        }
    }
}

