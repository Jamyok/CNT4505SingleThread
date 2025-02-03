import java.util.Scanner;

public class Client {

//     Client:

// Includes:

// Query user for server Ip address
// Fetch address (adress)
// Query user for server port(port);
// Create socket(adress, port)
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

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) 
        {
            System.out.println("Hello, What IP adress would you like to connect to: ");
            String ipAdress = scanner.nextLine();

            System.out.println("What port would you like to connect to: ");
            String portAdress = scanner.nextLine();

            //testing
            System.out.println("IP: "+ ipAdress);
            System.out.println("Port: "+ portAdress);
        }
            catch (Exception e) 
            {
            System.out.println("Exeption thrown for scanner: IP or Port");//handle exception
        }
    }
}
