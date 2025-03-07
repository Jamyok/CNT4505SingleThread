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
        try (ServerSocket ss = new ServerSocket(portAddress)) {
            System.out.println("Server started on port " + portAddress + ". Waiting for clients...");

            while (true) {
                Socket clientSocket = ss.accept();

                // prevents issues with multiple threads modifying the list of active connections
                synchronized (activeClients) {
                    activeClients.add(clientSocket);
                }
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle client in a separate thread
                new Thread(new ClientHandler(clientSocket)).start();

            }//end while

        }//end try
    } catch (FileNotFoundException e) {
        System.out.println("Error: portConfig.txt not found. Please create the file and specify a port.");
    } catch (IOException e) {
        System.out.println("Server error: " + e.getMessage());
    }
  }//end main
}//end server class

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
            out.println("Server received: " + clientMessage);
            if(clientMessage.equalsIgnoreCase("time")){
                ZonedDateTime now = ZonedDateTime.now();

                //attached the server time to its own variable so it can be sent to the client
                String serverTime = "Current Date and Time on Server: " + now;
                System.out.println(serverTime);
                out.println(serverTime);

                //System.out.printf("Current Date and Time on Server (YYYY-MM-DD)T(HR:MIN:SEC)[TIME/ZONE] \n"+now);
            }
            else if (clientMessage.equalsIgnoreCase("up")){
                Duration uptime = Duration.between(Server.serverStartTime, Instant.now());
                String uptimeMsg = String.format("Server uptime: %d hours, %d minutes, %d seconds", uptime.toHours(), uptime.toMinutesPart(), uptime.toSecondsPart());
                System.out.println(uptimeMsg);
                out.println(uptimeMsg);
            }
            else if (clientMessage.equalsIgnoreCase("mem")){
                Runtime runtime = Runtime.getRuntime();
                long totalMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();
                long usedMemory = totalMemory - freeMemory;
                String memMsg = String.format("Server memory: %d MB used", usedMemory / (1024 * 1024)); //convert bytes to MB
                System.out.println(memMsg);
                out.println(memMsg);
            }
            else if (clientMessage.equalsIgnoreCase("net")){
                out.println(getActiveConnections());
            }

            //this can probably be implemented better, just calling same method as before
            else if (clientMessage.equalsIgnoreCase("cusers")){
                out.println("Active users: " + getActiveConnections());
            }

            //2 possiblities found: ProcessHandle API? or system commands?
            else if (clientMessage.equalsIgnoreCase("rprocess")){

            }
        }
    } 
    catch (IOException e) {
        System.out.println("Error handling client: " + e.getMessage());
    } 
    finally {
        try {
            synchronized (Server.activeClients) {
                Server.activeClients.remove(clientSocket);
            }
            clientSocket.close();
        } 
        catch (IOException e) {
            System.out.println("Error closing client socket: " + e.getMessage());
        }
    }

  }//end run

  //method to get the current active network connections
  public static String getActiveConnections() {
    synchronized (Server.activeClients) {
        String result = "Active connections: " + Server.activeClients.size() + "\n";
        for(Socket client : Server.activeClients){
            result += client.getInetAddress() + ":" + client.getPort() + "\n";
        }
        return result;
    } 
}
 

}//end handler class