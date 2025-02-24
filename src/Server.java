import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.time.*;

public class Server {
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
            if(clientMessage == "time"){
                ZonedDateTime now = ZonedDateTime.now();
                System.out.printf("Current Date and Time on Server (YYYY-MM-DD)T(HR:MIN:SEC)[TIME/ZONE] \n"+now);
            }
        }
    } 
    catch (IOException e) {
        System.out.println("Error handling client: " + e.getMessage());
    } 
    finally {
        try {
            clientSocket.close();
        } 
        catch (IOException e) {
            System.out.println("Error closing client socket: " + e.getMessage());
        }
    }

  }//end run

}//end handler class