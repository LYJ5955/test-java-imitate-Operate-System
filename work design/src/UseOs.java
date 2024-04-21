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
            System.out.println("5: Adjust Priority");
            System.out.println("6: Output System Information");
            System.out.println("Please input:");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        //createDirectory(scanner);
                        break;
                    case 2:
                        //createFile(scanner);
                        break;
                    case 3:
                        //createProcess(scanner);
                        break;
                    case 4:
                        //shutdownSystem();
                        break;
                    case 5:
                        //adjustPriority(scanner);
                        break;
                    case 6:
                        //(scanner);
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

