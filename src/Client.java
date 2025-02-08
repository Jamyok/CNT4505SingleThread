import java.util.Scanner;
import java.net.*;
import java.io.*;

public class Client {


//     Client-TO-DO:
// Includes:
// Query user for server Ip address (DONE)
// Fetch address (adress) (DONE)
// Query user for server port(port); (DONE)
// Create socket(adress, port) (DONE)

// while(true){
// 	Present Menu of commands
// 	Fetch command to run (command)
// Query for how many times (number)
// Make threads
// Run threads
// Join
// Calculate total time
// Calculate avg time
// }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Hello, What IP address would you like to connect to: ");
            String ipAdress = scanner.nextLine();

            System.out.println("What port would you like to connect to: ");
            int portAdress = scanner.nextInt();

            if(portAdress < 0 || portAdress > 65535){
                System.out.println("Port Adress out of bounds,(0-65,535)");
                System.exit(0);
            }

            // Save port to portConfig.txt
            try (FileWriter writer = new FileWriter("portConfig.txt")) {
                writer.write(String.valueOf(portAdress));
            }

            System.out.println("Connecting to IP: " + ipAdress + " on port: " + portAdress);

             Socket s = new Socket(ipAdress, portAdress);
                System.out.println("Connected to server.");
            
            //msg TO server
            PrintWriter pr = new PrintWriter(s.getOutputStream());
            pr.println("Hello Server!");
            pr.flush();

            //msg FROM SERVER
            InputStreamReader in = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(in);

            String str = bf.readLine();
            System.out.println("Server"+ str);

        }//end try scanner
        
        catch (Exception e) {
            System.out.println("Exception: IP or Port issue..." + e.getMessage());
        }//end catch
        
    }//end main

}//end Client