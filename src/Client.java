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

    public static void main(String[] args) {
        System.out.println("***********************************************");
        System.out.println("* Iterative Socket Server                     *");
        System.out.println("* This project was created by;                *");
        System.out.println("* Amanda Olyer, Macy Hayes, and Jamy De Vries *");
        System.out.println("***********************************************");

        
        scanner = new Scanner(System.in);
        String ipAddress = "";
        int portAddress = -1;
        Socket s = null;
        String response;
        long totalTurnAroundTime = 0;
    
        class ClientWorker implements Runnable {
            private final Socket socket;
            public static PrintWriter out;
        
            public ClientWorker(Socket socket) {
                this.socket = socket;
            }
        
            
            @Override
            public void run() {
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("Hello from thread: " + Thread.currentThread().getId());
                } 
                catch (IOException e) {
                    System.out.println("Error in thread: " + e.getMessage());
                }
            }//end run 
        }//end ClientWorker
            



        // While input is not "q"
        while (true) {
            try {
                System.out.println("\n");
                System.out.println("_____________________________________");
                System.out.println("|Menu of commands:                  |");
                System.out.println("|ip      -Set IP address            |");
                System.out.println("|port    -Set port                  |");
                System.out.println("|view    -View connection           |");
                System.out.println("|sm      -Send message to server    |");
                System.out.println("|or      -Open reader               |");
                System.out.println("|t       -Set number of times       |");
                System.out.println("|thread  -Start thread              |");
                System.out.println("|time    -return server time & date |");
                System.out.println("|up      -return server uptime      |");
                System.out.println("|mem     -return Memory use         |");
                System.out.println("|net     -return network connections|");
                System.out.println("|cusers  -return current users      |");
                System.out.println("|rproces -return running processes  |");
                System.out.println("|q       -Quit                      |");
                System.out.println("-------------------------------------");

                System.out.print("Enter your command: ");
                String input = scanner.nextLine().trim().toLowerCase();

                outTo = new PrintWriter(s.getOutputStream(), true);

                if (input.equals("ip")) {
                    System.out.print("Input desired IP address: ");
                    ipAddress = scanner.nextLine().trim();
                    System.out.println("IP Address set to: " + ipAddress);
                } // end if ip

                else if (input.equals("port")) {
                    System.out.print("What port would you like to connect to: ");
                    portAddress = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (portAddress < 0 || portAddress > 65535) {
                        System.out.println("Port Address out of bounds (0-65,535)");
                    } else {
                        // Save port to portConfig.txt
                        try (FileWriter writer = new FileWriter("portConfig.txt")) {
                            writer.write(String.valueOf(portAddress));
                            System.out.println("Port saved successfully.");
                        }
                    }
                } // end if port

                else if (input.equals("view")) {
                    if (ipAddress.isEmpty() || portAddress == -1) {
                        System.out.println("Please set IP and port first.");
                    } else {
                        System.out.print("\nConnecting:  ");
                        
                        Thread spinner = new Thread(() -> {
                            String[] spinChars = {"|", "/", "-", "\\"};
                            int i = 0;
                            while (!Thread.currentThread().isInterrupted()) {
                                System.out.print("\b" + spinChars[i++ % spinChars.length]); // Overwrite last char
                                try {
                                    Thread.sleep(200); // Speed of animation
                                } catch (InterruptedException e) {
                                    break;
                                }
                            }
                        });
                
                        spinner.start(); 
                
                        try {
                            s = new Socket(ipAddress, portAddress);
                            spinner.interrupt(); // Stop animation
                            System.out.println("\bConnection successful!");
                        } catch (Exception e) {
                            spinner.interrupt(); // Stop animation
                            System.out.println("\bConnection failed: " + e.getMessage());
                        }
                    }
                }// end if view

                else if (input.equals("t")) {
                    System.out.print("How many times would you like to run the commands: ");
                    int times = scanner.nextInt();
                    scanner.nextLine();

                    if (times < 1 || times > 100) {
                        System.out.println("Out of range. Please select within (1-100)");
                    } else {
                        System.out.println("Running the command " + times + " times.");
                    }

                    for(int i = 0; i < times; i++){
                        long startTime = System.nanoTime();

                        outTo.println(input);
                        response = scanner.nextLine();

                        long endTime = System.nanoTime();

                        long turnAroundTime = (endTime - startTime) /1000000;
                        totalTurnAroundTime += turnAroundTime;

                        System.out.println("Request " + (i + 1) + ": Server response: " + response);
                        System.out.println("Turn-around time: " + turnAroundTime + " ms");
                    }

                }

                  // Message to server
                else if (input.equals("sm")) {
                    if (s != null && !s.isClosed()) {
                        PrintWriter pr = new PrintWriter(s.getOutputStream(), true);
                        System.out.print("Enter message to send: ");
                        String message = scanner.nextLine();
                        pr.println(message);
                        System.out.println("Message sent: " + message);
                    } else {
                        System.out.println("Error: No active connection. Use 'view' to connect first.");
                    }
                }

                // Message from server
                else if (input.equals("or")) {
                    if (s != null && !s.isClosed()) {
                        InputStreamReader in = new InputStreamReader(s.getInputStream());
                        BufferedReader bf = new BufferedReader(in);
                        String str = bf.readLine();
                        System.out.println("Server: " + str);
                    } else {
                        System.out.println("Error: No active connection. Use 'view' to connect first.");
                    }
                }

                else if (input.equals("thread")) {
                    if (s != null && !s.isClosed()) {
                        System.out.print("Enter the number of threads to create: ");
                        int numThreads = scanner.nextInt();
                        scanner.nextLine(); 
                
                        if (numThreads < 1 || numThreads > 10) {
                            System.out.println("Please enter a number between 1 and 10.");
                        } //end if
                        else {
                            Thread[] threads = new Thread[numThreads];
                            for (int i = 0; i < numThreads; i++) {
                                threads[i] = new Thread(new ClientWorker(s));
                                threads[i].start();
                            }//end for
                            for (Thread t : threads) {
                                try {
                                    t.join(); // Ensure threads complete
                                } catch (InterruptedException e) {
                                    System.out.println("Thread interrupted: " + e.getMessage());
                                }
                            }
                            System.out.println("All threads finished execution.");
                        }//end else
                    } 
                    else {
                        System.out.println("Error: No active connection. Use 'view' to connect first.");
                    }//end else
                }//end thread


                else if (input.equals("time")) {
                    //ZonedDateTime now = ZonedDateTime.now();

                    //System.out.printf("Current Date and Time (YYYY-MM-DD)T(HR:MIN:SEC)[TIME/ZONE] \n"+now);

                    // client can send request for time
                    //TODO: not working, says Client.out is null
                    outTo.println(input);

                    while(scanner.hasNextLine()) {
                        response = scanner.nextLine();
                        System.out.println(response);
                    }

                }

                else if (input.equals("up")) {
                    //TODO: not sure if working, no errors
                    outTo.println(input);

                    while(scanner.hasNextLine()) {
                        response = scanner.nextLine();
                        System.out.println(response);
                    }
                }

                else if (input.equals("mem")) {
                    //TODO: not sure if working, no errors
                    outTo.println(input);

                    while(scanner.hasNextLine()) {
                        response = scanner.nextLine();
                        System.out.println(response);
                    }
                }

                else if (input.equals("net")) {
                    //TODO: not sure if working, no errors
                    outTo.println(input);

                    while(scanner.hasNextLine()) {
                        response = scanner.nextLine();
                        System.out.println(response);
                    }
                }

                else if (input.equals("cusers")) {
                    //TODO: not sure if working, no errors
                    outTo.println(input);

                    while(scanner.hasNextLine()) {
                        response = scanner.nextLine();
                        System.out.println(response);
                    }
                }

                else if (input.equals("rproces")) {
                    //TODO: not sure if working, no errors
                    outTo.println(input);

                    while(scanner.hasNextLine()) {
                        response = scanner.nextLine();
                        System.out.println(response);
                    }
                }

                //quitting
                else if (input.equals("q")) {
                    System.out.println("\nQuitting...");
                    System.out.println("Thank you for using our program,");
                    System.out.println("Please give us an A+");
                    if (s != null && !s.isClosed()) {
                        s.close(); 
                    }
                    break;
                } else {
                    System.out.println("Invalid command. Please try again.");
                }

            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }

        scanner.close();
    } // end main

} // end Client