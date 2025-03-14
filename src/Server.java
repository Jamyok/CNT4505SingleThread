import java.io.*;
import java.net.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    //store the start time of the server so it can be used for uptime calculation
    public static final Instant serverStartTime = Instant.now();

    //create list to store active clients to be returned to client itself
    public static final List<Socket> activeClients = new ArrayList<>();

//     Server-TO-DO
//     Includes

// Query user for port to listen on (port) (DONE)
// Create server socket(port) (DONE)

// while(true){
// Accept user command(socket)
// Figure out which command
// Execute command
// Collect result
// Send back the result to the client(socket)
// }

public static void main(String[] args) {
    try {
        // Read port from portConfig.txt
        int portAddress;
        try (Scanner fileScanner = new Scanner(new File("portConfig.txt"))) {
            portAddress = fileScanner.nextInt();
        }

        // Create Server Socket
        try (ServerSocket serverSocket = new ServerSocket(portAddress)) {
            System.out.println("Server started on port " + portAddress + ". Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                // Prevent concurrent modification issues with synchronized list access
                synchronized (activeClients) {
                    activeClients.add(clientSocket);
                }
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle client in a separate thread
                new Thread(new ClientHandler(clientSocket)).start();
            }
        }
    } catch (FileNotFoundException e) {
        System.out.println("Error: portConfig.txt not found. Please create the file and specify a port.");
    } catch (IOException e) {
        System.out.println("Server error: " + e.getMessage());
    }
}
}

// Handles individual client connections
class ClientHandler implements Runnable {
private final Socket clientSocket;

public ClientHandler(Socket socket) {
    this.clientSocket = socket;
}

@Override
public void run() {
    try (
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
    ) {
        String clientMessage;
        while ((clientMessage = in.readLine()) != null) {
            System.out.println("Client: " + clientMessage);

            // Process the client's command
            String response = handleClientCommand(clientMessage);
            out.println(response); // Send response back to client
        }
    } catch (IOException e) {
        System.out.println("Error handling client: " + e.getMessage());
    } finally {
        try {
            synchronized (Server.activeClients) {
                Server.activeClients.remove(clientSocket);
            }
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing client socket: " + e.getMessage());
        }
    }
}

// Processes client commands and returns appropriate responses
private String handleClientCommand(String command) {
    switch (command.toLowerCase()) {
        case "time":
            return "Current Date and Time on Server: " + ZonedDateTime.now();

        case "up":
            Duration uptime = Duration.between(Server.serverStartTime, Instant.now());
            return String.format("Server uptime: %d hours, %d minutes, %d seconds",
                    uptime.toHours(), uptime.toMinutesPart(), uptime.toSecondsPart());

        case "mem":
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            return String.format("Server memory: %d MB used", usedMemory / (1024 * 1024));

        case "net":
            return getNetStat(23);
        case "cusers":
            return getActiveConnections();

        case "rprocess":
            return getRunningProcesses();

        default:
            return "Invalid command. Available commands: time, up, mem, net, cusers, rprocess.";
    }
}

public static String getNetStat(int port) {
    String command;
    StringBuilder netResult = new StringBuilder();
    String os = System.getProperty("os.name").toLowerCase();

    if(os.contains("win")) {
        command = "netstat -an | findstr :" + port;
    }
    else{
         command = "netstat -an | grep :" + port;   
    }

    try {
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        Process process = processBuilder.start();

        BufferedReader netReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while((line = netReader.readLine()) != null) {
            netResult.append(line).append("\n");
        }

        process.waitFor();

    } catch (Exception e) {
        return "Error retrieving network connections: " + e.getMessage();
    }
    return netResult.toString();
}

// Retrieves active network connections
public static String getActiveConnections() {
    synchronized (Server.activeClients) {
        StringBuilder result = new StringBuilder("Active connections: " + Server.activeClients.size() + "\t");
        for (Socket client : Server.activeClients) {
            result.append(client.getInetAddress()).append(":").append(client.getPort()).append("\t");
        }
        return result.toString();
    }
}

// Retrieves running processes (supports both Windows and Linux/Mac)
public static String getRunningProcesses() {
    StringBuilder processesResult = new StringBuilder();
    String os = System.getProperty("os.name").toLowerCase();
    Process process;
    try {
        if (os.contains("win")) {
            process = new ProcessBuilder("cmd", "/c", "tasklist").start();
        } else {
            process = new ProcessBuilder("bash", "-c", "ps -aux").start();
        }

        BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = processReader.readLine()) != null) {
            processesResult.append(line).append("\t");
        }
    } catch (IOException e) {
        return "Error retrieving processes: " + e.getMessage();
    }
    return processesResult.toString();
}
}