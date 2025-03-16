import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Client {

    
    // This project was created by Amanda Olyer, Macy Hayes, and Jamy De Vries

    // Client-TO-DO:
    // Includes:
    // Query user for server IP address (DONE)
    // Fetch address (address) (DONE)
    // Query user for server port(port); (DONE)
    // Create socket(address, port) (DONE)
    // while(true){(DONE)
    // 	Present Menu of commands(DONE)
    // 	Fetch command to run (command)
    // Query for how many times (number)(DONE)
    // Make threads
    // Run threads
    // Join
    // Calculate total time
    // Calculate avg time
    // }

    private static Scanner scanner;
    private static PrintWriter outTo;
    private static BufferedReader inFromServer;
    private static Socket s = null;

    private static double totalTat = 0; // Stores total turn-around time
    //private static int completedRequests = 0; // Counts completed threads
    private static int numThreads;

    public static void main(String[] args) {
        System.out.println("***********************************************");
        System.out.println("* Iterative Socket Server                     *");
        System.out.println("* Created by:                                 *");
        System.out.println("* Amanda Olyer, Macy Hayes, and Jamy De Vries *");
        System.out.println("***********************************************");

        scanner = new Scanner(System.in);
        String ipAddress = "";
        int portAddress = -1;
        
        while (true) {
            try {
                displayMenu();
                System.out.print("Enter your command: ");
                String input = scanner.nextLine().trim().toLowerCase();

                if (input.equals("ip")) {
                    System.out.print("Enter IP address: ");
                    ipAddress = scanner.nextLine().trim();
                    System.out.println("IP Address set to: " + ipAddress);
                } 

                else if (input.equals("port")) {
                    System.out.print("Enter port number: ");
                    portAddress = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (portAddress < 0 || portAddress > 65535) {
                        System.out.println("Invalid port (0-65535).");
                    } else {
                        // Save port to file
                        try (FileWriter writer = new FileWriter("portConfig.txt")) {
                            writer.write(String.valueOf(portAddress));
                            System.out.println("Port saved successfully.");
                        }
                    }
                } 

                else if (input.equals("view")) {
                    if (ipAddress.isEmpty() || portAddress == -1) {
                        System.out.println("Set IP and port first.");
                    } else {
                        System.out.print("\nConnecting... ");
                        try {
                            s = new Socket(ipAddress, portAddress);
                            outTo = new PrintWriter(s.getOutputStream(), true);
                            inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
                            System.out.println("Connected!");
                        } catch (Exception e) {
                            System.out.println("Connection failed: " + e.getMessage());
                        }
                    }
                }

                else if (input.equals("sm")) {
                    if (s != null && !s.isClosed()) {
                        System.out.print("Enter message: ");
                        String message = scanner.nextLine();
                        outTo.println(message);
                        System.out.println("Sent: " + message);
                        System.out.println("Server response: " + inFromServer.readLine());
                    } else {
                        System.out.println("No active connection. Use 'view' first.");
                    }
                }

                else if (input.equals("or")) {
                    if (s != null && !s.isClosed()) {
                        String serverResponse = inFromServer.readLine();
                        System.out.println("Server: " + serverResponse);
                    } else {
                        System.out.println("No active connection. Use 'view' first.");
                    }
                }

                else if (input.equals("thread")) {
                    handleThreadOperation(scanner, ipAddress, portAddress);

                }

                else if (isValidCommand(input)) {
                    sendCommandToServer(input);
                }

                else if (input.equals("q")) {
                    System.out.println("Quitting...");
                    if (s != null && !s.isClosed()) {
                        s.close();
                    }
                    break;
                } 

                else {
                    System.out.println("Invalid command. Try again.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void handleThreadOperation(Scanner scanner, String ipAddress, int portAddress) {
        System.out.print("Enter number of threads (1-25): ");
        numThreads = scanner.nextInt();
        scanner.nextLine();
        if(numThreads == -1) return;

        while (true) { 
            System.out.println("\nAvailable commands: time, up, mem, net, cusers, rprocess, exit");
            System.out.print("Enter command: ");
            String operation = scanner.nextLine().trim().toLowerCase();

            if (operation.equals("exit")) {
                System.out.println("Returning to main menu.");
                return;
            }

            String[] validCommands = {"time", "up", "mem", "net", "cusers", "rprocess"};
            if (!Arrays.asList(validCommands).contains(operation)) {
                System.out.println("Invalid command. Try again.");
                continue;
            }

            executeThreads(numThreads, operation, ipAddress, portAddress);
        }
    
    }

    private static void executeThreads(int numThreads, String operation, String ipAddress, int portAddress) {
        List<Thread> threads = new ArrayList<>();
        List<Double> turnaroundTimes = Collections.synchronizedList(new ArrayList<>());

        for (int i = 1; i <= numThreads; i++) {
            int threadId = i;
            Thread thread = new Thread(() -> {
                long startTime = System.nanoTime();
                String response = sendRequest(operation, ipAddress, portAddress);
                long endTime = System.nanoTime();
                double turnaroundTime = (endTime - startTime) / 1000000.0; // Convert to ms
                
                turnaroundTimes.add(turnaroundTime);
                System.out.printf("Thread %d received: %s%n", threadId, response);
                System.out.printf("Thread %d | TAT: %.2fms%n", threadId, turnaroundTime);
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join(); // Wait for all threads to complete
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Calculate and display the average turn-around time
        double avgTAT = turnaroundTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        System.out.printf("%nAverage Turn-around Time for all threads: %.2fms%n", avgTAT);
    }

    
    private static String sendRequest(String request, String ipAddress, int portAddress) {
        try (Socket socket = new Socket(ipAddress, portAddress);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request);
            return in.readLine(); // Get server response
        } catch (IOException e) {
            return "Error: Unable to contact server";
        } 
    } 
    
  

    // Display the available commands
    private static void displayMenu() {
        System.out.println("\n_____________________________________");
        System.out.println("|Menu of commands:                  |");
        System.out.println("|ip      - Set IP address           |");
        System.out.println("|port    - Set port                 |");
        System.out.println("|view    - View connection          |");
        System.out.println("|sm      - Send message to server   |");
        System.out.println("|or      - Open reader              |");
        System.out.println("|thread  - Start thread             |");
        System.out.println("|time    - Get server time & date   |");
        System.out.println("|up      - Get server uptime        |");
        System.out.println("|mem     - Get memory usage         |");
        System.out.println("|net     - Get network connections  |");
        System.out.println("|cusers  - Get current users        |");
        System.out.println("|rprocess- Get running processes    |");
        System.out.println("|q       - Quit                     |");
        System.out.println("-------------------------------------");
    }

    // Check if input is a valid server command
    private static boolean isValidCommand(String input) {
        return input.equals("time") || input.equals("up") || input.equals("mem") ||
               input.equals("net") || input.equals("cusers") || input.equals("rprocess");
    }

    // Send a command and receive the response
    private static void sendCommandToServer(String command) {
        try {
            if (s != null && !s.isClosed()) {
                outTo.println(command);
                String response;
                while ((response = inFromServer.readLine()) != null) {
                    System.out.println(response);
                }
            } else {
                System.out.println("No active connection. Use 'view' first.");
            }
        } catch (IOException e) {
            System.out.println("Error sending command: " + e.getMessage());
        }
    }

    // Threaded worker for multiple requests
    static class ClientWorker implements Runnable {
        private final Socket socket;
        private final int threadId;

        public ClientWorker(Socket socket, int threadId) {
            this.socket = socket;
            this.threadId = threadId;
        }

        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                double totalTat = 0;

                double startTime = System.currentTimeMillis();

                out.println("Hello from thread: " + threadId);

                String response;

                double endTime = System.currentTimeMillis();

                double tat = endTime - startTime;

                while((response = in.readLine()) != null){
                    System.out.println("Thread " + threadId + " received: " + response + " | Turn-around Time: " + tat + "ms");

                }

                totalTat += tat;

                System.out.println("Thread " + threadId + " | Turn-around Time: " + tat + "ms");

            } catch (IOException e) {
                System.out.println("Thread " + threadId + " error: " + e.getMessage());
            }
        }
    }
}