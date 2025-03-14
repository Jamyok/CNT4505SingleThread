import java.io.*;
import java.net.*;
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
    private static int completedRequests = 0; // Counts completed threads
    private static int numThreads; // Total number of threads

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
                    if (s != null && !s.isClosed()) {
                        System.out.print("Enter number of threads (1-25): ");
                        numThreads = scanner.nextInt();
                        scanner.nextLine(); 

                        if (numThreads < 1 || numThreads > 25) {
                            System.out.println("Number must be between 1 and 25.");
                        } else {
                            Thread[] threads = new Thread[numThreads];
                            for (int i = 0; i < numThreads; i++) {
                                threads[i] = new Thread(new ClientWorker(s, i + 1));
                                threads[i].start();
                            }
                            for (Thread t : threads) {
                                t.join();
                            }
                            System.out.println("All threads completed.");
                            System.out.println("Average Turn-around Time: " + (totalTat / numThreads) + "ms");
                        }
                    } else {
                        System.out.println("No active connection. Use 'view' first.");
                    }
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

    private synchronized static void updateResults(double tat){
        totalTat += tat;
        completedRequests++;

        //System.out.println("Average Turn-around Time: " + (totalTat / numThreads));

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
                //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                double startTime = System.currentTimeMillis();

                out.println("Hello from thread: " + threadId);

                double endTime = System.currentTimeMillis();

                double tat = endTime - startTime;

                //String response = in.readLine();

                System.out.println("Thread " + threadId + " received: | Turn-around Time: " + tat + "ms");

                updateResults(tat);

            } catch (IOException e) {
                System.out.println("Thread " + threadId + " error: " + e.getMessage());
            }
        }
    }
}