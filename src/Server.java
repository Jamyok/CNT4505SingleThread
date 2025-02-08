import java.net.*;
import java.io.*;
import java.util.Scanner;

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
        int portAdress;
        try (Scanner fileScanner = new Scanner(new File("portConfig.txt"))) {
            portAdress = fileScanner.nextInt();
        }//end try scanner

        try (// Create Server Socket
        ServerSocket ss = new ServerSocket(portAdress)) {
            System.out.println("Server started on port " + portAdress + ", waiting for client...");

            Socket s = ss.accept();

            //receive msg FROM client
            InputStreamReader in = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(in);

            String str = bf.readLine();
            System.out.println("Client"+ str);

            //Messege sent TO client
            PrintWriter pr = new PrintWriter(s.getOutputStream());
            pr.println("Hello Client!");
            pr.flush();


        }//end try serversocket
        System.out.println("Client connected!");
    }//end try

    catch (Exception e) {
        System.out.println("Exception in Server: " + e.getMessage());
    }//end catch

}//end main
}//end server class